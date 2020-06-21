package lofimodding.gradient.network;

import lofimodding.gradient.tileentities.ToolStationTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public final class UpdateToolStationNeighboursPacket {
  public static void send(final DimensionType dimension, final BlockPos pos) {
    Packets.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 64.0d, dimension)), new UpdateToolStationNeighboursPacket(pos));
  }

  private final BlockPos pos;

  private UpdateToolStationNeighboursPacket(final BlockPos pos) {
    this.pos = pos;
  }

  public static void encode(final UpdateToolStationNeighboursPacket packet, final PacketBuffer buffer) {
    buffer.writeBlockPos(packet.pos);
  }

  public static UpdateToolStationNeighboursPacket decode(final PacketBuffer buffer) {
    return new UpdateToolStationNeighboursPacket(buffer.readBlockPos());
  }

  public static boolean handle(final UpdateToolStationNeighboursPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      final ToolStationTile te = WorldUtils.getTileEntity(Minecraft.getInstance().world, packet.pos, ToolStationTile.class);

      if(te != null) {
        te.updateNeighbours();
      }
    });

    return true;
  }
}
