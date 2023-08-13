package me13.core.multicraft;

import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.entities.Effect;

public class Recipe {
    public IOEntry input = null;
    public IOEntry output = null;
    public float craftTime = 0f;
    public Effect craftEffect = Fx.none;
    public Prov<TextureRegion> icon = null;
    public Color iconColor = null;

    public Recipe(IOEntry input, IOEntry output) {
        this(input, output, 90);
    }

    public Recipe(IOEntry input, IOEntry output, float craftTime) {
        this.input = input;
        this.output = output;
        this.craftTime = craftTime;
        this.icon = output.icon;
    }

    public Recipe() {
    }

    public boolean isAnyEmpty() {
        if (input == null || output == null) return true;
        return input.isEmpty() || output.isEmpty();
    }

    public boolean isOutputFluid() {
        return !output.liquids.isEmpty();
    }

    public boolean isOutputItem() {
        return !output.items.isEmpty();
    }

    public boolean isConsumeFluid() {
        return !input.liquids.isEmpty();
    }

    public boolean isConsumeItem() {
        return !input.items.isEmpty();
    }

    public boolean isConsumePower() {
        return input.power > 0f;
    }

    public boolean isOutputPower() {
        return output.power > 0f;
    }

    public boolean isConsumeHeat() {
        return input.heat > 0f;
    }

    public boolean isOutputHeat() {
        return output.heat > 0f;
    }

    public boolean hasItem() {
        return isConsumeItem() || isOutputItem();
    }

    public boolean hasFluid() {
        return isConsumeFluid() || isOutputFluid();
    }

    public boolean hasPower() {
        return isConsumePower() || isOutputPower();
    }

    public boolean hasHeat() {
        return isConsumeHeat() || isOutputHeat();
    }

    public int maxItemAmount() {
        return Math.max(input.maxItemAmount(), output.maxItemAmount());
    }

    public float maxFluidAmount() {
        return Math.max(input.maxLiquidAmount(), output.maxLiquidAmount());
    }

    public float maxPower() {
        return Math.max(input.power, output.power);
    }

    public float maxHeat() {
        return Math.max(input.heat, output.heat);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "input=" + input +
                "output=" + output +
                "craftTime" + craftTime +
                "}";
    }

    @SuppressWarnings("unchecked")
    public static Seq<Recipe> get(Object object) {
        if(object == null) {
            return null;
        } else if(object instanceof Seq) {
            return (Seq<Recipe>) object;
        } else if(object instanceof Recipe[]) {
            return Seq.with((Recipe[]) object);
        } else if(object instanceof Recipe) {
            return Seq.with((Recipe) object);
        } else {
            return null;
        }
    }
}