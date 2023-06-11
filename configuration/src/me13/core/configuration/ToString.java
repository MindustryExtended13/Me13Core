package me13.core.configuration;

import mindustry.Vars;
import mindustry.game.Saves;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;

public class ToString {
    public static String toString(ItemStack stack) {
        return stack == null ? "null" : stack.item.name + "/" + stack.amount;
    }

    public static String toString(LiquidStack stack) {
        return stack == null ? "null" : stack.liquid.name + "/" + stack.amount;
    }

    public static String toString(Saves.SaveSlot map) {
        return map == null ? "null" : map.file.absolutePath();
    }

    public static Saves.SaveSlot fromStringSave(String str) {
        if(str.equals("null")) {
            return null;
        }

        return Vars.control.saves.getSaveSlots().find(slot -> {
            return slot != null && slot.file.absolutePath().equals(str);
        });
    }

    public static LiquidStack fromStringStackLiquid(String str) {
        if(str.equals("null")) {
            return null;
        }

        int i = str.indexOf('/');
        if(i != -1) {
            return new LiquidStack(
                    Vars.content.liquid(str.substring(0, i)),
                    Float.parseFloat(str.substring(i+1))
            );
        } else {
            return null;
        }
    }

    public static ItemStack fromStringStack(String str) {
        if(str.equals("null")) {
            return null;
        }

        int i = str.indexOf('/');
        if(i != -1) {
            return new ItemStack(
                    Vars.content.item(str.substring(0, i)),
                    Integer.parseInt(str.substring(i+1))
            );
        } else {
            return null;
        }
    }
}