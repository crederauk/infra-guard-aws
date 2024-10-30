package uk.co.credera.infraguardaws.service

/**
 * Enum representing query strings used to extract specific command details from an SSM command invocation.
 *
 * Each query string corresponds to a particular piece of command information that can be retrieved
 * from the command invocation's response structure.
 *
 * @property value The query string used to retrieve the detail from the SSM command invocation response.
 */
enum class SsmCommandDetailsQuery(val value: String) {
    /** Query string for extracting the command's execution status. */
    STATUS("CommandInvocations[].CommandPlugins[].Status"),

    /** Query string for retrieving the command's output data. */
    OUTPUT("CommandInvocations[].CommandPlugins[].Output[]")
}
