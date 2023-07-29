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

    default void log(LogLevel level, String prefix, String message, Object... args) {
        log(level, '[' + prefix + "] " + message, args);
    }

    default void info(String prefix, String message, Object... args) {
        log(LogLevel.info, prefix, message, args);
    }

    default void err(String prefix, String message, Object... args) {
        log(LogLevel.err, prefix, message, args);
    }

    default void warn(String prefix, String message, Object... args) {
        log(LogLevel.warn, prefix, message, args);
    }

    default void debug(String prefix, String message, Object... args) {
        log(LogLevel.debug, prefix, message, args);
    }

    default void log(String prefix, String message, Object... args) {
        log(LogLevel.none, prefix, message, args);
    }

    default LogBinder at(LogLevel level) {
        LogBinder binder = new LogBinder();
        binder.parent = this;
        return binder.atLevel(level);
    }

    default LogBinder atInfo() {
        return at(LogLevel.info);
    }

    default LogBinder atError() {
        return at(LogLevel.err);
    }

    default LogBinder atWarn() {
        return at(LogLevel.warn);
    }

    default LogBinder atDebug() {
        return at(LogLevel.debug);
    }

    default LogBinder at() {
        return at(LogLevel.none);
    }
}