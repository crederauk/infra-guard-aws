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
class EC2ServiceStatusSteps(@Autowired val ssmService: SsmService) : SpringContextConfiguration(), En {
    lateinit var commandResult: CommandResult

    init {
        When("service status on host {string} is active for service {string} with timeout {long} seconds") { host: String, serviceName: String, timeoutSeconds: Long ->
            require(host.isNotBlank())
            require(serviceName.isNotBlank())
            require(timeoutSeconds > -1)
            commandResult = ssmService.exec(host,"service $serviceName status" , timeoutSeconds)
        }
        Then("the service is running") {
            assertThat(commandResult.output)
                .doesNotContain("failed")
        }

    }
}