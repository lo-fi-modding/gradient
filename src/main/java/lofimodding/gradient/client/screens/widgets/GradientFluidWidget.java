package lofimodding.gradient.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.GradientFluidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GradientFluidWidget extends Widget {
  private static final ResourceLocation WIDGETS = Gradient.loc("textures/gui/widgets.png");

  private static final DecimalFormat FLUID_FORMAT = new DecimalFormat("#.###");
  private static final DecimalFormat TEMPERATURE_FORMAT = new DecimalFormat("#.#");

  private final Screen screen;
  private final GradientFluidTank tank;

  public GradientFluidWidget(final Screen screen, final GradientFluidTank tank, final int x, final int y, final int width, final int height) {
    super(x, y, width, height, "");
    this.screen = screen;
    this.tank = tank;
  }

  @Override
  public void renderButton(final int mouseX, final int mouseY, final float partialTicks) {
    final Minecraft mc = Minecraft.getInstance();

    final GradientFluidStack stack = this.tank.getFluidStack();

    if(!stack.isEmpty()) {
      final ResourceLocation textureLoc = stack.getStillTexture();
      final TextureAtlasSprite sprite = mc.getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(textureLoc);
      final float renderHeight = this.height * MathHelper.clamp(this.tank.getFluidAmount() / this.tank.getCapacity(), 0.0f, 1.0f);
      final int color = stack.getColour();

      mc.textureManager.bindTexture(new ResourceLocation(textureLoc.getNamespace(), "textures/" + textureLoc.getPath() + ".png"));
      final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      RenderUtils.renderTexture(this.x, (this.y + this.height) - renderHeight, this.getBlitOffset(), this.width, renderHeight, sprite, color, 1.0f, buffer);
      buffer.finishDrawing();
      RenderSystem.enableAlphaTest();
      WorldVertexBufferUploader.draw(buffer);
    }

    mc.getTextureManager().bindTexture(WIDGETS);
    this.blit(this.x, this.y, 32, 0, 12, 47);
  }

  @Override
  public void renderToolTip(final int mouseX, final int mouseY) {
    super.renderToolTip(mouseX, mouseY);

    final List<String> tooltip = new ArrayList<>();
    tooltip.add(this.tank.getFluidStack().getName().getFormattedText());
    tooltip.add(I18n.format("meltable.display", FLUID_FORMAT.format(this.tank.getFluidAmount()), FLUID_FORMAT.format(this.tank.getCapacity()), TEMPERATURE_FORMAT.format(this.tank.getFluidStack().getTemperature())));

    this.screen.renderTooltip(tooltip, mouseX, mouseY);
  }
}
