package lofimodding.gradient.tileentities.pieces;

import net.minecraft.nbt.CompoundNBT;

public interface IEnergySource {
  boolean consumeEnergy();
  CompoundNBT write(final CompoundNBT compound);
  void read(final CompoundNBT compound);
}
