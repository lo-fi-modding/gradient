package lofimodding.gradient.energy;

import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class EnergyNetworkState {
  private final Long2FloatMap storages = new Long2FloatOpenHashMap();
  private final Long2FloatMap transfers = new Long2FloatOpenHashMap();
  private Capability<? extends IEnergyStorage> storageCap;
  private Capability<? extends IEnergyTransfer> transferCap;

  public boolean isDirty() {
    return !this.storages.isEmpty();
  }

  void reset() {
    this.storages.clear();
    this.transfers.clear();
  }

  public void setCapabilities(final Capability<? extends IEnergyStorage> storage, final Capability<? extends IEnergyTransfer> transfer) {
    this.storageCap = storage;
    this.transferCap = transfer;
  }

  void addStorage(final BlockPos pos, final Direction facing, final float energy) {
    this.storages.put(WorldUtils.serializeBlockPosAndFacing(pos, facing), energy);
  }

  public void addStorage(final long serialized, final float energy) {
    this.storages.put(serialized, energy);
  }

  void addTransfer(final BlockPos pos, final Direction facing, final float energy) {
    this.transfers.put(WorldUtils.serializeBlockPosAndFacing(pos, facing), energy);
  }

  public void addTransfer(final long serialized, final float energy) {
    this.transfers.put(serialized, energy);
  }

  public int storagesSize() {
    return this.storages.size();
  }

  public int transfersSize() {
    return this.transfers.size();
  }

  public ObjectSet<Long2FloatMap.Entry> storageEntries() {
    return this.storages.long2FloatEntrySet();
  }

  public ObjectSet<Long2FloatMap.Entry> transferEntries() {
    return this.transfers.long2FloatEntrySet();
  }

  public Capability<? extends IEnergyStorage> getStorageCapability() {
    return this.storageCap;
  }

  public Capability<? extends IEnergyTransfer> getTransferCapability() {
    return this.transferCap;
  }
}
