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

public class SyncHeatSinkerHeatPacket {
  public static void send(final DimensionType dimension, final BlockPos sinkerPos, final float heat) {
    Packets.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(sinkerPos.getX(), sinkerPos.getY(), sinkerPos.getZ(), 64.0d, dimension)), new SyncHeatSinkerHeatPacket(sinkerPos, heat));
  }

  private final BlockPos sinkerPos;
  private final float heat;

  private SyncHeatSinkerHeatPacket(final BlockPos sinkerPos, final float heat) {
    this.sinkerPos = sinkerPos;
    this.heat = heat;
  }

  public static void encode(final SyncHeatSinkerHeatPacket packet, final PacketBuffer buffer) {
    buffer.writeBlockPos(packet.sinkerPos);
    buffer.writeFloat(packet.heat);
  }

  public static SyncHeatSinkerHeatPacket decode(final PacketBuffer buffer) {
    return new SyncHeatSinkerHeatPacket(buffer.readBlockPos(), buffer.readFloat());
  }

  public static boolean handle(final SyncHeatSinkerHeatPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      final HeatSinkerTile te = WorldUtils.getTileEntity(Minecraft.getInstance().world, packet.sinkerPos, HeatSinkerTile.class);

      if(te != null) {
        te.setHeat(packet.heat);
      }
    });

    return true;
  }
}
