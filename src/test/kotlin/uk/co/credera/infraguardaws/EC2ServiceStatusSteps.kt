package uk.co.credera.infraguardaws

import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.service.CommandResult
import uk.co.credera.infraguardaws.service.SsmService

/** See https://cucumber.io/docs/cucumber/step-definitions/?lang=kotlin */
class EC2ServiceStatusSteps(@Autowired val ssmService: SsmService) :
        SpringContextConfiguration(), En {

    private lateinit var commandResult: CommandResult

    init {
        Given("a list of specified hosts and services") {
            // This Given step serves as context and doesn't require an action
        }

        When(
            "checking service status on host {string} for service {string} with timeout of {long} seconds"
        ) { host: String, service: String, timeoutSeconds: Long ->
            require(host.isNotBlank()) { "Host cannot be blank" }
            require(service.isNotBlank()) { "Service cannot be blank" }
            require(timeoutSeconds > 0) { "Timeout must be greater than zero" }

            commandResult = ssmService.exec(host, "service $service status", timeoutSeconds)
        }

        Then("the service should be running") {
            assertThat(commandResult.output).doesNotContain("failed")
        }
    }
}
