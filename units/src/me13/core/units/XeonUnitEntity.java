package me13.core.units;

import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

public class XeonUnitEntity extends UnitEntity {
    public transient boolean loaded = false;

    public void load() {
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "#" + this.id;
    }

    @Override
    public int classId() {
        return XeonUnits.classID(this.getClass());
    }

    @Override
    public void setType(UnitType type) {
        super.setType(type);

        if(!this.loaded) {
            this.load();
            this.loaded = true;
        }
    }
}