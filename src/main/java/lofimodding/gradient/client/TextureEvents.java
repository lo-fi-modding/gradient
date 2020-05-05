package lofimodding.gradient.client;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.fluids.GradientFluid;
import lofimodding.gradient.fluids.GradientFluidStack;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TextureEvents {
  private TextureEvents() { }

  @SubscribeEvent
  public static void onTextureStitch(final TextureStitchEvent.Pre event) {
    if(event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
      for(final GradientFluid fluid : GradientFluid.REGISTRY.get().getValues()) {
        final GradientFluidStack stack = new GradientFluidStack(fluid, 1.0f, 0.0f);
        event.addSprite(stack.getStill());
      }
    }
  }
}
