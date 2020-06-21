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
    CHANNEL.messageBuilder(SwitchCastPacket.class, id++).encoder(SwitchCastPacket::encode).decoder(SwitchCastPacket::decode).consumer(SwitchCastPacket::handle).add();
    CHANNEL.messageBuilder(SyncEnergyNetworkPacket.class, id++).encoder(SyncEnergyNetworkPacket::encode).decoder(SyncEnergyNetworkPacket::decode).consumer(SyncEnergyNetworkPacket::handle).add();
    CHANNEL.messageBuilder(SyncHeatSinkerHeatPacket.class, id++).encoder(SyncHeatSinkerHeatPacket::encode).decoder(SyncHeatSinkerHeatPacket::decode).consumer(SyncHeatSinkerHeatPacket::handle).add();
    CHANNEL.messageBuilder(UpdateHeatNeighboursPacket.class, id++).encoder(UpdateHeatNeighboursPacket::encode).decoder(UpdateHeatNeighboursPacket::decode).consumer(UpdateHeatNeighboursPacket::handle).add();
    CHANNEL.messageBuilder(UpdateClayMetalMixerNeighboursPacket.class, id++).encoder(UpdateClayMetalMixerNeighboursPacket::encode).decoder(UpdateClayMetalMixerNeighboursPacket::decode).consumer(UpdateClayMetalMixerNeighboursPacket::handle).add();

    CHANNEL.messageBuilder(ChangeCreativeGeneratorEnergyPacket.class, id++).encoder(ChangeCreativeGeneratorEnergyPacket::encode).decoder(ChangeCreativeGeneratorEnergyPacket::decode).consumer(ChangeCreativeGeneratorEnergyPacket::handle).add();
    CHANNEL.messageBuilder(ChangeCreativeSinkerEnergyPacket.class, id++).encoder(ChangeCreativeSinkerEnergyPacket::encode).decoder(ChangeCreativeSinkerEnergyPacket::decode).consumer(ChangeCreativeSinkerEnergyPacket::handle).add();
  }
}
