package lofimodding.gradient.client.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import lofimodding.gradient.blocks.FirepitBlock;
import lofimodding.gradient.client.RenderUtils;
import lofimodding.gradient.tileentities.FirepitTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class FirepitRenderer extends TileEntityRenderer<FirepitTile> {
  public FirepitRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
    super(rendererDispatcher);
  }

  @Override
  public void render(final FirepitTile firepit, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {
    matrixStack.push();
    matrixStack.translate(0.5d, 0.5d, 0.5d);

    final BlockState state = firepit.getBlockState();
    final Direction facing = state.get(FirepitBlock.FACING);
    final double facingAngle = Math.toRadians(facing.getHorizontalAngle());

    final double fuelAngleOffset = firepit.hasFurnace(state) ? Math.PI / 2 : 0.0d;

    final Minecraft mc = Minecraft.getInstance();

    for(int slot = 0; slot < FirepitTile.FUEL_SLOTS_COUNT; slot++) {
      if(firepit.hasFuel(slot)) {
        final ItemStack fuel = firepit.getFuel(slot);

        final double angle = (5 - slot) * Math.PI / 4 + facingAngle - fuelAngleOffset;
        final double inputX = Math.cos(angle) * 0.25d;
        final double inputZ = Math.sin(angle) * 0.25d;

        matrixStack.push();
        matrixStack.translate(inputX, -0.4375d, inputZ);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));

        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        mc.getItemRenderer().renderItem(fuel, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
        matrixStack.pop();

        if(firepit.isBurning(slot)) {
          matrixStack.translate(-0.1f, 0.2f, 0.0f);
          matrixStack.scale(0.2f * (1.0f - firepit.getBurningFuel(slot).percentBurned()), 0.025f, 1.0f);
          //TODO
//          GlStateManager.disableCull();
//          this.setLightmapDisabled(true);
//          GlStateManager.disableLighting();
//          Gui.drawRect(0, 0, 1, 1, 0xFF1AFF00);
//          GlStateManager.enableLighting();
//          this.setLightmapDisabled(false);
//          GlStateManager.enableCull();
        }

        matrixStack.pop();
      }
    }

    if(firepit.hasInput()) {
      final ItemStack input = firepit.getInput();

      matrixStack.push();

      matrixStack.translate(0.0d, -0.3125d, 0.0d);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));

      matrixStack.push();
      matrixStack.scale(0.5f, 0.5f, 0.5f);
      mc.getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
      matrixStack.pop();

      if(firepit.isCooking()) {
        matrixStack.translate(-0.1f, 0.2f, 0.0f);
        matrixStack.scale(0.2f * (1.0f - firepit.getCookingPercent()), 0.025f, 1.0f);
        //TODO
//        GlStateManager.disableCull();
//        this.setLightmapDisabled(true);
//        GlStateManager.disableLighting();
//        Gui.drawRect(0, 0, 1, 1, 0xFF1AFF00);
//        GlStateManager.enableLighting();
//        this.setLightmapDisabled(false);
//        GlStateManager.enableCull();
      }

      matrixStack.pop();
    }

    if(firepit.hasOutput()) {
      final ItemStack output = firepit.getOutput();

      final double inputX = Math.cos(facingAngle) * 0.25f;
      final double inputZ = Math.sin(facingAngle) * 0.25f;

      matrixStack.push();
      matrixStack.translate(inputX, -0.40625d, inputZ);

      if(output.getCount() > 1) {
        matrixStack.push();
        matrixStack.translate(-0.5d, -1.05d, -0.5d);
        RenderUtils.renderText(Integer.toString(output.getCount()), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }

      matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
      matrixStack.scale(0.5f, 0.5f, 0.5f);
      mc.getItemRenderer().renderItem(output, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);

      matrixStack.pop();
    }

    if(mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
      final BlockRayTraceResult trace = (BlockRayTraceResult)mc.objectMouseOver;

      if(trace.getPos().equals(firepit.getPos())) {
        matrixStack.push();
        matrixStack.translate(0.0d, 0.5d, 0.0d);
        RenderUtils.renderText(I18n.format(state.getBlock().getTranslationKey() + ".heat", Math.round(firepit.getHeat())), matrixStack, buffer, combinedLight);
        matrixStack.pop();
      }
    }

    matrixStack.pop();
  }
}
