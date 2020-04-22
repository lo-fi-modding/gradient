package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.blocks.GrindstoneBlock;
import lofimodding.gradient.tileentities.GrindstoneTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
    matrixStack.push();
    matrixStack.translate(0.0d, 0.3125d, 0.2d + te.getAnimation() * 0.6d);
    matrixStack.rotate(Vector3f.XP.rotation(te.getAnimation() * (float)Math.PI));
    this.renderWheel(te, matrixStack, buffer, combinedLight);
    matrixStack.pop();

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
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        this.renderName(Integer.toString(input.getCount()), matrixStack, buffer, combinedLight);
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

      if(output.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        this.renderName(Integer.toString(output.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.scale(0.5f, 0.5f, 0.5f);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      Minecraft.getInstance().getItemRenderer().renderItem(te.getOutput(), ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();
    }
  }

  protected static BlockRendererDispatcher blockRenderer;

  private void renderWheel(final GrindstoneTile te, final MatrixStack mat, final IRenderTypeBuffer renderer, final int light) {
    if(blockRenderer == null) {
      blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
      this.wheel = Minecraft.getInstance().getModelManager().getModel(Gradient.loc("block/grindstone_wheel"));
    }

    final BlockPos pos = te.getPos();
    final ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
    final BlockState state = world.getBlockState(pos);
    final IModelData data = this.wheel.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
    blockRenderer.getBlockModelRenderer().renderModel(world, this.wheel, state, pos, mat, renderer.getBuffer(Atlases.getSolidBlockType()), false, new Random(), 42, light, data);
  }

  protected void renderName(final String text, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight) {
    final Minecraft mc = Minecraft.getInstance();

    matrixStack.push();
    matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
    matrixStack.scale(-0.025F, -0.025F, 0.025F);

    final float opacity = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
    final int background = (int)(opacity * 255.0F) << 24;
    final FontRenderer font = mc.getRenderManager().getFontRenderer();
    final float x = -font.getStringWidth(text) / 2;

    final Matrix4f top = matrixStack.getLast().getMatrix();
    font.renderString(text, x, 0.0f, 0x20ffffff, false, top, buffer, true, background, combinedLight);
    font.renderString(text, x, 0.0f, 0xffffffff, false, top, buffer, false, 0, combinedLight);

    matrixStack.pop();
  }
}
