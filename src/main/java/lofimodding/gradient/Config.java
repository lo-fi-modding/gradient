package lofimodding.gradient;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class Config {
  private Config() { }

  public static final ForgeConfigSpec INTEROP_SPEC;
  public static final Interop INTEROP;
  public static final ForgeConfigSpec ENET_SPEC;
  public static final Enet ENET;

  static {
    final Pair<Interop, ForgeConfigSpec> interopSpec = new ForgeConfigSpec.Builder().configure(Interop::new);
    INTEROP_SPEC = interopSpec.getRight();
    INTEROP = interopSpec.getLeft();

    final Pair<Enet, ForgeConfigSpec> enetSpec = new ForgeConfigSpec.Builder().configure(Enet::new);
    ENET_SPEC = enetSpec.getRight();
    ENET = enetSpec.getLeft();
  }

  public static final class Interop {
    public final ForgeConfigSpec.BooleanValue REMOVE_LEATHER_RECIPES;
    public final ForgeConfigSpec.BooleanValue REMOVE_VANILLA_LEASH_RECIPE;
    public final ForgeConfigSpec.BooleanValue REPLACE_PLANK_RECIPES;
    public final ForgeConfigSpec.BooleanValue REPLACE_STICK_RECIPES;
    public final ForgeConfigSpec.BooleanValue HALVE_PLANK_RECIPE_OUTPUT;
    public final ForgeConfigSpec.BooleanValue HALVE_STICK_RECIPE_OUTPUT;
    public final ForgeConfigSpec.BooleanValue DISABLE_VANILLA_CRAFTING_TABLE;
    public final ForgeConfigSpec.BooleanValue DISABLE_VANILLA_FURNACE;

    private Interop(final ForgeConfigSpec.Builder builder) {
      builder
        .comment("Potentially mod-breaking config")
        .push("interop");

      this.REMOVE_LEATHER_RECIPES = builder
        .comment("Should leather recipes be removed?")
        .translation("config.gradient.interop.remove_leather_recipes")
        .define("remove_leather_recipes", true);

      this.REMOVE_VANILLA_LEASH_RECIPE = builder
        .comment("Should the vanilla leash (lead) recipe be removed?")
        .translation("config.gradient.interop.remove_vanilla_leash_recipe")
        .define("remove_vanilla_leash_recipe", true);

      this.REPLACE_PLANK_RECIPES = builder
        .comment("Should plank recipes be modified to require axes to split?")
        .translation("config.gradient.interop.replace_plank_recipes")
        .define("replace_plank_recipes", true);

      this.REPLACE_STICK_RECIPES = builder
        .comment("Should stick recipes be modified to require axes to split?")
        .translation("config.gradient.interop.replace_stick_recipes")
        .define("replace_stick_recipes", true);

      this.HALVE_PLANK_RECIPE_OUTPUT = builder
        .comment("Should plank recipes output be halved?")
        .translation("config.gradient.interop.halve_plank_recipe_output")
        .define("halve_plank_recipe_output", true);

      this.HALVE_STICK_RECIPE_OUTPUT = builder
        .comment("Should stick recipes output be halved?")
        .translation("config.gradient.interop.halve_stick_recipe_output")
        .define("halve_stick_recipe_output", true);

      this.DISABLE_VANILLA_CRAFTING_TABLE = builder
        .comment("Disables the vanilla crafting table. Try disabling this if any recipes or crafting machines are broken.")
        .translation("config.gradient.interop.disable_vanilla_crafting_table")
        .define("disable_vanilla_crafting_table", true);

      this.DISABLE_VANILLA_FURNACE = builder
        .comment("Disables the vanilla furnace. Try disabling this if any recipes or furnaces are broken.")
        .translation("config.gradient.interop.disable_vanilla_furnace")
        .define("disable_vanilla_furnace", true);

      builder.pop();
    }
  }

  public static final class Enet {
    public final ForgeConfigSpec.DoubleValue WOODEN_AXLE_MAX_ENERGY;
    public final ForgeConfigSpec.DoubleValue WOODEN_AXLE_LOSS_PER_BLOCK;
    public final ForgeConfigSpec.DoubleValue WOODEN_GEARBOX_MAX_ENERGY;
    public final ForgeConfigSpec.DoubleValue WOODEN_GEARBOX_LOSS_PER_BLOCK;

    public final ForgeConfigSpec.BooleanValue ENABLE_NODE_DEBUG;
    public final ForgeConfigSpec.BooleanValue ENABLE_PATH_DEBUG;
    public final ForgeConfigSpec.BooleanValue ENABLE_TICK_DEBUG;

    private Enet(final ForgeConfigSpec.Builder builder) {
      builder
        .comment("Energy network settings")
        .push("enet");

      this.WOODEN_AXLE_MAX_ENERGY = builder
        .comment("Maximum energy a wooden axle may carry. It will still transfer the energy; however, it will break by doing so.")
        .translation("config.gradient.enet.wooden_axle_max_energy")
        .defineInRange("wooden_axle_max_energy", 5.0d, 0.0d, Double.MAX_VALUE);

      this.WOODEN_AXLE_LOSS_PER_BLOCK = builder
        .comment("The amount of energy lost by a wooden axle per block.")
        .translation("config.gradient.enet.wooden_axle_loss_per_block")
        .defineInRange("wooden_axle_loss_per_block", 0.0d, 0.0d, Double.MAX_VALUE);

      this.WOODEN_GEARBOX_MAX_ENERGY = builder
        .comment("Maximum energy a wooden gearbox may carry. It will still transfer the energy; however, it will break by doing so.")
        .translation("config.gradient.enet.wooden_gearbox_max_energy")
        .defineInRange("wooden_gearbox_max_energy", 20.0d, 0.0d, Double.MAX_VALUE);

      this.WOODEN_GEARBOX_LOSS_PER_BLOCK = builder
        .comment("The amount of energy lost by a wooden gearbox per block.")
        .translation("config.gradient.enet.wooden_gearbox_loss_per_block")
        .defineInRange("wooden_gearbox_loss_per_block", 0.05d, 0.0d, Double.MAX_VALUE);

      this.ENABLE_NODE_DEBUG = builder
        .comment("Enable verbose debug logging for connecting/disconnecting nodes.")
        .translation("config.gradient.enet.enable_node_debug")
        .define("enable_node_debug", false);

      this.ENABLE_PATH_DEBUG = builder
        .comment("Enable verbose debug logging for pathfinding nodes.")
        .translation("config.gradient.enet.enable_path_debug")
        .define("enable_path_debug", false);

      this.ENABLE_TICK_DEBUG = builder
        .comment("Enable verbose debug logging for ticking nodes.")
        .translation("config.gradient.enet.enable_tick_debug")
        .define("enable_tick_debug", false);

      builder.pop();
    }
  }
}
