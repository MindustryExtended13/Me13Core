package me13.core.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class LoggerFormatter {
    public abstract String parse(String message, Object... args);

    public static class DefaultLoggerFormatter extends LoggerFormatter {
        @Override
        public String parse(String message, Object... args) {
            if(args == null) {
                return message;
            }

            String[] argsStr = new String[args.length];
            for(int i = 0; i < argsStr.length; i++) {
                Object obj = args[i];
                if(obj instanceof Throwable throwable) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    argsStr[i] = sw.toString();
                } else {
                    argsStr[i] = String.valueOf(obj);
                }
            }

            int index = 0;
            StringBuilder result = new StringBuilder();
            StringBuilder ins2 = new StringBuilder();
            boolean opened = false, skip = false;
            for(char ch : message.toCharArray()) {
                if(ch == '\\') {
                    skip = true;
                    continue;
                }

                if(!skip) {
                    if(ch == '{' && !opened) {
                        opened = true;
                        ins2 = new StringBuilder();
                        continue;
                    }

                    if(ch == '}' && opened) {
                        opened = false;
                        int num;
                        try {
                            num = Integer.parseInt(ins2.toString());
                        } catch(Throwable ignored) {
                            num = index++;
                        }
                        result.append(argsStr[num]);
                        continue;
                    }
                } else {
                    skip = false;
                }

                if(opened) {
                    ins2.append(ch);
                } else {
                    result.append(ch);
                }
            }

            return result.toString();
        }
    }
}