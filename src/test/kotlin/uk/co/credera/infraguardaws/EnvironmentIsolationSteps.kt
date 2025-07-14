package uk.co.credera.infraguardaws

import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.service.CommandResult
import uk.co.credera.infraguardaws.service.CommandStatus
import uk.co.credera.infraguardaws.service.SsmService

class EnvironmentIsolationSteps(@Autowired val ssmService: SsmService) {
    lateinit var commandResult: CommandResult

    @When("host {string} pings host {string} with timeout {long} seconds")
    fun host_pings_host_with_timeout_seconds(hostA: String, hostB: String, timeoutSeconds: Long) {
        require(hostA.isNotBlank())
        require(hostB.isNotBlank())
        require(timeoutSeconds > -1)
        commandResult = ssmService.exec(hostA, "ping -c1 $hostB", timeoutSeconds)
    }

    @Then("the ping is successful")
    fun the_ping_is_successful() {
        assertThat(commandResult.status)
            .isNotIn(CommandStatus.TIMED_OUT, CommandStatus.FAILED)
        assertThat(commandResult.output)
            .withFailMessage("Response was blank. Check previous output for warnings. This could need dev debugging as one of the `aws ssm` commands might have failed.")
            .isNotBlank()
            .doesNotContain("100% packet loss")
    }

    @Then("the ping is failed")
    fun the_ping_is_failed() {
        assertThat(commandResult.output)
            .doesNotContain(" 0% packet loss")
    }
}
