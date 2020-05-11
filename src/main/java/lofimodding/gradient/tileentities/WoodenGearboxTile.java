package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyTransfer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class WoodenGearboxTile extends TileEntity {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyTransfer transfer = new KineticEnergyTransfer();
  private final LazyOptional<IKineticEnergyTransfer> lazyTransfer = LazyOptional.of(() -> this.transfer);

  public WoodenGearboxTile() {
    super(GradientTileEntities.WOODEN_GEARBOX.get());
  }

  @Override
  public void onLoad() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueConnection(this.pos, this);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == TRANSFER) {
      return this.lazyTransfer.cast();
    }

    return super.getCapability(capability, facing);
  }
}
