package lofimodding.gradient.tileentities;

import lofimodding.gradient.Config;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.WoodenAxleBlock;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyTransfer;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class WoodenAxleTile extends TileEntity {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyTransfer transfer = new KineticEnergyTransfer() {
    @Override
    public void setEnergyTransferred(final float amount) {
      final double maxSpeed = Config.ENET.WOODEN_AXLE_MAX_ENERGY.get();

      WoodenAxleTile.this.energySamples[WoodenAxleTile.this.energySampleIndex++ % WoodenAxleTile.this.energySamples.length] = amount;

      final double speedPercent = WoodenAxleTile.this.averageEnergy() / maxSpeed;
      WoodenAxleTile.this.rotation = (WoodenAxleTile.this.rotation + 10.0d * speedPercent) % 360.0d;
    }
  };

  private final LazyOptional<IKineticEnergyTransfer> lazyTransfer = LazyOptional.of(() -> this.transfer);

  private static final int TICKS_TO_SAMPLE_FOR_ENERGY_AVERAGE = 40;

  private final float[] energySamples = new float[TICKS_TO_SAMPLE_FOR_ENERGY_AVERAGE];
  private int energySampleIndex;

  private double rotation;

  public WoodenAxleTile() {
    super(GradientTileEntities.WOODEN_AXLE.get());
  }

  private float averageEnergy() {
    float total = 0.0f;

    for(final float energySample : this.energySamples) {
      total += energySample;
    }

    return total / this.energySamples.length;
  }

  public double getRotation() {
    return this.rotation;
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
      final BlockState state = this.world.getBlockState(this.pos);

      if(state.getBlock() == GradientBlocks.WOODEN_AXLE.get()) {
        if(facing != null && facing.getAxis() == state.get(WoodenAxleBlock.AXIS)) {
          return this.lazyTransfer.cast();
        }
      }
    }

    return super.getCapability(capability, facing);
  }
}
