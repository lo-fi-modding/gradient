package lofimodding.gradient.integrations.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.fluids.GradientFluidStack;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GradientFluidStackRenderer implements IIngredientRenderer<GradientFluidStack> {
  private static final int TEX_WIDTH = 16;
  private static final int TEX_HEIGHT = 16;
  private final float capacity;
  private final TooltipMode tooltipMode;
  private final int width;
  private final int height;
  @Nullable
  private final IDrawable overlay;

  public GradientFluidStackRenderer() {
    this(1.0f, TooltipMode.ITEM_LIST, TEX_WIDTH, TEX_HEIGHT, null);
  }

  public GradientFluidStackRenderer(final float capacity, final boolean showCapacity, final int width, final int height, @Nullable final IDrawable overlay) {
    this(capacity, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height, overlay);
  }

  public GradientFluidStackRenderer(final float capacity, final TooltipMode tooltipMode, final int width, final int height, @Nullable final IDrawable overlay) {
    this.capacity = capacity;
    this.tooltipMode = tooltipMode;
    this.width = width;
    this.height = height;
    this.overlay = overlay;
  }

  @Override
  public void render(final int xPosition, final int yPosition, @Nullable final GradientFluidStack fluidStack) {
    RenderSystem.enableBlend();
    RenderSystem.enableAlphaTest();
    this.drawFluid(xPosition, yPosition, fluidStack);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    if(this.overlay != null) {
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, 0.0F, 200.0F);
      this.overlay.draw(xPosition, yPosition);
      RenderSystem.popMatrix();
    }

    RenderSystem.disableAlphaTest();
    RenderSystem.disableBlend();
  }

  private void drawFluid(final int xPosition, final int yPosition, @Nullable final GradientFluidStack fluidStack) {
    if(fluidStack != null) {
      final TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
      final int fluidColor = fluidStack.getColour();
      final float amount = fluidStack.getAmount();
      int scaledAmount = (int)(amount * this.height / this.capacity);
      if(amount > 0 && scaledAmount < 1) {
        scaledAmount = 1;
      }

      if(scaledAmount > this.height) {
        scaledAmount = this.height;
      }

      this.drawTiledSprite(xPosition, yPosition, this.width, this.height, fluidColor, scaledAmount, fluidStillSprite);
    }
  }

  private void drawTiledSprite(final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, final int color, final int scaledAmount, final TextureAtlasSprite sprite) {
    final Minecraft minecraft = Minecraft.getInstance();
    minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    setGLColorFromInt(color);
    final int xTileCount = tiledWidth / 16;
    final int xRemainder = tiledWidth - xTileCount * 16;
    final int yTileCount = scaledAmount / 16;
    final int yRemainder = scaledAmount - yTileCount * 16;
    final int yStart = yPosition + tiledHeight;

    for(int xTile = 0; xTile <= xTileCount; ++xTile) {
      for(int yTile = 0; yTile <= yTileCount; ++yTile) {
        final int width = xTile == xTileCount ? xRemainder : 16;
        final int height = yTile == yTileCount ? yRemainder : 16;
        final int x = xPosition + xTile * 16;
        final int y = yStart - (yTile + 1) * 16;
        if(width > 0 && height > 0) {
          final int maskTop = 16 - height;
          final int maskRight = 16 - width;
          drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100.0D);
        }
      }
    }
  }

  private static TextureAtlasSprite getStillFluidSprite(final GradientFluidStack fluidStack) {
    final Minecraft minecraft = Minecraft.getInstance();
    final ResourceLocation fluidStill = fluidStack.getStillTexture();
    return minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
  }

  private static void setGLColorFromInt(final int color) {
    final float red = (color >> 16 & 255) / 255.0F;
    final float green = (color >> 8 & 255) / 255.0F;
    final float blue = (color & 255) / 255.0F;
    final float alpha = (color >> 24 & 255) / 255.0F;
    RenderSystem.color4f(red, green, blue, alpha);
  }

  private static void drawTextureWithMasking(final double xCoord, final double yCoord, final TextureAtlasSprite textureSprite, final int maskTop, final int maskRight, final double zLevel) {
    final double uMin = textureSprite.getMinU();
    double uMax = textureSprite.getMaxU();
    final double vMin = textureSprite.getMinV();
    double vMax = textureSprite.getMaxV();
    uMax -= maskRight / 16.0D * (uMax - uMin);
    vMax -= maskTop / 16.0D * (vMax - vMin);
    final Tessellator tessellator = Tessellator.getInstance();
    final BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(xCoord, yCoord + 16.0D, zLevel).tex((float)uMin, (float)vMax).endVertex();
    bufferBuilder.pos(xCoord + 16.0D - maskRight, yCoord + 16.0D, zLevel).tex((float)uMax, (float)vMax).endVertex();
    bufferBuilder.pos(xCoord + 16.0D - maskRight, yCoord + maskTop, zLevel).tex((float)uMax, (float)vMin).endVertex();
    bufferBuilder.pos(xCoord, yCoord + maskTop, zLevel).tex((float)uMin, (float)vMin).endVertex();
    tessellator.draw();
  }

  @Override
  public List<String> getTooltip(final GradientFluidStack fluidStack, final ITooltipFlag tooltipFlag) {
    final List<String> tooltip = new ArrayList<>();
    final ITextComponent displayName = fluidStack.getName();
    final String displayNameFormatted = displayName.getFormattedText();
    tooltip.add(displayNameFormatted);
    final float amount = fluidStack.getAmount();
    final String amountString;
    if(this.tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
      amountString = I18n.format("jei.gradient_fluid.amount_with_capacity", amount, this.capacity);
      tooltip.add(TextFormatting.GRAY + amountString);
    } else if(this.tooltipMode == TooltipMode.SHOW_AMOUNT) {
      amountString = I18n.format("jei.gradient_fluid.amount", amount);
      tooltip.add(TextFormatting.GRAY + amountString);
    }

    return tooltip;
  }

  enum TooltipMode {
    SHOW_AMOUNT, SHOW_AMOUNT_AND_CAPACITY, ITEM_LIST
  }
}
