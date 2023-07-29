package me13.core.block;

import arc.struct.Seq;
import mindustry.gen.Building;

public class BlockConnections {
    public static boolean[] getBufferConnections(Building source, ConnectionHand hand) {
        int prox = source.proximity.size;
        boolean[] buffer = new boolean[prox];
        for(int i = 0; i < prox; i++) {
            Building building = source.proximity.get(i);
            buffer[i] = hand.valid(source, building, building.tile);
        }
        return buffer;
    }

    public static int[] getBitsConnections(Building source, ConnectionHand hand) {
        boolean[] buffer = getBufferConnections(source, hand);
        int[] bits = new int[buffer.length];
        for(int i = 0; i < buffer.length; i++) {
            bits[i] = buffer[i] ? 1 : 0;
        }
        return bits;
    }

    public static Seq<Building> getConnections(Building source, ConnectionHand hand) {
        Seq<Building> seq = new Seq<>();
        source.proximity.each(b -> {
            if(hand.valid(source, b, b.tile)) {
                seq.add(b);
            }
        });
        return seq;
    }
}