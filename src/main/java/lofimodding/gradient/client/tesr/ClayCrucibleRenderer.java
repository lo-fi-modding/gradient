package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class ClayCrucibleRenderer extends TileEntityRenderer<ClayCrucibleTile> {
  private static final float OFFSET_1 =  2.0f / 16.0f;
  private static final float OFFSET_2 = 14.0f / 16.0f;

  public ClayCrucibleRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final ClayCrucibleTile crucible, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final GradientFluidStack stack = crucible.tank.getFluidStack();

    if(!stack.isEmpty()) {
      final ResourceLocation textureLoc = stack.getFluid().getStillTexture(stack);
      final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(textureLoc);

      final float height = (1.0f + stack.getAmount() / ClayCrucibleTile.FLUID_CAPACITY * 11.5f) / 16.0f;
      final float a = (stack.getColour() >>> 24 & 255) / 255.0f;
      final float r = (stack.getColour() >>> 16 & 255) / 255.0f;
      final float g = (stack.getColour() >>> 8 & 255) / 255.0f;
      final float b = (stack.getColour() & 255) / 255.0f;

      final IVertexBuilder wr = buffer.getBuffer(RenderType.getTranslucent());
      final Matrix4f top = matrixStack.getLast().getMatrix();
      wr.pos(top, OFFSET_1, height, OFFSET_1).color(r, g, b, a).tex(sprite.getMinU(), sprite.getMinV()).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
      wr.pos(top, OFFSET_1, height, OFFSET_2).color(r, g, b, a).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
      wr.pos(top, OFFSET_2, height, OFFSET_2).color(r, g, b, a).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
      wr.pos(top, OFFSET_2, height, OFFSET_1).color(r, g, b, a).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(combinedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
    }
  }
}
