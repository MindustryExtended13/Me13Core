package me13.core.items;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.type.LiquidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LiquidStacks {
    @Contract("null -> new")
    public static LiquidStack @NotNull[] toArray(Seq<LiquidStack> stackSeq) {
        if(stackSeq == null || stackSeq.size == 0) {
            return new LiquidStack[0];
        }

        LiquidStack[] stacks = new LiquidStack[stackSeq.size];
        for(int i = 0; i < stacks.length; i++) {
            stacks[i] = stackSeq.get(i);
        }
        return stacks;
    }

    public static LiquidStack[] merge(LiquidStack[] a, LiquidStack[] b) {
        Seq<LiquidStack> stacks = new Seq<>();
        Cons<LiquidStack> cons = (stack) -> {
            if(stack == null) {
                return;
            }
            LiquidStack found = stacks.find(s -> s.liquid == stack.liquid);
            if(found != null) {
                found.amount += stack.amount;
            } else {
                stacks.add(stack);
            }
        };
        for(LiquidStack stack : a) {
            cons.get(stack);
        }
        for(LiquidStack stack : b) {
            cons.get(stack);
        }
        return toArray(stacks);
    }
}
