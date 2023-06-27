package me13.core.flywheel;

import arc.Core;
import arc.graphics.g2d.TextureRegion;

public class Animations {
    public static TextureRegion[] createAnimation(String prefix) {
        int indexer = 0;
        while(Core.atlas.has(prefix + indexer)) {
            indexer++;
        }
        TextureRegion[] out = new TextureRegion[indexer];
        for(int i = 0; i < indexer; i++) {
            out[i] = Core.atlas.find(prefix + i);
        }
        return out;
    }
}