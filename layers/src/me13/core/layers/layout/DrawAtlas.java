package me13.core.layers.layout;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.util.Eachable;
import me13.core.layers.ILayer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import net.tmmc.util.Geom;
import net.tmmc.util.XWorld;

import java.util.HashMap;
import java.util.Map;

public class DrawAtlas implements ILayer {
    public Map<String, TextureRegion> cache;
    public Hand boolf;
    public ShemHand boolfHeme;
    public String prefix = "-atlas";

    public interface Hand {
        boolean get(Tile a, Building b);
    }

    public interface ShemHand {
        boolean get(Tile a, BuildPlan b, BuildPlan c);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        StringBuilder builder = new StringBuilder();
        Geom.each4dAngle(point -> {
            BuildPlan[] res = new BuildPlan[1];
            list.each(plan2 -> {
                if(plan2.x == point.x && plan2.y == point.y) {
                    res[0] = plan2;
                }
            });
            if(boolfHeme.get(XWorld.at(point), plan, res[0])) {
                builder.append('t');
            } else {
                builder.append('f');
            }
        }, new Point2(plan.x, plan.y));

        TextureRegion out;
        String id = builder.toString();
        if(cache.containsKey(id)) {
            out = cache.get(id);
        } else {
            out = Core.atlas.find(block.name + prefix + '-' + id);
            cache.put(id, out);
        }

        Draw.rect(out, plan.drawx(), plan.drawy(), 0);
    }

    @Override
    public void draw(Block block, Building build) {
        StringBuilder builder = new StringBuilder();
        Geom.each4dAngle(point -> {
            if(boolf.get(XWorld.at(point), build)) {
                builder.append('t');
            } else {
                builder.append('f');
            }
        }, Geom.toPoint(build));

        TextureRegion out;
        String id = builder.toString();
        if(cache.containsKey(id)) {
            out = cache.get(id);
        } else {
            out = Core.atlas.find(block.name + prefix + '-' + id);
            cache.put(id, out);
        }

        Draw.z(Layer.block + 2);
        Draw.rect(out, build.x, build.y, 0);
    }

    @Override
    public void load(Block block) {
        if(block.size > 1) {
            throw new IllegalStateException("DrawAtlas only for 1x1 blocks");
        }

        cache = new HashMap<>();
    }
}