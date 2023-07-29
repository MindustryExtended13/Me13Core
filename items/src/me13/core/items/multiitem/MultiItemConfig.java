package me13.core.items.multiitem;

import arc.func.Func;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;

public class MultiItemConfig {
    public static
    <T extends Building, E extends UnlockableContent> void configure(Block block,
                                                                     Class<E> contentTypeClass,
                                                                     Class<E[]> contentArrayTypeClass,
                                                                     Func<T, MultiItemData<E>> getter)
    {
        block.config(Integer.class, (T build, Integer config) -> {
            getter.get(build).toggle(config);
        });

        block.config(String.class, (T build, String config) -> {
            getter.get(build).toggle(config);
        });

        block.config(contentTypeClass, (T build, E config) -> {
            getter.get(build).toggle(config);
        });

        block.config(int[].class, (T build, int[] config) -> {
            var data = getter.get(build);
            for(int i : config) {
                data.toggle(i);
            }
        });

        block.config(String[].class, (T build, String[] config) -> {
            var data = getter.get(build);
            for(String i : config) {
                data.toggle(i);
            }
        });

        block.config(contentArrayTypeClass, (T build, E[] config) -> {
            var data = getter.get(build);
            for(E i : config) {
                data.toggle(i);
            }
        });

        block.configClear((T build) -> {
            getter.get(build).clear();
        });
    }
}