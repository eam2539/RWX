package io.github.rwx

import android.util.Log
import timber.log.Timber
import java.io.*
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.format.DateTimeFormatter
import java.util.*

internal class FileLoggingTree(
    private val logFile: File,
    private val minimumPriority: Int = Log.VERBOSE,
    private val maxFileSizeBytes: Long = DEFAULT_MAX_FILE_SIZE_BYTES,
    private val clock: Clock = Clock.systemDefaultZone(),
) : Timber.Tree(), Closeable {
    private val lock = Any()
    private var output: OutputStream? = null
    private var writeErrorReported = false

    init {
        require(maxFileSizeBytes > 0) { "maxFileSizeBytes must be greater than zero" }
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= minimumPriority

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val entry = formatEntry(priority, tag, message).toByteArray(StandardCharsets.UTF_8)
        synchronized(lock) {
            try {
                rotateIfNeeded(entry.size.toLong())
                val stream = output ?: openOutput().also { output = it }
                stream.write(entry)
                stream.flush()
                writeErrorReported = false
            } catch (error: IOException) {
                handleWriteError(error)
            } catch (error: SecurityException) {
                handleWriteError(error)
            }
        }
    }

    override fun close() {
        synchronized(lock) {
            closeOutput()
        }
    }

    private fun formatEntry(priority: Int, tag: String?, message: String): String = buildString {
        append(TIMESTAMP_FORMATTER.format(clock.instant().atZone(clock.zone)))
        append(" [")
        append(Thread.currentThread().name)
        append("] ")
        append(priorityName(priority).padEnd(PRIORITY_WIDTH))
        append(' ')
        append(tag ?: DEFAULT_TAG)
        append(" - ")
        append(message)
        append('\n')
    }

    private fun rotateIfNeeded(incomingBytes: Long) {
        if (!logFile.isFile || logFile.length() == 0L) return
        if (logFile.length() + incomingBytes <= maxFileSizeBytes) return

        closeOutput()
        val backupFile = File(logFile.parentFile, "${logFile.name}.1")
        if (backupFile.exists() && !backupFile.delete()) return
        logFile.renameTo(backupFile)
    }

    private fun openOutput(): OutputStream {
        logFile.parentFile?.mkdirs()
        return BufferedOutputStream(FileOutputStream(logFile, true))
    }

    private fun handleWriteError(error: Exception) {
        closeOutput()
        if (!writeErrorReported) {
            writeErrorReported = true
            Log.e(ERROR_TAG, "Unable to write file log", error)
        }
    }

    private fun closeOutput() {
        try {
            output?.close()
        } catch (_: IOException) {
            // The tree must not fail application shutdown because the log could not be flushed.
        } finally {
            output = null
        }
    }

    private fun priorityName(priority: Int): String = when (priority) {
        Log.VERBOSE -> "TRACE"
        Log.DEBUG -> "DEBUG"
        Log.INFO -> "INFO"
        Log.WARN -> "WARN"
        Log.ERROR -> "ERROR"
        Log.ASSERT -> "ASSERT"
        else -> priority.toString()
    }

    private companion object {
        const val DEFAULT_MAX_FILE_SIZE_BYTES: Long = 20L * 1024L * 1024L
        const val DEFAULT_TAG: String = "App"
        const val ERROR_TAG: String = "FileLoggingTree"
        const val PRIORITY_WIDTH: Int = 5
        val TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ROOT)
    }
}
