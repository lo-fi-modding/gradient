package lofimodding.gradient.energy;

import lofimodding.gradient.Config;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.network.SyncEnergyNetworkPacket;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public final class EnergyNetworkManager {
  private static final Map<World, List<EnergyNetwork<?, ?>>> networks = new HashMap<>();

  public static <STORAGE extends IEnergyStorage, TRANSFER extends IEnergyTransfer> EnergyNetwork<STORAGE, TRANSFER> getManager(final World world, final Capability<STORAGE> storage, final Capability<TRANSFER> transfer) {
    if(world.isRemote) {
      Gradient.LOGGER.error("Attempted to access energy network from client");
      throw new RuntimeException("Attempted to access energy network from client");
    }

    final List<EnergyNetwork<?, ?>> list = networks.computeIfAbsent(world, k -> new ArrayList<>());

    for(final EnergyNetwork<?, ?> manager : list) {
      if(manager.storage == storage && manager.transfer == transfer) {
        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("Using manager {}", manager);
        }

        return (EnergyNetwork<STORAGE, TRANSFER>)manager;
      }
    }

    final EnergyNetwork<STORAGE, TRANSFER> network = new EnergyNetwork<>(world.getDimension().getType(), world, storage, transfer);

    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("New manager {}", network);
    }

    list.add(network);
    return network;
  }

  @SubscribeEvent
  public static void onServerTick(final TickEvent.ServerTickEvent event) {
    if(event.phase == TickEvent.Phase.START) {
      for(final List<EnergyNetwork<?, ?>> networks : networks.values()) {
        for(final EnergyNetwork<?, ?> network : networks) {
          final EnergyNetworkState state = network.tick();

          if(state.isDirty()) {
            SyncEnergyNetworkPacket.send(network.dimension, state);
          }
        }
      }
    }
  }
}
