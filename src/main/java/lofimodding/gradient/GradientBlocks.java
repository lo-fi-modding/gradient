package lofimodding.gradient;

import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.blocks.PebbleBlock;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class GradientBlocks {
  private GradientBlocks() { }

  private static final DeferredRegister<Block> REGISTRY = new DeferredRegister<>(ForgeRegistries.BLOCKS, Gradient.MOD_ID);

  public static final RegistryObject<PebbleBlock> PEBBLE = REGISTRY.register(GradientIds.PEBBLE, () -> new PebbleBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0f).doesNotBlockMovement()));

  private static final Map<Ore, RegistryObject<PebbleBlock>> PEBBLES = new HashMap<>();

  static {
    for(final Ore ore : Ores.all()) {
      PEBBLES.put(ore, REGISTRY.register(GradientIds.PEBBLE(ore), () -> new PebbleBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0f))));
    }
  }

  private static final Map<Ore, RegistryObject<MetalBlock>> ORES = new HashMap<>();

  static {
    for(final Ore ore : Ores.all()) {
      ORES.put(ore, REGISTRY.register(GradientIds.ORE(ore), () -> new MetalBlock(ore.metal, Block.Properties.create(Material.ROCK).hardnessAndResistance(ore.metal.hardness).harvestTool(ToolType.PICKAXE).harvestLevel(ore.metal.harvestLevel))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalBlock>> METAL_BLOCKS = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      METAL_BLOCKS.put(metal, REGISTRY.register(GradientIds.METAL_BLOCK(metal), () -> new MetalBlock(metal, Block.Properties.create(Material.ROCK).hardnessAndResistance(metal.hardness).harvestTool(ToolType.PICKAXE).harvestLevel(metal.harvestLevel))));
    }
  }

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering blocks...");
    REGISTRY.register(bus);
  }

  public static RegistryObject<PebbleBlock> PEBBLE(final Ore ore) {
    return PEBBLES.get(ore);
  }

  public static RegistryObject<MetalBlock> ORE(final Ore ore) {
    return ORES.get(ore);
  }

  public static RegistryObject<MetalBlock> METAL_BLOCK(final Metal metal) {
    return METAL_BLOCKS.get(metal);
  }
}
