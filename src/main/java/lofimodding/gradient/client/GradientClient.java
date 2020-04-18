package lofimodding.gradient.client;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class GradientClient {
  private GradientClient() { }

  public static void clientSetup(final FMLClientSetupEvent event) {
    final RenderType cutoutMipped = RenderType.getCutoutMipped();

    for(final Ore ore : Ores.all()) {
      RenderTypeLookup.setRenderLayer(GradientBlocks.ORE(ore).get(), cutoutMipped);
    }

    final Minecraft mc = event.getMinecraftSupplier().get();
    final BlockColors blockColors = mc.getBlockColors();
    final ItemColors itemColors = mc.getItemColors();

    for(final Ore ore : Ores.all()) {
      blockColors.register(GradientClient::oreColour, GradientBlocks.ORE(ore).get());
      itemColors.register(GradientClient::oreColour, GradientItems.ORE(ore).get());
    }
  }

  private static int oreColour(final BlockState state, final ILightReader world, final BlockPos pos, final int tintIndex) {
    final Metal metal = ((MetalBlock)state.getBlock()).metal;

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

  private static int oreColour(final ItemStack stack, final int tintIndex) {
    final Metal metal = ((MetalBlock)((BlockItem)stack.getItem()).getBlock()).metal;

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
