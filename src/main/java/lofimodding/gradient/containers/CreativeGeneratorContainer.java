package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.tileentities.CreativeGeneratorTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class CreativeGeneratorContainer extends GradientContainer {
  public final CreativeGeneratorTile generator;
  private final IIntArray syncedData;

  public CreativeGeneratorContainer(final int id, final PlayerInventory playerInv, final CreativeGeneratorTile generator) {
    this(id, playerInv, generator, new IntArray(1));
  }

  public CreativeGeneratorContainer(final int id, final PlayerInventory playerInv, final CreativeGeneratorTile generator, final IIntArray syncedData) {
    super(GradientContainers.CREATIVE_GENERATOR.get(), id, playerInv);
    this.generator = generator;
    this.syncedData = syncedData;

    this.trackIntArray(syncedData);
  }

  public float getEnergy() {
    return Float.intBitsToFloat(this.syncedData.get(0));
  }

  public void setEnergy(final float amount) {
    this.syncedData.set(0, Float.floatToIntBits(Math.max(0.0f, amount)));
  }

  public void changeEnergy(final float amount) {
    this.setEnergy(this.getEnergy() + amount);
  }
}
