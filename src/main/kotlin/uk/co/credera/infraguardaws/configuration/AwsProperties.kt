package uk.co.credera.infraguardaws.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * As stated in the docs
 * (https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties):
 * A "Java Bean" has to be provided in order to use ConfigurationProperties. This means your properties need to have
 * getters and setters, thus val is not possible at the moment. There's an open issue related to Kotlin for your use
 * case though: https://github.com/spring-projects/spring-boot/issues/8762
 */
@Component
@ConfigurationProperties(prefix = "aws", ignoreUnknownFields = false)
@Validated
class AwsProperties {
    lateinit var profile: String
    lateinit var region: String
}