
package uk.co.credera.infraguardaws.util

import java.util.concurrent.TimeUnit
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.co.credera.infraguardaws.configuration.AwsProperties

/**
 * Utility class for interacting with AWS Systems Manager (SSM) commands.
 */
@Component
class Ssm(@Autowired val awsProperties: AwsProperties) {

    private val log = KotlinLogging.logger {}

    /**
     * Checks the status of a given AWS SSM command using the command ID.
     *
     * @param commandId The AWS SSM command ID whose status is being checked.
     * @param query The query string used to extract the specific status details from the command invocation.
     * @return The status of the command as a string.
     */
    private fun checkCommandStatus(commandId: String, query: String): String {
        return Exec.command(
                "aws ssm list-command-invocations " +
                        "--command-id '$commandId' " +
                        "--details " +
                        "--query '$query' " +
                        "--output text " +
                        "--profile ${awsProperties.profile} " +
                        "--region ${awsProperties.region} " +
                        "--no-cli-pager"
        )
    }

    /**
     * Sends a command to a specified EC2 instance using AWS SSM.
     *
     * @param instanceId The ID of the EC2 instance where the command will be executed.
     * @param command The command to be executed on the specified instance.
     * @param timeoutSeconds The maximum time (in seconds) to wait for the command to complete.
     * @return The response from the command execution, including status and any relevant output.
     * @throws IllegalStateException If there is an error related to SSO token retrieval.
     */
    fun exec(instanceId: String, command: String, timeoutSeconds: Long): String {

        lateinit var response: String
        val pingCommand =
                """
                aws ssm send-command \
                --document-name "AWS-RunShellScript" \
                --instance-ids $instanceId \
                --parameters 'commands=$command' \
                --query "Command.CommandId" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region}
            """.trimIndent()
        val commandId: String
        try {
            commandId = Exec.command(pingCommand)
        } catch (e: RuntimeException) {
            if (e.message!!.contains("Error when retrieving token from sso")) {
                throw IllegalStateException(
                        "Error when retrieving token from SSO: Token has expired and refresh failed. " +
                                "Try executing the following command before running the tests: " +
                                "\naws sso login --profile ${awsProperties.profile}"
                )
            }
            throw e
        }

        var count = 0
        // Wait for the command to complete, until the status is Success or Failed
        var status: String =
                checkCommandStatus(commandId, "CommandInvocations[*].CommandPlugins[*].Status")
        while (status == "InProgress" && count < timeoutSeconds) {
            status = checkCommandStatus(commandId, "CommandInvocations[*].CommandPlugins[*].Status")
            TimeUnit.SECONDS.sleep(1)
            count++
        }

        if (status == "InProgress") {
            log.warn {
                "Attempting to gather command output while it is still in progress. " +
                        "Consider increasing the timeout in the feature file: " +
                        "When $instanceId runs '$command' with a timeout of $timeoutSeconds seconds."
            }
        } else if (status != "Success") {
            log.warn {
                "Non-successful status '$status' returned when $instanceId ran '$command' with a timeout of $timeoutSeconds seconds."
            }
        }
        response = checkCommandStatus(commandId, "CommandInvocations[*].CommandPlugins[*].Status")
        return response
    }
}
