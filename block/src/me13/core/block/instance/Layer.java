package me13.core.block.instance;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import me13.core.block.ConnectionHand;
import me13.core.block.SchemeConnectionHand;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public class Layer {
    public EnumTextureMapping mapping;
    public TextureRegion[] regions;
    public SchemeConnectionHand hand2 = null;
    public ConnectionHand hand = null;
    public String regionName;
    public boolean rotate = true;
    public Color color = Color.white;
    public float alpha = 1f;

    public Layer(String regionName, EnumTextureMapping mapping) {
        this.regionName = regionName;
        this.mapping = mapping;
    }

    public Layer(Block block, String prefix, EnumTextureMapping mapping) {
        this(block.name + prefix, mapping);
    }

    public void load(Block block) {
        regions = AdvancedBlockHelper.loadRegions(regionName, mapping);
    }

    public void draw(Building building) {
        AdvancedBlockHelper.drawRegions(building, regions, mapping, color, rotate, hand, alpha);
    }

    public void drawPlan(BuildPlan plan, Eachable<BuildPlan> all) {
        AdvancedBlockHelper.drawRegionsPlan(plan, all, regions, mapping, color, rotate, hand2);
    }
}