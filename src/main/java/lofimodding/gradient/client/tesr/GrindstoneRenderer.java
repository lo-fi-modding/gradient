package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.blocks.GrindstoneBlock;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.tileentities.GrindstoneTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public class GrindstoneRenderer extends TileEntityRenderer<GrindstoneTile> {
  private IBakedModel wheel;

  public GrindstoneRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final GrindstoneTile te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final Minecraft mc = Minecraft.getInstance();

    final boolean mouseOver;
    if(mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
      final BlockRayTraceResult trace = (BlockRayTraceResult)mc.objectMouseOver;

      mouseOver = trace.getPos().equals(te.getPos());
    } else {
      mouseOver = false;
    }

    final Direction facing = te.getBlockState().get(GrindstoneBlock.FACING);

    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(180 - facing.getHorizontalAngle()));
    matrixStack.translate(-0.5d, -0.5d, -0.5d);
    matrixStack.translate(0.0d, 0.3125d, 0.2d + te.getAnimation() * 0.6d);
    matrixStack.rotate(Vector3f.XP.rotation(te.getAnimation() * (float)Math.PI));
    this.renderWheel(te, matrixStack, buffer, combinedLight, combinedOverlay);
    matrixStack.pop();

    matrixStack.translate(0.5d, 0.5d, 0.5d);

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

      if(mouseOver && input.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(Integer.toString(input.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
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

      if(mouseOver && output.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(Integer.toString(output.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.scale(0.5f, 0.5f, 0.5f);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      Minecraft.getInstance().getItemRenderer().renderItem(te.getOutput(), ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();
    }
  }

  private void renderWheel(final GrindstoneTile te, final MatrixStack mat, final IRenderTypeBuffer renderer, final int combinedLight, final int combinedOverlay) {
    if(this.wheel == null) {
      this.wheel = Minecraft.getInstance().getModelManager().getModel(Gradient.loc("block/grindstone_wheel"));
    }

    final BlockPos pos = te.getPos();
    final ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
    final BlockState state = world.getBlockState(pos);
    final IModelData data = this.wheel.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
    final IVertexBuilder buffer = renderer.getBuffer(Atlases.getSolidBlockType());

    for(final BakedQuad quad : this.wheel.getQuads(state, null, new Random(), data)) {
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

      buffer.addQuad(mat.getLast(), quad, r, g, b, combinedLight, combinedOverlay);
    }
  }
}
