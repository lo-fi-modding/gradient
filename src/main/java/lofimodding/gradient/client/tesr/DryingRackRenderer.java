package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.blocks.DryingRackBlock;
import lofimodding.gradient.tileentities.DryingRackTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class DryingRackRenderer extends TileEntityRenderer<DryingRackTile> {
  public DryingRackRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final DryingRackTile rack, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final BlockState state = rack.getWorld().getBlockState(rack.getPos());

    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);

    final Direction facing = state.get(DryingRackBlock.FACING);
    final boolean roof = state.get(DryingRackBlock.ROOF);

    final float facingAngle = facing.getHorizontalAngle();

    if(rack.hasItem()) {
      final ItemStack input = rack.getItem();

      matrixStack.push();

      if(!roof) {
        final double amount = -7.0d / 16.0d;
        final double angle = Math.toRadians(facingAngle) - Math.PI / 2.0d;
        matrixStack.translate(Math.cos(angle) * amount, 0.0d, Math.sin(angle) * amount);
      }

      matrixStack.rotate(Vector3f .YP.rotationDegrees(-facingAngle));
      Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.NONE, combinedLight, combinedOverlay, matrixStack, buffer);

      matrixStack.pop();
    }

    matrixStack.pop();
  }
}
