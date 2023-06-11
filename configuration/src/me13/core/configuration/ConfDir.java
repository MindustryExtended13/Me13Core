package me13.core.configuration;

import arc.files.Fi;
import arc.util.OS;
import mindustry.Vars;
import org.jetbrains.annotations.Nullable;

public class ConfDir {
    public static final String prefix = "\\\\configurations\\\\";
    public static final String path = OS.getAppDataDirectory(Vars.appName) + prefix;

    public static @Nullable Fi getFile() {
        return path.equals(prefix) ? null : new Fi(path);
    }

    public static boolean hasFile() {
        Fi fi = getFile();
        return fi != null && (fi.exists() || fi.mkdirs()) && fi.isDirectory();
    }
}
