package me13.core.bundle2;

import arc.Core;
import arc.files.Fi;
import arc.files.ZipFi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.mod.Mods;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

public class Bundle2 {
    public static final String COMMENT_PREFIX = "//";
    public static final String VAR_DEF_PREFIX = "--vardef ";
    public static final String CDE_PREFIX = "--cde";
    public static final String CD_PREFIX = "--cd ";

    @Contract(pure = true)
    public static void load(@NotNull Mods.LoadedMod mod) {
        Locale locale = Core.bundle.getLocale();
        Fi US, CR, root;
        if(mod.file.isDirectory()) {
            root = mod.file.child("locales");
        } else {
            root = new ZipFi(mod.file).child("locales");
        }
        if(root != null && root.exists()) {
            CR = root.child(locale.toString());
            US = root.child("en_US");
            if(US.exists()) {
                load(US);
            }
            if(CR.exists()) {
                load(CR);
            }
        }
    }

    public static void load(@NotNull Fi fi) {
        load(fi.readString("UTF-8"));
    }

    public static void load(@NotNull String string) {
        load(string.split("\n"));
    }

    public static void load(String @NotNull [] buffer) {
        Seq<String> keys = new Seq<>();
        Seq<String> values = new Seq<>();
        for(String string : buffer) {
            string = string.trim();
            if(string.isEmpty()) {
                continue;
            }

            if(string.equals(COMMENT_PREFIX)) {
                keys.add(COMMENT_PREFIX);
                values.add("");
                continue;
            }

            if(string.startsWith(COMMENT_PREFIX)) {
                keys.add(COMMENT_PREFIX);
                values.add(string.substring(2));
                continue;
            }

            if(string.equals(CDE_PREFIX)) {
                keys.add(CDE_PREFIX);
                values.add("");
                continue;
            }

            if(string.equals(CD_PREFIX)) {
                continue;
            }

            if(string.startsWith(CD_PREFIX)) {
                keys.add(CD_PREFIX);
                values.add(string.substring(CD_PREFIX.length()));
                continue;
            }

            String[] bfr = string.split(" = ");
            if(bfr.length == 2) {
                keys.add(bfr[0]);
                values.add(bfr[1]);
            }
        }
        load(keys, values);
    }

    @Contract(pure = true)
    public static void load(@NotNull Seq<String> keys, @NotNull Seq<String> values) {
        if(keys.size != values.size) throw new IllegalStateException();
        ObjectMap<String, String> output = Core.bundle.getProperties();
        ObjectMap<String, String> vars = new ObjectMap<>();
        Seq<String> categoryIndex = new Seq<>();
        Runnable pop = () -> {
            if(!categoryIndex.isEmpty()) {
                int ns = categoryIndex.size - 1;
                String[] n = new String[ns];
                for(int i = 0; i < ns; i++) {
                    n[i] = categoryIndex.get(i);
                }
                categoryIndex.clear();
                categoryIndex.addAll(n);
            }
        };
        for(int i = 0; i < keys.size; i++) {
            String value = values.get(i);
            String key = keys.get(i);

            //ignore bundle comments
            if(COMMENT_PREFIX.equals(key)) {
                continue;
            }

            if(CDE_PREFIX.equals(key)) {
                pop.run();
                continue;
            }

            if(CD_PREFIX.equals(key)) {
                if("./".equals(value)) {
                    continue;
                }

                if("../".equals(value)) {
                    pop.run();
                    continue;
                }

                categoryIndex.add(Bundle2Format.format(value, vars));
                continue;
            }

            if(key.startsWith(VAR_DEF_PREFIX) && !VAR_DEF_PREFIX.equals(key)) {
                String variableName = key.substring(VAR_DEF_PREFIX.length());
                vars.put(Bundle2Format.format(variableName, vars), Bundle2Format.format(value, vars));
                continue;
            }

            value = Bundle2Format.format(value, vars);
            key = Bundle2Format.format(key, vars);
            StringBuilder cat = new StringBuilder();
            for(String category : categoryIndex) {
                cat.append(category);
            }
            key = cat.toString() + key;
            output.put(key, value);
        }
        Core.bundle.setProperties(output);
    }
}