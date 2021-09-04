package lofimodding.gradient.energy;

import lofimodding.gradient.utils.MathHelper;
import net.minecraft.nbt.CompoundNBT;

public interface IEnergyStorage extends IEnergyNode {
  /**
   * Adds energy to the storage. Returns quantity of energy that was accepted.
   *
   * @param maxSink
   *            Maximum amount of energy to be inserted.
   * @param action
   *            If TRUE, the insertion will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
   */
  float sinkEnergy(float maxSink, Action action);

  /**
   * Removes energy from the storage. Returns quantity of energy that was removed.
   *
   * @param maxSource
   *            Maximum amount of energy to be extracted.
   * @param action
   *            If TRUE, the extraction will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
   */
  float sourceEnergy(float maxSource, Action action);

  /**
   * Resets tracker for the energy sourced this tick
   *
   * @return The amount of energy sourced this tick
   */
  float resetEnergySourced();

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
  float addEnergy(float amount, final Action action);

  /**
   * Remove energy from the storage, bypassing source restrictions (used by things
   * like machines which don't source power, but still need an internal power buffer)
   */
  float removeEnergy(float amount, final Action action);

  /**
   * Returns the maximum amount of energy that can be stored.
   */
  float getCapacity();

  float getMaxSink();
  float getMaxSource();

  default float getRequestedEnergy() {
    if(!this.canSink()) {
      return 0.0f;
    }

    final float space = this.getCapacity() - this.getEnergy();

    if(MathHelper.flEq(space, 0.0f)) {
      return 0.0f;
    }

    return Math.min(this.getMaxSink(), space);
  }

  default CompoundNBT write() {
    final CompoundNBT nbt = new CompoundNBT();
    nbt.putFloat("Energy", this.getEnergy());
    return nbt;
  }

  default void read(final CompoundNBT nbt) {
    this.setEnergy(nbt.getFloat("Energy"));
    this.onLoad();
  }

  default void onEnergyChanged() { }
  default void onLoad() { }

  enum Action {
    EXECUTE, SIMULATE;

    public boolean execute() {
      return this == EXECUTE;
    }

    public boolean simulate() {
      return this == SIMULATE;
    }
  }
}
