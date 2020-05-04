package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ClayCrucibleContainer extends GradientContainer {
  public static final int FUEL_SLOTS_X = 13;
  public static final int FUEL_SLOTS_Y = 34;

  public static final int METAL_SLOTS_X = 13;
  public static final int METAL_SLOTS_Y = 17;

  public final ClayCrucibleTile crucible;

  public ClayCrucibleContainer(final int id, final PlayerInventory playerInv, final ClayCrucibleTile crucible) {
    super(GradientContainers.CLAY_CRUCIBLE.get(), id, playerInv, crucible);

    this.crucible = crucible;

    for(int i = 0; i < ClayCrucibleTile.METAL_SLOTS_COUNT; i++) {
      final int i2 = i;

      this.addSlot(new SlotItemHandler(this.inventory, ClayCrucibleTile.FIRST_METAL_SLOT + i, METAL_SLOTS_X + (SLOT_X_SPACING + 8) * (i % 5), METAL_SLOTS_Y + (SLOT_Y_SPACING + 2) * (i / 5)) {
        @Override public void onSlotChanged() {
          crucible.updateStages(playerInv.player);
          crucible.markDirty();
        }

        @Override public boolean canTakeStack(final PlayerEntity player) {
          return !crucible.isMelting(i2);
        }

        @Override public boolean isItemValid(final ItemStack stack) {
          return crucible.tank.getFluidAmount() < crucible.tank.getCapacity() && super.isItemValid(stack);
        }
      });
    }

    this.addPlayerSlots(playerInv);
  }
}
