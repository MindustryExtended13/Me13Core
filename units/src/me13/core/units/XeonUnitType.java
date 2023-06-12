package me13.core.units;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.MultiPacker;
import mindustry.type.UnitType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class XeonUnitType extends UnitType {
    public XeonUnitType(String name) {
        super(name);

        this.outlineColor = Color.valueOf("4A4B53");
    }

    public void outline(MultiPacker packer, TextureRegion region, String prefix) {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(packer);

        Outliner.outlineRegion(packer, region, this.outlineColor, prefix, this.outlineRadius);
    }

    public float unitLayer(@NotNull Unit unit) {
        return unit.elevation > 0.5f ? (this.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) :
                this.groundLayer + Mathf.clamp(this.hitSize / 4000f, 0, 0.01f);
    }
}