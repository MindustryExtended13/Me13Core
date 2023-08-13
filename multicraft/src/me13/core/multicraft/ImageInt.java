package me13.core.multicraft;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.ui.Styles;

import static mindustry.core.UI.*;

public class ImageInt extends Stack {
    public ImageInt(TextureRegion region, float amount) {
        this(region, Color.white, amount);
    }

    public ImageInt(TextureRegion region, Color color, float amount) {
        add(new Table(o -> {
            o.left();
            o.add(new Image(new TextureRegionDrawable(region), Scaling.fit)).size(32f).color(color);
        }));

        if(amount != 0){
            add(new Table(t -> {
                t.left().bottom();
                //noinspection ConcatenationWithEmptyString
                t.add(amount >= 1000 ? formatAmount(amount) : amount + "").style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }

    public static String formatAmount(float number) {
        if(number == Float.POSITIVE_INFINITY) return "∞";
        if(number == Float.NEGATIVE_INFINITY) return "-∞";

        float mag = Math.abs(number);
        String sign = number < 0 ? "-" : "";
        if(mag >= 1_000_000_000){
            return sign + Strings.fixed(mag / 1_000_000_000f, 1) + "[gray]" + billions + "[]";
        }else if(mag >= 1_000_000){
            return sign + Strings.fixed(mag / 1_000_000f, 1) + "[gray]" + millions + "[]";
        }else if(mag >= 10_000){
            return number / 1000 + "[gray]" + thousands + "[]";
        }else if(mag >= 1000){
            return sign + Strings.fixed(mag / 1000f, 1) + "[gray]" + thousands + "[]";
        }else{
            //noinspection ConcatenationWithEmptyString
            return number + "";
        }
    }
}