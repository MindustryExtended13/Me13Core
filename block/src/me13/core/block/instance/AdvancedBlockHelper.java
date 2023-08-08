package me13.core.block.instance;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import me13.core.block.ConnectionHand;
import me13.core.block.SchemeConnectionHand;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public class AdvancedBlockHelper {
    public static String[] getTFMappingId() {
        return new String[] {
                "ffff", "tfff", "ftff", "ttff",
                "fftf", "tftf", "fttf", "tttf",
                "ffft", "tfft", "ftft", "ttft",
                "fftt", "tftt", "fttt", "tttt"
        };
    }

    public static void drawRegionsPlan(BuildPlan plan, Eachable<BuildPlan> plans, TextureRegion[] regions,
                                       EnumTextureMapping mapping, Color color, boolean rotate,
                                       SchemeConnectionHand hand) {
        if(color != null) {
            Draw.color(color);
        }
        float x = plan.drawx();
        float y = plan.drawy();
        float r = rotate ? plan.rotation * 90 : 0;
        switch(mapping) {
            case ROT -> Draw.rect(regions[plan.rotation], x, y, r);
            case REGION -> Draw.rect(regions[0], x, y, r);
            case BITS, TF_TYPE -> {
                byte bits = 0;
                for(int i = 0; i < 4; i++) {
                    Point2 point2 = Geometry.d4(i).cpy().add(plan.x, plan.y);
                    final BuildPlan[] other = {null};
                    plans.each(plan2 -> {
                        if(plan2.x == point2.x && plan2.y == point2.y) {
                            other[0] = plan2;
                        }
                    });
                    if(hand.valid(plan, other[0], Vars.world.tile(point2.x, point2.y))) {
                        bits += 1 << i;
                    }
                }
                Draw.rect(regions[bits], x, y, r);
            }
        }
        Draw.reset();
    }

    public static void drawRegions(Building building, TextureRegion[] regions,
                                   EnumTextureMapping mapping, Color color, boolean rotate,
                                   ConnectionHand hand, float alpha) {
        if(color != null) {
            Draw.color(color);
        }
        Draw.alpha(alpha);
        float x = building.x;
        float y = building.y;
        float r = rotate ? building.drawrot() : 0;
        switch(mapping) {
            case ROT -> Draw.rect(regions[building.rotation], x, y, r);
            case REGION -> Draw.rect(regions[0], x, y, r);
            case BITS, TF_TYPE -> {
                byte bits = 0;
                for(int i = 0; i < 4; i++) {
                    Point2 point2 = Geometry.d4(i).cpy().add(building.tileX(), building.tileY());
                    if(hand.valid(building, Vars.world.build(point2.x, point2.y),
                            Vars.world.tile(point2.x, point2.y))) {
                        bits += 1 << i;
                    }
                }
                Draw.rect(regions[bits], x, y, r);
            }
        }
        Draw.reset();
    }

    public static TextureRegion[] loadRegions(Block block, String prefix, EnumTextureMapping mapping) {
        return loadRegions(block.name + prefix, mapping);
    }

    public static TextureRegion[] loadRegions(String id, EnumTextureMapping mapping) {
        switch(mapping) {
            case ROT -> {
                TextureRegion[] regions = new TextureRegion[4];
                for(int i = 0; i < 4; i++) {
                    regions[i] = Core.atlas.find(id + i);
                }
                return regions;
            }
            case BITS -> {
                TextureRegion[] regions = new TextureRegion[16];
                for(int i = 0; i < 16; i++) {
                    regions[i] = Core.atlas.find(id + i);
                }
                return regions;
            }
            case REGION -> {
                return new TextureRegion[] {Core.atlas.find(id)};
            }
            case TF_TYPE -> {
                TextureRegion[] regions = new TextureRegion[16];
                int i = 0;
                for(String s : getTFMappingId()) {
                    regions[i++] = Core.atlas.find(id + s);
                }
                return regions;
            }
        }
        return new TextureRegion[0];
    }
}