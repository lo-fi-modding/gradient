package lofimodding.gradient.energy;

import net.minecraft.util.Direction;

public class EnergyTransfer implements IEnergyTransfer {
  private float energy;

  @Override
  public void transfer(final float amount, final Direction from, final Direction to) {
    this.energy += amount;
  }

  @Override
  public float getEnergyTransferred() {
    return this.energy;
  }

  @Override
  public void resetEnergyTransferred() {
    this.energy = 0.0f;
  }

  @Override
  public boolean canSink() {
    return true;
  }

  @Override
  public boolean canSource() {
    return true;
  }
}
