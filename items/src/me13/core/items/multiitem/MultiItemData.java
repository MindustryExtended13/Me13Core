package me13.core.items.multiitem;

import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;

import static mindustry.Vars.content;

public class MultiItemData<E extends UnlockableContent> {
    private final Seq<E> items = new Seq<>();
    private ContentType type;

    public static<E extends UnlockableContent> MultiItemData<E> create(ContentType type) {
        MultiItemData<E> data = new MultiItemData<>();
        data.type = type;
        return data;
    }

    public ContentType getType() {
        return type;
    }

    public int length() {
        return items.size;
    }

    public IntSet asIntSet() {
        IntSet seq = new IntSet();
        items.each((i) -> {
            seq.add(i.id);
        });
        return seq;
    }

    public void write(Writes writes) {
        writes.i(items.size);
        items.each(item -> {
            writes.str(item.name);
        });
    }

    public void read(Reads reads) {
        int len = reads.i();
        for(int i = 0; i < len; i++) {
            toggle(reads.str());
        }
    }

    public int[] config() {
        int[] config = new int[items.size];
        for(int i = 0; i < config.length; i++) {
            config[i] = items.get(i).id;
        }
        return config;
    }

    public boolean isToggled(E item) {
        return items.contains(item);
    }

    public boolean isToggled(String name) {
        return isToggled(content.getByName(type, name));
    }

    public boolean isToggled(int id) {
        return isToggled(content.getByID(type, id));
    }

    public void toggle(E item) {
        if(item != null) {
            if(items.contains(item)) {
                items.remove(item);
            } else {
                items.add(item);
            }
        }
    }

    public void toggle(String name) {
        toggle(content.getByName(type, name));
    }

    public void toggle(int id) {
        toggle(content.getByID(type, id));
    }

    public void clear() {
        items.clear();
    }

    public void enable(E item) {
        if(!items.contains(item)) {
            items.add(item);
        }
    }

    public void disable(E item) {
        if(items.contains(item)) {
            items.remove(item);
        }
    }
}