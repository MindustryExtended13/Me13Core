package me13.core.util;

import arc.struct.IntSeq;

public class IntCircle {
    private final IntSeq array;
    private int round = 0;

    public IntCircle(IntSeq seq) {
        this.array = seq;
    }

    public IntCircle(IntBuffer buffer) {
        this(buffer.ints());
    }

    public int nextRound() {
        if(round >= array.size) {
            round = 0;
        }
        return array.get(round++);
    }

    public int round() {
        return round;
    }
}