package uk.co.credera.infraguardaws

import io.cucumber.java8.En
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.configuration.AwsProperties
import uk.co.credera.infraguardaws.util.Exec
import java.util.concurrent.TimeUnit

/**
 * @see https://cucumber.io/docs/cucumber/step-definitions/?lang=kotlin
 */
class StepDefinitions(@Autowired val awsProperties: AwsProperties) : SpringContextConfiguration(), En {
    val log = KotlinLogging.logger {}

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
            require(timeoutSeconds > 0)
            val pingCommand = """
                aws ssm send-command \
                --document-name "AWS-RunShellScript" \
                --instance-ids "$hostA" \
                --parameters 'commands=["ping -c3 $hostB"]' \
                --query "Command.CommandId" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region}
            """.trimIndent()
            val commandId = Exec.command(pingCommand)
            val checkStatusCommand = """
                aws ssm list-command-invocations \
                --command-id "${commandId}" \
                --details \
                --query "CommandInvocations[*].CommandPlugins[*].Status" \
                --output text \
                --profile ${awsProperties.profile} \
                --region ${awsProperties.region} \
                --no-cli-pager
            """.trimIndent()
            var count = 0
            // Wait for the command to complete, until Status is Success or Failed
            var status: String
            do {
                status = Exec.command(checkStatusCommand)
                TimeUnit.SECONDS.sleep(1)
                count++
            } while (count <= timeoutSeconds && status.equals("InProgress"))
            if (!status.equals("Success")) {
                log.warn { "Non-Success status ${status} returned when $hostA pings $hostB with timeout ${timeoutSeconds} seconds" }
            }
        }
        Then("the ping is successful") {

        }
        Then("the ping is failed") {

        }
    }
}