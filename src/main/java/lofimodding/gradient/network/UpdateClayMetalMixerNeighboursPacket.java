package lofimodding.gradient.network;

import lofimodding.gradient.tileentities.ClayMetalMixerTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class UpdateClayMetalMixerNeighboursPacket {
  public static void send(final DimensionType dimension, final BlockPos pos) {
    Packets.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 256.0d, dimension)), new UpdateClayMetalMixerNeighboursPacket(pos));
  }

  private final BlockPos entityPos;

  public UpdateClayMetalMixerNeighboursPacket(final BlockPos entityPos) {
    this.entityPos = entityPos;
  }

  public static void encode(final UpdateClayMetalMixerNeighboursPacket packet, final PacketBuffer buffer) {
    buffer.writeBlockPos(packet.entityPos);
  }

  public static UpdateClayMetalMixerNeighboursPacket decode(final PacketBuffer buffer) {
    return new UpdateClayMetalMixerNeighboursPacket(buffer.readBlockPos());
  }

  public static boolean handle(final UpdateClayMetalMixerNeighboursPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      final ClayMetalMixerTile te = WorldUtils.getTileEntity(Minecraft.getInstance().world, packet.entityPos, ClayMetalMixerTile.class);

      if(te == null) {
        return;
      }

      te.updateAllSides();
      te.outputUpdated();
    });

    return true;
  }
}
