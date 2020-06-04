package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.tileentities.ProcessorTile;
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

public class ProcessorRenderer<TE extends ProcessorTile> extends TileEntityRenderer<TE> {
  private boolean mouseOver;

  protected ProcessorRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  protected boolean isMouseOver() {
    return this.mouseOver;
  }

  @Override
  public void render(final TE te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final Minecraft mc = Minecraft.getInstance();

    if(mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
      final BlockRayTraceResult trace = (BlockRayTraceResult)mc.objectMouseOver;

      this.mouseOver = trace.getPos().equals(te.getPos());
    } else {
      this.mouseOver = false;
    }
  }

  protected void rotateFacing(final Direction facing, final MatrixStack matrixStack) {
    matrixStack.translate(0.5d, 0.5d, 0.5d);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(180 - facing.getHorizontalAngle()));
    matrixStack.translate(-0.5d, -0.5d, -0.5d);
  }

  protected void renderInput(final TE te, final int slot, final Direction facing, final double x, final double y, final double z, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    if(te.hasInput(slot)) {
      matrixStack.push();
      matrixStack.translate(x, y, z);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(180 + facing.getHorizontalAngle()));

      final ItemStack input = te.getInput(slot);

      if(this.isMouseOver() && input.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(Integer.toString(input.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.scale(0.5f, 0.5f, 0.5f);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();
    }
  }

  protected void renderOutput(final TE te, final int slot, final Direction facing, final double x, final double y, final double z, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    if(te.hasOutput(slot)) {
      matrixStack.push();
      matrixStack.translate(x, y, z);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(180 + facing.getHorizontalAngle()));

      final ItemStack output = te.getOutput(slot);

      if(this.isMouseOver() && output.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(Integer.toString(output.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.scale(0.5f, 0.5f, 0.5f);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      Minecraft.getInstance().getItemRenderer().renderItem(output, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();
    }
  }

  protected void renderModel(final TE te, final IBakedModel model, final MatrixStack matrixStack, final IRenderTypeBuffer renderer, final int combinedLight, final int combinedOverlay) {
    final BlockPos pos = te.getPos();
    final ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
    final BlockState state = world.getBlockState(pos);
    final IModelData data = model.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
    final IVertexBuilder buffer = renderer.getBuffer(Atlases.getSolidBlockType());

    for(final BakedQuad quad : model.getQuads(state, null, new Random(), data)) {
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

      buffer.addQuad(matrixStack.getLast(), quad, r, g, b, combinedLight, combinedOverlay);
    }
  }
}
