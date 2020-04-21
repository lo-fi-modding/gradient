package lofimodding.gradient;

import lofimodding.gradient.tileentities.GrindstoneTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientTileEntities {
  private GradientTileEntities() { }

  private static final DeferredRegister<TileEntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Gradient.MOD_ID);

  public static final RegistryObject<TileEntityType<GrindstoneTile>> GRINDSTONE = REGISTRY.register(GradientIds.GRINDSTONE, () -> TileEntityType.Builder.create(GrindstoneTile::new, GradientBlocks.GRINDSTONE.get()).build(null));
}
