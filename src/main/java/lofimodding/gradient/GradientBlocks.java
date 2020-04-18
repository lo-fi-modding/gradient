package lofimodding.gradient;

import lofimodding.gradient.blocks.MetalBlock;
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

  private static final Map<Ore, RegistryObject<MetalBlock>> ORES = new HashMap<>();

  static {
    for(final Ore ore : Ores.all()) {
      ORES.put(ore, REGISTRY.register(GradientIds.ORE(ore), () -> new MetalBlock(ore.metal, Block.Properties.create(Material.ROCK).hardnessAndResistance(ore.metal.hardness).harvestTool(ToolType.PICKAXE).harvestLevel(ore.metal.harvestLevel))));
    }
  }

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering blocks...");
    REGISTRY.register(bus);
  }

  public static RegistryObject<MetalBlock> ORE(final Ore ore) {
    return ORES.get(ore);
  }
}
