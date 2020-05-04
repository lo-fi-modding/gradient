package lofimodding.gradient.fluids;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class GradientFluidTank implements IGradientFluidHandler, IGradientFluidTank {
  protected Predicate<GradientFluidStack> validator;
  protected GradientFluidStack stack = GradientFluidStack.EMPTY;
  protected int capacity;

  public GradientFluidTank(final int capacity) {
    this(capacity, e -> true);
  }

  public GradientFluidTank(final int capacity, final Predicate<GradientFluidStack> validator) {
    this.capacity = capacity;
    this.validator = validator;
  }

  public GradientFluidTank setCapacity(final int capacity) {
    this.capacity = capacity;
    return this;
  }

  public GradientFluidTank setValidator(final Predicate<GradientFluidStack> validator) {
    this.validator = validator;
    return this;
  }

  @Override
  public boolean isFluidValid(final GradientFluidStack stack) {
    return this.validator.test(stack);
  }

  @Override
  public float getCapacity() {
    return this.capacity;
  }

  @Override
  @Nonnull
  public GradientFluidStack getFluidStack() {
    return this.stack;
  }

  @Override
  public float getFluidAmount() {
    return this.stack.getAmount();
  }

  public GradientFluidTank read(final CompoundNBT nbt) {
    final GradientFluidStack fluid = GradientFluidStack.read(nbt);
    this.setFluid(fluid);
    return this;
  }

  public CompoundNBT write(final CompoundNBT nbt) {
    this.stack.write(nbt);
    return nbt;
  }

  @Override
  public float fill(final GradientFluidStack resource, final IGradientFluidHandler.FluidAction action) {
    if(resource.isEmpty() || !this.isFluidValid(resource)) {
      return 0;
    }

    if(action.simulate()) {
      if(this.stack.isEmpty()) {
        return Math.min(this.capacity, resource.getAmount());
      }

      if(!this.stack.isFluidEqual(resource)) {
        return 0.0f;
      }

      return Math.min(this.capacity - this.stack.getAmount(), resource.getAmount());
    }

    if(this.stack.isEmpty()) {
      this.stack = new GradientFluidStack(resource.getFluid(), Math.min(this.capacity, resource.getAmount()), resource.getTemperature());
      this.onContentsChanged();
      return this.stack.getAmount();
    }

    if(!this.stack.isFluidEqual(resource)) {
      return 0.0f;
    }

    float space = this.getSpace();

    if(resource.getAmount() < space) {
      this.stack.mix(resource);
      space = resource.getAmount();
    } else {
      this.stack.grow(space, resource.getTemperature());
    }

    if(space > 0.0f) {
      this.onContentsChanged();
    }

    return space;
  }

  @Nonnull
  @Override
  public GradientFluidStack drain(final GradientFluidStack resource, final FluidAction action) {
    if(resource.isEmpty() || !resource.isFluidEqual(this.stack)) {
      return GradientFluidStack.EMPTY;
    }

    return this.drain(resource.getAmount(), action);
  }

  @Nonnull
  @Override
  public GradientFluidStack drain(final float maxDrain, final FluidAction action) {
    float drained = maxDrain;

    if(this.stack.getAmount() < drained) {
      drained = this.stack.getAmount();
    }

    final GradientFluidStack stack = new GradientFluidStack(this.stack.getFluid(), drained, this.stack.getTemperature());

    if(action.execute() && drained > 0.0f) {
      this.stack.shrink(drained);
      this.onContentsChanged();
    }

    return stack;
  }

  protected void onContentsChanged() {

  }

  public void setFluid(final GradientFluidStack stack) {
    this.stack = stack;
  }

  public boolean isEmpty() {
    return this.stack.isEmpty();
  }

  public float getSpace() {
    return Math.max(0, this.capacity - this.stack.getAmount());
  }
}
