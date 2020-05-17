package lofimodding.gradient;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

//TODO i18n

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

    private Interop(final ForgeConfigSpec.Builder builder) {
      builder
        .comment("Potentially mod-breaking config")
        .push("interop");

      this.REMOVE_LEATHER_RECIPES = builder
        .comment("Should leather recipes be removed?")
        .translation("config.gradient.interop.remove_leather_recipes")
        .define("remove_leather_recipes", true);

      builder.pop();
    }
  }

  public static final class Enet {
    public final ForgeConfigSpec.BooleanValue ENABLE_NODE_DEBUG;
    public final ForgeConfigSpec.BooleanValue ENABLE_PATH_DEBUG;
    public final ForgeConfigSpec.BooleanValue ENABLE_TICK_DEBUG;

    private Enet(final ForgeConfigSpec.Builder builder) {
      builder
        .comment("Energy network settings")
        .push("enet");

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
