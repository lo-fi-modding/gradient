package lofimodding.gradient;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

//TODO i18n

public final class Config {
  private Config() { }

  public static final ForgeConfigSpec ENET_SPEC;
  public static final Enet ENET;

  static {
    final Pair<Enet, ForgeConfigSpec> gameplaySpec = new ForgeConfigSpec.Builder().configure(Enet::new);
    ENET_SPEC = gameplaySpec.getRight();
    ENET = gameplaySpec.getLeft();
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
