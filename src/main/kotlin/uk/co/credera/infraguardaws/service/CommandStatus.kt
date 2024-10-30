package uk.co.credera.infraguardaws.service

/**
 * Enum representing the possible states of a command execution.
 *
 * Each state indicates a different stage or outcome of the command's lifecycle.
 */
enum class CommandStatus {
    /** Terminal state; transitions from IN_PROGRESS if the command times out. */
    TIMED_OUT,

    /** Terminal state; transitions from IN_PROGRESS if the command fails. */
    FAILED,

    /** Terminal state; transitions from IN_PROGRESS if the command succeeds. */
    SUCCESS
}
