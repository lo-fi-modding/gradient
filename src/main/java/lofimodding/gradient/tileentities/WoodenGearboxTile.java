package lofimodding.gradient.tileentities;

import com.mojang.authlib.GameProfile;
import lofimodding.gradient.Config;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyTransfer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class WoodenGearboxTile extends TileEntity {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyTransfer transfer = new KineticEnergyTransfer() {
    @Override
    public float getLoss() {
      return Config.ENET.WOODEN_GEARBOX_LOSS_PER_BLOCK.get().floatValue();
    }

    @Override
    public void resetEnergyTransferred() {
      WoodenGearboxTile.this.handleBreaking(this.getEnergyTransferred());
      super.resetEnergyTransferred();
    }

    @Override
    public void setEnergyTransferred(final float amount) {
      super.setEnergyTransferred(amount);
      WoodenGearboxTile.this.handleEnergyTransferred(amount);
    }
  };

  private final LazyOptional<IKineticEnergyTransfer> lazyTransfer = LazyOptional.of(() -> this.transfer);

  private WeakReference<ServerPlayerEntity> fakePlayer = new WeakReference<>(null);

  private static final int TICKS_TO_SAMPLE_FOR_ENERGY_AVERAGE = 60;

  private final float[] energySamples = new float[TICKS_TO_SAMPLE_FOR_ENERGY_AVERAGE];
  private int energySampleIndex;
  private float averageEnergy;

  private float damage;

  public WoodenGearboxTile() {
    super(GradientTileEntities.WOODEN_GEARBOX.get());
  }

  private void handleEnergyTransferred(final float amount) {
    this.energySamples[this.energySampleIndex++ % this.energySamples.length] = amount;
    this.updateAverage();
  }

  private void handleBreaking(final float energyTransferred) {
    final double maxSpeed = Config.ENET.WOODEN_GEARBOX_MAX_ENERGY.get();
    final double speedPercent = energyTransferred / maxSpeed;

    if(speedPercent > 1.0d) {
      // Break after 5 seconds at 10% over, 2.5 at 20%, etc.
      this.damage += (speedPercent - 1.0f) / 10.0f;

      ServerPlayerEntity fakePlayer = this.fakePlayer.get();

      if(fakePlayer == null) {
        fakePlayer = FakePlayerFactory.get((ServerWorld)this.world, new GameProfile(UUID.randomUUID(), "Axle Breaking"));
        this.fakePlayer = new WeakReference<>(fakePlayer);
      }

      this.world.sendBlockBreakProgress(fakePlayer.getEntityId(), this.pos, (int)(this.damage * 10.0f));

      if(this.damage >= 1.0f) {
        this.world.removeBlock(this.pos, false);
        this.world.playSound(null, this.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1.0f, 1.0f);
      }
    } else {
      // Heal fully within 10 seconds
      if(this.damage > 0.0f) {
        this.damage -= 0.005f;
      }
    }
  }

  private void updateAverage() {
    float total = 0.0f;

    for(final float energySample : this.energySamples) {
      total += energySample;
    }

    this.averageEnergy = total / this.energySamples.length;
  }

  public float getAverageEnergy() {
    return this.averageEnergy;
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
