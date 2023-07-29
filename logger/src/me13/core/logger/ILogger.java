package me13.core.logger;

import arc.util.Log.LogLevel;

public interface ILogger {
    void log(LogLevel level, String message, Object... args);

    default void info(String message, Object... args) {
        log(LogLevel.info, message, args);
    }

    default void err(String message, Object... args) {
        log(LogLevel.err, message, args);
    }

    default void warn(String message, Object... args) {
        log(LogLevel.warn, message, args);
    }

    default void debug(String message, Object... args) {
        log(LogLevel.debug, message, args);
    }

    default void log(String message, Object... args) {
        log(LogLevel.none, message, args);
    }
}