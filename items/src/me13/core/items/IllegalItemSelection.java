package me13.core.items;

import arc.func.Cons;
import arc.func.Prov;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.world.Block;

import static mindustry.Vars.control;

public class IllegalItemSelection {
    private static TextField search;
    private static int rowCount;

    public static <T extends UnlockableContent> void buildTable(Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer)
    {
        buildTable(table, items, holder, consumer, true);
    }

    public static <T extends UnlockableContent> void buildTable(Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer,
                                                                boolean closeSelect)
    {
        buildTable(null, table, items, holder, consumer, closeSelect, 5, 4);
    }

    public static <T extends UnlockableContent> void buildTable(Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer,
                                                                int columns)
    {
        buildTable(null, table, items, holder, consumer, true, 5, columns);
    }

    public static <T extends UnlockableContent> void buildTable(Block block,
                                                                Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer)
    {
        buildTable(block, table, items, holder, consumer, true, 5, 4);
    }

    public static <T extends UnlockableContent> void buildTable(Block block,
                                                                Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer,
                                                                boolean closeSelect)
    {
        buildTable(block, table, items, holder, consumer, closeSelect, 5 ,4);
    }

    public static <T extends UnlockableContent> void buildTable(Block block,
                                                                Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer,
                                                                int rows,
                                                                int columns)
    {
        buildTable(block, table, items, holder, consumer, true, rows, columns);
    }

    public static <T extends UnlockableContent> void buildTable(Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer,
                                                                int rows,
                                                                int columns)
    {
        buildTable(null, table, items, holder, consumer, true, rows, columns);
    }

    public static <T extends UnlockableContent> void buildTable(@Nullable Block block,
                                                                Table table,
                                                                Seq<T> items,
                                                                Prov<T> holder,
                                                                Cons<T> consumer,
                                                                boolean closeSelect,
                                                                int rows,
                                                                int columns)
    {
        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);
        Table cont = new Table().top();
        cont.defaults().size(40);

        if(search != null) {
            search.clearText();
        }

        Runnable rebuild = () -> {
            group.clear();
            cont.clearChildren();

            String text = search != null ? search.getText() : "";

            int i = 0;
            rowCount = 0;

            Seq<T> list = items.select(u -> {
                return text.isEmpty() ||
                        u.localizedName.toLowerCase()
                                .contains(text.toLowerCase());
            });

            for(T item : list) {
                ImageButton button = cont.button(
                                Tex.whiteui,
                                Styles.clearNoneTogglei,
                                Mathf.clamp(item.selectionSize, 0f, 40f),

                                () ->
                                {
                                    if(closeSelect) {
                                        control.input.config.hideConfig();
                                    }
                                })

                        .tooltip(item.localizedName)
                        .group(group)
                        .get();

                button.changed(() -> {
                    consumer.get(button.isChecked() ? item : null);
                });

                button.getStyle().imageUp =
                        new TextureRegionDrawable(item.uiIcon);

                button.update(() -> {
                    button.setChecked(holder.get() == item);
                });

                if(i++ % columns == (columns - 1)){
                    cont.row();
                    rowCount++;
                }
            }
        };

        rebuild.run();

        Table main = new Table()
                .background(Styles.black6);

        if(rowCount > rows * 1.5f) {
            search = main.field(null, text -> {
                        rebuild.run();

                    }).width(40 * columns)
                    .padBottom(4)
                    .left()
                    .growX()
                    .get();

            search.setMessageText("@players.search");
            main.row();
        }

        ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
        pane.setScrollingDisabled(true, false);

        if(block != null) {
            pane.setScrollYForce(block.selectScroll);

            pane.update(() -> {
                block.selectScroll = pane.getScrollY();
            });
        }

        pane.setOverscroll(false, false);
        main.add(pane).maxHeight(40 * rows);
        table.top().add(main);
    }
}