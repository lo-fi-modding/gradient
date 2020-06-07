package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.IEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.utils.MathHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class KineticEnergySource<Recipe extends IGradientRecipe, Tile extends ProcessorTile<Recipe, KineticEnergySource<Recipe, Tile>, Tile>> implements IEnergySource<Recipe, KineticEnergySource<Recipe, Tile>, Tile> {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyStorage node;
  private final LazyOptional<IKineticEnergyStorage> lazyNode;

  private final float energyConsumedPerTick;

  public KineticEnergySource(final float capacity, final float maxSink, final float energyConsumedPerTick) {
    this.node = new KineticEnergyStorage(capacity, maxSink, 0.0f);
    this.lazyNode = LazyOptional.of(() -> this.node);
    this.energyConsumedPerTick = energyConsumedPerTick;
  }

  @Override
  public void onAddToWorld(final Tile tile) {
    EnergyNetworkManager.getManager(tile.getWorld(), STORAGE, TRANSFER).queueConnection(tile.getPos(), tile);
  }

  @Override
  public void onRemoveFromWorld(final Tile tile) {
    EnergyNetworkManager.getManager(tile.getWorld(), STORAGE, TRANSFER).queueDisconnection(tile.getPos());
  }

  @Override
  public boolean consumeEnergy() {
    if(MathHelper.flLess(this.node.removeEnergy(this.energyConsumedPerTick, IEnergyStorage.Action.SIMULATE), this.energyConsumedPerTick)) {
      return false;
    }

    this.node.removeEnergy(this.energyConsumedPerTick, IEnergyStorage.Action.EXECUTE);
    return true;
  }

  @Override
  public CompoundNBT write(final CompoundNBT nbt) {
    nbt.put("Energy", this.node.write());
    return nbt;
  }

  @Override
  public void read(final CompoundNBT nbt) {
    final CompoundNBT energy = nbt.getCompound("Energy");
    this.node.read(energy);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
    if(cap == STORAGE) {
      return this.lazyNode.cast();
    }

    return LazyOptional.empty();
  }
}
