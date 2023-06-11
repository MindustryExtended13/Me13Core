package me13.core.block;

import mindustry.entities.units.BuildPlan;
import mindustry.world.Tile;

public interface SchemeConnectionHand {
    boolean valid(BuildPlan self, BuildPlan other, Tile tile);
}