package me13.core.queue;

import arc.struct.IntSeq;
import arc.util.io.Reads;
import arc.util.io.Writes;

public class IntBuffer {
    private IntSeq ints = new IntSeq();

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
        ints = new IntSeq();
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