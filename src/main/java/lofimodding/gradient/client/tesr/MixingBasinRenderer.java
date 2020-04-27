package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.tileentities.MixingBasinTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class MixingBasinRenderer extends TileEntityRenderer<MixingBasinTile> {
  public MixingBasinRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final MixingBasinTile te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);

    final Direction facing = te.getBlockState().get(MixingBasinBlock.FACING);
    final double facingAngle = Math.toRadians(facing.getHorizontalAngle());

    for(int slot = 0; slot < MixingBasinTile.INPUT_SIZE; slot++) {
      if(te.hasInput(slot)) {
        final ItemStack input = te.getInput(slot);

        matrixStack.push();

        final double angle = (6 - slot) * Math.PI / 4 + facingAngle;
        final float inputX = (float)Math.cos(angle) * 0.2f;
        final float inputZ = (float)Math.sin(angle) * 0.2f;

        matrixStack.translate(inputX, -0.15f, inputZ);
        matrixStack.rotate(Vector3f .YP.rotationDegrees(-facing.getHorizontalAngle()));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

        matrixStack.pop();
      }
    }

    if(te.hasOutput()) {
      final ItemStack output = te.getOutput();

      matrixStack.push();

      final float inputX = (float)Math.cos(facingAngle) * 0.2f;
      final float inputZ = (float)Math.sin(facingAngle) * 0.2f;

      matrixStack.translate(inputX, -0.15f, inputZ);

      if(output.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(Integer.toString(output.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      matrixStack.scale(0.5f, 0.5f, 0.5f);
      Minecraft.getInstance().getItemRenderer().renderItem(output, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

      matrixStack.pop();
    }

    matrixStack.pop();
  }
}
