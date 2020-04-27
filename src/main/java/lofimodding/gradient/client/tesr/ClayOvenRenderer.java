package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.blocks.ClayOvenBlock;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.tileentities.ClayOvenTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class ClayOvenRenderer extends TileEntityRenderer<ClayOvenTile> {
  public ClayOvenRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final ClayOvenTile oven, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);

    final Direction facing = oven.getBlockState().get(ClayOvenBlock.FACING);

    if(oven.hasInput()) {
      final ItemStack input = oven.getInput();

      matrixStack.push();

      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      matrixStack.translate(0.0d, -0.40625d, 0.25d);

      matrixStack.push();
      matrixStack.scale(0.5f, 0.5f, 0.5f);
      Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();

      if(oven.isCooking()) {
        matrixStack.translate(-0.1f, 0.0f, 0.05f);
        matrixStack.scale(0.2f * (1.0f - oven.getCookingPercent()), 0.025f, 1.0f);
        //TODO
//        GlStateManager.disableCull();
//        this.setLightmapDisabled(true);
//        GlStateManager.disableLighting();
//        Gui.drawRect(0, 0, 1, 1, 0xFF1AFF00);
//        GlStateManager.enableLighting();
//        this.setLightmapDisabled(false);
//        GlStateManager.enableCull();
      }

      matrixStack.pop();
    }

    if(oven.hasOutput()) {
      final ItemStack output = oven.getOutput();

      matrixStack.push();

      if(output.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(Integer.toString(output.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.translate(0.0d, -0.0625d, 0.0d);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      matrixStack.scale(0.5f, 0.5f, 0.5f);
      Minecraft.getInstance().getItemRenderer().renderItem(output, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

      matrixStack.pop();
    }

    matrixStack.pop();
  }
}
