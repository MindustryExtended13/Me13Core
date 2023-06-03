package me13.core.layers;

import mindustry.gen.Building;
import mindustry.world.Block;

public interface ILayer {
    void draw(Block block, Building build);
    void load(Block block);
}