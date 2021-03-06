package lofimodding.gradient.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientLoot;
import lofimodding.gradient.GradientStages;
import lofimodding.gradient.GradientTags;
import lofimodding.gradient.advancements.criterion.AdvancementUnlockedTrigger;
import lofimodding.gradient.advancements.criterion.BlockRightClickedTrigger;
import lofimodding.gradient.advancements.criterion.UsedHoeTrigger;
import lofimodding.gradient.science.Minerals;
import lofimodding.progression.advancements.StageUnlockedTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.command.FunctionObject;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

public class AdvancementsProvider extends AdvancementProvider {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private final DataGenerator generator;

  public AdvancementsProvider(final DataGenerator generator) {
    super(generator);
    this.generator = generator;

    this.advancements = new ArrayList<>();
    this.advancements.add(new Age1());
  }

  public static class Age1 implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(final Consumer<Advancement> finished) {
      this.age1(finished);
      this.age2(finished);
    }

    public void age1(final Consumer<Advancement> finished) {
      final Advancement root = builder(1, GradientItems.PEBBLE.get(), "root", 0.0f, 3.0f, new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK)
        .withCriterion("has_age_1", StageUnlockedTrigger.Instance.forStage(GradientStages.AGE_1.get()))
        .register(finished, loc("age1/root"));

      final Advancement basicMaterials = builder(1, Items.STICK, "basic_materials", 1.0f, 3.5f, FrameType.TASK, root)
        .withCriterion("has_sticks", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(Tags.Items.RODS_WOODEN).build()))
        .withCriterion("has_fibre", InventoryChangeTrigger.Instance.forItems(GradientItems.FIBRE.get()))
        .withCriterion("has_pebble", InventoryChangeTrigger.Instance.forItems(GradientItems.PEBBLE.get()))
        .register(finished, loc("age1/basic_materials"));

      final Advancement stoneHammer = builder(1, GradientItems.STONE_HAMMER.get(), "stone_hammer", 2.0f, 3.0f, FrameType.TASK, basicMaterials)
        .withCriterion("has_hammer", InventoryChangeTrigger.Instance.forItems(GradientItems.STONE_HAMMER.get()))
        .register(finished, loc("age1/stone_hammer"));

      final Advancement stoneHatchet = builder(1, GradientItems.STONE_HATCHET.get(), "stone_hatchet", 2.0f, 4.0f, FrameType.TASK, basicMaterials)
        .withCriterion("has_hatchet", InventoryChangeTrigger.Instance.forItems(GradientItems.STONE_HATCHET.get()))
        .register(finished, loc("age1/stone_hatchet"));

