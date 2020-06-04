package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.IGradientRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;

public class ManualInteractor<Recipe extends IGradientRecipe> implements IInteractor<Recipe> {
  @Override
  public ActionResultType onInteract(final Processor<Recipe> processor, final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    // Water
    if(FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent()) {
      final FluidStack fluid = FluidUtil.getFluidContained(player.getHeldItem(hand)).orElse(FluidStack.EMPTY);

      // Make sure the fluid handler is either empty, or contains 1000 mB of water
      if(!fluid.isEmpty() && fluid.getAmount() < 1000) {
        return ActionResultType.PASS;
      }

      processor.unlockFluid();
      if(FluidUtil.interactWithFluidHandler(player, hand, world, pos, hit.getFace())) {
        if(fluid.isEmpty()) {
          world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.NEUTRAL, 1.0f, world.rand.nextFloat() * 0.1f + 0.9f);
        } else {
          world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1.0f, world.rand.nextFloat() * 0.1f + 0.9f);
        }
      }
      processor.lockFluid();

      return ActionResultType.SUCCESS;
    }

    // Remove input
    if(player.isSneaking()) {
      for(int slot = 0; slot < processor.inputSlots(); slot++) {
        if(processor.hasInput(slot)) {
          final ItemStack stack = processor.takeInput(slot, player);
          ItemHandlerHelper.giveItemToPlayer(player, stack);
          return ActionResultType.SUCCESS;
        }
      }

      return ActionResultType.PASS;
    }

    // Take stuff out
    for(int slot = 0; slot < processor.outputSlots(); slot++) {
      if(processor.hasOutput(slot)) {
        final ItemStack stack = processor.takeOutput(slot);
        ItemHandlerHelper.giveItemToPlayer(player, stack);
        return ActionResultType.SUCCESS;
      }
    }

    final ItemStack held = player.getHeldItem(hand);

    // Put stuff in
    if(!held.isEmpty()) {
      final ItemStack remaining = processor.insertItem(held.copy(), player);

      if(remaining.getCount() != held.getCount()) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
      }

      if(!player.isCreative()) {
        player.setHeldItem(hand, remaining);
      }

      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }
}
