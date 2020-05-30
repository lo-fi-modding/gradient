package lofimodding.gradient;

import lofimodding.gradient.containers.ClayCrucibleContainer;
import lofimodding.gradient.containers.WoodenHopperContainer;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import lofimodding.gradient.tileentities.WoodenHopperTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientContainers {
  private GradientContainers() { }

  private static final DeferredRegister<ContainerType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Gradient.MOD_ID);

  public static final RegistryObject<ContainerType<ClayCrucibleContainer>> CLAY_CRUCIBLE = REGISTRY.register(GradientIds.CLAY_CRUCIBLE, () -> new ContainerType<>((IContainerFactory<ClayCrucibleContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new ClayCrucibleContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), ClayCrucibleTile.class)))));
  public static final RegistryObject<ContainerType<WoodenHopperContainer>> WOODEN_HOPPER = REGISTRY.register(GradientIds.WOODEN_HOPPER, () -> new ContainerType<>((IContainerFactory<WoodenHopperContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new WoodenHopperContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), WoodenHopperTile.class)))));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering containers...");
    REGISTRY.register(bus);
  }
}
