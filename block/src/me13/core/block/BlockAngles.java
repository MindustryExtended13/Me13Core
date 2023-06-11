package me13.core.block;

import arc.math.geom.Point2;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;

public class BlockAngles {
    public static int reverse(int rotation) {
        return switch(rotation) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 0;
            case 3 -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + rotation);
        };
    }

    public static int angleTo(Point2 a, Point2 b) {
        return (a.x == b.x) ? (a.y > b.y ? 3 : 1) : (a.x > b.x ? 2 : 0);
    }

    public static int angleTo(BuildPlan planA, BuildPlan planB) {
        return angleTo(new Point2(planA.x, planA.y), new Point2(planB.x, planB.y));
    }

    public static int angleTo(Building a, Building b) {
        return angleTo(new Point2(a.tileX(), a.tileY()), new Point2(b.tileX(), b.tileY()));
    }
}
