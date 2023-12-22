package uk.co.credera.infraguardaws

import io.cucumber.java8.En
import org.springframework.beans.factory.annotation.Autowired
import uk.co.credera.infraguardaws.configuration.AwsProperties

/**
 * @see https://cucumber.io/docs/cucumber/step-definitions/?lang=kotlin
 */
class StepDefinitions(@Autowired val awsProperties: AwsProperties) : SpringContextConfiguration(), En {
    init {
        When("host {string} pings host {string}") { hostA: String, hostB: String ->
            println("profile: ${awsProperties.profile}")
            println("region: ${awsProperties.region}")
            println("When: $hostA pings $hostB")
        }
        Then("the ping is successful") {

        }
        Then("the ping is failed") {

        }
    }
}