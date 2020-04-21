package lofimodding.gradient.events;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public final class BlockStripping {
  private BlockStripping() { }

  @SubscribeEvent
  public static void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
    if(event.getSide() == LogicalSide.CLIENT) {
      return;
    }

    final ItemStack held = event.getItemStack();

    if(!held.getToolTypes().contains(ToolType.AXE)) {
      return;
    }

    final BlockState state = event.getWorld().getBlockState(event.getPos());

    if(AxeItem.BLOCK_STRIPPING_MAP.containsKey(state.getBlock())) {
      Block.spawnAsEntity(event.getWorld(), event.getPos(), new ItemStack(GradientItems.BARK.get()));
    }
  }
}
