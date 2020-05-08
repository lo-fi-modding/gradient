package lofimodding.gradient.energy;

public class StorageNode implements IEnergyStorage {
  private final float capacity;
  private final float maxSink;
  private final float maxSource;
  private float energy;

  public StorageNode() {
    this(0.0f, 1.0f, 1.0f, 0.0f);
  }

  public StorageNode(final float capacity, final float maxSink, final float maxSource, final float energy) {
    this.capacity = capacity;
    this.maxSink = maxSink;
    this.maxSource = maxSource;
    this.energy = energy;
  }

  @Override
  public boolean canSink() {
    return this.maxSink != 0.0f;
  }

  @Override
  public boolean canSource() {
    return this.maxSource != 0.0f;
  }

  @Override
  public float sinkEnergy(final float maxSink, final boolean simulate) {
    return this.addEnergy(Math.min(this.maxSink, maxSink), simulate);
  }

  @Override
  public float sourceEnergy(final float maxSource, final boolean simulate) {
    return this.removeEnergy(Math.min(this.getMaxSource(), maxSource), simulate);
  }

  @Override
  public float getEnergy() {
    return this.energy;
  }

  @Override
  public void setEnergy(final float energy) {
    this.energy = energy;
  }

  @Override
  public float addEnergy(final float amount, final boolean simulate) {
    final float energyReceived = Math.min(this.capacity - this.energy, amount);

    if(!simulate) {
      this.energy += energyReceived;
    }

    return energyReceived;
  }

  @Override
  public float removeEnergy(final float amount, final boolean simulate) {
    final float energyExtracted = Math.min(this.energy, amount);

    if(!simulate) {
      this.energy -= energyExtracted;
      this.onEnergyChanged();
    }

    return energyExtracted;
  }

  @Override
  public float getCapacity() {
    return this.capacity;
  }

  @Override
  public float getMaxSink() {
    return this.maxSink;
  }

  @Override
  public float getMaxSource() {
    return this.maxSource;
  }
}
