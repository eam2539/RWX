package com.corrodinggames.rts.gameFramework

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


enum class LogLevel { DEBUG, INFO, WARN, ERROR }

object GlobalLogger {

    var TAG = "RWX"
    var writeToFile = true

    @Volatile
    var minimumLevel: LogLevel = LogLevel.INFO

    //    private val logFile: File by lazy { resolveLogFile() }
    private var isFirstWrite = true

    inline fun debug(crossinline message: () -> Any) = log(LogLevel.DEBUG, msg = message)
    inline fun info(crossinline message: () -> Any) = log(LogLevel.INFO, msg = message)
    inline fun warn(
        throwable: Throwable? = null,
        crossinline message: () -> Any = { throwable?.message ?: "" }
    ) =
        log(LogLevel.WARN, throwable, msg = message)

    inline fun error(
        throwable: Throwable? = null,
        crossinline message: () -> Any = { throwable?.message ?: "" }
    ) =
        log(LogLevel.ERROR, throwable, message)

    @PublishedApi
    internal inline fun log(level: LogLevel, throwable: Throwable? = null, crossinline msg: () -> Any) {
        if (level.ordinal < minimumLevel.ordinal) return
        val caller = resolveCaller()
        val timestamp =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val levelPrefix = String.format(
            "%5s", when (level) {
                LogLevel.DEBUG -> "DEBUG"
                LogLevel.INFO -> "INFO"
                LogLevel.WARN -> "WARN"
                LogLevel.ERROR -> "ERROR"
            }
        )
        val content = "[$levelPrefix] [$timestamp] [$caller] ${msg()}"
//        val logEntry = content + if (throwable != null) "\n${throwable.stackTraceToString()}" else "" + '\n'

//            if (writeToFile) {
//                writeToFile(logEntry)
//            }

        System.err.println(content)
        throwable?.printStackTrace()

    }

    @PublishedApi
    internal inline fun logToFile(crossinline msg: () -> Any) {

    }
    /*  @PublishedApi
      @Synchronized
      internal fun writeToFile(logEntry: String) {
          runCatching {
              if (isFirstWrite) {
                  logFile.writeText(logEntry, Charsets.UTF_8)
              } else {
                  logFile.appendText(logEntry, Charsets.UTF_8)
              }
              if (isFirstWrite) isFirstWrite = false

          } .onFailure { e->
              System.err.println( "Failed to write log to file: ${e.message}")
              e.printStackTrace()
          }
      }*/

    @PublishedApi
    internal fun resolveCaller(): String {
        val trace = Throwable().stackTrace
        for (element in trace) {
            val className = element.className
            if (className.startsWith("io.github.rwx.GlobalLogger")) continue
            if (className.startsWith("io.github.rwx.LoggerKt")) continue
            if (className == Thread::class.java.name) continue
            return className
        }
        return ""
    }

    /*private fun resolveLogFile(): File {

        return if (localStorage.exists()) {
            File(localStorage, "rwx.log")
        } else {
            File(System.getProperty("java.io.tmpdir"), "rwx.log")
        }
    }*/

}

val logger: GlobalLogger = GlobalLogger
