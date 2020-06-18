package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.tileentities.WoodenHopperTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class WoodenHopperContainer extends GradientContainer<WoodenHopperTile> {
  private final IInventory hopperInventory;

  public WoodenHopperContainer(final int id, final PlayerInventory playerInv, final WoodenHopperTile te) {
    super(GradientContainers.WOODEN_HOPPER.get(), id, playerInv, te);
    this.hopperInventory = te;
    assertInventorySize(te, WoodenHopperTile.SLOTS);
    te.openInventory(playerInv.player);

    for(int slot = 0; slot < WoodenHopperTile.SLOTS; ++slot) {
      this.addSlot(new Slot(te, slot, 62 + slot * 18, 20));
    }

    for(int y = 0; y < 3; ++y) {
      for(int x = 0; x < 9; ++x) {
        this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, y * 18 + 51));
      }
    }

    for(int slot = 0; slot < 9; ++slot) {
      this.addSlot(new Slot(playerInv, slot, 8 + slot * 18, 109));
    }
  }

  @Override
  public boolean canInteractWith(final PlayerEntity player) {
    return this.hopperInventory.isUsableByPlayer(player);
  }

  @Override
  public void onContainerClosed(final PlayerEntity player) {
    super.onContainerClosed(player);
    this.hopperInventory.closeInventory(player);
  }
}
