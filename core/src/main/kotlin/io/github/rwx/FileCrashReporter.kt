package io.github.rwx

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import java.util.*

const val CRASH_FILE_NAME: String = "crashes.txt"

class FileCrashReporter private constructor(
    private val crashFile: File,
    environment: Map<String, String>,
) : CrashReporter {
    private val lock = Any()
    private val environment = linkedMapOf<String, String>().apply { putAll(environment) }
    private val breadcrumbs = ArrayDeque<String>()
    private val customKeys = linkedMapOf<String, String>()
    private var userId: String? = null

    override fun recordException(throwable: Throwable) {
        writeException(
            type = "non-fatal",
            thread = Thread.currentThread(),
            throwable = throwable,
        )
    }

    override fun log(message: String) {
        val entry = "${Instant.now()} $message"
        synchronized(lock) {
            breadcrumbs.addLast(entry)
            while (breadcrumbs.size > MAX_BREADCRUMBS) {
                breadcrumbs.removeFirst()
            }
        }
    }

    override fun setUserId(userId: String) {
        synchronized(lock) {
            this.userId = userId
        }
    }

    override fun setCustomKey(key: String, value: String) {
        synchronized(lock) {
            customKeys[key] = value
        }
    }

    fun installAsDefaultUncaughtExceptionHandler() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        if (previous is FileCrashUncaughtExceptionHandler && previous.reporter === this) return
        Thread.setDefaultUncaughtExceptionHandler(
            FileCrashUncaughtExceptionHandler(
                reporter = this,
                previous = previous,
            ),
        )
    }

    private fun writeException(type: String, thread: Thread, throwable: Throwable) {
        runCatching {
            val report = synchronized(lock) {
                buildString {
                    appendLine("===== RWX crash report =====")
                    appendLine("timestamp=${Instant.now()}")
                    appendLine("type=$type")
                    appendLine("thread=${thread.name}")
                    environment.forEach { (key, value) -> appendLine("$key=$value") }
                    userId?.let { appendLine("userId=$it") }
                    customKeys.forEach { (key, value) -> appendLine("custom.$key=$value") }
                    if (breadcrumbs.isNotEmpty()) {
                        appendLine("breadcrumbs:")
                        breadcrumbs.forEach { appendLine("  $it") }
                    }
                    appendLine("stacktrace:")
                    append(stackTraceOf(throwable))
                    appendLine()
                }
            }
            crashFile.parentFile?.mkdirs()
            crashFile.appendText(report, Charsets.UTF_8)
        }
    }

    private class FileCrashUncaughtExceptionHandler(
        val reporter: FileCrashReporter,
        private val previous: Thread.UncaughtExceptionHandler?,
    ) : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            reporter.writeException(
                type = "fatal",
                thread = thread,
                throwable = throwable,
            )
            previous?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        private const val MAX_BREADCRUMBS: Int = 50
        private val instances = mutableMapOf<String, FileCrashReporter>()

        fun get(crashFile: File, environment: Map<String, String> = emptyMap()): FileCrashReporter {
            val path = crashFile.absoluteFile.normalize().path
            return synchronized(instances) {
                instances.getOrPut(path) {
                    FileCrashReporter(crashFile.absoluteFile, environment)
                }.also { reporter ->
                    synchronized(reporter.lock) {
                        reporter.environment.putAll(environment)
                    }
                }
            }
        }

        private fun stackTraceOf(throwable: Throwable): String {
            val writer = StringWriter()
            PrintWriter(writer).use { throwable.printStackTrace(it) }
            return writer.toString()
        }
    }
}
