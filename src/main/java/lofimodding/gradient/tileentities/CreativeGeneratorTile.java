package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.containers.CreativeGeneratorContainer;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class CreativeGeneratorTile extends TileEntity implements INamedContainerProvider {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyStorage energy = new KineticEnergyStorage(1000000000.0f, 0.0f, 1000000000.0f) {
    @Override
    public float addEnergy(final float amount, final boolean simulate) {
      return 0.0f;
    }

    @Override
    public float removeEnergy(final float amount, final boolean simulate) {
      CreativeGeneratorTile.this.energyRequested = Math.min(this.getEnergy(), amount);
      return CreativeGeneratorTile.this.energyRequested;
    }
  };

  private final LazyOptional<IKineticEnergyStorage> lazyEnergy = LazyOptional.of(() -> this.energy);

  private final IIntArray syncedData = new IIntArray() {
    @Override
    public int get(final int index) {
      switch(index) {
        case 0:
          return Float.floatToIntBits(CreativeGeneratorTile.this.energy.getEnergy());

        case 1:
          return Float.floatToIntBits(CreativeGeneratorTile.this.energyRequested);
      }

      return 0;
    }

    @Override
    public void set(final int index, final int value) {
      switch(index) {
        case 0:
          CreativeGeneratorTile.this.energy.setEnergy(Float.intBitsToFloat(value));
          break;

        case 1:
          CreativeGeneratorTile.this.energyRequested = Float.intBitsToFloat(value);
      }
    }

    @Override
    public int size() {
      return 2;
    }
  };

  private float energyRequested;

  public CreativeGeneratorTile() {
    super(GradientTileEntities.CREATIVE_GENERATOR.get());
  }

  public void setEnergy(final float energy) {
    this.energy.setEnergy(energy);
  }

  @Override
  public void onLoad() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueConnection(this.pos, this);
  }

  @Override
  public void remove() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueDisconnection(this.pos);
  }

  @Override
  public Container createMenu(final int id, final PlayerInventory playerInv, final PlayerEntity player) {
    return new CreativeGeneratorContainer(id, playerInv, this, this.syncedData);
  }

  @Override
  public CompoundNBT write(final CompoundNBT nbt) {
    nbt.put("Energy", this.energy.serializeNbt());
    return super.write(nbt);
  }

  @Override
  public void read(final CompoundNBT nbt) {
    final CompoundNBT energy = nbt.getCompound("Energy");
    this.energy.deserializeNbt(energy);
    super.read(nbt);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == STORAGE) {
      return this.lazyEnergy.cast();
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("container.gradient.creative_generator");
  }
}