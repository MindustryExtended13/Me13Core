package me13.core.items;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.type.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemStacks {
    @Contract("null -> new")
    public static ItemStack @NotNull[] toArray(Seq<ItemStack> stackSeq) {
        if(stackSeq == null || stackSeq.size == 0) {
            return new ItemStack[0];
        }

        ItemStack[] stacks = new ItemStack[stackSeq.size];
        for(int i = 0; i < stacks.length; i++) {
            stacks[i] = stackSeq.get(i);
        }
        return stacks;
    }

    public static ItemStack[] merge(ItemStack[] a, ItemStack[] b) {
        Seq<ItemStack> stacks = new Seq<>();
        Cons<ItemStack> cons = (stack) -> {
            if(stack == null) {
                return;
            }
            ItemStack found = stacks.find(s -> s.item == stack.item);
            if(found != null) {
                found.amount += stack.amount;
            } else {
                stacks.add(stack);
            }
        };
        for(ItemStack stack : a) {
            cons.get(stack);
        }
        for(ItemStack stack : b) {
            cons.get(stack);
        }
        return toArray(stacks);
    }
}