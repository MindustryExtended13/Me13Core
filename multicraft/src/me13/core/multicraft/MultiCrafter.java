package me13.core.multicraft;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class MultiCrafter extends Block {
    public Object selector = RecipeSelector.DEFAULT;
    public RecipeSelector resolvedSelector;
    public Seq<Recipe> resolvedRecipes;
    public Object recipes = null;
    public Color heatColor = new Color(1f, 0.22f, 0.22f, 0.8f);
    public float powerCapacity = 0f;
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public Effect changeRecipeEffect = Fx.rotateBlock;
    public int[] fluidOutputDirections = {-1};
    public float updateEffectChance = 0.04f;
    public float warmupSpeed = 0.019f;
    public float warmupRate = 0.15f;
    public float maxEfficiency = 1f;
    public int defaultRecipeIndex = 0;
    public float itemCapacityMultiplier = 1f;
    public float fluidCapacityMultiplier = 1f;
    public float powerCapacityMultiplier = 1f;
    protected boolean showNameTooltip = false;
    public DrawBlock drawer = new DrawDefault();

    protected boolean isHeat, isConsumeFluid, isConsumePower, isOutputItem;

    public MultiCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        sync = true;
        destructible = true;
        flags = EnumSet.of(BlockFlag.factory);
        ambientSound = Sounds.machine;
        configurable = true;
        saveConfig = true;
        ambientSoundVolume = 0.03f;
        config(Integer.class, MultiCrafterBuild::setCureRecipeIndexFromRemote);
        configClear(MultiCrafterBuild::setCureRecipeIndexFromRemoteDefault);
    }

    public void resolve() {
        RecipeSelector resolved2 = RecipeSelector.get(selector);
        resolvedSelector = resolved2 == null ? RecipeSelector.DEFAULT : resolved2;
        Seq<Recipe> resolved = Recipe.get(recipes);
        resolvedRecipes = resolved == null ? new Seq<>() : resolved;
    }

    public void setupBlockForRecipes() {
        hasItems = false;
        hasPower = false;
        hasLiquids = false;
        outputsPower = false;
        acceptsItems = false;
        outputsLiquid = false;

        int maxItem = 0;
        float maxLiquid = 0;
        for(Recipe recipe : resolvedRecipes) {
            boolean liquids = recipe.hasFluid();
            boolean item = recipe.hasItem();
            hasLiquids |= liquids;
            hasPower |= recipe.hasPower();
            hasItems |= item;
            outputsPower |= recipe.isOutputPower();
            outputsLiquid |= recipe.isOutputFluid();
            acceptsItems |= recipe.isConsumeItem();
            if(liquids) {
                maxLiquid = Math.max(recipe.maxFluidAmount(), maxLiquid);
            }
            if(item) {
                maxItem = Math.max(recipe.maxItemAmount(), maxItem);
            }
        }

        powerCapacity = resolvedRecipes.max(Recipe::maxPower).maxPower() * powerCapacityMultiplier;
        liquidCapacity = maxLiquid * fluidCapacityMultiplier;
        itemCapacity = Mathf.round(maxItem * itemCapacityMultiplier);

        isHeat = resolvedRecipes.contains(Recipe::hasHeat);
        isConsumeFluid = resolvedRecipes.contains(Recipe::isConsumeFluid);
        isConsumePower = resolvedRecipes.contains(Recipe::isConsumePower);
        isOutputItem = resolvedRecipes.contains(Recipe::isOutputItem);
    }

    public void buildStats(Table stat) {
        stat.row();
        for (Recipe recipe : resolvedRecipes) {
            Table t = new Table();
            t.background(Tex.whiteui);
            t.setColor(Pal.darkestGray);
            buildIOEntry(t, recipe, true);
            Table time = new Table();
            final float[] duration = {0f};
            float visualCraftTime = recipe.craftTime;
            time.update(() -> {
                duration[0] += Time.delta;
                if (duration[0] > visualCraftTime) duration[0] = 0f;
            });
            String craftTime = recipe.craftTime == 0 ? "0" : String.format("%.2f", recipe.craftTime / 60f);
            Cell<Bar> barCell = time.add(new Bar(() -> craftTime,
                            () -> Pal.accent,
                            () -> Interp.smooth.apply(duration[0] / visualCraftTime)))
                    .height(45f);
            barCell.width(Vars.mobile ? 220f : 250f);
            Cell<Table> timeCell = t.add(time).pad(12f);
            if (showNameTooltip) timeCell.tooltip(Stat.productionTime
                    .localized() + ": " + craftTime + " " + StatUnit.seconds.localized());
            buildIOEntry(t, recipe, false);
            stat.add(t).pad(10f).grow();
            stat.row();
        }
        stat.row();
        stat.defaults().grow();
    }

    protected void buildIOEntry(Table table, Recipe recipe, boolean isInput) {
        Table t = new Table();
        if (isInput) t.left();
        else t.right();
        Table mat = new Table();
        IOEntry entry = isInput ? recipe.input : recipe.output;
        int i = 0;
        for (ItemStack stack : entry.items) {
            Cell<ImageInt> iconCell = mat.add(new ImageInt(stack.item.uiIcon, stack.amount))
                    .pad(2f);
            if (showNameTooltip)
                iconCell.tooltip(stack.item.localizedName);
            if (isInput) iconCell.left();
            else iconCell.right();
            if (i != 0 && i % 2 == 0) mat.row();
            i++;
        }
        for (LiquidStack stack : entry.liquids) {
            Cell<ImageInt> iconCell = mat.add(new ImageInt(stack.liquid.uiIcon, stack.amount * 60f))
                    .pad(2f);
            if (showNameTooltip)
                iconCell.tooltip(stack.liquid.localizedName);
            if (isInput) iconCell.left();
            else iconCell.right();
            if (i != 0 && i % 2 == 0) mat.row();
            i++;
        }
        if (entry.power > 0f) {
            Cell<ImageInt> iconCell = mat.add(new ImageInt(Icon.power.getRegion(),
                            Pal.power, entry.power * 60f)).pad(2f);
            if (isInput) iconCell.left();
            else iconCell.right();
            if (showNameTooltip)
                iconCell.tooltip(entry.power + " " + StatUnit.powerSecond.localized());
            i++;
            if (i != 0 && i % 2 == 0) mat.row();
        }
        //Heat
        if (entry.heat > 0f) {
            Cell<ImageInt> iconCell = mat.add(new ImageInt(Icon.waves
                    .getRegion(), heatColor, entry.heat)).pad(2f);
            if (isInput) iconCell.left();
            else iconCell.right();
            if (showNameTooltip)
                iconCell.tooltip(entry.heat + " " + StatUnit.heatUnits.localized());
            i++;
            if (i != 0 && i % 2 == 0) mat.row();
        }
        Cell<Table> matCell = t.add(mat);
        if (isInput) matCell.left();
        else matCell.right();
        Cell<Table> tCell = table.add(t).pad(12f).fill();
        tCell.width(Vars.mobile ? 90f : 120f);
    }

    protected void setupConsumers() {
        if (acceptsItems) consume(new ConsumeItemDynamic(
                (MultiCrafterBuild b) -> b.getCurRecipe().input.items.toArray(ItemStack.class)
        ));
        if (isConsumeFluid) consume(new ConsumeLiquidDynamic(
                // fluids seq is already shrunk, it's safe to access
                (MultiCrafterBuild b) -> b.getCurRecipe().input.liquids.toArray(LiquidStack.class)
        ));
        if (isConsumePower) consume(new ConsumePowerDynamic(b ->
                ((MultiCrafterBuild)b).getCurRecipe().input.power
        ));
    }

    @Override
    public void setBars() {
        super.setBars();

        if (hasPower)
            addBar("power", (MultiCrafterBuild b) -> new Bar(
                    b.getCurRecipe().isOutputPower() ? Core.bundle.format("bar.poweroutput",
                            Strings.fixed(b.getPowerProduction() * 60f * b.timeScale(), 1)) : "bar.power",
                    Pal.powerBar,
                    () -> b.efficiency
            ));
        if (isHeat)
            addBar("heat", (MultiCrafterBuild b) -> new Bar(
                    b.getCurRecipe().isConsumeHeat() ? Core.bundle.format("bar.heatpercent", (int) (b.heat + 0.01f), (int) (b.efficiencyScale() * 100 + 0.01f)) : "bar.heat",
                    Pal.lightOrange,
                    b::heatFrac
            ));
        addBar("progress", (MultiCrafterBuild b) -> new Bar(
                "bar.loadprogress",
                Pal.accent,
                b::progress
        ));
    }

    @Override
    public boolean rotatedOutput(int x, int y) {
        return false;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out) {
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public boolean outputsItems() {
        return isOutputItem;
    }

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        Recipe firstRecipe = resolvedRecipes.get(defaultRecipeIndex);
        Seq<LiquidStack> fluids = firstRecipe.output.liquids;
        for (int i = 0; i < fluids.size; i++) {
            int dir = fluidOutputDirections.length > i ? fluidOutputDirections[i] : -1;

            if (dir != -1) Draw.rect(
                    fluids.get(i).liquid.fullIcon,
                    x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                    y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                    8f, 8f
            );
        }
    }

    @Override
    public void init() {
        resolve();
        setupBlockForRecipes();
        setupConsumers();
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, t -> {
            showNameTooltip = true;
            buildStats(t);
            showNameTooltip = false;
        });
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class MultiCrafterBuild extends Building implements HeatBlock, HeatConsumer {
        public float[] sideHeat = new float[4];
        public int curRecipeIndex = 0;
        public float craftingTime;
        public float heat = 0f;
        public float warmup;

        public MultiCrafter blockCast() {
            return (MultiCrafter) block;
        }

        public Recipe getCurRecipe() {
            return resolvedRecipes.get(Mathf.clamp(curRecipeIndex, 0, resolvedRecipes.size - 1));
        }

        public void setCureRecipeIndexFromRemoteDefault() {
            setCureRecipeIndexFromRemote(defaultRecipeIndex);
        }

        public void setCureRecipeIndexFromRemote(int index) {
            int newIndex = Mathf.clamp(index, 0, resolvedRecipes.size - 1);
            if(newIndex != curRecipeIndex) {
                curRecipeIndex = newIndex;
                spawnEffect(changeRecipeEffect);
                craftingTime = 0;
            }
        }

        public void craft() {
            consume();
            Recipe cur = getCurRecipe();
            if (cur.isOutputItem()) for (ItemStack output : cur.output.items)
                for (int i = 0; i < output.amount; i++) offload(output.item);

            if (wasVisible) createCraftEffect();
            if (cur.craftTime > 0f)
                craftingTime %= cur.craftTime;
            else
                craftingTime = 0f;
        }

        public void createCraftEffect() {
            Recipe cur = getCurRecipe();
            Effect curFx = cur.craftEffect;
            Effect fx = curFx != Fx.none ? curFx : craftEffect;
            spawnEffect(fx);
        }

        public void updateBars() {
            barMap.clear();
            setBars();
        }

        public void spawnEffect(Effect effect) {
            effect.at(x, y, drawrot());
        }

        public float getCurPowerStore() {
            if (power == null) return 0f;
            return power.status * powerCapacity;
        }

        public void setCurPowerStore(float powerStore) {
            if (power == null) return;
            power.status = Mathf.clamp(powerStore / powerCapacity);
        }

        public void dumpOutputs() {
            Recipe cur = getCurRecipe();
            if (cur.isOutputItem() && timer(timerDump, dumpTime / timeScale))
                for (ItemStack output : cur.output.items) dump(output.item);

            if (cur.isOutputFluid()) {
                Seq<LiquidStack> fluids = cur.output.liquids;
                for (int i = 0; i < fluids.size; i++) {
                    int dir = fluidOutputDirections.length > i ? fluidOutputDirections[i] : -1;
                    dumpLiquid(fluids.get(i).liquid, 2f, dir);
                }
            }
        }

        public float warmupTarget() {
            Recipe cur = getCurRecipe();
            // When As HeatConsumer
            if(cur.isConsumeHeat())
                return Mathf.clamp(heat / cur.input.heat);
            else return 1f;
        }

        public float efficiencyScale() {
            Recipe cur = getCurRecipe();
            // When As HeatConsumer
            if (cur.isConsumeHeat()) {
                float heatRequirement = cur.input.heat;
                float over = Math.max(heat - heatRequirement, 0f);
                return Math.min(Mathf.clamp(heat / heatRequirement) + over /
                        heatRequirement, maxEfficiency);
            } else return 1f;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public float progress() {
            Recipe cur = getCurRecipe();
            return Mathf.clamp(cur.craftTime > 0f ? craftingTime / cur.craftTime : 1f);
        }

        @Override
        public void updateTile() {
            Recipe cur = getCurRecipe();
            float craftTimeNeed = cur.craftTime;

            if(cur.isConsumeHeat()) {
                heat = calculateHeat(sideHeat);
            }

            if(cur.isOutputHeat()) {
                float heatOutput = cur.output.heat;
                heat = Mathf.approachDelta(heat, heatOutput * efficiency, warmupRate * edelta());
            }

            if (efficiency > 0 && (!hasPower || getCurPowerStore() >= cur.input.power)) {
                if (craftTimeNeed > 0f) craftingTime += edelta();

                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);
                if (hasPower) {
                    float powerChange = (cur.output.power - cur.input.power) * delta();
                    if (!Mathf.zero(powerChange))
                        setCurPowerStore((getCurPowerStore() + powerChange));
                }

                if (cur.isOutputFluid()) {
                    float increment = getProgressIncrease(1f);
                    for (LiquidStack output : cur.output.liquids) {
                        Liquid fluid = output.liquid;
                        handleLiquid(this, fluid, Math.min(output.amount * increment,
                                liquidCapacity - liquids.get(fluid)));
                    }
                }

                if (wasVisible && Mathf.chanceDelta(updateEffectChance))
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
            } else warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);

            if (craftTimeNeed <= 0f) {
                if (efficiency > 0f)
                    craft();
            } else if (craftingTime >= craftTimeNeed)
                craft();

            updateBars();
            dumpOutputs();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return hasItems && getCurRecipe().input.contains(item) &&
                    items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return hasLiquids && getCurRecipe().input.contains(liquid) &&
                    liquids.get(liquid) < liquidCapacity;
        }

        @Override
        public float edelta() {
            Recipe cur = getCurRecipe();
            if (cur.input.power > 0f) return this.efficiency * this.delta()
                    * Mathf.clamp(getCurPowerStore() / cur.input.power);
            else return this.efficiency * this.delta();
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            Recipe cur = getCurRecipe();
            if (cur.isOutputHeat()) return heat / cur.output.heat;
            else if (cur.isConsumeHeat()) return heat / cur.input.heat;
            return 0f;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float heatRequirement() {
            Recipe cur = getCurRecipe();
            if (cur.isConsumeHeat()) return cur.input.heat;
            return 0f;
        }

        @Override
        public float getPowerProduction() {
            Recipe cur = getCurRecipe();
            if(cur.isOutputPower()) return cur.output.power * efficiency;
            else return 0f;
        }

        @Override
        public void buildConfiguration(Table table) {
            resolvedSelector.builder.get(this, table);
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public Object config() {
            return curRecipeIndex;
        }

        @Override
        public boolean shouldAmbientSound() {
            return efficiency > 0;
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress) return progress();
            if (sensor == LAccess.heat) return warmup();
            return super.sense(sensor);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(craftingTime);
            write.f(warmup);
            write.i(curRecipeIndex);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            craftingTime = read.f();
            warmup = read.f();
            curRecipeIndex = Mathf.clamp(read.i(), 0, resolvedRecipes.size - 1);
            heat = read.f();
        }

        @Override
        public void updateEfficiencyMultiplier() {
            Recipe cur = getCurRecipe();
            if (cur.isConsumeHeat()) {
                efficiency *= efficiencyScale();
                potentialEfficiency *= efficiencyScale();
            }
        }
    }
}