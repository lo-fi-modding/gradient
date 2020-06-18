package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.tileentities.ToolStationTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.items.SlotItemHandler;

public class ToolStationContainer extends GradientContainer<ToolStationTile> {
  public ToolStationContainer(final int id, final PlayerInventory playerInv, final ToolStationTile tile) {
    super(GradientContainers.TOOL_STATION.get(), id, playerInv, tile);

    final int size = tile.getCraftingSize();

    // Recipe input
    for(int slot = 0; slot < tile.getMergedRecipeInv().getSlots(); slot++) {
      this.addSlot(new SlotItemHandler(tile.getMergedStorageInv(), slot, INV_SLOTS_X + slot % size * SLOT_X_SPACING, slot / size * SLOT_Y_SPACING));
    }

    // Output
    for(int slot = 0; slot < tile.getMergedOutputInv().getSlots(); slot++) {
      this.addSlot(new SlotItemHandler(tile.getMergedStorageInv(), slot, INV_SLOTS_X + (size + 1) * SLOT_X_SPACING, SLOT_Y_SPACING));
    }

    // Tools
    for(int slot = 0; slot < tile.getMergedToolsInv().getSlots(); slot++) {
      this.addSlot(new SlotItemHandler(tile.getMergedStorageInv(), slot, INV_SLOTS_X + slot * SLOT_X_SPACING, (size + 1) * SLOT_Y_SPACING));
    }

    // Storage
    for(int slot = 0; slot < tile.getMergedStorageInv().getSlots(); slot++) {
      this.addSlot(new SlotItemHandler(tile.getMergedStorageInv(), slot, INV_SLOTS_X + slot % 9 * SLOT_X_SPACING, (slot / 9 + size + 3) * SLOT_Y_SPACING));
    }

    this.addPlayerSlots(playerInv);
  }
}
