package lofimodding.gradient.fluids;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.science.Metal;

public class MetalFluid extends GradientFluid {
  public final Metal metal;

  public MetalFluid(final Metal metal) {
    this.metal = metal;
    this.setStillTexture(Gradient.loc("fluid/metal_still"));
    this.setFlowingTexture(Gradient.loc("fluid/metal_flowing"));
  }

  @Override
  public int getColour(final GradientFluidStack stack) {
    return this.metal.colourDiffuse;
  }
}
