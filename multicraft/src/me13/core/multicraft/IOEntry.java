package me13.core.multicraft;

import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

public class IOEntry {
    public Seq<LiquidStack> liquids;
    public Seq<ItemStack> items;
    public float power, heat;

    public Color iconColor = Color.white;
    public Prov<TextureRegion> icon = () -> {
        if(hasItems()) {
            return items.get(0).item.uiIcon;
        } else if(hasLiquids()) {
            return liquids.get(0).liquid.uiIcon;
        } else if(power > 0) {
            return Icon.power.getRegion();
        } else if(heat > 0) {
            return Icon.waves.getRegion();
        } else {
            return Icon.cancel.getRegion();
        }
    };

    public Color getImageBaseColor(MultiCrafter block) {
        if(hasItems() || hasLiquids()) {
            return Color.white;
        } else if(power > 0) {
            return Pal.power;
        } else if(heat > 0) {
            return block.heatColor;
        } else {
            return Color.white;
        }
    }

    public boolean hasItems() {
        return items.size > 0;
    }

    public boolean hasLiquids() {
        return liquids.size > 0;
    }

    public int maxItemAmount() {
        int max = 0;
        for (ItemStack item : items) {
            max = Math.max(item.amount, max);
        }
        return max;
    }

    public float maxLiquidAmount() {
        float max = 0;
        for (LiquidStack liquid : liquids) {
            max = Math.max(liquid.amount, max);
        }
        return max;
    }

    public boolean contains(Item item) {
        return items.contains(stack -> stack.item == item);
    }

    public boolean contains(Liquid liquid) {
        return liquids.contains(stack -> stack.liquid == liquid);
    }

    public boolean isEmpty() {
        return items.isEmpty() && liquids.isEmpty() && power <= 0f && heat <= 0f;
    }

    public IOEntry(Seq<ItemStack> items) {
        this(items, Seq.with());
    }

    public IOEntry(Seq<ItemStack> items, Seq<LiquidStack> liquids) {
        this(items, liquids, 0f, 0f);
    }

    public IOEntry(Seq<ItemStack> items, Seq<LiquidStack> liquids, float power) {
        this(items, liquids, power, 0f);
    }

    public IOEntry(Seq<ItemStack> items, Seq<LiquidStack> liquids, float power, float heat) {
        this.items = items;
        this.liquids = liquids;
        this.power = power;
        this.heat = heat;
    }

    @Override
    public String toString() {
        return "IOEntry{items=" + items + "fluids=" + liquids + "power=" + power + "heat=" + heat + "}";
    }

    public static IOEntry liquidConstructor(Seq<LiquidStack> liquids) {
        return new IOEntry(Seq.with(), liquids);
    }
}