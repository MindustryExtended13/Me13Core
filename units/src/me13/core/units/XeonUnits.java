package me13.core.units;

import arc.func.Prov;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap.Entry;
import arc.struct.Seq;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

@SuppressWarnings("unused") //used class
public class XeonUnits {
    // Steal from Unlimited-Armament-Works
    private static final Seq<Entry<Class<? extends Entityc>, Prov<? extends Entityc>>> types = new Seq<>();
    private static final ObjectIntMap<Class<? extends Entityc>> idMap = new ObjectIntMap<>();

    /**
     * Setups all entity IDs and maps them into {@link EntityMapping}.
     * <p>
     * Put this inside load()
     * </p>
     * @author GlennFolker
     */
    public static void setupID() {
        for (int i = 0, j = 0; i < EntityMapping.idMap.length; i++) {
            if (EntityMapping.idMap[i] == null) {
                idMap.put(types.get(j).key, i);
                EntityMapping.idMap[i] = types.get(j).value;
                if (++j >= types.size) break;
            }
        }
    }

    static {
        add(XeonUnitEntity.class, XeonUnitEntity::new);
    }

    public static <T extends Entityc> void add(Class<T> type, Prov<T> prov) {
        Entry<Class<? extends Entityc>, Prov<? extends Entityc>> entry = new Entry<>();

        entry.key = type;
        entry.value = prov;

        types.add(entry);
    }

    /**
     * Retrieves the class ID for a certain entity type.
     * @author GlennFolker
     */
    public static <T extends Entityc> int classID(Class<T> type) {
        return idMap.get(type, -1);
    }
}