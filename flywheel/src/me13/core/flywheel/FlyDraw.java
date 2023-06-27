package me13.core.flywheel;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import me13.core.flycalculator.Angles;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;

public class FlyDraw {
    public static void draw(TextureRegion region, Building building,
                            float offsetX, float offsetY, int rotation) {
        draw(region, building, offsetX, offsetY, 0, rotation);
    }

    public static void draw(TextureRegion region, Building building, float offsetX,
                            float offsetY, float angle, int rotation) {
        draw(region, building.x, building.y, offsetX, offsetY, angle, rotation);
    }

    public static void draw(TextureRegion region, float x, float y, float offsetX,
                            float offsetY, float angle, int rotation) {
        float[] bufferOut = Angles.calculate(offsetX, offsetY, rotation % 4);
        Draw.rect(region, x + bufferOut[0], y + bufferOut[1], angle % 360);
    }

    public static void draw(TextureRegion region, float x, float y, float offsetX,
                            float offsetY, int rotation) {
        draw(region, x, y, offsetX, offsetY, 0, rotation);
    }

    public static void drawSpin(TextureRegion region, Building building, float offsetX,
                                float offsetY, float angle, int rotation) {
        drawSpin(region, building.x, building.y, offsetX, offsetY, angle, rotation);
    }

    public static void drawSpin(TextureRegion region, float x, float y, float offsetX,
                            float offsetY, float angle, int rotation) {
        float[] bufferOut = Angles.calculate(offsetX, offsetY, rotation % 4);
        Drawf.spinSprite(region, x + bufferOut[0], y + bufferOut[1], angle % 360);
    }
}