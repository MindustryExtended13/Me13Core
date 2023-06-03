package me13.core.intergration;

import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.LiquidModule;

public interface IMaterialEnergyBuilding {
    ItemStack acceptItem(ItemStack stack);
    ItemStack removeItem(ItemStack stack);

    LiquidStack acceptLiquid(LiquidStack stack);
    LiquidStack removeLiquid(LiquidStack stack);

    int maxCapacity();
    float maxLiquidCapacity();

    ItemModule storage();
    LiquidModule storageLiquid();
    boolean canConnectTo(Building other);
}