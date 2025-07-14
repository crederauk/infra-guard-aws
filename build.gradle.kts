import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlinJvm)
    // Gradle versions plugin adds `gradle dependencyUpdates` task
    alias(libs.plugins.benManesVersions)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
}

group = "aero.sita"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.javaLanguage.get())
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //region Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //endregion

    //region Logging
    implementation(libs.kotlinLoggingJvm)
    //endregion

    //region Testing (Cucumber)
    testImplementation(libs.cucumberJava)
    testImplementation(libs.cucumberSpring)
    testImplementation(libs.cucumberJunitPlatformEngine)
    testImplementation(libs.junitPlatformSuite)
    //endregion

    //region Additional Cucumber Reports
    implementation(libs.extentreportsCucumberAdapter)
    implementation(libs.extentreports)
    //endregion
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

fun syncExistingSystemProperties(
    systemPropertyPrefix: String,
    systemPropertySetter: (String, String) -> Unit
) {
    val existingSystemProps = System.getProperties()
        .entries
        .filter { it.key.toString().startsWith(systemPropertyPrefix) }
        .sortedBy { it.key.toString() }

    existingSystemProps.forEach { (key, value) ->
        println("🔁 [${this::class.simpleName}] Reapplying system property [$key] = \"$value\" to ensure propagation to test JVM")
        systemPropertySetter(key.toString(), value.toString())
    }
}

fun mapEnvVarsToSystemProperties(
    prefix: String,
    systemPropertyPrefix: String,
    env: Map<String, String> = System.getenv(),
    systemPropertySetter: (String, String) -> Unit
) {
    println("🔧 [${this::class.simpleName}] Mapping ${prefix}_* environment variables to $systemPropertyPrefix* system properties...")

    val prefixedEnvVars = env
        .filterKeys { it.startsWith("${prefix}_") }
        .toSortedMap()

    if (prefixedEnvVars.isEmpty()) {
        println("⚠️ [${this::class.simpleName}] No ${prefix}_* environment variables found")
    }

    prefixedEnvVars.forEach { (envKey, rawValue) ->
        val suffix = envKey
            .removePrefix("${prefix}_")
            .lowercase()
            .replace('_', '.')

        val systemKey = "$systemPropertyPrefix$suffix"
        val cleanValue = rawValue.trim().removeSurrounding("\"").removeSurrounding("'")

        systemPropertySetter(systemKey, cleanValue)
        println("✅ [${this::class.simpleName}] Mapped $envKey → $systemKey = $cleanValue")
    }
}

fun printSystemPropertiesWithPrefix(systemPropertyPrefix: String) {
    println("🔍 [${this::class.simpleName}] Final $systemPropertyPrefix* System Properties set both in current process and propagated to test JVM:")
    System.getProperties()
        .entries
        .sortedBy { it.key.toString() }
        .filter { it.key.toString().startsWith(systemPropertyPrefix) }
        .forEach { (key, value) ->
            println(" - $key = $value")
        }
}

fun loadPrefixedEnvVarsAsSystemProperties(
    prefix: String,
    systemPropertyPrefix: String = prefix.lowercase().replace('_', '.') + ".",
    env: Map<String, String> = System.getenv(),
    systemPropertySetter: (String, String) -> Unit = { k, v -> System.setProperty(k, v) }
) {
    syncExistingSystemProperties(systemPropertyPrefix, systemPropertySetter)
    mapEnvVarsToSystemProperties(prefix, systemPropertyPrefix, env, systemPropertySetter)
    printSystemPropertiesWithPrefix(systemPropertyPrefix)
}

tasks.withType<Test> {
    useJUnitPlatform()

    doFirst {
        loadPrefixedEnvVarsAsSystemProperties(
            prefix = "CUCUMBER",
            systemPropertySetter = { k, v ->
                // Set both in current process and propagate to test JVM
                System.setProperty(k, v)
                systemProperty(k, v)
            }
        )
    }
}

// Gradle versions plugin adds `gradle dependencyUpdates` task.
// Disallow release candidates as upgradable versions from stable versions.
// See also: https://github.com/ben-manes/gradle-versions-plugin/blob/master/examples/kotlin/build.gradle.kts
tasks.withType<DependencyUpdatesTask> {
    // Disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
        candidate.version.isNonStable() && !currentVersion.isNonStable()
    }
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}
