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
import net.minecraftforge.items.ItemHandlerHelper;

public class ManualItemInteractor<Recipe extends IGradientRecipe> implements IInteractor<Recipe> {
  @Override
  public ActionResultType onInteract(final Processor<Recipe> processor, final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
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
