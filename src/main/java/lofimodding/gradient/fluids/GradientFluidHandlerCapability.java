package lofimodding.gradient.fluids;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class GradientFluidHandlerCapability {
  @CapabilityInject(IGradientFluidHandler.class)
  public static Capability<IGradientFluidHandler> CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(IGradientFluidHandler.class, new DefaultFluidHandlerStorage<>(), () -> new GradientFluidTank(1.0f));
  }

  private static class DefaultFluidHandlerStorage<T extends IGradientFluidHandler> implements Capability.IStorage<T> {
    @Override
    public INBT writeNBT(final Capability<T> capability, final T instance, final Direction side) {
      if(!(instance instanceof GradientFluidTank)) {
        throw new RuntimeException("Cannot serialize to an instance that isn't the default implementation");
      }

      final CompoundNBT nbt = new CompoundNBT();
      final GradientFluidTank tank = (GradientFluidTank)instance;
      final GradientFluidStack fluid = tank.getFluidStack();
      fluid.write(nbt);
      nbt.putFloat("Capacity", tank.getCapacity());
      return nbt;
    }

    @Override
    public void readNBT(final Capability<T> capability, final T instance, final Direction side, final INBT nbt) {
      if(!(instance instanceof GradientFluidTank)) {
        throw new RuntimeException("Cannot deserialize to an instance that isn't the default implementation");
      }

      final CompoundNBT tags = (CompoundNBT)nbt;
      final GradientFluidTank tank = (GradientFluidTank)instance;
      tank.setCapacity(tags.getFloat("Capacity"));
      tank.read(tags);
    }
  }
}
