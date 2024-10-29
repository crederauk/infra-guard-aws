package uk.co.credera.infraguardaws

enum class CommandStatus {
    READY,          // Initial state; command is ready to be executed.
    IN_PROGRESS,    // Command is currently executing; transitions from READY.
    TIMED_OUT,      // Terminal state; transitions from IN_PROGRESS if the command times out.
    FAILED,         // Terminal state; transitions from IN_PROGRESS if the command fails.
    SUCCESS;        // Terminal state; transitions from IN_PROGRESS if the command succeeds.
}
