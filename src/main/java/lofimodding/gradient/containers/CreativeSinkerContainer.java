package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.network.ChangeCreativeSinkerEnergyPacket;
import lofimodding.gradient.tileentities.CreativeSinkerTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class CreativeSinkerContainer extends GradientContainer<TileEntity> {
  public final CreativeSinkerTile generator;
  private final IIntArray syncedData;

  public CreativeSinkerContainer(final int id, final PlayerInventory playerInv, final CreativeSinkerTile generator) {
    this(id, playerInv, generator, new IntArray(2));
  }

  public CreativeSinkerContainer(final int id, final PlayerInventory playerInv, final CreativeSinkerTile generator, final IIntArray syncedData) {
    super(GradientContainers.CREATIVE_SINKER.get(), id, playerInv);
    this.generator = generator;
    this.syncedData = syncedData;

    this.trackIntArray(syncedData);
  }

  public float getRequestedEnergy() {
    return Float.intBitsToFloat(this.syncedData.get(0));
  }

  public float getEnergySinked() {
    return Float.intBitsToFloat(this.syncedData.get(1));
  }

  public void setRequestedEnergy(final float amount) {
    ChangeCreativeSinkerEnergyPacket.sendToServer(this.generator.getPos(), amount);
  }

  public void changeRequestedEnergy(final float amount) {
    this.setRequestedEnergy(this.getRequestedEnergy() + amount);
  }
}
