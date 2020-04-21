package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.blocks.GrindstoneBlock;
import lofimodding.gradient.tileentities.GrindstoneTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class GrindstoneRenderer extends TileEntityRenderer<GrindstoneTile> {
  public GrindstoneRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final GrindstoneTile te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    matrixStack.translate(0.5d, 0.5d, 0.5d);

    final Direction facing = te.getBlockState().get(GrindstoneBlock.FACING);

    if(te.hasInput()) {
      matrixStack.push();

      switch(facing) {
        case NORTH:
          matrixStack.translate(0.25d, -0.25d, 0.0d);
          break;

        case SOUTH:
          matrixStack.translate(-0.25d, -0.25d, 0.0d);
          break;

        case EAST:
          matrixStack.translate(0.0d, -0.25d, 0.25d);
          break;

        case WEST:
          matrixStack.translate(0.0d, -0.25d, -0.25d);
          break;
      }

      final ItemStack input = te.getInput();

      if(input.getCount() > 1) {
        //TODO this.drawNameplate(te, Integer.toString(input.getCount()), -0.5d, -1.05d, -0.5d, 16);
      }

      matrixStack.scale(0.5f, 0.5f, 0.5f);
      matrixStack.rotate(Vector3f .YP.rotationDegrees(-facing.getHorizontalAngle()));
      Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();
    }

    if(te.hasOutput()) {
      matrixStack.push();

      switch(facing) {
        case NORTH:
          matrixStack.translate(-0.25d, -0.25d, 0.0d);
          break;

        case SOUTH:
          matrixStack.translate(0.25d, -0.25d, 0.0d);
          break;

        case EAST:
          matrixStack.translate(0.0d, -0.25d, -0.25d);
          break;

        case WEST:
          matrixStack.translate(0.0d, -0.25d, 0.25d);
          break;
      }

      final ItemStack output = te.getOutput();

      if(output.getCount() > 1) {
        //TODO this.drawNameplate(te, Integer.toString(output.getCount()), -0.5d, -1.05d, -0.5d, 16);
      }

      matrixStack.scale(0.5f, 0.5f, 0.5f);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      Minecraft.getInstance().getItemRenderer().renderItem(te.getOutput(), ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();
    }
  }
}
