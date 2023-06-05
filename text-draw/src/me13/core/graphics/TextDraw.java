package me13.core.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.util.Align;
import arc.util.pooling.Pools;
import mindustry.ui.Fonts;

/**
 * Fragments of code from prog-mats be MEEPofFaith
 * @author MEEPofFaith
 */
public class TextDraw {
    private static final GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
    private static final Font font = Fonts.outline;

    public static float text(float x, float y, float maxWidth, Color color, CharSequence text){
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        if(maxWidth <= 0){
            font.getData().setScale(1f / 3f);
            layout.setText(font, text);
        }else{
            font.getData().setScale(1f);
            layout.setText(font, text);
            font.getData().setScale(Math.min(1f / 3f, maxWidth / layout.width));
            layout.setText(font, text);
        }
        font.setColor(color);
        font.draw(text, x, y + (layout.height / 2f), Align.center);
        float width = layout.width;
        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);
        return width;
    }
}