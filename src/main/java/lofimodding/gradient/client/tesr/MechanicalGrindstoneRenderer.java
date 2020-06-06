package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.blocks.GrindstoneBlock;
import lofimodding.gradient.tileentities.MechanicalGrindstoneTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

public class MechanicalGrindstoneRenderer extends ProcessorRenderer<MechanicalGrindstoneTile> {
  private IBakedModel wheel;

  public MechanicalGrindstoneRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final MechanicalGrindstoneTile te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    if(this.wheel == null) {
      this.wheel = Minecraft.getInstance().getModelManager().getModel(Gradient.loc("block/grindstone_wheel"));
    }

    super.render(te, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

    final Direction facing = te.getBlockState().get(GrindstoneBlock.FACING);

    matrixStack.push();
    this.rotateFacing(facing, matrixStack);

    matrixStack.push();
    matrixStack.translate(0.0d, 0.3125d, 0.2d + te.getAnimation() * 0.6d);
    matrixStack.rotate(Vector3f.XP.rotation(te.getAnimation() * (float)Math.PI));
    this.renderModel(te, this.wheel, matrixStack, buffer, combinedLight, combinedOverlay);
    matrixStack.pop();

    this.renderInput(te, 0, facing, 0.75d, 0.25d, 0.5d, matrixStack, buffer, combinedLight, combinedOverlay);
    this.renderOutput(te, 0, facing, 0.25d, 0.25d, 0.5d, matrixStack, buffer, combinedLight, combinedOverlay);

    matrixStack.pop();
  }
}
