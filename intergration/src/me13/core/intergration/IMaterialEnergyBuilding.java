package me13.core.intergration;

import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;

public interface IMaterialEnergyBuilding {
    ItemStack acceptItem(ItemStack stack);
    LiquidStack acceptLiquid(LiquidStack stack);
}