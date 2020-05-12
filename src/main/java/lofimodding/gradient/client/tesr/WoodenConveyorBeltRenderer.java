package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.blocks.WoodenConveyorBeltBlock;
import lofimodding.gradient.tileentities.WoodenConveyorBeltDriverTile;
import lofimodding.gradient.tileentities.WoodenConveyorBeltTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class WoodenConveyorBeltRenderer extends TileEntityRenderer<WoodenConveyorBeltTile> {
  private static final ResourceLocation TEXTURE_LOC = Gradient.loc("block/wooden_conveyor_belt");
  private static final float OFFSET_X = 4.1f / 16.0f;
  private static final float OFFSET_Y = 4.0f / 16.0f + 0.0001f;
  private static final float OFFSET_Z = 1.1f / 16.0f;

  public WoodenConveyorBeltRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final WoodenConveyorBeltTile belt, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final BlockState state = belt.getBlockState();
    final Direction facing = state.get(WoodenConveyorBeltBlock.FACING);
    matrixStack.translate(0.5d, 0.0d, 0.5d);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(facing.getHorizontalAngle()));
    matrixStack.translate(-0.5d, 0.0d, -0.5d);

    float beltSpeed = 0.0f;
    for(final WoodenConveyorBeltDriverTile driver : belt.getDrivers().keySet()) {
      beltSpeed += driver.getBeltSpeed();
    }
    beltSpeed /= belt.getDrivers().size();

    final float ticks = 20.0f / beltSpeed;
    final float scale = Minecraft.getInstance().player.ticksExisted % ticks / ticks;
    final float z1 = 1.0f - OFFSET_Z;
    final float z2 = z1 - scale * (1.0f - OFFSET_Z - OFFSET_Z);
    final float z3 = OFFSET_Z;

    final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE_LOC);
    final float u1 = sprite.getInterpolatedU(4.0f);
    final float u2 = sprite.getInterpolatedU(12.0f);
    final float v1 = sprite.getInterpolatedV(16.0f);
    final float v2 = sprite.getInterpolatedV(16.0f * scale);
    final float v3 = sprite.getInterpolatedV(0.0f);

    final IVertexBuilder wr = buffer.getBuffer(RenderType.getTranslucent());
    final Matrix4f top = matrixStack.getLast().getMatrix();
    wr.pos(top, OFFSET_X, OFFSET_Y, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u1, v3).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, OFFSET_X, OFFSET_Y, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u1, v2).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, 1.0f - OFFSET_X, OFFSET_Y, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u2, v2).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, 1.0f - OFFSET_X, OFFSET_Y, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u2, v3).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, OFFSET_X, OFFSET_Y, z3).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u1, v2).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, OFFSET_X, OFFSET_Y, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u1, v1).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, 1.0f - OFFSET_X, OFFSET_Y, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u2, v1).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    wr.pos(top, 1.0f - OFFSET_X, OFFSET_Y, z3).color(1.0f, 1.0f, 1.0f, 1.0f).tex(u2, v2).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
  }
}
