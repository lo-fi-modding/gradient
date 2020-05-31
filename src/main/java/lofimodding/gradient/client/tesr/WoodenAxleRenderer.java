package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.blocks.WoodenAxleBlock;
import lofimodding.gradient.tileentities.WoodenAxleTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public class WoodenAxleRenderer extends TileEntityRenderer<WoodenAxleTile> {
  private IBakedModel axle;

  public WoodenAxleRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final WoodenAxleTile axle, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final Direction.Axis axis = axle.getBlockState().get(WoodenAxleBlock.AXIS);

    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);

    if(axis == Direction.Axis.X) {
      matrixStack.rotate(Vector3f.ZP.rotationDegrees(90.0f));
    } else if(axis == Direction.Axis.Z) {
      matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0f));
    }

    matrixStack.rotate(Vector3f.YP.rotationDegrees((float)-axle.getRotation()));
    matrixStack.translate(-0.5d, -0.5d, -0.5d);
    this.renderAxle(axle, matrixStack, buffer, combinedLight, combinedOverlay);
    matrixStack.pop();
  }

  public void renderAxle(final WoodenAxleTile te, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    if(this.axle == null) {
      this.axle = Minecraft.getInstance().getModelManager().getModel(Gradient.loc("block/" + GradientIds.WOODEN_AXLE));
    }

    final BlockPos pos = te.getPos();
    final ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
    final BlockState state = world.getBlockState(pos);
    final IModelData data = this.axle.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
    final IVertexBuilder vertices = buffer.getBuffer(Atlases.getSolidBlockType());

    for(final BakedQuad quad : this.axle.getQuads(state, null, new Random(), data)) {
      final float r;
      final float g;
      final float b;
      if(quad.hasTintIndex()) {
        final int i = Minecraft.getInstance().getBlockColors().getColor(state, world, pos, quad.getTintIndex());
        r = (i >> 16 & 255) / 255.0f;
        g = (i >> 8 & 255) / 255.0f;
        b = (i & 255) / 255.0f;
      } else {
        r = 1.0f;
        g = 1.0f;
        b = 1.0f;
      }

      vertices.addQuad(matrixStack.getLast(), quad, r, g, b, combinedLight, combinedOverlay);
    }
  }
}
