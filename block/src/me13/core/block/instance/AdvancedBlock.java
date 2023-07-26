package me13.core.block.instance;

import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import me13.core.block.BlockAngles;
import me13.core.block.BlockConnections;
import me13.core.block.ConnectionHand;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public class AdvancedBlock extends Block {
    public Seq<Layer> layers = new Seq<>();
    public boolean drawBase = true;

    public AdvancedBlock(String name) {
        super(name);
        destructible = true;
        solid = true;
    }

    public TextureRegion loadRegion(String prefix) {
        return loadRegions(prefix, EnumTextureMapping.REGION)[0];
    }

    public TextureRegion[] loadRegions(String prefix, EnumTextureMapping mapping) {
        return AdvancedBlockHelper.loadRegions(this, prefix, mapping);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(drawBase) {
            super.drawPlanRegion(plan, list);
        }

        layers.each(layer -> {
            if(layer != null) {
                layer.drawPlan(plan, list);
            }
        });
    }

    @Override
    public void load() {
        super.load();
        layers.each((layer) -> {
            if(layer != null) {
                layer.load(this);
            }
        });
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class AdvancedBuild extends Building {
        public Building nearby() {
            return nearby(rotation);
        }

        public Building reversedNearby() {
            return nearby(BlockAngles.reverse(rotation));
        }

        public int angleTo(Building other) {
            return BlockAngles.angleTo(this, other);
        }

        public Seq<Building> connections(ConnectionHand hand) {
            return BlockConnections.getConnections(this, hand);
        }

        @Override
        public void draw() {
            if(drawBase) {
                super.draw();
            }

            layers.each((layer) -> {
                if(layer != null) {
                    layer.draw(this);
                }
            });
        }
    }
}