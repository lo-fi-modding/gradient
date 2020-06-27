package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.network.ChangeCreativeGeneratorEnergyPacket;
import lofimodding.gradient.tileentities.CreativeGeneratorTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class CreativeGeneratorContainer extends GradientContainer<TileEntity> {
  public final CreativeGeneratorTile generator;
  private final IIntArray syncedData;

  public CreativeGeneratorContainer(final int id, final PlayerInventory playerInv, final CreativeGeneratorTile generator) {
    this(id, playerInv, generator, new IntArray(2));
  }

  public CreativeGeneratorContainer(final int id, final PlayerInventory playerInv, final CreativeGeneratorTile generator, final IIntArray syncedData) {
    super(GradientContainers.CREATIVE_GENERATOR.get(), id, playerInv);
    this.generator = generator;
    this.syncedData = syncedData;

    this.trackIntArray(syncedData);
  }

  public float getEnergyAvailable() {
    return Float.intBitsToFloat(this.syncedData.get(0));
  }

  public float getEnergyTransferred() {
    return Float.intBitsToFloat(this.syncedData.get(1));
  }

  public void setEnergyAvailable(final float amount) {
    ChangeCreativeGeneratorEnergyPacket.sendToServer(this.generator.getPos(), amount);
  }

  public void changeEnergyAvailable(final float amount) {
    this.setEnergyAvailable(this.getEnergyAvailable() + amount);
  }
}
