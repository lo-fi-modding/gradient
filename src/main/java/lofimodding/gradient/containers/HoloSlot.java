package lofimodding.gradient.containers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class HoloSlot extends SlotItemHandler {
  public HoloSlot(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public int getSlotStackLimit() {
    return 1;
  }

  @Override
  public int getItemStackLimit(@Nonnull final ItemStack stack) {
    return 1;
  }

  @Override
  public boolean isItemValid(@Nonnull final ItemStack stack) {
    return true;
  }
}
