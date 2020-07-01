package lofimodding.gradient;

import lofimodding.gradient.containers.ClayCrucibleContainer;
import lofimodding.gradient.containers.CreativeGeneratorContainer;
import lofimodding.gradient.containers.CreativeSinkerContainer;
import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.containers.WoodenHopperContainer;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import lofimodding.gradient.tileentities.CreativeGeneratorTile;
import lofimodding.gradient.tileentities.CreativeSinkerTile;
import lofimodding.gradient.tileentities.ToolStationTile;
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

  private static final DeferredRegister<ContainerType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, Gradient.MOD_ID);

  public static final RegistryObject<ContainerType<ClayCrucibleContainer>> CLAY_CRUCIBLE = REGISTRY.register(GradientIds.CLAY_CRUCIBLE, () -> new ContainerType<>((IContainerFactory<ClayCrucibleContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new ClayCrucibleContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), ClayCrucibleTile.class)))));
  public static final RegistryObject<ContainerType<WoodenHopperContainer>> WOODEN_HOPPER = REGISTRY.register(GradientIds.WOODEN_HOPPER, () -> new ContainerType<>((IContainerFactory<WoodenHopperContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new WoodenHopperContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), WoodenHopperTile.class)))));
  public static final RegistryObject<ContainerType<ToolStationContainer>> TOOL_STATION = REGISTRY.register(GradientIds.TOOL_STATION, () -> new ContainerType<>((IContainerFactory<ToolStationContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new ToolStationContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), ToolStationTile.class)))));

  public static final RegistryObject<ContainerType<CreativeGeneratorContainer>> CREATIVE_GENERATOR = REGISTRY.register(GradientIds.CREATIVE_GENERATOR, () -> new ContainerType<>((IContainerFactory<CreativeGeneratorContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new CreativeGeneratorContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), CreativeGeneratorTile.class)))));
  public static final RegistryObject<ContainerType<CreativeSinkerContainer>> CREATIVE_SINKER = REGISTRY.register(GradientIds.CREATIVE_SINKER, () -> new ContainerType<>((IContainerFactory<CreativeSinkerContainer>)(windowId, inv, data) -> DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new CreativeSinkerContainer(windowId, inv, WorldUtils.getTileEntity(Minecraft.getInstance().world, data.readBlockPos(), CreativeSinkerTile.class)))));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering containers...");
    REGISTRY.register(bus);
  }
}