      final Advancement wood = builder(1, Blocks.OAK_LOG, "wood", 3.0f, 3.5f, FrameType.TASK, stoneHammer, stoneHatchet)
        .withCriterion("has_wood", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(ItemTags.LOGS).build()))
        .register(finished, loc("age1/wood"));

      final Advancement planks = builder(1, Blocks.OAK_PLANKS, "planks", 4.0f, 3.5f, FrameType.TASK, wood)
        .withCriterion("has_planks", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(ItemTags.PLANKS).build()))
        .register(finished, loc("age1/planks"));

      final Advancement grindstone = builder(1, GradientBlocks.GRINDSTONE.get(), "grindstone", 5.25f, 3.0f, FrameType.GOAL, planks)
        .withCriterion("has_grindstone", InventoryChangeTrigger.Instance.forItems(GradientItems.GRINDSTONE.get()))
        .register(finished, loc("age1/grindstone"));

      final Advancement mixingBasin = builder(1, GradientBlocks.MIXING_BASIN.get(), "mixing_basin", 5.5f, 4.0f, FrameType.GOAL, planks)
        .withCriterion("has_mixing_basin", InventoryChangeTrigger.Instance.forItems(GradientItems.MIXING_BASIN.get()))
        .register(finished, loc("age1/mixing_basin"));

      final Advancement firepit = builder(1, GradientItems.FIREPIT.get(), "firepit", 3.0f, 4.75f, FrameType.TASK, basicMaterials)
        .withCriterion("has_firepit", InventoryChangeTrigger.Instance.forItems(GradientItems.FIREPIT.get()))
        .register(finished, loc("age1/firepit"));

      final Advancement fireStarter = builder(1, GradientItems.FIRE_STARTER.get(), "fire_starter", 4.0f, 4.75f, FrameType.TASK, firepit)
        .withCriterion("has_fire_starter", InventoryChangeTrigger.Instance.forItems(GradientItems.FIRE_STARTER.get()))
        .withCriterion("lit_fire", BlockRightClickedTrigger.Instance.of(GradientBlocks.FIREPIT.get(), Ingredient.fromItems(GradientItems.FIRE_STARTER.get())))
        .register(finished, loc("age1/fire_starter"));

      final Advancement fibreTorch = builder(1, GradientItems.LIT_FIBRE_TORCH.get(), "fibre_torch", 5.75f, 5.0f, FrameType.GOAL, fireStarter)
        .withCriterion("has_unlit", InventoryChangeTrigger.Instance.forItems(GradientItems.UNLIT_FIBRE_TORCH.get()))
        .withCriterion("has_lit", InventoryChangeTrigger.Instance.forItems(GradientItems.LIT_FIBRE_TORCH.get()))
        .register(finished, loc("age1/fibre_torch"));

      final Advancement pelt = builder(1, GradientItems.COW_PELT.get(), "pelt", 1.0f, 2.5f, FrameType.TASK, root)
        .withCriterion("has_pelt", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(GradientTags.Items.PELTS).build()))
        .register(finished, loc("age1/pelt"));

      final Advancement boneAwl = builder(1, GradientItems.BONE_AWL.get(), "bone_awl", 2.0f, 1.375f, FrameType.TASK, pelt)
        .withCriterion("has_bone_awl", InventoryChangeTrigger.Instance.forItems(GradientItems.BONE_AWL.get()))
        .register(finished, loc("age1/bone_awl"));

      final Advancement waterskin = builder(1, GradientItems.EMPTY_WATERSKIN.get(), "waterskin", 5.75f, 1.0f, FrameType.GOAL, boneAwl)
        .withCriterion("has_waterskin", InventoryChangeTrigger.Instance.forItems(GradientItems.EMPTY_WATERSKIN.get()))
        .register(finished, loc("age1/waterskin"));

      final Advancement hideBedding = builder(1, GradientItems.HIDE_BEDDING.get(), "hide_bedding", 5.5f, 2.0f, FrameType.GOAL, pelt)
        .withCriterion("has_hide_bedding", InventoryChangeTrigger.Instance.forItems(GradientItems.HIDE_BEDDING.get()))
        .register(finished, loc("age1/hide_bedding"));

      final Advancement hideArmour = builder(1, GradientItems.HIDE_PANTS.get(), "hide_armour", 3.0f, 0.375f, FrameType.CHALLENGE, boneAwl)
        .withCriterion("has_hide_hat", InventoryChangeTrigger.Instance.forItems(GradientItems.HIDE_HAT.get()))
        .withCriterion("has_hide_chest", InventoryChangeTrigger.Instance.forItems(GradientItems.HIDE_SHIRT.get()))
        .withCriterion("has_hide_pants", InventoryChangeTrigger.Instance.forItems(GradientItems.HIDE_PANTS.get()))
        .withCriterion("has_hide_boots", InventoryChangeTrigger.Instance.forItems(GradientItems.HIDE_BOOTS.get()))
        .withRewards(new AdvancementRewards(0, new ResourceLocation[] {GradientLoot.HIDE_ARMOUR_ADVANCEMENT}, new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY))
        .register(finished, loc("age1/hide_armour"));

      final Advancement goal = builder(1, Items.WHEAT, "goal", 7.0f, 3.0f, FrameType.CHALLENGE, grindstone, mixingBasin, fibreTorch, waterskin, hideBedding)
        .withRewards(new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], new FunctionObject.CacheableFunction(Gradient.loc("grant_age_2"))))
        .register(finished, loc("age1/goal"));
    }

    public void age2(final Consumer<Advancement> finished) {
      final Advancement root = builder(2, Items.WHEAT, "root", 0.0f, 2.25f, new ResourceLocation("textures/block/farmland.png"), FrameType.TASK)
        .withCriterion("has_age_2", StageUnlockedTrigger.Instance.forStage(GradientStages.AGE_2.get()))
        .register(finished, loc("age2/root"));

      final Advancement standingTorch = builder(2, GradientItems.TORCH_STAND.get(), "standing_torch", 0.5f, 0.375f, FrameType.TASK, root)
        .withCriterion("has_standing_torch", InventoryChangeTrigger.Instance.forItems(GradientItems.TORCH_STAND.get()))
        .register(finished, loc("age2/standing_torch"));

      final Advancement clay = builder(2, Items.CLAY_BALL, "clay", 3.0f, 1.0f, FrameType.TASK, root)
        .withCriterion("has_clay", InventoryChangeTrigger.Instance.forItems(Items.CLAY_BALL))
        .register(finished, loc("age2/clay"));

      final Advancement clayFurnace = builder(2, GradientItems.CLAY_FURNACE.get(), "clay_furnace", 4.0f, 1.0f, FrameType.GOAL, clay)
        .withCriterion("has_clay_furnace", InventoryChangeTrigger.Instance.forItems(GradientItems.CLAY_FURNACE.get()))
        .register(finished, loc("age2/clay_furnace"));

      final Advancement clayOven = builder(2, GradientItems.CLAY_OVEN.get(), "clay_oven", 5.0f, -0.5f, FrameType.TASK, clayFurnace)
        .withCriterion("has_clay_oven", InventoryChangeTrigger.Instance.forItems(GradientItems.CLAY_OVEN.get()))
        .register(finished, loc("age2/clay_oven"));

      //TODO: bellows

      final Advancement dryingRack = builder(2, GradientItems.DRYING_RACK.get(), "drying_rack", 2.0f, 4.5f, FrameType.TASK, root)
        .withCriterion("has_drying_rack", InventoryChangeTrigger.Instance.forItems(GradientItems.DRYING_RACK.get()))
        .register(finished, loc("age2/drying_rack"));

      final Advancement bark = builder(2, GradientItems.BARK.get(), "bark", 1.0f, 3.5f, FrameType.TASK, root)
        .withCriterion("has_bark", InventoryChangeTrigger.Instance.forItems(GradientItems.BARK.get()))
        .register(finished, loc("age2/bark"));

      final Advancement mulch = builder(2, GradientItems.MULCH.get(), "mulch", 2.0f, 3.5f, FrameType.TASK, bark)
        .withCriterion("has_mulch", InventoryChangeTrigger.Instance.forItems(GradientItems.MULCH.get()))
        .register(finished, loc("age2/mulch"));

      final Advancement flintKnife = builder(2, GradientItems.FLINT_KNIFE.get(), "flint_knife", 3.0f, 4.0f, FrameType.TASK, dryingRack, mulch)
        .withCriterion("has_flint_knife", InventoryChangeTrigger.Instance.forItems(GradientItems.FLINT_KNIFE.get()))
        .register(finished, loc("age2/flint_knife"));

      final Advancement leather = builder(2, Items.LEATHER, "leather", 4.0f, 4.0f, FrameType.GOAL, flintKnife)
        .withCriterion("has_leather", InventoryChangeTrigger.Instance.forItems(Items.LEATHER))
        .register(finished, loc("age2/leather"));

      final Advancement hardenedLog = builder(2, GradientItems.HARDENED_LOG.get(), "hardened_log", 1.0f, 2.25f, FrameType.TASK, root)
        .withCriterion("has_hardened_wood", InventoryChangeTrigger.Instance.forItems(GradientItems.HARDENED_LOG.get()))
        .register(finished, loc("age2/hardened_wood"));

      final Advancement hardenedPlanks = builder(2, GradientItems.HARDENED_PLANKS.get(), "hardened_planks", 2.0f, 2.25f, FrameType.TASK, hardenedLog)
        .withCriterion("has_hardened_planks", InventoryChangeTrigger.Instance.forItems(GradientItems.HARDENED_PLANKS.get()))
        .register(finished, loc("age2/hardened_planks"));

      final Advancement hardenedStick = builder(2, GradientItems.HARDENED_STICK.get(), "hardened_stick", 3.0f, 2.25f, FrameType.TASK, hardenedPlanks)
        .withCriterion("has_hardened_stick", InventoryChangeTrigger.Instance.forItems(GradientItems.HARDENED_STICK.get()))
        .register(finished, loc("age2/hardened_stick"));

      final Advancement toolStation = builder(2, GradientItems.TOOL_STATION.get(), "tool_station", 4.0f, 2.25f, FrameType.GOAL, hardenedStick)
        .withCriterion("has_tool_station", InventoryChangeTrigger.Instance.forItems(GradientItems.TOOL_STATION.get()))
        .register(finished, loc("age2/tool_station"));

      final Advancement stonePickaxe = builder(2, GradientItems.STONE_PICKAXE.get(), "stone_pickaxe", 5.0f, 1.75f, FrameType.TASK, clayFurnace, leather, toolStation)
        .withCriterion("has_stone_pickaxe", InventoryChangeTrigger.Instance.forItems(GradientItems.STONE_PICKAXE.get()))
        .register(finished, loc("age2/stone_pickaxe"));

      final Advancement coal = builder(2, Items.COAL, "coal", 6.0f, 1.75f, FrameType.TASK, stonePickaxe)
        .withCriterion("has_nugget", InventoryChangeTrigger.Instance.forItems(GradientItems.COAL_NUGGET.get()))
        .withCriterion("has_ore", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(ItemTags.COALS).build()))
        .withRequirementsStrategy(requirements -> new String[][] {{"has_parent_0"}, {"has_nugget", "has_ore"}})
        .register(finished, loc("age2/coal"));

      final Advancement ore = builder(2, GradientItems.ORE(Minerals.COPPER_ORE).get(), "ore", 7.0f, 1.75f, FrameType.TASK, coal)
        .withCriterion("has_nugget", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(Tags.Items.NUGGETS).build()))
        .withCriterion("has_ore", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(Tags.Items.ORES).build()))
        .withRequirementsStrategy(requirements -> new String[][] {{"has_parent_0"}, {"has_nugget", "has_ore"}})
        .register(finished, loc("age2/ore"));

      final Advancement stoneMattock = builder(2, GradientItems.STONE_MATTOCK.get(), "stone_mattock", 5.0f, 0.75f, FrameType.TASK, clayFurnace, leather, toolStation)
        .withCriterion("has_stone_mattock", InventoryChangeTrigger.Instance.forItems(GradientItems.STONE_MATTOCK.get()))
        .register(finished, loc("age2/stone_mattock"));

      final Advancement till = builder(2, Blocks.FARMLAND, "till", 6.0f, 0.75f, FrameType.TASK, stoneMattock)
        .withCriterion("tilled_soil", new UsedHoeTrigger.Instance())
        .register(finished, loc("age2/till"));

      final Advancement wheat = builder(2, Items.WHEAT, "wheat", 7.0f, 0.75f, FrameType.TASK, till)
        .withCriterion("has_wheat", InventoryChangeTrigger.Instance.forItems(Items.WHEAT))
        .register(finished, loc("age2/wheat"));

      final Advancement bread = builder(2, Items.BREAD, "bread", 8.0f, 0.75f, FrameType.GOAL, wheat)
        .withCriterion("has_bread", InventoryChangeTrigger.Instance.forItems(Items.BREAD))
        .register(finished, loc("age2/bread"));

      final Advancement leash = builder(2, Items.LEAD, "leash", 5.0f, 3.0f, FrameType.TASK, clayFurnace, leather, toolStation)
        .withCriterion("has_leash", InventoryChangeTrigger.Instance.forItems(Items.LEAD))
        .register(finished, loc("age2/leash"));

      final Advancement saddle = builder(2, Items.SADDLE, "saddle", 6.0f, 3.0f, FrameType.GOAL, leash)
        .withCriterion("has_saddle", InventoryChangeTrigger.Instance.forItems(Items.SADDLE))
        .register(finished, loc("age2/saddle"));

      final Advancement woodenAxle = builder(2, GradientItems.WOODEN_AXLE.get(), "wooden_axle", 5.0f, 4.25f, FrameType.TASK, clayFurnace, leather, toolStation)
        .withCriterion("has_wooden_axle", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_AXLE.get()))
        .register(finished, loc("age2/wooden_axle"));

      final Advancement woodenGear = builder(2, GradientItems.WOODEN_GEAR.get(), "wooden_gear", 5.0f, 5.25f, FrameType.TASK, clayFurnace, leather, toolStation)
        .withCriterion("has_wooden_gear", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_GEAR.get()))
        .register(finished, loc("age2/wooden_gear"));

      final Advancement woodenGearbox = builder(2, GradientItems.WOODEN_GEARBOX.get(), "wooden_gearbox", 6.0f, 4.25f, FrameType.TASK, woodenAxle, woodenGear)
        .withCriterion("has_wooden_gearbox", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_GEARBOX.get()))
        .register(finished, loc("age2/wooden_gearbox"));

      final Advancement woodenHopper = builder(2, GradientItems.WOODEN_HOPPER.get(), "wooden_hopper", 6.0f, 5.25f, FrameType.TASK, woodenGear)
        .withCriterion("has_wooden_hopper", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_HOPPER.get()))
        .register(finished, loc("age2/wooden_hopper"));

      final Advancement woodenConveyorBelt = builder(2, GradientItems.WOODEN_CONVEYOR_BELT.get(), "wooden_conveyor_belt", 7.0f, 5.25f, FrameType.TASK, woodenHopper)
        .withCriterion("has_wooden_conveyor_belt", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_CONVEYOR_BELT.get()))
        .withCriterion("has_wooden_conveyor_belt_driver", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_CONVEYOR_BELT_DRIVER.get()))
        .register(finished, loc("age2/wooden_conveyor_belt"));

      final Advancement woodenCrank = builder(2, GradientItems.WOODEN_CRANK.get(), "wooden_crank", 7.0f, 3.625f, FrameType.TASK, saddle, woodenGearbox)
        .withCriterion("has_wooden_crank", InventoryChangeTrigger.Instance.forItems(GradientItems.WOODEN_CRANK.get()))
        .register(finished, loc("age2/wooden_crank"));

      final Advancement mechanicalGrindstone = builder(2, GradientItems.MECHANICAL_GRINDSTONE.get(), "mechanical_grindstone", 8.0f, 2.75f, FrameType.TASK, woodenCrank)
        .withCriterion("has_mechanical_grindstone", InventoryChangeTrigger.Instance.forItems(GradientItems.MECHANICAL_GRINDSTONE.get()))
        .register(finished, loc("age2/mechanical_grindstone"));

      final Advancement mechanicalMixingBasin = builder(2, GradientItems.MECHANICAL_MIXING_BASIN.get(), "mechanical_mixing_basin", 8.0f, 4.5f, FrameType.TASK, woodenCrank)
        .withCriterion("has_mechanical_mixing_basin", InventoryChangeTrigger.Instance.forItems(GradientItems.MECHANICAL_MIXING_BASIN.get()))
        .register(finished, loc("age2/mechanical_mixing_basin"));

      final Advancement mechanicalPump = builder(2, GradientItems.MECHANICAL_PUMP.get(), "mechanical_pump", 9.0f, 4.0f, FrameType.TASK, mechanicalMixingBasin)
        .withCriterion("has_mechanical_pump", InventoryChangeTrigger.Instance.forItems(GradientItems.MECHANICAL_PUMP.get()))
        .register(finished, loc("age2/mechanical_pump"));

      final Advancement recipeFilter = builder(2, GradientItems.RECIPE_FILTER.get(), "recipe_filter", 9.0f, 5.0f, FrameType.TASK, mechanicalMixingBasin)
        .withCriterion("has_recipe_filter", InventoryChangeTrigger.Instance.forItems(GradientItems.RECIPE_FILTER.get()))
        .register(finished, loc("age2/recipe_filter"));

      final Advancement crushedOre = builder(2, GradientItems.CRUSHED(Minerals.COPPER_ORE).get(), "crushed_ore", 9.0f, 1.75f, FrameType.TASK, ore, mechanicalGrindstone)
        .withCriterion("has_crushed_ore", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(GradientTags.Items.CRUSHED_ORES).build()))
        .register(finished, loc("age2/crushed_ore"));

      final Advancement clayCrucible = builder(2, GradientItems.CLAY_CRUCIBLE.get(), "clay_crucible", 9.0f, 2.75f, FrameType.TASK, mechanicalGrindstone)
        .withCriterion("has_clay_crucible", InventoryChangeTrigger.Instance.forItems(GradientItems.CLAY_CRUCIBLE.get()))
        .register(finished, loc("age2/clay_crucible"));

      final Advancement clayMetalMixer = builder(2, GradientItems.CLAY_METAL_MIXER.get(), "clay_metal_mixer", 10.0f, 2.25f, FrameType.TASK, crushedOre, clayCrucible)
        .withCriterion("has_clay_metal_mixer", InventoryChangeTrigger.Instance.forItems(GradientItems.CLAY_METAL_MIXER.get()))
        .register(finished, loc("age2/clay_metal_mixer"));

      final Advancement clayCast = builder(2, GradientItems.CLAY_CAST(GradientCasts.INGOT).get(), "clay_cast", 11.0f, 2.25f, FrameType.TASK, clayMetalMixer)
        .withCriterion("has_clay_cast", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(GradientTags.Items.CASTS).build()))
        .register(finished, loc("age2/clay_cast"));

      final Advancement bronze = builder(2, GradientItems.INGOT(Minerals.BRONZE).get(), "bronze", 12.0f, 2.25f, FrameType.CHALLENGE, clayMetalMixer)
        .withCriterion("has_bronze", InventoryChangeTrigger.Instance.forItems(GradientItems.INGOT(Minerals.BRONZE).get()))
        .withRewards(new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], new FunctionObject.CacheableFunction(Gradient.loc("grant_age_3"))))
        .register(finished, loc("age2/bronze"));
    }

    private static Advancement.Builder builder(final int age, final IItemProvider icon, final String id, final float x, final float y, @Nullable final ResourceLocation background, final FrameType frame, final Advancement... parents) {
      final DisplayInfo display = new DisplayInfo(new ItemStack(icon), text(age, id + ".title"), text(age, id + ".description"), background, frame, background == null, background == null, false);
      display.setPosition(x, y);

      final Advancement.Builder builder = Advancement.Builder.builder().withDisplay(display);

      int i = 0;
      for(final Advancement parent : parents) {
        builder
          .withParent(parent)
          .withCriterion("has_parent_" + i++, AdvancementUnlockedTrigger.Instance.forAdvancement(parent));
      }

      return builder;
    }

    private static Advancement.Builder builder(final int age, final IItemProvider icon, final String id, final float x, final float y, final FrameType frame, final Advancement... parents) {
      return builder(age, icon, id, x, y, null, frame, parents);
    }

    private static TranslationTextComponent text(final int age, final String key) {
      return new TranslationTextComponent("advancements.gradient.age" + age + '.' + key);
    }
  }

  private static String loc(final String path) {
    return Gradient.loc(path).toString();
  }

  /**
   * Allows specifying specific coordinates for advancements
   */
  @Override
  public void act(final DirectoryCache cache) {
    final Path path = this.generator.getOutputFolder();
    final Set<ResourceLocation> set = Sets.newHashSet();

    final Consumer<Advancement> consumer = advancement -> {
      if (!set.add(advancement.getId())) {
        throw new IllegalStateException("Duplicate advancement " + advancement.getId());
      }

      final Path path1 = getPath(path, advancement);

      final JsonObject json = advancement.copy().serialize();

      if(json.has("display") && advancement.getDisplay() != null) {
        final JsonObject displayJson = JSONUtils.getJsonObject(json, "display");
        displayJson.addProperty("x", advancement.getDisplay().getX());
        displayJson.addProperty("y", advancement.getDisplay().getY());
      }

      try {
        IDataProvider.save(GSON, cache, json, path1);
      } catch (final IOException ioexception) {
        LOGGER.error("Couldn't save advancement {}", path1, ioexception);
      }
    };

    for(final Consumer<Consumer<Advancement>> consumer1 : this.advancements) {
      consumer1.accept(consumer);
    }
  }
}
