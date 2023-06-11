package me13.core.block;

import mindustry.gen.Building;
import mindustry.world.Tile;

public interface ConnectionHand {
    boolean valid(Building self, Building other, Tile tile);
}