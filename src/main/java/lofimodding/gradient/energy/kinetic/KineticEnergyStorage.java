package lofimodding.gradient.energy.kinetic;

import lofimodding.gradient.energy.EnergyStorage;

public class KineticEnergyStorage extends EnergyStorage implements IKineticEnergyStorage {
  public KineticEnergyStorage(final float capacity) {
    super(capacity);
  }

  public KineticEnergyStorage(final float capacity, final float maxTransfer) {
    super(capacity, maxTransfer);
  }

  public KineticEnergyStorage(final float capacity, final float maxSink, final float maxSource) {
    super(capacity, maxSink, maxSource);
  }

  public KineticEnergyStorage(final float capacity, final float maxSink, final float maxSource, final float energy) {
    super(capacity, maxSink, maxSource, energy);
  }
}
