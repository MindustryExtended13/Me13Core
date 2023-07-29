package me13.core.items;

import arc.struct.Seq;
import mindustry.type.ItemStack;

public class ItemStackStorage {
    private Seq<ItemStack> stacks = new Seq<>();

    public static ItemStackStorage create() {
        return new ItemStackStorage();
    }

    public static ItemStackStorage create(ItemStackStorage other) {
        return create(other.stacks);
    }

    public static ItemStackStorage create(Seq<ItemStack> stacks) {
        ItemStackStorage storage = create();
        storage.stacks = stacks == null ? new Seq<>() : stacks;
        return storage;
    }

    public static ItemStackStorage create(ItemStack... stacks) {
        return create(Seq.with(stacks));
    }

    public ItemStack[] getStacks() {
        return ItemStacks.toArray(stacks);
    }

    public boolean contains(ItemStack stack) {
        return stacks.contains(s -> {
            return s.item == stack.item && s.amount >= stack.amount;
        });
    }

    public void remove(ItemStack... stacks) {
        if(stacks == null || stacks.length == 0) {
            return;
        }
        //reverse all numbers to negative
        for(ItemStack stack : stacks) {
            stack.amount *= -1;
        }
        add(stacks);
        //amount must be not < 0
        for(ItemStack stack : this.stacks) {
            if(stack.amount < 0) {
                stack.amount = 0;
            }
        }
    }

    public void add(ItemStack... stacks) {
        if(stacks == null || stacks.length == 0) {
            return;
        }
        ItemStack[] out = ItemStacks.merge(getStacks(), stacks);
        this.stacks.clear();
        this.stacks.addAll(out);
    }
}