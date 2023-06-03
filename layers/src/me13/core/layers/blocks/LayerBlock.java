package me13.core.layers.blocks;

import arc.struct.Seq;
import me13.core.layers.ILayer;
import me13.core.layers.ILayerBlock;
import me13.core.layers.ILayerBuilding;
import net.tmmc.util.GraphBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LayerBlock extends GraphBlock implements ILayerBlock {
    public Seq<ILayer> layers = new Seq<>();

    public LayerBlock(String name) {
        super(name);
    }

    @Override
    public @NotNull List<ILayer> getLayers() {
        return layers == null ? List.of() : List.of(layers.items);
    }

    @Override
    public void load() {
        super.load();
        loadLayers(this);
    }

    public class LayerBuild extends GraphBlockBuild implements ILayerBuilding {
        @Override
        public void draw() {
            super.draw();
            draw(getLayers(), block, this);
        }

        public List<ILayer> getLayers() {
            return LayerBlock.this.getLayers();
        }
    }
}
