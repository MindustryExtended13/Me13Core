package me13.core.items;

import arc.struct.Seq;
import mindustry.type.LiquidStack;

public class LiquidStackStorage {
    private Seq<LiquidStack> stacks = new Seq<>();

    public static LiquidStackStorage create() {
        return new LiquidStackStorage();
    }

    public static LiquidStackStorage create(LiquidStackStorage other) {
        return create(other.stacks);
    }

    public static LiquidStackStorage create(Seq<LiquidStack> stacks) {
        LiquidStackStorage storage = create();
        storage.stacks = stacks == null ? new Seq<>() : stacks;
        return storage;
    }

    public static LiquidStackStorage create(LiquidStack... stacks) {
        return create(Seq.with(stacks));
    }

    public LiquidStack[] getStacks() {
        return LiquidStacks.toArray(stacks);
    }

    public boolean contains(LiquidStack stack) {
        return stacks.contains(s -> {
            return s.liquid == stack.liquid && s.amount >= stack.amount;
        });
    }

    public void remove(LiquidStack... stacks) {
        if(stacks == null || stacks.length == 0) {
            return;
        }
        //reverse all numbers to negative
        for(LiquidStack stack : stacks) {
            stack.amount *= -1;
        }
        add(stacks);
        //amount must be not < 0
        for(LiquidStack stack : this.stacks) {
            if(stack.amount < 0) {
                stack.amount = 0;
            }
        }
    }

    public void add(LiquidStack... stacks) {
        if(stacks == null || stacks.length == 0) {
            return;
        }
        LiquidStack[] out = LiquidStacks.merge(getStacks(), stacks);
        this.stacks.clear();
        this.stacks.addAll(out);
    }
}