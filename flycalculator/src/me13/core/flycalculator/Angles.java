package me13.core.flycalculator;

import arc.math.geom.Geometry;

public class Angles {
    public static float[] calculate(float[] buffer, int rotation) {
        var point = Geometry.d4(rotation);
        buffer[0] *= point.x == 0 ? 1 : point.x;
        buffer[1] *= point.y == 0 ? 1 : point.y;
        return buffer;
    }

    public static float[] calculate(float x, float y, int rotation) {
        return calculate(new float[] {x, y}, rotation);
    }

    public static float calcX(float x, int rotation) {
        return calculate(x, 0, rotation)[0];
    }

    public static float calcY(float y, int rotation) {
        return calculate(0, y, rotation)[1];
    }
}