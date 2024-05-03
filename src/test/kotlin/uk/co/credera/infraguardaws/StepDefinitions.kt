package uk.co.credera.infraguardaws

import io.cucumber.java8.En
import mu.KotlinLogging
import org.assertj.core.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.configuration.AwsProperties
import uk.co.credera.infraguardaws.util.Exec
import java.util.concurrent.TimeUnit

/**
 * @see https://cucumber.io/docs/cucumber/step-definitions/?lang=kotlin
 */
class StepDefinitions(@Autowired val awsProperties: AwsProperties) : SpringContextConfiguration(), En {
    val log = KotlinLogging.logger {}
    lateinit var response: String

    init {
        requireNotNull(awsProperties)
        requireNotNull(awsProperties.profile)
        require(awsProperties.profile.isNotBlank())
        requireNotNull(awsProperties.region)
        require(awsProperties.region.isNotBlank())

        When("host {string} pings host {string} with timeout {long} seconds")
        { hostA: String, hostB: String, timeoutSeconds: Long ->
            requireNotNull(hostA)
            require(hostA.isNotBlank())
            requireNotNull(hostB)
            require(hostB.isNotBlank())
            require(timeoutSeconds > -1)
            val pingCommand = """
                aws ssm send-command \
                --document-name "AWS-RunShellScript" \
                --instance-ids "$hostA" \
                --parameters 'commands=["ping -c1 $hostB"]' \
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
                    fail<Nothing>(
                        "Error when retrieving token from sso: Token has expired and refresh failed: " +
                                "Try executing the following command before running the tests: " +
                                "\naws sso login --profile ${awsProperties.profile}"
                    )
                }
                throw e
            }
            val checkStatusCommand = """
                aws ssm list-command-invocations \
                --command-id "$commandId" \
                --details \
                --query "CommandInvocations[*].CommandPlugins[*].Status" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region} \
                --no-cli-pager
            """.trimIndent()
            var count = 0
            // Wait for the command to complete, until Status is Success or Failed
            var status: String = Exec.command(checkStatusCommand)
            while (status == "InProgress" && count < timeoutSeconds) {
                status = Exec.command(checkStatusCommand)
                TimeUnit.SECONDS.sleep(1)
                count++
            }
            if (status == "InProgress") {
                log.warn {
                    "Attempting to gather command output while it's still in progress: " +
                            "Consider increasing the timeout inside the feature file: " +
                            "When $hostA pings $hostB with timeout $timeoutSeconds seconds"
                }
            } else if (status != "Success") {
                log.warn { "Non-Success status $status returned when $hostA pings $hostB with timeout $timeoutSeconds seconds" }
            }
            val gatherStdoutCommand = """
                aws ssm list-command-invocations \
                --command-id "$commandId" \
                --details \
                --query "CommandInvocations[*].CommandPlugins[*].Output[]" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region} \
                --no-cli-pager
            """.trimIndent()
            response = Exec.command(gatherStdoutCommand)
            if (response.isBlank()) {
                log.warn { "Response was blank when $hostA pings $hostB with timeout $timeoutSeconds seconds" }
            }
        }
        Then("the ping is successful") {
            assertThat(response)
                .withFailMessage("Response was blank. Check previous output for warnings. This could need dev debugging as one of the `aws ssm` commands might have failed.")
                .isNotBlank()
            assertThat(response)
                .doesNotContain("100% packet loss")
        }
        Then("the ping is failed") {
            assertThat(response)
                .doesNotContain(" 0% packet loss")
        }
    }
}