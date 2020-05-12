package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.tileentities.WoodenCrankTile;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class WoodenCrankRenderer extends TileEntityRenderer<WoodenCrankTile> {
  public WoodenCrankRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final WoodenCrankTile crank, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    crank.getWorkers().forEach(worker -> this.renderLeash(worker, partialTicks, matrixStack, buffer, crank.getPos().add(0.5d, 1.0d, 0.5d)));
  }

  private void renderLeash(final AnimalEntity worker, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final BlockPos anchor) {
    matrixStack.push();
    matrixStack.translate(MathHelper.lerp(partialTicks, worker.prevPosX, worker.getPosX()) - anchor.getX(), MathHelper.lerp(partialTicks, worker.prevPosY, worker.getPosY()) - anchor.getY(), MathHelper.lerp(partialTicks, worker.prevPosZ, worker.getPosZ()) - anchor.getZ());

    final double anchorX = anchor.getX() + 0.5d;
    final double anchorY = anchor.getY() + 1.0d;
    final double anchorZ = anchor.getZ() + 0.5d;
    final double yaw = MathHelper.lerp(partialTicks, worker.renderYawOffset, worker.prevRenderYawOffset) * ((float)Math.PI / 180.0f) + Math.PI / 2.0d;
    final double neckOffsetX = Math.cos(yaw) * worker.getWidth() * 0.4d;
    final double neckOffsetZ = Math.sin(yaw) * worker.getWidth() * 0.4d;
    final double workerX = MathHelper.lerp(partialTicks, worker.prevPosX, worker.getPosX()) + neckOffsetX;
    final double workerY = MathHelper.lerp(partialTicks, worker.prevPosY, worker.getPosY()) + worker.getHeight() / 2.0d;
    final double workerZ = MathHelper.lerp(partialTicks, worker.prevPosZ, worker.getPosZ()) + neckOffsetZ;
    matrixStack.translate(neckOffsetX, -(1.6d - worker.getHeight()) * 0.5d + worker.getHeight() / 2.0d, neckOffsetZ);
    final float offsetX = (float)(anchorX - workerX);
    final float offsetY = (float)(anchorY - workerY);
    final float offsetZ = (float)(anchorZ - workerZ);
    final IVertexBuilder builder = buffer.getBuffer(RenderType.getLeash());
    final Matrix4f top = matrixStack.getLast().getMatrix();
    final float distance = MathHelper.fastInvSqrt(offsetX * offsetX + offsetZ * offsetZ) * 0.025f / 2.0f;
    final float f5 = offsetZ * distance;
    final float f6 = offsetX * distance;

    final int i = worker.world.getLightFor(LightType.BLOCK, new BlockPos(worker.getEyePosition(partialTicks)));
    final int j = worker.world.getLightFor(LightType.BLOCK, anchor);
    final int k = worker.world.getLightFor(LightType.SKY, new BlockPos(worker.getEyePosition(partialTicks)));
    final int l = worker.world.getLightFor(LightType.SKY, anchor);

    MobRenderer.renderSide(builder, top, offsetX, offsetY, offsetZ, i, j, k, l, 0.025f, 0.025f, f5, f6);
    MobRenderer.renderSide(builder, top, offsetX, offsetY, offsetZ, i, j, k, l, 0.025f, 0.0f, f5, f6);

    matrixStack.pop();
  }
}
