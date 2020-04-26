package lofimodding.gradient.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;

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
}
