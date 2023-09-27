package me13.core.bundle2;

import arc.struct.ObjectMap;
import org.jetbrains.annotations.NotNull;

public class Bundle2Format {
    public static final char NEW_LINE_CHAR = '&';
    public static final char FORMAT_CHAR = '%';
    public static final char SKIP_CHAR = '\\';

    public static @NotNull String format(@NotNull String string, ObjectMap<String, String> vars) {
        StringBuilder formatted = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        boolean opened = false, doSkip = false;
        for(char ch : string.toCharArray()) {
            if(ch == SKIP_CHAR && !doSkip) {
                doSkip = true;
                continue;
            }

            if(doSkip) {
                if(opened) {
                    buffer.append(ch);
                } else {
                    formatted.append(ch);
                }
                doSkip = false;
                continue;
            }

            if(ch == NEW_LINE_CHAR) {
                if(opened) {
                    buffer.append('\n');
                } else {
                    formatted.append('\n');
                }
                continue;
            }

            if(ch == FORMAT_CHAR) {
                if(opened) {
                    formatted.append(vars.get(buffer.toString()));
                    buffer = new StringBuilder();
                }

                opened = !opened;
                continue;
            }

            if(opened) {
                buffer.append(ch);
            } else {
                formatted.append(ch);
            }
        }
        return formatted.toString();
    }
}