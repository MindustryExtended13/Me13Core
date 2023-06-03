package me13.core.layers;

import mindustry.world.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ILayerBlock {
    @NotNull List<ILayer> getLayers();

    default void loadLayers(Block block) {
        getLayers().forEach((layer) -> {
            if(layer != null) {
                layer.load(block);
            }
        });
    }
}