package lofimodding.gradient.fluids;

import javax.annotation.Nonnull;

public interface IGradientFluidTank {
  /**
   * @return GradientFluidStack representing the fluid in the tank, null if the tank is empty.
   */
  @Nonnull
  GradientFluidStack getFluidStack();

  /**
   * @return Current amount of fluid in the tank.
   */
  float getFluidAmount();

  /**
   * @return Capacity of this fluid tank.
   */
  float getCapacity();

  /**
   * @param stack GradientFluidStack holding the Fluid to be queried.
   *
   * @return If the tank can hold the fluid (EVER, not at the time of query).
   */
  boolean isFluidValid(GradientFluidStack stack);

  /**
   * @param resource GradientFluidStack attempting to fill the tank.
   * @param action   If SIMULATE, the fill will only be simulated.
   *
   * @return Amount of fluid that was accepted (or would be, if simulated) by the tank.
   */
  float fill(GradientFluidStack resource, IGradientFluidHandler.FluidAction action);

  /**
   * @param maxDrain Maximum amount of fluid to be removed from the container.
   * @param action   If SIMULATE, the drain will only be simulated.
   *
   * @return Amount of fluid that was removed (or would be, if simulated) from the tank.
   */
  @Nonnull
  GradientFluidStack drain(float maxDrain, IGradientFluidHandler.FluidAction action);

  /**
   * @param resource Maximum amount of fluid to be removed from the container.
   * @param action   If SIMULATE, the drain will only be simulated.
   *
   * @return GradientFluidStack representing fluid that was removed (or would be, if simulated) from the tank.
   */
  @Nonnull
  GradientFluidStack drain(GradientFluidStack resource, IGradientFluidHandler.FluidAction action);
}
