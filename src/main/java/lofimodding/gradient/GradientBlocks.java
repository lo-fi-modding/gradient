package lofimodding.gradient;

import lofimodding.gradient.blocks.ClayCastBlock;
import lofimodding.gradient.blocks.ClayCrucibleBlock;
import lofimodding.gradient.blocks.ClayFurnaceBlock;
import lofimodding.gradient.blocks.ClayMetalMixerBlock;
import lofimodding.gradient.blocks.ClayOvenBlock;
import lofimodding.gradient.blocks.CreativeGeneratorBlock;
import lofimodding.gradient.blocks.CreativeSinkerBlock;
import lofimodding.gradient.blocks.DryingRackBlock;
import lofimodding.gradient.blocks.FirepitBlock;
import lofimodding.gradient.blocks.GrindstoneBlock;
import lofimodding.gradient.blocks.LitFibreTorchBlock;
import lofimodding.gradient.blocks.LitFibreWallTorchBlock;
import lofimodding.gradient.blocks.MechanicalGrindstoneBlock;
import lofimodding.gradient.blocks.MechanicalMixingBasinBlock;
import lofimodding.gradient.blocks.MechanicalPumpBlock;
import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.blocks.OreBlock;
import lofimodding.gradient.blocks.PebbleBlock;
import lofimodding.gradient.blocks.TorchStandBlock;
import lofimodding.gradient.blocks.UnhardenedClayBlock;
import lofimodding.gradient.blocks.UnlitFibreTorchBlock;
import lofimodding.gradient.blocks.UnlitFibreWallTorchBlock;
import lofimodding.gradient.blocks.WoodenAxleBlock;
import lofimodding.gradient.blocks.WoodenConveyorBeltBlock;
import lofimodding.gradient.blocks.WoodenConveyorBeltDriverBlock;
import lofimodding.gradient.blocks.WoodenCrankBlock;
import lofimodding.gradient.blocks.WoodenGearboxBlock;
import lofimodding.gradient.blocks.WoodenHopperBlock;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Minerals;
import lofimodding.gradient.science.Ore;
import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.SlabBlock;
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

  public static final RegistryObject<PebbleBlock> PEBBLE = REGISTRY.register(GradientIds.PEBBLE, () -> new PebbleBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.0f).doesNotBlockMovement()));

  private static final Map<Ore, RegistryObject<PebbleBlock>> PEBBLES = new HashMap<>();

  static {
    for(final Ore ore : Minerals.ores()) {
      PEBBLES.put(ore, REGISTRY.register(GradientIds.PEBBLE(ore), () -> new PebbleBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(1.0f))));
    }
  }

  private static final Map<Ore, RegistryObject<OreBlock>> ORES = new HashMap<>();

  static {
    for(final Ore ore : Minerals.ores()) {
      ORES.put(ore, REGISTRY.register(GradientIds.ORE(ore), () -> new OreBlock(ore, Block.Properties.create(Material.ROCK).hardnessAndResistance(ore.hardness).harvestTool(ToolType.PICKAXE).harvestLevel(ore.harvestLevel))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalBlock>> METAL_BLOCKS = new HashMap<>();

  static {
    for(final Metal metal : Minerals.metals()) {
      METAL_BLOCKS.put(metal, REGISTRY.register(GradientIds.METAL_BLOCK(metal), () -> new MetalBlock(metal, Block.Properties.create(Material.ROCK).sound(SoundType.METAL).hardnessAndResistance(metal.hardness).harvestTool(ToolType.PICKAXE).harvestLevel(metal.harvestLevel))));
    }
  }

  public static final RegistryObject<Block> SALT_BLOCK = REGISTRY.register(GradientIds.SALT_BLOCK, () -> new Block(Block.Properties.create(Material.SAND, MaterialColor.QUARTZ).sound(SoundType.SAND).hardnessAndResistance(0.5f)));

  public static final RegistryObject<LogBlock> HARDENED_LOG = REGISTRY.register(GradientIds.HARDENED_LOG, () -> new LogBlock(MaterialColor.WOOD, Block.Properties.create(Material.WOOD).hardnessAndResistance(3.0f).sound(SoundType.WOOD).harvestTool(ToolType.AXE).harvestLevel(1)));
  public static final RegistryObject<Block> HARDENED_PLANKS = REGISTRY.register(GradientIds.HARDENED_PLANKS, () -> new Block(Block.Properties.create(Material.WOOD).hardnessAndResistance(3.0f).sound(SoundType.WOOD).harvestTool(ToolType.AXE).harvestLevel(1)));
  public static final RegistryObject<SlabBlock> HARDENED_LOG_SLAB = REGISTRY.register(GradientIds.HARDENED_LOG_SLAB, () -> new SlabBlock(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(3.0F).sound(SoundType.WOOD).harvestTool(ToolType.AXE).harvestLevel(1)));
  public static final RegistryObject<SlabBlock> HARDENED_PLANKS_SLAB = REGISTRY.register(GradientIds.HARDENED_PLANKS_SLAB, () -> new SlabBlock(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(3.0F).sound(SoundType.WOOD).harvestTool(ToolType.AXE).harvestLevel(1)));

  public static final RegistryObject<FirepitBlock> FIREPIT = REGISTRY.register(GradientIds.FIREPIT, FirepitBlock::new);
  public static final RegistryObject<UnlitFibreTorchBlock> UNLIT_FIBRE_TORCH = REGISTRY.register(GradientIds.UNLIT_FIBRE_TORCH, () -> new UnlitFibreTorchBlock(litFibreTorch(), Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).sound(SoundType.WOOD)));
  public static final RegistryObject<UnlitFibreWallTorchBlock> UNLIT_FIBRE_WALL_TORCH = REGISTRY.register(GradientIds.UNLIT_FIBRE_WALL_TORCH, () -> new UnlitFibreWallTorchBlock(litFibreWallTorch(), Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).sound(SoundType.WOOD)));
  public static final RegistryObject<LitFibreTorchBlock> LIT_FIBRE_TORCH = REGISTRY.register(GradientIds.LIT_FIBRE_TORCH, () -> new LitFibreTorchBlock(UNLIT_FIBRE_TORCH::get, Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).lightValue(10).sound(SoundType.WOOD)));
  public static final RegistryObject<LitFibreWallTorchBlock> LIT_FIBRE_WALL_TORCH = REGISTRY.register(GradientIds.LIT_FIBRE_WALL_TORCH, () -> new LitFibreWallTorchBlock(UNLIT_FIBRE_WALL_TORCH::get, Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).lightValue(10).sound(SoundType.WOOD)));
  public static final RegistryObject<TorchStandBlock> TORCH_STAND = REGISTRY.register(GradientIds.TORCH_STAND, TorchStandBlock::new);
  public static final RegistryObject<UnlitFibreTorchBlock> UNLIT_TORCH_STAND_TORCH = REGISTRY.register(GradientIds.UNLIT_TORCH_STAND_TORCH, () -> new UnlitFibreTorchBlock(litTorchStandTorch(), Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).sound(SoundType.WOOD)));
  public static final RegistryObject<LitFibreTorchBlock> LIT_TORCH_STAND_TORCH = REGISTRY.register(GradientIds.LIT_TORCH_STAND_TORCH, () -> new LitFibreTorchBlock(UNLIT_TORCH_STAND_TORCH::get, Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0f).lightValue(14).sound(SoundType.WOOD)));

  public static final RegistryObject<GrindstoneBlock> GRINDSTONE = REGISTRY.register(GradientIds.GRINDSTONE, GrindstoneBlock::new);
  public static final RegistryObject<MixingBasinBlock> MIXING_BASIN = REGISTRY.register(GradientIds.MIXING_BASIN, MixingBasinBlock::new);
  public static final RegistryObject<DryingRackBlock> DRYING_RACK = REGISTRY.register(GradientIds.DRYING_RACK, DryingRackBlock::new);

  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_FURNACE = REGISTRY.register(GradientIds.UNHARDENED_CLAY_FURNACE, () -> new UnhardenedClayBlock(VoxelShapes.fullCube()));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_CRUCIBLE = REGISTRY.register(GradientIds.UNHARDENED_CLAY_CRUCIBLE, () -> new UnhardenedClayBlock(Block.makeCuboidShape(1.0d, 0.0d, 1.0d, 15.0d, 12.0d, 15.0d)));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_OVEN = REGISTRY.register(GradientIds.UNHARDENED_CLAY_OVEN, () -> new UnhardenedClayBlock(Block.makeCuboidShape(2.0d, 0.0d, 2.0d, 14.0d, 6.0d, 14.0d)));
  public static final RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_METAL_MIXER = REGISTRY.register(GradientIds.UNHARDENED_CLAY_METAL_MIXER, () -> new UnhardenedClayBlock(Block.makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d)));
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
  public static final RegistryObject<ClayCrucibleBlock> CLAY_CRUCIBLE = REGISTRY.register(GradientIds.CLAY_CRUCIBLE, ClayCrucibleBlock::new);
  public static final RegistryObject<ClayMetalMixerBlock> CLAY_METAL_MIXER = REGISTRY.register(GradientIds.CLAY_METAL_MIXER, ClayMetalMixerBlock::new);
  private static final Map<GradientCasts, RegistryObject<ClayCastBlock>> CLAY_CASTS = new EnumMap<>(GradientCasts.class);

  static {
    for(final GradientCasts cast : GradientCasts.values()) {
      CLAY_CASTS.put(cast, REGISTRY.register(GradientIds.CLAY_CAST(cast), () -> new ClayCastBlock(cast)));
    }
  }

  public static final RegistryObject<WoodenAxleBlock> WOODEN_AXLE = REGISTRY.register(GradientIds.WOODEN_AXLE, WoodenAxleBlock::new);
  public static final RegistryObject<WoodenGearboxBlock> WOODEN_GEARBOX = REGISTRY.register(GradientIds.WOODEN_GEARBOX, WoodenGearboxBlock::new);
  public static final RegistryObject<WoodenConveyorBeltBlock> WOODEN_CONVEYOR_BELT = REGISTRY.register(GradientIds.WOODEN_CONVEYOR_BELT, WoodenConveyorBeltBlock::new);
  public static final RegistryObject<WoodenConveyorBeltDriverBlock> WOODEN_CONVEYOR_BELT_DRIVER = REGISTRY.register(GradientIds.WOODEN_CONVEYOR_BELT_DRIVER, WoodenConveyorBeltDriverBlock::new);
  public static final RegistryObject<WoodenHopperBlock> WOODEN_HOPPER = REGISTRY.register(GradientIds.WOODEN_HOPPER, WoodenHopperBlock::new);
  public static final RegistryObject<WoodenCrankBlock> WOODEN_CRANK = REGISTRY.register(GradientIds.WOODEN_CRANK, WoodenCrankBlock::new);
  public static final RegistryObject<MechanicalGrindstoneBlock> MECHANICAL_GRINDSTONE = REGISTRY.register(GradientIds.MECHANICAL_GRINDSTONE, MechanicalGrindstoneBlock::new);
  public static final RegistryObject<MechanicalMixingBasinBlock> MECHANICAL_MIXING_BASIN = REGISTRY.register(GradientIds.MECHANICAL_MIXING_BASIN, MechanicalMixingBasinBlock::new);
  public static final RegistryObject<MechanicalPumpBlock> MECHANICAL_PUMP = REGISTRY.register(GradientIds.MECHANICAL_PUMP, MechanicalPumpBlock::new);

  public static final RegistryObject<CreativeGeneratorBlock> CREATIVE_GENERATOR = REGISTRY.register(GradientIds.CREATIVE_GENERATOR, CreativeGeneratorBlock::new);
  public static final RegistryObject<CreativeSinkerBlock> CREATIVE_SINKER = REGISTRY.register(GradientIds.CREATIVE_SINKER, CreativeSinkerBlock::new);

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

  private static Supplier<TorchBlock> litTorchStandTorch() {
    return () -> LIT_TORCH_STAND_TORCH.get();
  }

  public static RegistryObject<PebbleBlock> PEBBLE(final Ore ore) {
    return PEBBLES.get(ore);
  }

  public static RegistryObject<OreBlock> ORE(final Ore ore) {
    return ORES.get(ore);
  }

  public static RegistryObject<MetalBlock> METAL_BLOCK(final Metal metal) {
    return METAL_BLOCKS.get(metal);
  }

  public static RegistryObject<UnhardenedClayBlock> UNHARDENED_CLAY_CAST(final GradientCasts cast) {
    return UNHARDENED_CLAY_CASTS.get(cast);
  }

  public static RegistryObject<ClayCastBlock> CLAY_CAST(final GradientCasts cast) {
    return CLAY_CASTS.get(cast);
  }
}
