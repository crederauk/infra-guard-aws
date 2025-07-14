package uk.co.credera.infraguardaws

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.service.CommandResult
import uk.co.credera.infraguardaws.service.CommandStatus
import uk.co.credera.infraguardaws.service.SsmService

class ActiveServiceStatusSteps(@Autowired val ssmService: SsmService) {
    private var timeoutSeconds = 9L
    private lateinit var service: String
    private lateinit var host: String
    private lateinit var commandResult: CommandResult

    @Given("a timeout of {long} seconds")
    fun a_timeout_of_seconds(timeoutSeconds: Long) {
        this.timeoutSeconds = timeoutSeconds
    }

    @Given("the service {string} on host {string}")
    fun the_service_on_host(service: String, host: String) {
        this.service = service
        this.host = host
    }

    @When("the service status is retrieved")
    fun the_service_status_is_retrieved() {
        require(host.isNotBlank()) { "Host cannot be blank" }
        require(service.isNotBlank()) { "Service cannot be blank" }
        require(timeoutSeconds > 0) { "Timeout must be greater than zero" }
        commandResult = ssmService.exec(host, "service $service status", timeoutSeconds)
    }

    @Then("the service status must be active")
    fun the_service_status_must_be_active() {
        assertThat(commandResult.status).isEqualTo(CommandStatus.SUCCESS)
        assertThat(commandResult.output)
            .withFailMessage("Expected service status to be active but was: ${commandResult.output}")
            .contains("Active: active (running)")
    }
}
