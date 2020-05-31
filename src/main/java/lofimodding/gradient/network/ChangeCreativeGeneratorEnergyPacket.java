package lofimodding.gradient.network;

import lofimodding.gradient.tileentities.CreativeGeneratorTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeCreativeGeneratorEnergyPacket {
  public static void sendToServer(final BlockPos pos, final float energy) {
    Packets.CHANNEL.sendToServer(new ChangeCreativeGeneratorEnergyPacket(pos.toLong(), energy));
  }

  private final long pos;
  private final float energy;

  private ChangeCreativeGeneratorEnergyPacket(final long pos, final float energy) {
    this.pos = pos;
    this.energy = energy;
  }

  public static void encode(final ChangeCreativeGeneratorEnergyPacket packet, final PacketBuffer buffer) {
    buffer.writeLong(packet.pos);
    buffer.writeFloat(packet.energy);
  }

  public static ChangeCreativeGeneratorEnergyPacket decode(final PacketBuffer buffer) {
    final long pos = buffer.readLong();
    final float energy = buffer.readFloat();

    return new ChangeCreativeGeneratorEnergyPacket(pos, energy);
  }

  public static boolean handle(final ChangeCreativeGeneratorEnergyPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      final ServerPlayerEntity player = ctx.get().getSender();
      final ServerWorld world = player.getServerWorld();
      final BlockPos pos = BlockPos.fromLong(packet.pos);

      if(!world.isAreaLoaded(pos, 4) || player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > 256.0f) {
        return;
      }

      final CreativeGeneratorTile generator = WorldUtils.getTileEntity(world, pos, CreativeGeneratorTile.class);

      if(generator != null) {
        generator.setEnergy(packet.energy);
      }
    });

    return true;
  }
}
