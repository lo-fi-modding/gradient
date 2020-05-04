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

public class GradientContainer extends Container {
  public static final int SLOT_X_SPACING = 18;
  public static final int SLOT_Y_SPACING = 18;

  public static final int INV_SLOTS_X =   8;
  public static final int INV_SLOTS_Y =  84;
  public static final int HOT_SLOTS_Y = 142;

  public final PlayerInventory playerInv;
  protected final IItemHandler inventory;

  public GradientContainer(final ContainerType<? extends Container> type, final int id, final PlayerInventory playerInv) {
    super(type, id);
    this.playerInv = playerInv;
    this.inventory = null;
  }

  public GradientContainer(final ContainerType<? extends Container> type, final int id, final PlayerInventory playerInv, final TileEntity te) {
    super(type, id);
    this.playerInv = playerInv;
    this.inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).orElseThrow(() -> new RuntimeException("TE wasn't an item handler"));
  }

  protected void addPlayerSlots(final PlayerInventory invPlayer) {
    // Player inv
    for(int y = 0; y < 3; ++y) {
      for(int x = 0; x < 9; ++x) {
        this.addSlot(new Slot(invPlayer, x + y * 9 + 9, INV_SLOTS_X + x * SLOT_X_SPACING, INV_SLOTS_Y + y * SLOT_Y_SPACING));
      }
    }

    // Player hotbar
    for(int i = 0; i < 9; ++i) {
      this.addSlot(new Slot(invPlayer, i, INV_SLOTS_X + i * SLOT_X_SPACING, HOT_SLOTS_Y));
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
      if(!this.mergeItemStack(itemstack1, containerSlots, this.inventorySlots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else if(!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
      return ItemStack.EMPTY;
    }

    if(itemstack1.isEmpty()) {
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
   * This is an exact copy-and-paste but fixes shift-clicking ignoring stack limits
   */
  @Override
  protected boolean mergeItemStack(final ItemStack stack, final int startIndex, final int endIndex, final boolean reverseDirection) {
    boolean flag = false;

    if(stack.isStackable()) {
      int i = reverseDirection ? endIndex - 1 : startIndex;

      while(!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
        final Slot slot = this.inventorySlots.get(i);
        final ItemStack itemstack = slot.getStack();

        if(areItemStacksEqual(stack, itemstack)) {
          final int j = itemstack.getCount() + stack.getCount();
          final int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

          if(j <= maxSize) {
            stack.setCount(0);
            itemstack.setCount(j);
            slot.onSlotChanged();
            flag = true;
          } else if(itemstack.getCount() < maxSize) {
            stack.setCount(stack.getCount() - (maxSize - itemstack.getCount()));
            itemstack.setCount(maxSize);
            slot.onSlotChanged();
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

    if(!stack.isEmpty()) {
      int i = reverseDirection ? endIndex - 1 : startIndex;

      while(reverseDirection ? i >= startIndex : i < endIndex) {
        final Slot slot = this.inventorySlots.get(i);
        final ItemStack itemstack = slot.getStack();

        if(itemstack.isEmpty() && slot.isItemValid(stack)) { // Forge: Make sure to respect isItemValid in the slot.
          slot.putStack(stack.split(slot.getItemStackLimit(stack)));
          slot.onSlotChanged();
          flag = true;

          if(stack.isEmpty()) {
            break;
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
