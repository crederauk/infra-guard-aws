package uk.co.credera.infraguardaws

import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.service.CommandResult
import uk.co.credera.infraguardaws.service.CommandStatus
import uk.co.credera.infraguardaws.service.SsmService

/**
 * See https://cucumber.io/docs/cucumber/step-definitions/?lang=kotlin
 */
class PingSteps(@Autowired val ssmService: SsmService) : SpringContextConfiguration(), En {
    lateinit var commandResult: CommandResult

    init {
        When("host {string} pings host {string} with timeout {long} seconds")
        { hostA: String, hostB: String, timeoutSeconds: Long ->
            require(hostA.isNotBlank())
            require(hostB.isNotBlank())
            require(timeoutSeconds > -1)
            commandResult = ssmService.exec(hostA, "ping -c1 $hostB", timeoutSeconds)
        }
        Then("the ping is successful") {
            assertThat(commandResult.status)
                .isNotIn(CommandStatus.TIMED_OUT, CommandStatus.FAILED)
            assertThat(commandResult.output)
                .withFailMessage("Response was blank. Check previous output for warnings. This could need dev debugging as one of the `aws ssm` commands might have failed.")
                .isNotBlank()
                .doesNotContain("100% packet loss")
        }
        Then("the ping is failed") {
            assertThat(commandResult.output)
                .doesNotContain(" 0% packet loss")
        }
    }
}