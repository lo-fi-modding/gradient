package lofimodding.gradient.network;

import lofimodding.gradient.Gradient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class Packets {
  private Packets() { }

  private static final String PROTOCOL_VERSION = "1";
  static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
    .named(new ResourceLocation(Gradient.MOD_ID, "main_channel"))
    .clientAcceptedVersions(PROTOCOL_VERSION::equals)
    .serverAcceptedVersions(PROTOCOL_VERSION::equals)
    .networkProtocolVersion(() -> PROTOCOL_VERSION)
    .simpleChannel();

  private static int id;

  public static void register() {
    Gradient.LOGGER.info("Registering packets...");
    CHANNEL.registerMessage(id++, SwitchCastPacket.class, SwitchCastPacket::encode, SwitchCastPacket::decode, SwitchCastPacket::handle);
    CHANNEL.registerMessage(id++, UpdateHeatNeighboursPacket.class, UpdateHeatNeighboursPacket::encode, UpdateHeatNeighboursPacket::decode, UpdateHeatNeighboursPacket::handle);
  }
}
