package lofimodding.gradient.energy;

import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

public final class EnergyCapability {
  private EnergyCapability() { }

  public static <STORAGE extends IEnergyStorage, TRANSFER extends IEnergyTransfer> void register(final Class<STORAGE> storage, final Class<TRANSFER> transfer, final Callable<STORAGE> defaultStorage, final Callable<TRANSFER> defaultTransfer) {
    CapabilityManager.INSTANCE.register(storage, new Capability.IStorage<STORAGE>() {
      @Override
      public INBT writeNBT(final Capability<STORAGE> capability, final STORAGE instance, final Direction side) {
        return FloatNBT.valueOf(instance.getEnergy());
      }

      @Override
      public void readNBT(final Capability<STORAGE> capability, final STORAGE instance, final Direction side, final INBT nbt) {
        instance.setEnergy(((NumberNBT)nbt).getFloat());
      }
    }, defaultStorage);

    CapabilityManager.INSTANCE.register(transfer, new Capability.IStorage<TRANSFER>() {
      @Override
      public INBT writeNBT(final Capability<TRANSFER> capability, final TRANSFER instance, final Direction side) {
        return null;
      }

      @Override
      public void readNBT(final Capability<TRANSFER> capability, final TRANSFER instance, final Direction side, final INBT nbt) {

      }
    }, defaultTransfer);
  }
}
