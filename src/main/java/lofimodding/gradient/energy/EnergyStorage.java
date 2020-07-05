package lofimodding.gradient.energy;

public class EnergyStorage implements IEnergyStorage {
  private final float capacity;
  private final float maxSink;
  private final float maxSource;
  float energy;

  public EnergyStorage(final float capacity) {
    this(capacity, capacity, capacity, 0);
  }

  public EnergyStorage(final float capacity, final float maxTransfer) {
    this(capacity, maxTransfer, maxTransfer, 0);
  }

  public EnergyStorage(final float capacity, final float maxSink, final float maxSource) {
    this(capacity, maxSink, maxSource, 0);
  }

  public EnergyStorage(final float capacity, final float maxSink, final float maxSource, final float energy) {
    this.capacity = capacity;
    this.maxSink = maxSink;
    this.maxSource = maxSource;
    this.energy = Math.max(0, Math.min(capacity, energy));
  }

  @Override
  public float sinkEnergy(final float maxSink, final Action action) {
    return this.addEnergy(Math.min(this.getMaxSink(), maxSink), action);
  }

  private float sourcedThisTick;

  @Override
  public float sourceEnergy(final float maxSource, final Action action) {
    final float sourced = this.removeEnergy(Math.min(this.getMaxSource() - this.sourcedThisTick, maxSource), action);
    this.sourcedThisTick += sourced;
    return sourced;
  }

  @Override
  public void resetEnergySourced() {
    this.sourcedThisTick = 0.0f;
  }

  @Override
  public float getEnergy() {
    return this.energy;
  }

  @Override
  public void setEnergy(final float amount) {
    this.energy = amount;
    this.onEnergyChanged();
  }

  @Override
  public float addEnergy(final float amount, final Action action) {
    final float energyReceived = Math.min(this.capacity - this.energy, amount);

    if(action.execute()) {
      this.energy += energyReceived;
      this.onEnergyChanged();
    }

    return energyReceived;
  }

  @Override
  public float removeEnergy(final float amount, final Action action) {
    final float energyExtracted = Math.min(this.energy, amount);

    if(action.execute()) {
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

  @Override
  public boolean canSink() {
    return this.maxSink > 0;
  }

  @Override
  public boolean canSource() {
    return this.maxSource > 0;
  }
}
