package me13.core.util;

import arc.struct.IntSeq;
import arc.util.io.Reads;
import arc.util.io.Writes;

public class IntBuffer {
    private final IntSeq ints = new IntSeq();

    public IntSeq ints() {
        return ints;
    }

    public void register(int i) {
        ints.add(i);
    }

    public int get(int index) {
        return ints.get(index);
    }

    public int length() {
        return ints.size;
    }

    public void clear() {
        ints.clear();
    }

    public void read(Reads reads) {
        ints.clear();
        int len = reads.i();
        for(int i = 0; i < len; i++) {
            ints.add(reads.i());
        }
    }

    public void write(Writes writes) {
        writes.i(ints.size);
        ints.each(writes::i);
    }
}