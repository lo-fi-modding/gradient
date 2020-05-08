package lofimodding.gradient.energy;

import net.minecraft.nbt.CompoundNBT;

public interface IEnergyStorage extends IEnergyNode {
  /**
   * Adds energy to the storage. Returns quantity of energy that was accepted.
   *
   * @param maxSink
   *            Maximum amount of energy to be inserted.
   * @param simulate
   *            If TRUE, the insertion will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
   */
  //TODO boolean
  float sinkEnergy(float maxSink, boolean simulate);

  /**
   * Removes energy from the storage. Returns quantity of energy that was removed.
   *
   * @param maxSource
   *            Maximum amount of energy to be extracted.
   * @param simulate
   *            If TRUE, the extraction will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
   */
  //TODO boolean
  float sourceEnergy(float maxSource, boolean simulate);

  /**
   * Returns the amount of energy currently stored.
   */
  float getEnergy();

  /**
   * Sets the amount of energy that is stored
   */
  void setEnergy(float amount);

  /**
   * Add energy to the storage, bypassing sink restrictions (used by things like
   * generators which don't sink power, but still need an internal power buffer)
   */
  //TODO boolean
  float addEnergy(float amount, final boolean simulate);

  /**
   * Remove energy from the storage, bypassing source restrictions (used by things
   * like machines which don't source power, but still need an internal power buffer)
   */
  //TODO boolean
  float removeEnergy(float amount, final boolean simulate);

  /**
   * Returns the maximum amount of energy that can be stored.
   */
  float getCapacity();

  float getMaxSink();
  float getMaxSource();

  default float getRequestedEnergy() {
    final float space = this.getCapacity() - this.getEnergy();

    if(!this.canSink() || space < 0.0001f) {
      return 0.0f;
    }

    return Math.min(this.getMaxSink(), space);
  }

  //TODO: rename to write
  default CompoundNBT serializeNbt() {
    final CompoundNBT nbt = new CompoundNBT();
    nbt.putFloat("Energy", this.getEnergy());
    return nbt;
  }

  //TODO: rename to read
  default void deserializeNbt(final CompoundNBT nbt) {
    this.setEnergy(nbt.getFloat("Energy"));
    this.onLoad();
  }

  default void onEnergyChanged() { }
  default void onLoad() { }
}
