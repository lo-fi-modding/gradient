package lofimodding.gradient.fluids;

import lofimodding.gradient.science.Metal;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class MetalSourceFluid extends ForgeFlowingFluid.Source {
  public final Metal metal;

  public MetalSourceFluid(final Metal metal, final Properties properties) {
    super(properties);
    this.metal = metal;
  }
}
