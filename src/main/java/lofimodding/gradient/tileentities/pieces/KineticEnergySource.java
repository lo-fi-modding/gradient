package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class KineticEnergySource<Recipe extends IGradientRecipe, Tile extends ProcessorTile<Recipe, KineticEnergySource<Recipe, Tile>, Tile>> implements IEnergySource<Recipe, KineticEnergySource<Recipe, Tile>, Tile> {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyStorage node = new KineticEnergyStorage(1.0f, 1.0f, 0.0f);

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
    return false;
  }


  @Override
  public CompoundNBT write(final CompoundNBT nbt) {
    nbt.put("Energy", this.node.write());
    return super.write(nbt);
  }

  @Override
  public void read(final CompoundNBT nbt) {
    final CompoundNBT energy = nbt.getCompound("Energy");
    this.node.read(energy);
    super.read(nbt);
  }
}
