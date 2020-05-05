package lofimodding.gradient.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public final class RenderUtils {
  private RenderUtils() { }

  public static void renderText(final String text, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight) {
    final Minecraft mc = Minecraft.getInstance();

    matrixStack.push();
    matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
    matrixStack.scale(-0.025F, -0.025F, 0.025F);

    final float opacity = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
    final int background = (int)(opacity * 255.0F) << 24;
    final FontRenderer font = mc.getRenderManager().getFontRenderer();
    final float x = -font.getStringWidth(text) / 2;

    final Matrix4f top = matrixStack.getLast().getMatrix();
    font.renderString(text, x, 0.0f, 0x20ffffff, false, top, buffer, true, background, combinedLight);
    font.renderString(text, x, 0.0f, 0xffffffff, false, top, buffer, false, 0, combinedLight);

    matrixStack.pop();
  }

  public static void renderTexture(final float x, final float y, final float z, final float width, final float height, final TextureAtlasSprite sprite, final int color, final float scaleIn, final IVertexBuilder buffer) {
    final float scale = scaleIn * 16.0f;

    final float uS = 0.0f;
    final float vS = 0.0f;
    final float spriteWidth = 1.0f;
    final float spriteHeight = 1.0f;

    final int a = color >>> 24 & 255;
    final int r = color >>> 16 & 255;
    final int g = color >>> 8 & 255;
    final int b = color & 255;

    for(float xS = x; xS <= x + width; xS += scale) {
      final float xE = Math.min(xS + scale, x + width);
      final float uE = uS + (xE - xS) / scale * spriteWidth;

      for(float yS = y; yS <= y + height; yS += scale) {
        final float yE = Math.min(yS + scale, y + height);
        final float vE = vS + (yE - yS) / scale * spriteHeight / sprite.getFrameCount();

        buffer.pos(xS, yS, z).tex(uS, vS).color(r, g, b, a).endVertex();
        buffer.pos(xS, yE, z).tex(uS, vE).color(r, g, b, a).endVertex();
        buffer.pos(xE, yE, z).tex(uE, vE).color(r, g, b, a).endVertex();
        buffer.pos(xE, yS, z).tex(uE, vS).color(r, g, b, a).endVertex();
      }
    }
  }
}
