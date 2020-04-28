package lofimodding.gradient;

import lofimodding.gradient.blocks.ClayFurnaceBlock;
import lofimodding.gradient.blocks.ClayOvenBlock;
import lofimodding.gradient.blocks.DryingRackBlock;
import lofimodding.gradient.blocks.FirepitBlock;
import lofimodding.gradient.blocks.GrindstoneBlock;
import lofimodding.gradient.blocks.LitFibreTorchBlock;
import lofimodding.gradient.blocks.LitFibreWallTorchBlock;
import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.blocks.PebbleBlock;
import lofimodding.gradient.blocks.UnhardenedClayBlock;
import lofimodding.gradient.blocks.UnlitFibreTorchBlock;
import lofimodding.gradient.blocks.UnlitFibreWallTorchBlock;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
      METAL_BLOCKS.put(metal, REGISTRY.register(GradientIds.METAL_BLOCK(metal), () -> new MetalBlock(metal, Block.Properties.create(Material.ROCK).sound(SoundType.METAL).hardnessAndResistance(metal.hardness).harvestTool(ToolType.PICKAXE).harvestLevel(metal.harvestLevel))));
    }
  }

  public static final RegistryObject<Block> SALT_BLOCK = REGISTRY.register(GradientIds.SALT_BLOCK, () -> new Block(Block.Properties.create(Material.SAND, MaterialColor.QUARTZ).sound(SoundType.SAND).hardnessAndResistance(0.5f)));

  public static final RegistryObject<FirepitBlock> FIREPIT = REGISTRY.register(GradientIds.FIREPIT, FirepitBlock::new);
  public static final RegistryObject<UnlitFibreTorchBlock> UNLIT_FIBRE_TORCH = REGISTRY.register(GradientIds.UNLIT_FIBRE_TORCH, () -> new UnlitFibreTorchBlock(litFibreTorch(), Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).sound(SoundType.WOOD)));
  public static final RegistryObject<UnlitFibreWallTorchBlock> UNLIT_FIBRE_WALL_TORCH = REGISTRY.register(GradientIds.UNLIT_FIBRE_WALL_TORCH, () -> new UnlitFibreWallTorchBlock(litFibreWallTorch(), Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).sound(SoundType.WOOD)));
  public static final RegistryObject<LitFibreTorchBlock> LIT_FIBRE_TORCH = REGISTRY.register(GradientIds.LIT_FIBRE_TORCH, () -> new LitFibreTorchBlock(UNLIT_FIBRE_TORCH::get, Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).lightValue(10).sound(SoundType.WOOD)));
  public static final RegistryObject<LitFibreWallTorchBlock> LIT_FIBRE_WALL_TORCH = REGISTRY.register(GradientIds.LIT_FIBRE_WALL_TORCH, () -> new LitFibreWallTorchBlock(UNLIT_FIBRE_WALL_TORCH::get, Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).lightValue(10).sound(SoundType.WOOD)));

  public static final RegistryObject<GrindstoneBlock> GRINDSTONE = REGISTRY.register(GradientIds.GRINDSTONE, GrindstoneBlock::new);
  public static final RegistryObject<MixingBasinBlock> MIXING_BASIN = REGISTRY.register(GradientIds.MIXING_BASIN, MixingBasinBlock::new);
  public static final RegistryObject<DryingRackBlock> DRYING_RACK = REGISTRY.register(GradientIds.DRYING_RACK, DryingRackBlock::new);

  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_FURNACE = REGISTRY.register(GradientIds.UNHARDENED_CLAY_FURNACE, () -> new UnhardenedClayBlock(VoxelShapes.fullCube()));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_CRUCIBLE = REGISTRY.register(GradientIds.UNHARDENED_CLAY_CRUCIBLE, () -> new UnhardenedClayBlock(Block.makeCuboidShape(1.0d, 0.0d, 1.0d, 15.0d, 12.0d, 15.0d)));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_OVEN = REGISTRY.register(GradientIds.UNHARDENED_CLAY_OVEN, () -> new UnhardenedClayBlock(Block.makeCuboidShape(2.0d, 0.0d, 2.0d, 14.0d, 6.0d, 14.0d)));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_MIXER = REGISTRY.register(GradientIds.UNHARDENED_CLAY_MIXER, () -> new UnhardenedClayBlock(Block.makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d)));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_BUCKET = REGISTRY.register(GradientIds.UNHARDENED_CLAY_BUCKET, () -> new UnhardenedClayBlock(Block.makeCuboidShape(3.0d, 0.0d, 3.0d, 13.0d, 8.0d, 13.0d)));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_CAST_BLANK = REGISTRY.register(GradientIds.UNHARDENED_CLAY_CAST_BLANK, () -> new UnhardenedClayBlock(Block.makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d)));
  private static final Map<GradientCasts, RegistryObject<UnhardenedClayBlock>> UNHARDENED_CLAY_CASTS = new EnumMap<>(GradientCasts.class);

  static {
    for(final GradientCasts cast : GradientCasts.values()) {
      UNHARDENED_CLAY_CASTS.put(cast, REGISTRY.register(GradientIds.UNHARDENED_CLAY_CAST(cast), () -> new UnhardenedClayBlock(Block.makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d))));
    }
  }

  public static final RegistryObject<ClayFurnaceBlock> CLAY_FURNACE = REGISTRY.register(GradientIds.CLAY_FURNACE, ClayFurnaceBlock::new);
  public static final RegistryObject<ClayOvenBlock> CLAY_OVEN = REGISTRY.register(GradientIds.CLAY_OVEN, ClayOvenBlock::new);

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering blocks...");
    REGISTRY.register(bus);
  }

  private static Supplier<TorchBlock> litFibreTorch() {
    return () -> LIT_FIBRE_TORCH.get();
  }

  private static Supplier<TorchBlock> litFibreWallTorch() {
    return () -> LIT_FIBRE_WALL_TORCH.get();
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

  public static RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_CAST(final GradientCasts cast) {
    return UNHARDENED_CLAY_CASTS.get(cast);
  }
}
