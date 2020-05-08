package lofimodding.gradient.energy;

public interface IEnergyNode {
  /**
   * Can this node sink power?
   */
  boolean canSink();

  /**
   * Can this node source power?
   */
  boolean canSource();
}
