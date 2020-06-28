package lofimodding.gradient.fluids;

import lofimodding.gradient.science.Metal;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class MetalFlowingFluid extends ForgeFlowingFluid.Source {
  public final Metal metal;

  public MetalFlowingFluid(final Metal metal, final Properties properties) {
    super(properties);
    this.metal = metal;
  }
}
