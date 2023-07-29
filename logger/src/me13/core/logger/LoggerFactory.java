package me13.core.logger;

import arc.util.Log;

public class LoggerFactory {
    public static String toLogString(Class<?> cl) {
        return cl == null ? "null" : cl.getSimpleName() + ".java";
    }

    public static ILogger build() {
        return build((LoggerFormatter) null);
    }

    public static ILogger build(LoggerFormatter formatter) {
        return build((String) null, formatter);
    }

    public static ILogger build(Class<?> cl) {
        return build(toLogString(cl));
    }

    public static ILogger build(String prefix) {
        return build(prefix, null);
    }

    public static ILogger build(Class<?> cl, LoggerFormatter formatter) {
        return build(toLogString(cl), formatter);
    }

    public static ILogger build(String prefix, LoggerFormatter formatter) {
        String pr = "";
        if(prefix != null && !prefix.isEmpty()) {
            pr = '[' + prefix + "] ";
        }

        if(formatter == null) {
            formatter = new LoggerFormatter.DefaultLoggerFormatter();
        }

        LoggerFormatter finalFormatter = formatter;
        String finalPr = pr;
        return new ILogger() {
            @Override
            public void log(Log.LogLevel level, String message, Object... args) {
                if(level == null) {
                    level = Log.LogLevel.none;
                }
                if(message == null) {
                    message = "null";
                }
                Log.log(level, finalPr + finalFormatter.parse(message, args));
            }
        };
    }
}