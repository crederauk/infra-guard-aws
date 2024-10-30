package uk.co.credera.infraguardaws.service

/**
 * Represents the result of an executed command, including its status and output.
 *
 * @property status The final status of the command after execution.
 * @property output The output from the command, represented as a non-null string.
 */
data class CommandResult(
    val status: CommandStatus,
    val output: String
)