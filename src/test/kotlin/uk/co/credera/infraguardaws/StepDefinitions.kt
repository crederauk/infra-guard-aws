package uk.co.credera.infraguardaws

import io.cucumber.java8.En
import mu.KotlinLogging
import org.assertj.core.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.configuration.AwsProperties
import uk.co.credera.infraguardaws.util.Ssm

/** @see https://cucumber.io/docs/cucumber/step-definitions/?lang=kotlin */
class StepDefinitions(
        @Autowired val awsProperties: AwsProperties,
        @Autowired val ssmCommands: Ssm
) : SpringContextConfiguration(), En {
    val log = KotlinLogging.logger {}
    lateinit var response: String

    init {
        requireNotNull(awsProperties)
        requireNotNull(awsProperties.profile)
        require(awsProperties.profile.isNotBlank())
        requireNotNull(awsProperties.region)
        require(awsProperties.region.isNotBlank())

        When("host {string} pings host {string} with timeout {long} seconds") {
                hostA: String,
                hostB: String,
                timeoutSeconds: Long ->
            requireNotNull(hostA)
            require(hostA.isNotBlank())
            requireNotNull(hostB)
            require(hostB.isNotBlank())
            require(timeoutSeconds > -1)
            response = ssmCommands.exec(hostA, "ping -c1 $hostB", 10)

            if (response.isBlank()) {
                log.warn {
                    "Response was blank when $hostA pings $hostB with timeout $timeoutSeconds seconds"
                }
            }
        }
        Then("the ping is successful") {
            assertThat(response)
                    .withFailMessage(
                            "Response was blank. Check previous output for warnings. This could need dev debugging as one of the `aws ssm` commands might have failed."
                    )
                    .isNotBlank()
            assertThat(response).doesNotContain("100% packet loss")
        }
        Then("the ping is failed") { assertThat(response).doesNotContain(" 0% packet loss") }
    }
}
