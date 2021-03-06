package lofimodding.gradient.client;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.GradientEntities;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.blocks.OreBlock;
import lofimodding.gradient.client.screens.ClayCrucibleScreen;
import lofimodding.gradient.client.screens.CreativeGeneratorScreen;
import lofimodding.gradient.client.screens.CreativeSinkerScreen;
import lofimodding.gradient.client.screens.ToolStationScreen;
import lofimodding.gradient.client.screens.WoodenHopperScreen;
import lofimodding.gradient.client.tesr.ClayCrucibleRenderer;
import lofimodding.gradient.client.tesr.ClayMetalMixerRenderer;
import lofimodding.gradient.client.tesr.ClayOvenRenderer;
import lofimodding.gradient.client.tesr.DryingRackRenderer;
import lofimodding.gradient.client.tesr.FirepitRenderer;
import lofimodding.gradient.client.tesr.GrindstoneRenderer;
import lofimodding.gradient.client.tesr.MechanicalGrindstoneRenderer;
import lofimodding.gradient.client.tesr.MechanicalMixingBasinRenderer;
import lofimodding.gradient.client.tesr.MixingBasinRenderer;
import lofimodding.gradient.client.tesr.WoodenAxleRenderer;
import lofimodding.gradient.client.tesr.WoodenConveyorBeltRenderer;
import lofimodding.gradient.client.tesr.WoodenCrankRenderer;
import lofimodding.gradient.items.MetalHammerItem;
import lofimodding.gradient.items.MetalItem;
import lofimodding.gradient.items.MetalMattockItem;
import lofimodding.gradient.items.MetalPickaxeItem;
import lofimodding.gradient.items.MetalSwordItem;
import lofimodding.gradient.items.OreItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Minerals;
import lofimodding.gradient.science.Ore;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GradientClient {
  private GradientClient() { }

  public static void clientSetup(final FMLClientSetupEvent event) {
    // By default, layered item models can only have 5 layers. We need 7.
    for(int i = 0; i <= 7; i++) {
      if(!ItemModelGenerator.LAYERS.contains("layer" + i)) {
        ItemModelGenerator.LAYERS.add("layer" + i);
      }
    }

    final RenderType cutoutMipped = RenderType.getCutoutMipped();

    for(final Ore ore : Minerals.ores()) {
      RenderTypeLookup.setRenderLayer(GradientBlocks.ORE(ore).get(), cutoutMipped);
    }

    for(final GradientCasts cast : GradientCasts.values()) {
      RenderTypeLookup.setRenderLayer(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get(), cutoutMipped);
      RenderTypeLookup.setRenderLayer(GradientBlocks.CLAY_CAST(cast).get(), cutoutMipped);
    }

    RenderTypeLookup.setRenderLayer(GradientBlocks.UNLIT_FIBRE_TORCH.get(), cutoutMipped);
    RenderTypeLookup.setRenderLayer(GradientBlocks.UNLIT_FIBRE_WALL_TORCH.get(), cutoutMipped);
    RenderTypeLookup.setRenderLayer(GradientBlocks.LIT_FIBRE_TORCH.get(), cutoutMipped);
    RenderTypeLookup.setRenderLayer(GradientBlocks.LIT_FIBRE_WALL_TORCH.get(), cutoutMipped);
    RenderTypeLookup.setRenderLayer(GradientBlocks.UNLIT_TORCH_STAND_TORCH.get(), cutoutMipped);
    RenderTypeLookup.setRenderLayer(GradientBlocks.LIT_TORCH_STAND_TORCH.get(), cutoutMipped);

    final RenderType translucent = RenderType.getTranslucent();

    RenderTypeLookup.setRenderLayer(GradientBlocks.MIXING_BASIN.get(), translucent);

    final Minecraft mc = event.getMinecraftSupplier().get();
    final BlockColors blockColors = mc.getBlockColors();
    final ItemColors itemColors = mc.getItemColors();

    for(final Ore ore : Minerals.ores()) {
      blockColors.register(GradientClient::oreBlockColour, GradientBlocks.ORE(ore).get());
      itemColors.register(GradientClient::oreBlockColour, GradientItems.ORE(ore).get());
      itemColors.register(GradientClient::oreItemColour, GradientItems.CRUSHED(ore).get());
      itemColors.register(GradientClient::oreItemColour, GradientItems.PURIFIED(ore).get());
    }

    for(final Metal metal : Minerals.metals()) {
      itemColors.register(GradientClient::metalItemColour, GradientItems.DUST(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.NUGGET(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.PLATE(metal).get());
      itemColors.register(GradientClient::mattockItemColour, GradientItems.METAL_MATTOCKS.get(metal).get());
      itemColors.register(GradientClient::pickaxeItemColour, GradientItems.METAL_PICKAXES.get(metal).get());
      itemColors.register(GradientClient::swordItemColour, GradientItems.METAL_SWORDS.get(metal).get());
      itemColors.register(GradientClient::hammerItemColour, GradientItems.METAL_HAMMERS.get(metal).get());
      blockColors.register(GradientClient::metalBlockColour, GradientBlocks.METAL_BLOCK(metal).get());
      itemColors.register(GradientClient::metalBlockColour, GradientItems.METAL_BLOCK(metal).get());

      for(final GradientCasts cast : GradientCasts.values()) {
        itemColors.register(GradientClient::metalItemColour, cast.getItem(metal));
      }
    }

    blockColors.register(GradientClient::waterColour, GradientBlocks.MIXING_BASIN.get());
    blockColors.register(GradientClient::waterColour, GradientBlocks.MECHANICAL_MIXING_BASIN.get());

    RenderingRegistry.registerEntityRenderingHandler(GradientEntities.PEBBLE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));

    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.FIREPIT.get(), FirepitRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.GRINDSTONE.get(), GrindstoneRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.MIXING_BASIN.get(), MixingBasinRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.DRYING_RACK.get(), DryingRackRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.CLAY_OVEN.get(), ClayOvenRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.CLAY_CRUCIBLE.get(), ClayCrucibleRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.CLAY_METAL_MIXER.get(), ClayMetalMixerRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.WOODEN_AXLE.get(), WoodenAxleRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.WOODEN_CONVEYOR_BELT.get(), WoodenConveyorBeltRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.WOODEN_CRANK.get(), WoodenCrankRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.MECHANICAL_GRINDSTONE.get(), MechanicalGrindstoneRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.MECHANICAL_MIXING_BASIN.get(), MechanicalMixingBasinRenderer::new);

    ScreenManager.registerFactory(GradientContainers.CLAY_CRUCIBLE.get(), ClayCrucibleScreen::new);
    ScreenManager.registerFactory(GradientContainers.WOODEN_HOPPER.get(), WoodenHopperScreen::new);
    ScreenManager.registerFactory(GradientContainers.TOOL_STATION.get(), ToolStationScreen::new);
    ScreenManager.registerFactory(GradientContainers.CREATIVE_GENERATOR.get(), CreativeGeneratorScreen::new);
    ScreenManager.registerFactory(GradientContainers.CREATIVE_SINKER.get(), CreativeSinkerScreen::new);
  }

  @SubscribeEvent
  public static void onModelRegister(final ModelRegistryEvent event) {
    Gradient.LOGGER.info("Registering extra models...");
    ModelLoader.addSpecialModel(Gradient.loc("block/" + GradientIds.GRINDSTONE + "_wheel"));
    ModelLoader.addSpecialModel(Gradient.loc("block/" + GradientIds.MIXING_BASIN + "_rod"));
    ModelLoader.addSpecialModel(Gradient.loc("block/" + GradientIds.CLAY_METAL_MIXER + "_auger"));
    ModelLoader.addSpecialModel(Gradient.loc("block/" + GradientIds.WOODEN_AXLE));
    ModelLoader.addSpecialModel(Gradient.loc("block/" + GradientIds.MECHANICAL_MIXING_BASIN + "_rod"));
  }

  private static int metalBlockColour(final BlockState state, final ILightReader world, final BlockPos pos, final int tintIndex) {
    final Metal metal = ((MetalBlock)state.getBlock()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int metalBlockColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalBlock)((BlockItem)stack.getItem()).getBlock()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int metalItemColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalItem)stack.getItem()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int mattockItemColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalMattockItem)stack.getItem()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int pickaxeItemColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalPickaxeItem)stack.getItem()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int swordItemColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalSwordItem)stack.getItem()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int hammerItemColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalHammerItem)stack.getItem()).metal;
    return getMetalColour(metal, tintIndex);
  }

  private static int getMetalColour(final Metal metal, final int tintIndex) {
    switch(tintIndex) {
      case 1:
        return metal.colourDiffuse;
      case 2:
        return metal.colourSpecular;
      case 3:
        return metal.colourShadow1;
      case 4:
        return metal.colourShadow2;
      case 5:
        return metal.colourEdge1;
      case 6:
        return metal.colourEdge2;
      case 7:
        return metal.colourEdge3;
    }

    return 0xffffffff;
  }

  private static int oreBlockColour(final BlockState state, final ILightReader world, final BlockPos pos, final int tintIndex) {
    final Ore ore = ((OreBlock)state.getBlock()).ore;
    return getOreColour(ore, tintIndex);
  }

  private static int oreBlockColour(final ItemStack stack, final int tintIndex) {
    final Ore ore = ((OreBlock)((BlockItem)stack.getItem()).getBlock()).ore;
    return getOreColour(ore, tintIndex);
  }

  private static int oreItemColour(final ItemStack stack, final int tintIndex) {
    final Ore ore = ((OreItem)stack.getItem()).ore;
    return getOreColour(ore, tintIndex);
  }

  private static int getOreColour(final Ore ore, final int tintIndex) {
    switch(tintIndex) {
      case 1:
        return ore.colourDiffuse;
      case 2:
        return ore.colourSpecular;
      case 3:
        return ore.colourShadow1;
      case 4:
        return ore.colourShadow2;
      case 5:
        return ore.colourEdge1;
      case 6:
        return ore.colourEdge2;
      case 7:
        return ore.colourEdge3;
    }

    return 0xffffffff;
  }

  private static int waterColour(final BlockState state, final ILightReader world, final BlockPos pos, final int tintIndex) {
    if(world == null || pos == null) {
      return 0xffffffff;
    }

    return BiomeColors.getWaterColor(world, pos);
  }
}
