package lofimodding.gradient.client;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientEntities;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.client.tesr.GrindstoneRenderer;
import lofimodding.gradient.items.MetalItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

    for(final Ore ore : Ores.all()) {
      RenderTypeLookup.setRenderLayer(GradientBlocks.ORE(ore).get(), cutoutMipped);
    }

    for(final GradientCasts cast : GradientCasts.values()) {
      RenderTypeLookup.setRenderLayer(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get(), cutoutMipped);
    }

    final Minecraft mc = event.getMinecraftSupplier().get();
    final BlockColors blockColors = mc.getBlockColors();
    final ItemColors itemColors = mc.getItemColors();

    for(final Ore ore : Ores.all()) {
      blockColors.register(GradientClient::metalBlockColour, GradientBlocks.ORE(ore).get());
      itemColors.register(GradientClient::metalBlockColour, GradientItems.ORE(ore).get());
    }

    for(final Metal metal : Metals.all()) {
      itemColors.register(GradientClient::metalItemColour, GradientItems.CRUSHED(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.PURIFIED(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.INGOT(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.DUST(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.NUGGET(metal).get());
      itemColors.register(GradientClient::metalItemColour, GradientItems.PLATE(metal).get());
      blockColors.register(GradientClient::metalBlockColour, GradientBlocks.METAL_BLOCK(metal).get());
      itemColors.register(GradientClient::metalBlockColour, GradientItems.METAL_BLOCK(metal).get());
    }

    RenderingRegistry.registerEntityRenderingHandler(GradientEntities.PEBBLE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));

    ClientRegistry.bindTileEntityRenderer(GradientTileEntities.GRINDSTONE.get(), GrindstoneRenderer::new);
  }

  @SubscribeEvent
  public static void onModelRegister(final ModelRegistryEvent event) {
    Gradient.LOGGER.info("Registering extra models...");
    ModelLoader.addSpecialModel(Gradient.loc("block/grindstone_wheel"));
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
}
