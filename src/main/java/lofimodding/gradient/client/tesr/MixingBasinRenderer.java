package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.tileentities.MixingBasinTile;
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

public class MixingBasinRenderer extends TileEntityRenderer<MixingBasinTile> {
  private IBakedModel rod;

  public MixingBasinRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final MixingBasinTile te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final Minecraft mc = Minecraft.getInstance();

    final boolean mouseOver;
    if(mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
      final BlockRayTraceResult trace = (BlockRayTraceResult)mc.objectMouseOver;

      mouseOver = trace.getPos().equals(te.getPos());
    } else {
      mouseOver = false;
    }

    final Direction facing = te.getBlockState().get(MixingBasinBlock.FACING);

    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(180 - facing.getHorizontalAngle()));
    matrixStack.translate(-0.5d, -0.5d, -0.5d);

    if(te.isMixing()) {
      matrixStack.translate(0.5d, 0.5d, 0.5d);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(te.getAnimation() * 360.0f));
      matrixStack.translate(-0.35d, -0.5d, -0.35d);
    } else {
      matrixStack.translate(0.5d, 0.5d, 0.5d);
      matrixStack.rotate(Vector3f.ZP.rotationDegrees(45.0f));
      matrixStack.rotate(Vector3f.XP.rotationDegrees(45.0f));
      matrixStack.translate(-0.7d, -0.4d, -0.45d);
    }

    this.renderRod(te, matrixStack, buffer, combinedLight, combinedOverlay);
    matrixStack.pop();

    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);

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

      if(mouseOver && output.getCount() > 1) {
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

  private void renderRod(final MixingBasinTile te, final MatrixStack mat, final IRenderTypeBuffer renderer, final int combinedLight, final int combinedOverlay) {
    if(this.rod == null) {
      this.rod = Minecraft.getInstance().getModelManager().getModel(Gradient.loc("block/mixing_basin_rod"));
    }

    final BlockPos pos = te.getPos();
    final ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
    final BlockState state = world.getBlockState(pos);
    final IModelData data = this.rod.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
    final IVertexBuilder buffer = renderer.getBuffer(Atlases.getSolidBlockType());

    for(final BakedQuad quad : this.rod.getQuads(state, null, new Random(), data)) {
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
