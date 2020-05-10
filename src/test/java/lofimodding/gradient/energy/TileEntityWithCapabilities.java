package lofimodding.gradient.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TileEntityWithCapabilities extends TileEntity {
  public static TileEntityWithCapabilities sink() {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(1000.0f, 32.0f, 0.0f, 0.0f));
  }

  public static TileEntityWithCapabilities sink(final Direction... sides) {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(1000.0f, 32.0f, 0.0f, 0.0f), sides);
  }

  public static TileEntityWithCapabilities source() {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(1000.0f, 0.0f, 32.0f, 10000000.0f));
  }

  public static TileEntityWithCapabilities source(final Direction... sides) {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(1000.0f, 0.0f, 32.0f, 10000000.0f), sides);
  }

  public static TileEntityWithCapabilities storage() {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode());
  }

  public static TileEntityWithCapabilities storage(final Direction... sides) {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(), sides);
  }

  public static TileEntityWithCapabilities transfer() {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.TRANSFER, new TransferNode());
  }

  public static TileEntityWithCapabilities transfer(final Direction... sides) {
    return new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.TRANSFER, new TransferNode(), sides);
  }

  private final Map<Direction, Map<Capability, LazyOptional<Object>>> caps = new EnumMap<>(Direction.class);

  public TileEntityWithCapabilities() {
    super(null);
    for(final Direction side : Direction.values()) {
      this.caps.put(side, new HashMap<>());
    }
  }

  public <T> TileEntityWithCapabilities addCapability(final Capability<T> capability, final T obj, final Direction... sides) {
    final LazyOptional<Object> opt = LazyOptional.of(() -> obj);

    for(final Direction side : sides) {
      this.caps.get(side).put(capability, opt);
    }

    return this;
  }

  public <T> TileEntityWithCapabilities addCapability(final Capability<T> capability, final T obj) {
    return this.addCapability(capability, obj, Direction.values());
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    return this.caps.get(facing).containsKey(capability) ? (LazyOptional<T>)this.caps.get(facing).get(capability) : super.getCapability(capability, facing);
  }
}
