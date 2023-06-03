package me13.core.layers;

import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public interface ILayer {
    void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list);
    void draw(Block block, Building build);
    void load(Block block);
}