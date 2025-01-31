package uk.co.credera.infraguardaws

import io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.SelectClasspathResource
/**
 * This class serves as the runner for Cucumber/Gherkin tests. Gherkin feature files are located within the specified
 * src/test/resources/gherkin directory.
 *
 * To guide both your IDE and Gradle in locating tests, utilize the junit-platform-suite. The "gherkin" folder in
 * src/test/resources holds all .feature files for this project. It's worth noting that the SelectClasspathResource
 * annotation is repeatable, allowing the specification of multiple locations for feature files.
 * 
 * @see https://github.com/cronn/cucumber-junit5-example/blob/main/src/test/java/com/example/RunAllCucumberTests.java
 */
@Suite
@SelectClasspathResource("gherkin")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:") 
class RunAllCucumberTests
