package io.github.rwx

import io.github.rwx.mod.api.LogLevel
import org.koin.mp.KoinPlatform.getKoin

object GlobalLogger {

    @Volatile
    var minimumLevel: LogLevel = LogLevel.INFO

    inline fun debug(tag: String? = null, crossinline message: () -> Any) = log(LogLevel.DEBUG, tag, msg = message)
    inline fun info(tag: String? = null, crossinline message: () -> Any) = log(LogLevel.INFO, tag, msg = message)
    inline fun warn(
        throwable: Throwable? = null,
        tag: String? = null,
        crossinline message: () -> Any = { throwable?.message ?: "" }
    ) = log(LogLevel.WARN, tag, throwable, msg = message)

    inline fun error(
        throwable: Throwable? = null,
        tag: String? = null,
        crossinline message: () -> Any = { throwable?.message ?: "" }
    ) = log(LogLevel.ERROR, tag, throwable, message)

    @PublishedApi
    internal inline fun log(
        level: LogLevel,
        tag: String? = null,
        throwable: Throwable? = null,
        crossinline msg: () -> Any
    ) {
        if (level.ordinal < minimumLevel.ordinal) return
        val messageStr = msg().toString()

        runCatching {
            val loggerImpl = getKoin().get<AppLogger>()
            when (level) {
                LogLevel.DEBUG -> loggerImpl.debug(tag, messageStr)
                LogLevel.INFO -> loggerImpl.info(tag, messageStr)
                LogLevel.WARN -> loggerImpl.warn(tag, messageStr, throwable)
                LogLevel.ERROR -> loggerImpl.error(tag, messageStr, throwable)
            }
            return
        }.onFailure { e ->
            e.printStackTrace()
            throwable?.printStackTrace()
        }
    }

    fun reportException(throwable: Throwable) {
        runCatching {
            getKoin().get<CrashReporter>().recordException(throwable)
        }.onFailure { e ->
            e.printStackTrace()
            throwable.printStackTrace()
        }
    }

}

val logger: GlobalLogger = GlobalLogger
