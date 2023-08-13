package me13.core.multicraft;

import arc.func.Cons;
import arc.func.Cons2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;

import me13.core.multicraft.MultiCrafter.MultiCrafterBuild;
import mindustry.ui.Styles;

public class RecipeSelector {
    public static final Seq<RecipeSelector> all = new Seq<>();
    public final Cons2<MultiCrafterBuild, Table> builder;
    public final String name;

    public RecipeSelector(String name, Cons2<MultiCrafterBuild, Table> builder) {
        this.builder = builder;
        this.name = name;
    }

    public static RecipeSelector get(Object object) {
        if(object instanceof RecipeSelector) {
            return (RecipeSelector) object;
        } else if(object instanceof MultiCrafter) {
            return get(((MultiCrafter) object).selector);
        } else if(object instanceof String) {
            return get((String) object);
        } else if(object instanceof StringBuilder) {
            return get(((StringBuilder) object).toString());
        } else {
            return null;
        }
    }

    public static RecipeSelector get(String name) {
        //noinspection EqualsReplaceableByObjectsCall
        return all.find(s -> s.name == null ? name == null : s.name.equals(name));
    }

    public static Image getIcon(IOEntry entry) {
        Image image = new Image(new TextureRegionDrawable(entry.icon.get()), Scaling.fit);
        if(entry.iconColor != null) image.setColor(entry.iconColor);
        return image;
    }

    public static final RecipeSelector DEFAULT = new RecipeSelector("default", (build, table) -> {
        Table t = new Table();
        t.background(Tex.whiteui);
        t.setColor(Pal.darkerGray);
        MultiCrafter b = build.blockCast();
        for (int i = 0; i < b.resolvedRecipes.size; i++) {
            Recipe recipe = b.resolvedRecipes.get(i);
            int finalI = i;
            ImageButton button = new ImageButton(Styles.clearTogglei);
            Image img;
            if (recipe.icon != null) {
                img = new Image(recipe.icon.get());
                if (recipe.iconColor != null)
                    img.setColor(recipe.iconColor);
            } else {
                img = getIcon(recipe.output);
            }
            button.replaceImage(img);
            button.getImageCell().scaling(Scaling.fit).size(Vars.iconLarge);
            button.changed(() -> build.configure(finalI));
            button.update(() -> button.setChecked(build.curRecipeIndex == finalI));
            t.add(button).grow().margin(10f);
            if (i != 0 && i % 3 == 0) {
                t.row();
            }
        }
        table.add(t).grow();
    });
}