package lofimodding.gradient.items;

import lofimodding.gradient.GradientItems;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FilledWaterskinItem extends Item {
  public FilledWaterskinItem() {
    super(new Item.Properties().group(GradientItems.GROUP).maxStackSize(1));
  }

  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
    return new Handler();
  }

  private static class Handler implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @Nonnull
    @Override
    public ItemStack getContainer() {
      return new ItemStack(GradientItems.EMPTY_WATERSKIN.get());
    }

    @Override
    public int getTanks() {
      return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(final int tank) {
      return new FluidStack(Fluids.WATER, 1000);
    }

    @Override
    public int getTankCapacity(final int tank) {
      return 1000;
    }

    @Override
    public boolean isFluidValid(final int tank, @Nonnull final FluidStack stack) {
      return true;
    }

    @Override
    public int fill(final FluidStack resource, final FluidAction doFill) {
      return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {
      if(resource.getFluid() != Fluids.WATER) {
        return FluidStack.EMPTY;
      }

      return this.drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
      return new FluidStack(Fluids.WATER, Math.min(maxDrain, 1000));
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, @Nullable final Direction facing) {
      return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
    }
  }
}
