package me13.core.layers;

import mindustry.gen.Building;
import mindustry.world.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ILayerBuilding {
    default void draw(@NotNull List<ILayer> layerList, Block block, Building build) {
        layerList.forEach(layer -> {
            if(layer != null) {
                layer.draw(block, build);
            }
        });
    }
}