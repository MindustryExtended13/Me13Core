package me13.core.layers;

import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ILayerBlock {
    @NotNull List<ILayer> getLayers();

    default void drawPlanLayers(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        getLayers().forEach((layer) -> {
            if(layer != null) {
                layer.drawPlan(block, plan, list);
            }
        });
    }

    default void loadLayers(Block block) {
        getLayers().forEach((layer) -> {
            if(layer != null) {
                layer.load(block);
            }
        });
    }
}