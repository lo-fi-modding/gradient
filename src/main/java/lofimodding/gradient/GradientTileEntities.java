package lofimodding.gradient;

import lofimodding.gradient.tileentities.ClayCrucibleTile;
import lofimodding.gradient.tileentities.ClayMetalMixerTile;
import lofimodding.gradient.tileentities.ClayOvenTile;
import lofimodding.gradient.tileentities.DryingRackTile;
import lofimodding.gradient.tileentities.FirepitTile;
import lofimodding.gradient.tileentities.GrindstoneTile;
import lofimodding.gradient.tileentities.MixingBasinTile;
import lofimodding.gradient.tileentities.WoodenAxleTile;
import lofimodding.gradient.tileentities.WoodenGearboxTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientTileEntities {
  private GradientTileEntities() { }

  private static final DeferredRegister<TileEntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Gradient.MOD_ID);

  public static final RegistryObject<TileEntityType<FirepitTile>> FIREPIT = REGISTRY.register(GradientIds.FIREPIT, () -> TileEntityType.Builder.create(FirepitTile::new, GradientBlocks.FIREPIT.get()).build(null));
  public static final RegistryObject<TileEntityType<GrindstoneTile>> GRINDSTONE = REGISTRY.register(GradientIds.GRINDSTONE, () -> TileEntityType.Builder.create(GrindstoneTile::new, GradientBlocks.GRINDSTONE.get()).build(null));
  public static final RegistryObject<TileEntityType<MixingBasinTile>> MIXING_BASIN = REGISTRY.register(GradientIds.MIXING_BASIN, () -> TileEntityType.Builder.create(MixingBasinTile::new, GradientBlocks.MIXING_BASIN.get()).build(null));
  public static final RegistryObject<TileEntityType<DryingRackTile>> DRYING_RACK = REGISTRY.register(GradientIds.DRYING_RACK, () -> TileEntityType.Builder.create(DryingRackTile::new, GradientBlocks.DRYING_RACK.get()).build(null));
  public static final RegistryObject<TileEntityType<ClayOvenTile>> CLAY_OVEN = REGISTRY.register(GradientIds.CLAY_OVEN, () -> TileEntityType.Builder.create(ClayOvenTile::new, GradientBlocks.CLAY_OVEN.get()).build(null));
  public static final RegistryObject<TileEntityType<ClayCrucibleTile>> CLAY_CRUCIBLE = REGISTRY.register(GradientIds.CLAY_CRUCIBLE, () -> TileEntityType.Builder.create(ClayCrucibleTile::new, GradientBlocks.CLAY_CRUCIBLE.get()).build(null));
  public static final RegistryObject<TileEntityType<ClayMetalMixerTile>> CLAY_METAL_MIXER = REGISTRY.register(GradientIds.CLAY_METAL_MIXER, () -> TileEntityType.Builder.create(ClayMetalMixerTile::new, GradientBlocks.CLAY_METAL_MIXER.get()).build(null));
  public static final RegistryObject<TileEntityType<WoodenAxleTile>> WOODEN_AXLE = REGISTRY.register(GradientIds.WOODEN_AXLE, () -> TileEntityType.Builder.create(WoodenAxleTile::new, GradientBlocks.WOODEN_AXLE.get()).build(null));
  public static final RegistryObject<TileEntityType<WoodenGearboxTile>> WOODEN_GEARBOX = REGISTRY.register(GradientIds.WOODEN_GEARBOX, () -> TileEntityType.Builder.create(WoodenGearboxTile::new, GradientBlocks.WOODEN_GEARBOX.get()).build(null));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering tile entities...");
    REGISTRY.register(bus);
  }
}
