package lofimodding.gradient.network;

import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.items.UnhardenedClayCastItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SwitchCastPacket {
  public static void sendToServer(final GradientCasts cast) {
    Packets.CHANNEL.sendToServer(new SwitchCastPacket(cast));
  }

  private final GradientCasts cast;

  private SwitchCastPacket(final GradientCasts cast) {
    this.cast = cast;
  }

  public static void encode(final SwitchCastPacket packet, final PacketBuffer buffer) {
    buffer.writeVarInt(packet.cast.ordinal());
  }

  public static SwitchCastPacket decode(final PacketBuffer buffer) {
    final int cast = buffer.readVarInt();

    if(cast < 0 || cast >= GradientCasts.values().length) {
      return new SwitchCastPacket(GradientCasts.MATTOCK);
    }

    return new SwitchCastPacket(GradientCasts.values()[cast]);
  }

  public static void handle(final SwitchCastPacket packet, final Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      final ServerPlayerEntity player = ctx.get().getSender();

      if(!(player.getHeldItemMainhand().getItem() instanceof UnhardenedClayCastItem)) {
        return;
      }

      player.setHeldItem(Hand.MAIN_HAND, new ItemStack(GradientItems.UNHARDENED_CLAY_CAST(packet.cast).get(), player.getHeldItemMainhand().getCount()));
    });
  }
}
