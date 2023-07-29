package me13.core.logger;

import arc.util.Log.LogLevel;

public class LogBinder {
    public LogLevel level = LogLevel.none;
    public ILogger parent;
    public String message;
    public String prefix;
    public Object[] args;

    public LogBinder atLevel(LogLevel level) {
        this.level = level;
        return this;
    }

    public LogBinder setMessage(String message) {
        this.message = message;
        return this;
    }

    public LogBinder setArgs(Object... args) {
        this.args = args;
        return this;
    }

    public LogBinder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public LogBinder removePrefix() {
        return setPrefix(null);
    }

    public LogBinder log() {
        return log(message);
    }

    public LogBinder log(String message) {
        return log(message, args);
    }

    public LogBinder log(String message, Object... args) {
        if(prefix == null) {
            parent.log(level, message, args);
        } else {
            parent.log(level, prefix, message, args);
        }
        return this;
    }
}