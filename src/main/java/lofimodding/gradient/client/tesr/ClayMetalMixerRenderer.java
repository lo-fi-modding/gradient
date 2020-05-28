package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.fluids.GradientFluidHandlerCapability;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.IGradientFluidHandler;
import lofimodding.gradient.tileentities.ClayMetalMixerTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public class ClayMetalMixerRenderer extends TileEntityRenderer<ClayMetalMixerTile> {
  private IBakedModel auger;

  public ClayMetalMixerRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final ClayMetalMixerTile te, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    final TileEntity output = te.getWorld().getTileEntity(te.getPos().down());

    if(output != null) {
      output.getCapability(GradientFluidHandlerCapability.CAPABILITY, Direction.UP).ifPresent(handler -> {
        if(!handler.drain(0.001f, IGradientFluidHandler.FluidAction.SIMULATE).isEmpty()) {
          matrixStack.translate(0.5f, 0.5f, 0.5f);
          matrixStack.rotate(Vector3f.YP.rotationDegrees((Minecraft.getInstance().player.ticksExisted % 60) / 60.0f * 360.0f));
          matrixStack.translate(-0.5f, -0.5f, -0.5f);
        }
      });
    }

    this.renderAuger(te, matrixStack, buffer, combinedLight, combinedOverlay);

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      final GradientFluidStack stack = te.getFlowingFluid(side);

      if(!stack.isEmpty()) {
        matrixStack.push();
        matrixStack.translate(0.5d, 0.5d, 0.5d);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(side.getHorizontalAngle()));
        matrixStack.translate(-0.5d, -0.5d, -0.5d);
        this.renderFluid(stack, matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }
    }
  }

  public void renderAuger(final ClayMetalMixerTile te, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    if(this.auger == null) {
      this.auger = Minecraft.getInstance().getModelManager().getModel(Gradient.loc("block/" + GradientIds.CLAY_METAL_MIXER + "_auger"));
    }

    final BlockPos pos = te.getPos();
    final ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
    final BlockState state = world.getBlockState(pos);
    final IModelData data = this.auger.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
    final IVertexBuilder vertices = buffer.getBuffer(Atlases.getSolidBlockType());

    for(final BakedQuad quad : this.auger.getQuads(state, null, new Random(), data)) {
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

  public void renderFluid(final GradientFluidStack stack, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight) {
    if(!stack.isEmpty()) {
      final ResourceLocation textureLoc = stack.getFluid().getStillTexture(stack);
      final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(textureLoc);

      final float a = (stack.getColour() >>> 24 & 255) / 255.0f;
      final float r = (stack.getColour() >>> 16 & 255) / 255.0f;
      final float g = (stack.getColour() >>> 8 & 255) / 255.0f;
      final float b = (stack.getColour() & 255) / 255.0f;

      final float minV = sprite.getInterpolatedV(1.0d);
      final float maxV = sprite.getInterpolatedV(14.0d);

      final IVertexBuilder vertices = buffer.getBuffer(RenderType.getTranslucent());
      final Matrix4f top = matrixStack.getLast().getMatrix();

      // North
      vertices.pos(top, 6.5f / 16.0f, -14.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(0.0d), minV).lightmap(combinedLight).normal(0.0f, 0.0f, -1.0f).endVertex();
      vertices.pos(top, 6.5f / 16.0f,  -1.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(0.0d), maxV).lightmap(combinedLight).normal(0.0f, 0.0f, -1.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f,  -1.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(3.0d), maxV).lightmap(combinedLight).normal(0.0f, 0.0f, -1.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f, -14.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(3.0d), minV).lightmap(combinedLight).normal(0.0f, 0.0f, -1.0f).endVertex();

      // West
      vertices.pos(top, 6.5f / 16.0f, -14.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(3.0d), minV).lightmap(combinedLight).normal(-1.0f, 0.0f, 0.0f).endVertex();
      vertices.pos(top, 6.5f / 16.0f,  -1.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(3.0d), maxV).lightmap(combinedLight).normal(-1.0f, 0.0f, 0.0f).endVertex();
      vertices.pos(top, 6.5f / 16.0f,  -1.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(4.0d), maxV).lightmap(combinedLight).normal(-1.0f, 0.0f, 0.0f).endVertex();
      vertices.pos(top, 6.5f / 16.0f, -14.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(4.0d), minV).lightmap(combinedLight).normal(-1.0f, 0.0f, 0.0f).endVertex();

      // South
      vertices.pos(top, 6.5f / 16.0f,  -1.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(4.0d), minV).lightmap(combinedLight).normal(0.0f, 0.0f, 1.0f).endVertex();
      vertices.pos(top, 6.5f / 16.0f, -14.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(4.0d), maxV).lightmap(combinedLight).normal(0.0f, 0.0f, 1.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f, -14.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(7.0d), maxV).lightmap(combinedLight).normal(0.0f, 0.0f, 1.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f,  -1.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(7.0d), minV).lightmap(combinedLight).normal(0.0f, 0.0f, 1.0f).endVertex();

      // East
      vertices.pos(top, 9.5f / 16.0f,  -1.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(7.0d), minV).lightmap(combinedLight).normal(1.0f, 0.0f, 0.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f, -14.0f / 16.0f, 3.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(7.0d), maxV).lightmap(combinedLight).normal(1.0f, 0.0f, 0.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f, -14.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(8.0d), maxV).lightmap(combinedLight).normal(1.0f, 0.0f, 0.0f).endVertex();
      vertices.pos(top, 9.5f / 16.0f,  -1.0f / 16.0f, 2.5f / 16.0f).color(r, g, b, a).tex(sprite.getInterpolatedU(8.0d), minV).lightmap(combinedLight).normal(1.0f, 0.0f, 0.0f).endVertex();
    }
  }
}
