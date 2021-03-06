package lofimodding.gradient.energy;

import net.minecraft.util.Direction;

public interface IEnergyTransfer extends IEnergyNode {
  /**
   * Used for distance-based energy loss
   *
   * @return The amount lost
   */
  default float getLoss() {
    return 0.0f;
  }

  /**
   * Called when energy is transferred through this object.
   *
   * <b>NOTE:</b> The actual energy transfer is done by the energy network.  This method
   * is only called to notify the transfer object that energy was transferred through it.
   *
   * @param amount The amount of energy
   * @param from   The side energy is flowing from
   * @param to     The side energy is flowing to
   */
  void transfer(final float amount, final Direction from, final Direction to);

  float getEnergyTransferred();

  /**
   * <b>NOTE:</b> Only called on client side
   *
   * @param amount The amount of energy transferred
   */
  void setEnergyTransferred(final float amount);
  void resetEnergyTransferred();
}
