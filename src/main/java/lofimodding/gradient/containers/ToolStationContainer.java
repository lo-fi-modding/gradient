package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.tileentities.ToolStationTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ToolStationContainer extends GradientContainer<ToolStationTile> {
  public final int height;
  public final int recipeY;
  public final int toolsY;
  public final int storageY;

  public ToolStationContainer(final int id, final PlayerInventory playerInv, final ToolStationTile tile) {
    super(GradientContainers.TOOL_STATION.get(), id, playerInv, tile);

    final int size = tile.getCraftingSize();

    int y = INV_SLOTS_X + 13;
    this.recipeY = y;

    // Recipe input
    for(int slot = 0; slot < tile.getMergedRecipeInv().getSlots(); slot++) {
      this.addSlot(new HoloSlot(tile.getMergedRecipeInv(), slot, INV_SLOTS_X + slot % size * SLOT_X_SPACING, y + slot / size * SLOT_Y_SPACING));
    }

    // Output
    for(int slot = 0; slot < tile.getMergedOutputInv().getSlots(); slot++) {
      this.addSlot(new SlotItemHandler(tile.getMergedOutputInv(), slot, INV_SLOTS_X + (size + 1) * SLOT_X_SPACING, y + (size - 1) * SLOT_Y_SPACING / 2));
    }

    y += size * SLOT_Y_SPACING + 13;
    this.toolsY = y;

    // Tools
    for(int slot = 0; slot < tile.getMergedToolsInv().getSlots(); slot++) {
      this.addSlot(new ExtractableSlot(tile.getMergedToolsInv(), slot, INV_SLOTS_X + slot * SLOT_X_SPACING, y));
    }

    y += SLOT_Y_SPACING + 13;
    this.storageY = y;

    // Storage
    for(int slot = 0; slot < tile.getMergedStorageInv().getSlots(); slot++) {
      this.addSlot(new ExtractableSlot(tile.getMergedStorageInv(), slot, INV_SLOTS_X + slot % 9 * SLOT_X_SPACING, y));
    }

    y += tile.getMergedStorageInv().getSlots() / 9 * SLOT_Y_SPACING + 13;

    this.addPlayerSlots(y, playerInv);

    this.height = y + 4 * SLOT_Y_SPACING + 11;
  }

  @Override
  public ItemStack slotClick(final int slotId, final int dragType, final ClickType clickType, final PlayerEntity player) {
    if(slotId >= 0) {
      final Slot slot = this.getSlot(slotId);

      if(slot instanceof HoloSlot) {
        final PlayerInventory inv = player.inventory;

        if(clickType != ClickType.PICKUP && clickType != ClickType.THROW && clickType != ClickType.QUICK_CRAFT) {
          return inv.getItemStack();
        }

        if(!inv.getItemStack().isEmpty()) {
          slot.putStack(ItemHandlerHelper.copyStackWithSize(inv.getItemStack(), 1));
          return inv.getItemStack();
        }

        if(slot.getHasStack()) {
          slot.putStack(ItemStack.EMPTY);
          return ItemStack.EMPTY;
        }
      }
    }

    return super.slotClick(slotId, dragType, clickType, player);
  }

  // The tool/storage item handlers can't be extracted from so that hoppers can't pull out contents - this bypasses that
  public static class ExtractableSlot extends SlotItemHandler {
    public ExtractableSlot(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean canTakeStack(final PlayerEntity player) {
      return true;
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(final int amount) {
      final ItemStack stack = this.getStack();
      final ItemStack split = stack.split(amount);
      this.putStack(stack);
      return split;
    }
  }
}
