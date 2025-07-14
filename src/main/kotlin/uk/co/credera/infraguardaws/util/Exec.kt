package uk.co.credera.infraguardaws.util

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Utility methods to invoke OS commands from within Kotlin code.
 */
object Exec {
    val log = KotlinLogging.logger {}
    const val TRIM_COMMAND_TO_LENGTH_IN_ERROR_MESSAGES: Int = 100

    /**
     * Execute the given {@code command} from the given (optional) {@code workDir}, and return it's output.
     *
     * Unlike the {@link #shell} method, this method handles a nonzero exit code when the underlying shell command
     * terminates with error. The exit code and relevant information are subsequently wrapped into a {@link
     * RuntimeException}. Thus, this method is a convenience method suitable for the majority of cases.
     *
     * @param command the command to execute
     * @param workDir optional work directory to execute the command from
     * @return the output of the command
     * @throws RuntimeException if the command exited with error
     */
    fun command(command: String, workDir: File? = null): String {
        val output = StringBuilder()
        val exitValue: Int = shell(command, workDir, output, false)
        if (exitValue > 0) {
            throw RuntimeException(
                "$output: The following command exited with error value $exitValue: ${
                    trimCommandToLength(
                        command
                    )
                }"
            )
        }
        return output.toString()
    }

    /**
     * Execute the given {@code command} in shell and return the termination exit code. A nonzero exit code means the
     * command terminated with error.
     *
     * @param command the command to execute
     * @param workDir optional work directory to execute the command from
     * @param output optional {@link StringBuilder} to write the output to or {@code null} to write to log
     * @param echoCommandUponError {@code true} results in the command itself to be logged at error level upon
     *                             unsuccessful execution
     * @return termination exit code (0=success, >0=error)
     */
    fun shell(
        command: String, workDir: File? = null, output: StringBuilder? = null,
        echoCommandUponError: Boolean = false
    ): Int {
        require(command.isNotBlank())
        val cmd: List<String> = listOf("sh", "-c", command)
        val pb: ProcessBuilder = ProcessBuilder(cmd)
            .redirectErrorStream(true)
        if (workDir != null) {
            pb.directory(workDir)
        }
        val env = pb.environment()
        env["PATH"] = "${env["PATH"]}:/usr/local/bin"
        val process = pb.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        if (output == null) {
            reader.useLines { lines ->
                lines.forEach { line ->
                    log.info { line }
                }
            }
        } else {
            reader.useLines {
                it.forEach { line ->
                    output.append(line).append('\n')
                }
            }
            if (output.isNotEmpty()) {
                output.delete(output.length - 1, output.length)
            }
        }
        process.waitFor()
        if (process.exitValue() > 0 && echoCommandUponError) {
            log.error { trimCommandToLength(command) }
        }
        return process.exitValue()
    }

    private fun trimCommandToLength(command: String): String {
        if (TRIM_COMMAND_TO_LENGTH_IN_ERROR_MESSAGES < command.length) {
            return command.substring(0, TRIM_COMMAND_TO_LENGTH_IN_ERROR_MESSAGES) + "..."
        }
        return command
    }
}
