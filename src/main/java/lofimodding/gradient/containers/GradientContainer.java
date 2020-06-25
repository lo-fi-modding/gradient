package lofimodding.gradient.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GradientContainer<Tile extends TileEntity> extends Container {
  public static final int SLOT_X_SPACING = 18;
  public static final int SLOT_Y_SPACING = 18;

  public static final int INV_SLOTS_X =   8;
  public static final int INV_SLOTS_Y =  84;
  public static final int HOT_SLOTS_Y = 142;

  public final PlayerInventory playerInv;
  public final Tile tile;
  protected final IItemHandler inventory;

  public GradientContainer(final ContainerType<? extends Container> type, final int id, final PlayerInventory playerInv) {
    super(type, id);
    this.playerInv = playerInv;
    this.tile = null;
    this.inventory = null;
  }

  public GradientContainer(final ContainerType<? extends Container> type, final int id, final PlayerInventory playerInv, final Tile tile) {
    super(type, id);
    this.playerInv = playerInv;
    this.tile = tile;
    this.inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).orElseThrow(() -> new RuntimeException("TE wasn't an item handler"));
  }

  protected void addPlayerSlots(final PlayerInventory playerInv) {
    this.addPlayerSlots(INV_SLOTS_Y, playerInv);
  }

  protected void addPlayerSlots(final int yOffset, final PlayerInventory playerInv) {
    // Player inv
    for(int y = 0; y < 3; ++y) {
      for(int x = 0; x < 9; ++x) {
        this.addSlot(new Slot(playerInv, x + y * 9 + 9, INV_SLOTS_X + x * SLOT_X_SPACING, yOffset + y * SLOT_Y_SPACING));
      }
    }

    // Player hotbar
    for(int i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInv, i, INV_SLOTS_X + i * SLOT_X_SPACING, HOT_SLOTS_Y - INV_SLOTS_Y + yOffset));
    }
  }

  @Override
  public boolean canInteractWith(final PlayerEntity player) {
    return true;
  }

  @Override
  public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
    final Slot slot = this.inventorySlots.get(index);

    if(slot == null || !slot.getHasStack()) {
      return ItemStack.EMPTY;
    }

    final ItemStack itemstack1 = slot.getStack();
    final ItemStack itemstack = itemstack1.copy();

    final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();

    if(index < containerSlots) {
      if(!this.mergeItemStack(slot, containerSlots, this.inventorySlots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else if(!this.mergeItemStack(slot, 0, containerSlots, false)) {
      return ItemStack.EMPTY;
    }

    if(!slot.getHasStack()) {
      slot.putStack(ItemStack.EMPTY);
    } else {
      slot.onSlotChanged();
    }

    if(itemstack1.getCount() == itemstack.getCount()) {
      return ItemStack.EMPTY;
    }

    slot.onTake(player, itemstack1);

    return itemstack;
  }

  /**
   * This is an exact copy-and-paste but fixes shift-clicking ignoring stack limits and also respects item handler restrictions
   */
  protected boolean mergeItemStack(final Slot slot, final int startIndex, final int endIndex, final boolean reverseDirection) {
    boolean flag = false;

    if(slot.getStack().isStackable()) {
      int i = reverseDirection ? endIndex - 1 : startIndex;

      while(slot.getHasStack() && (reverseDirection ? i >= startIndex : i < endIndex)) {
        final Slot dest = this.inventorySlots.get(i);
        final ItemStack itemstack = dest.getStack();

        if(dest.isItemValid(slot.getStack()) && areItemStacksEqual(slot.getStack(), itemstack)) {
          final int j = itemstack.getCount() + slot.getStack().getCount();
          final int maxSize = Math.min(dest.getSlotStackLimit(), slot.getStack().getMaxStackSize());

          if(j <= maxSize) {
            slot.decrStackSize(slot.getStack().getCount());
            itemstack.setCount(j);
            dest.onSlotChanged();
            flag = true;
          } else if(itemstack.getCount() < maxSize) {
            slot.decrStackSize(maxSize - itemstack.getCount());
            itemstack.setCount(maxSize);
            dest.onSlotChanged();
            flag = true;
          }
        }

        if(reverseDirection) {
          --i;
        } else {
          ++i;
        }
      }
    }

    if(slot.getHasStack()) {
      int i = reverseDirection ? endIndex - 1 : startIndex;

      while(reverseDirection ? i >= startIndex : i < endIndex) {
        final Slot dest = this.inventorySlots.get(i);

        if(!(dest instanceof HoloSlot)) {
          final ItemStack itemstack = dest.getStack();

          if(itemstack.isEmpty() && dest.isItemValid(slot.getStack())) { // Forge: Make sure to respect isItemValid in the slot.
            final ItemStack split = slot.decrStackSize(dest.getItemStackLimit(slot.getStack()));
            dest.putStack(split);
            dest.onSlotChanged();
            flag = true;

            if(!slot.getHasStack()) {
              break;
            }
          }
        }

        if(reverseDirection) {
          --i;
        } else {
          ++i;
        }
      }
    }

    return flag;
  }

  private static boolean areItemStacksEqual(final ItemStack stackA, final ItemStack stackB) {
    return stackB.getItem() == stackA.getItem() && ItemStack.areItemStackTagsEqual(stackA, stackB);
  }
}
