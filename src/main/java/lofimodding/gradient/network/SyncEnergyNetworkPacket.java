package lofimodding.gradient.network;

import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import lofimodding.gradient.energy.EnergyNetworkState;
import lofimodding.gradient.energy.IEnergyStorage;
import lofimodding.gradient.energy.IEnergyTransfer;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.function.Supplier;

public class SyncEnergyNetworkPacket {
  public static void send(final DimensionType dimension, final EnergyNetworkState state) {
    Packets.CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), new SyncEnergyNetworkPacket(state));
  }

  private static final IdentityHashMap<String, Capability<?>> providers = ObfuscationReflectionHelper.getPrivateValue(CapabilityManager.class, CapabilityManager.INSTANCE, "providers");

  private final EnergyNetworkState state;

  public SyncEnergyNetworkPacket(final EnergyNetworkState state) {
    this.state = state;
  }

  public static void encode(final SyncEnergyNetworkPacket packet, final PacketBuffer buffer) {
    buffer.writeInt(packet.state.getStorageCapability().getName().length());
    buffer.writeCharSequence(packet.state.getStorageCapability().getName(), StandardCharsets.UTF_8);

    buffer.writeInt(packet.state.getTransferCapability().getName().length());
    buffer.writeCharSequence(packet.state.getTransferCapability().getName(), StandardCharsets.UTF_8);

    buffer.writeInt(packet.state.storagesSize());

    for(final Long2FloatMap.Entry entry : packet.state.storageEntries()) {
      buffer.writeLong(entry.getLongKey());
      buffer.writeFloat(entry.getFloatValue());
    }

    buffer.writeInt(packet.state.transfersSize());

    for(final Long2FloatMap.Entry entry : packet.state.transferEntries()) {
      buffer.writeLong(entry.getLongKey());
      buffer.writeFloat(entry.getFloatValue());
    }
  }

  public static SyncEnergyNetworkPacket decode(final PacketBuffer buffer) {
    final EnergyNetworkState state = new EnergyNetworkState();

    final int storageNameLength = buffer.readInt();
    final String storageName = buffer.readCharSequence(storageNameLength, StandardCharsets.UTF_8).toString().intern();

    final int transferNameLength = buffer.readInt();
    final String transferName = buffer.readCharSequence(transferNameLength, StandardCharsets.UTF_8).toString().intern();

    state.setCapabilities((Capability<? extends IEnergyStorage>)providers.get(storageName), (Capability<? extends IEnergyTransfer>)providers.get(transferName));

    final int storageSize = buffer.readInt();

    for(int i = 0; i < storageSize; i++) {
      final long serialized = buffer.readLong();
      final float energy = buffer.readFloat();

      state.addStorage(serialized, energy);
    }

    final int transferSize = buffer.readInt();

    for(int i = 0; i < transferSize; i++) {
      final long serialized = buffer.readLong();
      final float energy = buffer.readFloat();

      state.addTransfer(serialized, energy);
    }

    return new SyncEnergyNetworkPacket(state);
  }

  public static boolean handle(final SyncEnergyNetworkPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      for(final Long2FloatMap.Entry entry : packet.state.storageEntries()) {
        final long serialized = entry.getLongKey();
        final float energy = entry.getFloatValue();

        final World world = Minecraft.getInstance().world;
        final BlockPos pos = WorldUtils.getBlockPosFromSerialized(serialized);

        if(world.isBlockLoaded(pos)) {
          final TileEntity te = world.getTileEntity(pos);

          if(te != null) {
            final Direction facing = WorldUtils.getFacingFromSerialized(serialized);
            te.getCapability(packet.state.getStorageCapability(), facing).ifPresent(storage -> storage.setEnergy(energy));
          }
        }
      }

      for(final Long2FloatMap.Entry entry : packet.state.transferEntries()) {
        final long serialized = entry.getLongKey();
        final float energy = entry.getFloatValue();

        final World world = Minecraft.getInstance().world;
        final BlockPos pos = WorldUtils.getBlockPosFromSerialized(serialized);

        if(world.isBlockLoaded(pos)) {
          final TileEntity te = world.getTileEntity(pos);

          if(te != null) {
            final Direction facing = WorldUtils.getFacingFromSerialized(serialized);
            te.getCapability(packet.state.getTransferCapability(), facing).ifPresent(storage -> storage.setEnergyTransferred(energy));
          }
        }
      }
    });

    return true;
  }
}
