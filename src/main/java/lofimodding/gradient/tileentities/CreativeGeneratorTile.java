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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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
      return Math.min(this.getEnergy(), amount);
    }

    @Override
    public float getEnergy() {
      return super.getEnergy();
    }
  };

  private final LazyOptional<IKineticEnergyStorage> lazyEnergy = LazyOptional.of(() -> this.energy);

  public CreativeGeneratorTile() {
    super(GradientTileEntities.CREATIVE_GENERATOR.get());
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
    return new CreativeGeneratorContainer(id, playerInv, this);
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
