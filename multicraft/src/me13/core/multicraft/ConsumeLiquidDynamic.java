package me13.core.multicraft;

import arc.func.Func;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.gen.Building;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.modules.LiquidModule;

public class ConsumeLiquidDynamic extends Consume {
    public final Func<Building, LiquidStack[]> fluids;

    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumeLiquidDynamic(Func<T, LiquidStack[]> fluids) {
        this.fluids = (Func<Building, LiquidStack[]>) fluids;
    }

    @Override
    public void apply(Block block) {
        block.hasLiquids = true;
    }

    @Override
    public void update(Building build) {
        LiquidStack[] fluids = this.fluids.get(build);
        remove(build.liquids, fluids, build.edelta());
    }

    @Override
    public void build(Building build, Table table) {
        final LiquidStack[][] current = {fluids.get(build)};

        table.table(cont -> {
            table.update(() -> {
                LiquidStack[] newFluids = fluids.get(build);
                if (current[0] != newFluids) {
                    rebuild(build, cont);
                    current[0] = newFluids;
                }
            });

            rebuild(build, cont);
        });
    }

    private void rebuild(Building tile, Table table) {
        table.clear();
        int i = 0;

        LiquidStack[] fluids = this.fluids.get(tile);
        for (LiquidStack stack : fluids) {
            table.add(new ReqImage(new Image(new TextureRegionDrawable(stack.liquid.uiIcon), Scaling.fit),
                    () -> tile.liquids != null && tile.liquids.get(stack.liquid) >= stack.amount))
                    .padRight(8).left();
            if (++i % 4 == 0) table.row();
        }
    }

    @Override
    public float efficiency(Building build) {
        LiquidStack[] fluids = this.fluids.get(build);
        return build.consumeTriggerValid() || has(build.liquids, fluids) ? 1f : 0f;
    }

    public static boolean has(LiquidModule fluids, LiquidStack[] reqs) {
        for (LiquidStack req : reqs) {
            if (fluids.get(req.liquid) < req.amount)
                return false;
        }
        return true;
    }

    public static void remove(LiquidModule fluids, LiquidStack[] reqs, float multiplier) {
        for (LiquidStack req : reqs) {
            fluids.remove(req.liquid, req.amount * multiplier);
        }
    }
}