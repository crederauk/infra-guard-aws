package uk.co.credera.infraguardaws.service

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.co.credera.infraguardaws.configuration.AwsProperties
import uk.co.credera.infraguardaws.util.Exec
import java.util.concurrent.TimeUnit

/**
 * SSM service for interacting with AWS Systems Manager (SSM) commands.
 */
@Service
class SsmService(@Autowired val awsProperties: AwsProperties) {
    private final val log = KotlinLogging.logger {}

    init {
        log.info { "Initializing SsmService with required AWS properties." }
    }

    @PostConstruct
    fun validateAwsProperties() {
        require(awsProperties.profile.isNotBlank()) { "AWS profile must not be blank" }
        require(awsProperties.region.isNotBlank()) { "AWS region must not be blank" }

        log.info { "AWS properties successfully validated: profile=${awsProperties.profile}, region=${awsProperties.region}" }
    }

    /**
     * Sends a command to a specified EC2 instance using AWS SSM and returns the execution result.
     *
     * This function initiates the execution of a specified command on a given EC2 instance,
     * waits for the result, and returns the status and output.
     *
     * @param instanceId The ID of the EC2 instance where the command will be executed.
     * @param command The command string to be executed on the specified instance.
     * @param timeoutSeconds The maximum time (in seconds) to wait for the command to complete before timing out.
     * @return A [CommandResult] containing the status and output of the command execution.
     */
    fun exec(instanceId: String, command: String, timeoutSeconds: Long): CommandResult {
        // Send command...
        val ssmCommand = """
                aws ssm send-command \
                --document-name "AWS-RunShellScript" \
                --instance-ids "$instanceId" \
                --parameters 'commands=["$command"]' \
                --query "Command.CommandId" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region}
            """.trimIndent()
        val commandId: String
        try {
            commandId = Exec.command(ssmCommand)
        } catch (e: RuntimeException) {
            val output: String
            if (e.message!!.contains("Error when retrieving token from sso")) {
                output = "Error when retrieving token from sso: Token has expired and refresh failed: " +
                        "Try executing the following command before running the tests: " +
                        "\naws sso login --profile ${awsProperties.profile}"
                log.warn { output }
            } else if (e.message!!.contains("The config profile (${awsProperties.profile}) could not be found")) {
                output = "The config profile (${awsProperties.profile}) could not be found"
                log.warn { output }
            } else {
                output = e.message!!
            }
            return CommandResult(CommandStatus.FAILED, output)
        }

        // Wait with timeout for the command to complete, until status is different from InProgress...
        var count = 0
        var statusDetails: String = getCommandDetails(commandId, SsmCommandDetailsQuery.STATUS)
        while (statusDetails == "InProgress" && count < timeoutSeconds) {
            statusDetails = getCommandDetails(commandId, SsmCommandDetailsQuery.STATUS)
            TimeUnit.SECONDS.sleep(1)
            count++
        }
        val status: CommandStatus
        if (statusDetails == "InProgress") {
            status = CommandStatus.TIMED_OUT
            log.debug { "Command timed out after $timeoutSeconds seconds: When executing $command on instance $instanceId" }
        } else if (statusDetails != "Success") {
            status = CommandStatus.FAILED
            log.debug { "Non-Success status $statusDetails returned: When executing $command on instance $instanceId" }
        } else {
            status = CommandStatus.SUCCESS
        }

        // Gather command output...
        val output = getCommandDetails(commandId, SsmCommandDetailsQuery.OUTPUT)
        if (output.isBlank()) {
            log.debug { "Output was blank: When executing $command on instance $instanceId" }
        }

        // Return status and output
        return CommandResult(status, output)
    }

    private fun getCommandDetails(commandId: String, query: SsmCommandDetailsQuery): String {
        return Exec.command(
            """
                aws ssm list-command-invocations \
                --command-id "$commandId" \
                --details \
                --query "${query.value}" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region} \
                --no-cli-pager
            """.trimIndent()
        )
    }
}