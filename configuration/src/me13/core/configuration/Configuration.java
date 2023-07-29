package me13.core.configuration;

import arc.files.Fi;
import arc.struct.ObjectMap;

public class Configuration {
    private final ObjectMap<String, String> defaults = new ObjectMap<>();

    public void setDefKey(String id, String def) {
        defaults.put(id, def);
    }

    public void setKey(String id, String content) {
        boolean hasFile = false;
        Fi out = null;
        if(ConfDir.hasFile()) {
            Fi fi = ConfDir.getFile();
            if(fi != null) {
                fi = fi.child(id);
                try {
                    if(fi.exists() || fi.file().createNewFile()) {
                        hasFile = true;
                        out = fi;
                    }
                } catch(Throwable ignored) {
                }
            }
        }
        if(hasFile) {
            out.writeString(content);
        }
    }

    public String getKey(String id) {
        boolean hasFile = false;
        Fi out = null;
        if(ConfDir.hasFile()) {
            Fi fi = ConfDir.getFile();
            if(fi != null) {
                fi = fi.child(id);
                try {
                    if(fi.exists()) {
                        hasFile = true;
                        out = fi;
                    } else if(fi.file().createNewFile()) {
                        hasFile = true;
                        fi.writeString(defaults.get(id));
                        out = fi;
                    }
                } catch(Throwable ignored) {
                }
            }
        }
        if(hasFile) {
            return out.readString("UTF-8");
        } else {
            return defaults.get(id);
        }
    }
}