package lofimodding.gradient.network;

import lofimodding.gradient.tileentities.HeatSinkerTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class UpdateHeatNeighboursPacket {
  public static void send(final DimensionType dimension, final BlockPos sinkerPos, final BlockPos neighbourPos) {
    Packets.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(sinkerPos.getX(), sinkerPos.getY(), sinkerPos.getZ(), 64.0d, dimension)), new UpdateHeatNeighboursPacket(sinkerPos, neighbourPos));
  }

  private final BlockPos sinkerPos;
  private final BlockPos neighbourPos;

  private UpdateHeatNeighboursPacket(final BlockPos sinkerPos, final BlockPos neighbourPos) {
    this.sinkerPos = sinkerPos;
    this.neighbourPos = neighbourPos;
  }

  public static void encode(final UpdateHeatNeighboursPacket packet, final PacketBuffer buffer) {
    buffer.writeBlockPos(packet.sinkerPos);
    buffer.writeBlockPos(packet.neighbourPos);
  }

  public static UpdateHeatNeighboursPacket decode(final PacketBuffer buffer) {
    return new UpdateHeatNeighboursPacket(buffer.readBlockPos(), buffer.readBlockPos());
  }

  public static void handle(final UpdateHeatNeighboursPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      final HeatSinkerTile te = WorldUtils.getTileEntity(Minecraft.getInstance().world, packet.sinkerPos, HeatSinkerTile.class);

      if(te != null) {
        te.updateSink(packet.neighbourPos);
      }
    });
  }
}
