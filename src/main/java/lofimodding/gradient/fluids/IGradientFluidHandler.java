package lofimodding.gradient.fluids;

import javax.annotation.Nonnull;

public interface IGradientFluidHandler {
  enum FluidAction {
    EXECUTE, SIMULATE;

    public boolean execute() {
      return this == EXECUTE;
    }

    public boolean simulate() {
      return this == SIMULATE;
    }
  }

  /**
   * Fills fluid into internal tanks, distribution is left entirely to the IGradientFluidHandler.
   *
   * @param resource GradientFluidStack representing the Fluid and maximum amount of fluid to be filled.
   * @param action   If SIMULATE, fill will only be simulated.
   *
   * @return Amount of resource that was (or would have been, if simulated) filled.
   */
  float fill(GradientFluidStack resource, FluidAction action);

  /**
   * Drains fluid out of internal tanks, distribution is left entirely to the IGradientFluidHandler.
   *
   * @param resource GradientFluidStack representing the Fluid and maximum amount of fluid to be drained.
   * @param action   If SIMULATE, drain will only be simulated.
   *
   * @return GradientFluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Nonnull
  GradientFluidStack drain(GradientFluidStack resource, FluidAction action);

  /**
   * Drains fluid out of internal tanks, distribution is left entirely to the IGradientFluidHandler.
   *
   * This method is not Fluid-sensitive.
   *
   * @param maxDrain Maximum amount of fluid to drain.
   * @param action   If SIMULATE, drain will only be simulated.
   *
   * @return GradientFluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Nonnull
  GradientFluidStack drain(float maxDrain, FluidAction action);
}
