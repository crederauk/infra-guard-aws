package uk.co.credera.infraguardaws

import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

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
class RunAllCucumberTests
