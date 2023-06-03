package me13.core.layers.blocks;

import arc.util.Eachable;
import me13.core.layers.ILayer;
import me13.core.layers.ILayerBlock;
import me13.core.layers.ILayerBuilding;
import mindustry.entities.units.BuildPlan;
import net.tmmc.util.GraphBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LayerBlock extends GraphBlock implements ILayerBlock {
    public List<ILayer> layers = null;
    public boolean drawDefault = true;

    public LayerBlock(String name) {
        super(name);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanLayers(this, plan, list);
    }

    @Override
    public @NotNull List<ILayer> getLayers() {
        return layers == null ? List.of() : layers;
    }

    @Override
    public void load() {
        super.load();
        loadLayers(this);
    }

    public class LayerBuild extends GraphBlockBuild implements ILayerBuilding {
        @Override
        public void draw() {
            if(drawDefault) {
                super.draw();
            }
            draw(getLayers(), block, this);
        }

        public List<ILayer> getLayers() {
            return LayerBlock.this.getLayers();
        }
    }
}
