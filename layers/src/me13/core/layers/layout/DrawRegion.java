package me13.core.layers.layout;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import me13.core.layers.ILayer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public class DrawRegion implements ILayer {
    public String prefix = "-region";
    public boolean rotateDraw = true;
    public TextureRegion region;

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy(), !rotateDraw ? 0 : plan.rotation * 90);
    }

    @Override
    public void draw(Block block, Building build) {
        Draw.rect(region, build.x, build.y, rotateDraw ? build.drawrot() : 0);
    }

    @Override
    public void load(Block block) {
        region = Core.atlas.find(block.name + prefix);
    }
}
