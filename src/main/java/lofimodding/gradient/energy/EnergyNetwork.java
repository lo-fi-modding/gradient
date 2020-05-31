package lofimodding.gradient.energy;

import lofimodding.gradient.Config;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.utils.Tuple;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class EnergyNetwork<STORAGE extends IEnergyStorage, TRANSFER extends IEnergyTransfer> {
  final Capability<STORAGE> storage;
  final Capability<TRANSFER> transfer;

  private final List<EnergyNetworkSegment<STORAGE, TRANSFER>> networks = new ArrayList<>();
  public final DimensionType dimension;
  private final IBlockReader world;

  private final ConcurrentLinkedDeque<QueuedAction> nodeQueue = new ConcurrentLinkedDeque<>();

  private final Map<BlockPos, TileEntity> allNodes = new HashMap<>();

  private final EnergyNetworkState state = new EnergyNetworkState();

  public EnergyNetwork(final DimensionType dimension, final IBlockReader world, final Capability<STORAGE> storage, final Capability<TRANSFER> transfer) {
    this.dimension = dimension;
    this.world = world;
    this.storage = storage;
    this.transfer = transfer;

    this.state.setCapabilities(storage, transfer);
  }

  private final Map<STORAGE, Tuple<BlockPos, Direction>> tickSinkNodes = new HashMap<>();
  private final Map<TRANSFER, Tuple<BlockPos, Direction>> tickTransferNodes = new HashMap<>();

  public EnergyNetworkState tick() {
    QueuedAction queue;
    while((queue = this.nodeQueue.pollFirst()) != null) {
      switch(queue.action) {
        case CONNECT:
          this.connect(queue.pos, queue.te);
          break;

        case DISCONNECT:
          this.disconnect(queue.pos);
          break;
      }
    }

    if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
      Gradient.LOGGER.info("Ticking {}", this);
    }

    this.state.reset();

    this.tickSinkNodes.clear();
    this.tickTransferNodes.clear();

    for(final TileEntity te : this.allNodes.values()) {
      if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
        Gradient.LOGGER.info("Checking {}", te);
      }

      for(final Direction facing : Direction.values()) {
        te.getCapability(this.storage, facing).ifPresent(storage -> {
          if(storage.canSink()) {
            this.tickSinkNodes.put(storage, new Tuple<>(te.getPos(), facing));
          }
        });

        te.getCapability(this.transfer, facing).ifPresent(transfer -> {
          this.tickTransferNodes.put(transfer, new Tuple<>(te.getPos(), facing));
        });
      }
    }

    for(final Map.Entry<TRANSFER, Tuple<BlockPos, Direction>> entry : this.tickTransferNodes.entrySet()) {
      final TRANSFER transfer = entry.getKey();
      final BlockPos pos = entry.getValue().a;
      final Direction facing = entry.getValue().b;

      if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
        Gradient.LOGGER.info("Resetting transfer {}", transfer);
      }

      this.state.addTransfer(pos, facing, transfer.getEnergyTransferred());
      transfer.resetEnergyTransferred();
    }

    for(final Map.Entry<STORAGE, Tuple<BlockPos, Direction>> entry : this.tickSinkNodes.entrySet()) {
      final STORAGE sink = entry.getKey();
      final BlockPos pos = entry.getValue().a;
      final Direction facing = entry.getValue().b;

      if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
        Gradient.LOGGER.info("Ticking sink {} @ {}", sink, pos);
      }

      final float requested = sink.getRequestedEnergy();

      if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
        Gradient.LOGGER.info("{} requesting {} energy", sink, requested);
      }

      if(requested != 0.0f) {
        final float energy = this.requestEnergy(pos, requested);

        if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
          Gradient.LOGGER.info("{} got {} energy", sink, energy);
        }

        if(energy != 0.0f) {
          sink.sinkEnergy(energy, IEnergyStorage.Action.EXECUTE);
          this.state.addStorage(pos, facing, sink.getEnergy());
        }
      }
    }

    return this.state;
  }

  public int size() {
    return this.networks.size();
  }

  public List<EnergyNetworkSegment<STORAGE, TRANSFER>> getNetworksForBlock(final BlockPos pos) {
    final List<EnergyNetworkSegment<STORAGE, TRANSFER>> networks = new ArrayList<>();

    for(final EnergyNetworkSegment<STORAGE, TRANSFER> network : this.networks) {
      if(network.contains(pos)) {
        networks.add(network);
      }
    }

    return networks;
  }

  private boolean inConnectMethod;

  public void queueConnection(final BlockPos newNodePos, final TileEntity newTe) {
    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Adding {} @ {} to connection queue", newTe, newNodePos);
    }

    this.nodeQueue.addLast(new QueuedAction(Action.CONNECT, newNodePos, newTe));
  }

  public void queueDisconnection(final BlockPos newNodePos) {
    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Adding {} to disconnection queue", newNodePos);
    }

    this.nodeQueue.addLast(new QueuedAction(Action.DISCONNECT, newNodePos));
  }

  public Map<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> connect(final BlockPos newNodePos, final TileEntity newTe) {
    if(this.inConnectMethod) {
      throw new RuntimeException("Attempting to re-enter connect method! " + newTe + " @ " + newNodePos);
    }

    this.inConnectMethod = true;

    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Attempting to add {} @ {} to a network...", newTe, newNodePos);
    }

    final Map<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> added = new HashMap<>();
    final Map<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> merge = new HashMap<>();

    for(final Direction facing : Direction.values()) {
      final BlockPos networkPos = newNodePos.offset(facing);
      final TileEntity worldTe = this.world.getTileEntity(networkPos);

      // If there's no TE, there's no network
      if(worldTe == null) {
        continue;
      }

      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("Found adjacent TE {} @ {} ({})", worldTe, networkPos, facing);
      }

      final Direction opposite = facing.getOpposite();

      if(newTe.getCapability(this.storage, facing).isPresent()) {
        if(worldTe.getCapability(this.storage, opposite).isPresent()) {
          // New network if we can't connect to the storage's network (we can only connect if it's the only block in the network)
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Both TEs are storages");
          }

          this.addToOrCreateNetwork(newNodePos, newTe, networkPos, worldTe, added);
        } else if(worldTe.getCapability(this.transfer, opposite).isPresent()) {
          // Add to network, no merge
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("New TE is storage, world TE is transfer");
          }

          this.addToOrCreateNetwork(newNodePos, newTe, networkPos, worldTe, added);
        } else {
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Unconnectable");
          }
        }
      } else if(newTe.getCapability(this.transfer, facing).isPresent()) {
        if(worldTe.getCapability(this.storage, opposite).isPresent()) {
          // If worldTe is in its own network, add (may have to merge)
          // If worldTe is in an existing network, new (may have to merge)
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("New TE is transfer, world TE is storage");
          }

          final Map<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> networks = new EnumMap<>(Direction.class);
          this.addToOrCreateNetwork(newNodePos, newTe, networkPos, worldTe, networks);
          added.putAll(networks);
          merge.putAll(networks);
        } else if(worldTe.getCapability(this.transfer, opposite).isPresent()) {
          // Add to network (may have to merge)
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Both TEs are transfers");
          }

          final Map<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> networks = new HashMap<>();
          this.addToOrCreateNetwork(newNodePos, newTe, networkPos, worldTe, networks);
          added.putAll(networks);
          merge.putAll(networks);
        } else {
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Unconnectable");
          }
        }
      }
    }

    while(merge.size() > 1) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("Merging networks");
      }

      final Iterator<Map.Entry<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>>> it = merge.entrySet().iterator();
      final Map.Entry<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> firstEntry = it.next();
      final Direction firstFacing = firstEntry.getKey();
      final EnergyNetworkSegment<STORAGE, TRANSFER> firstNetwork = firstEntry.getValue();
      it.remove();

      while(it.hasNext()) {
        final Map.Entry<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> otherEntry = it.next();
        final Direction otherFacing = otherEntry.getKey();
        final EnergyNetworkSegment<STORAGE, TRANSFER> otherNetwork = otherEntry.getValue();

        if(firstNetwork == otherNetwork) {
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Skipping merge - same network");
          }

          it.remove();
          continue;
        }

        final EnergyNetworkSegment.EnergyNode firstNode = firstNetwork.getNode(newNodePos);
        final EnergyNetworkSegment.EnergyNode otherNode = otherNetwork.getNode(newNodePos);

        if(firstNode.te.getCapability(this.transfer, firstFacing) == otherNode.te.getCapability(this.transfer, otherFacing)) {
          this.networks.remove(otherNetwork);
          otherNetwork.invalidate();

          firstNetwork.merge(otherNetwork);
          added.put(otherFacing, firstNetwork);
          it.remove();

          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("There are now {} networks", this.size());
          }
        }
      }
    }

    // There were no adjacent nodes, create a new network
    if(added.isEmpty()) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("No adjacent nodes, creating new network");
      }

      final EnergyNetworkSegment<STORAGE, TRANSFER> network = new EnergyNetworkSegment<>(this.storage, this.transfer);
      network.connect(newNodePos, newTe);
      this.networks.add(network);
      added.put(null, network);

      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("There are now {} networks", this.size());
      }
    }

    this.allNodes.put(newNodePos, newTe);

    if(this.getNetworksForBlock(newNodePos).isEmpty()) {
      Gradient.LOGGER.error("MISSING ADD {}", newNodePos);
    }

    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Finished adding {} @ {}", newTe, newNodePos);
    }

    this.inConnectMethod = false;

    return added;
  }

  private void addToOrCreateNetwork(final BlockPos newNodePos, final TileEntity newTe, final BlockPos networkPos, final TileEntity worldTe, final Map<Direction, EnergyNetworkSegment<STORAGE, TRANSFER>> added) {
    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Trying to add new TE {} @ {} to existing networks at {} ({})", newTe, newNodePos, networkPos, this.getNetworksForBlock(networkPos));
    }

    boolean connected = false;
    for(final EnergyNetworkSegment<STORAGE, TRANSFER> network : this.getNetworksForBlock(networkPos)) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("Trying network {}...", network);
      }

      if(network.connect(newNodePos, newTe)) {
        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("Success!");
        }

        added.put(WorldUtils.getFacingTowards(newNodePos, networkPos), network);
        connected = true;
      }
    }

    if(!connected) {
      // Create a new network if we couldn't connect
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("Failed to find a network to connect to, creating a new one");
      }

      final EnergyNetworkSegment<STORAGE, TRANSFER> network = new EnergyNetworkSegment<>(this.storage, this.transfer);
      this.allNodes.put(networkPos, worldTe);
      network.connect(networkPos, worldTe);
      network.connect(newNodePos, newTe);
      this.networks.add(network);

      if(this.getNetworksForBlock(networkPos).isEmpty()) {
        Gradient.LOGGER.error("MISSING NETWORKPOS {}", networkPos);
      }

      if(this.getNetworksForBlock(newNodePos).isEmpty()) {
        Gradient.LOGGER.error("MISSING NEWNODEPOS {}", newNodePos);
      }

      added.put(WorldUtils.getFacingTowards(newNodePos, networkPos), network);

      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("There are now {} networks", this.size());
      }
    }
  }

  public void disconnect(final BlockPos pos) {
    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Removing node {}", pos);
    }

    this.allNodes.remove(pos);

    for(final EnergyNetworkSegment<STORAGE, TRANSFER> network : this.getNetworksForBlock(pos)) {
      // Do we need to rebuild the network?
      if(network.disconnect(pos)) {
        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("Rebuilding network {}", network);
        }

        this.networks.remove(network);
        network.invalidate();

        for(final EnergyNetworkSegment.EnergyNode node : network.nodes.values()) {
          this.connect(node.pos, node.te);
        }
      }
    }
  }

  private final Map<EnergyNetworkSegment<STORAGE, TRANSFER>, Direction> extractNetworks = new HashMap<>();

  public float requestEnergy(final BlockPos requestPosition, final float amount) {
    for(final EnergyNetworkSegment<STORAGE, TRANSFER> network : this.networks) {
      if(network.contains(requestPosition)) {
        final EnergyNetworkSegment.EnergyNode node = network.getNode(requestPosition);

        for(final Direction side : Direction.values()) {
          final EnergyNetworkSegment.EnergyNode connection = node.connection(side);

          if(connection != null) {
            final Direction opposite = side.getOpposite();

            final LazyOptional<STORAGE> storage = connection.te.getCapability(this.storage, opposite);

            if(storage.isPresent()) {
              if(storage.map(IEnergyNode::canSource).orElse(Boolean.FALSE)) {
                this.extractNetworks.put(network, side);
                break;
              }
            } else {
              final LazyOptional<TRANSFER> transfer = connection.te.getCapability(this.transfer, opposite);

              if(transfer.isPresent()) {
                if(transfer.map(IEnergyNode::canSource).orElse(Boolean.FALSE)) {
                  this.extractNetworks.put(network, side);
                  break;
                }
              }
            }
          }
        }
      }
    }

    if(this.extractNetworks.isEmpty()) {
      if(Config.ENET.ENABLE_TICK_DEBUG.get()) {
        Gradient.LOGGER.info("Failed to get energy from any network");
      }

      return 0.0f;
    }

    float share = amount / this.extractNetworks.size();
    float deficit = 0.0f;
    float total = 0.0f;

    while(total < amount) {
      for(final Iterator<Map.Entry<EnergyNetworkSegment<STORAGE, TRANSFER>, Direction>> it = this.extractNetworks.entrySet().iterator(); it.hasNext(); ) {
        final Map.Entry<EnergyNetworkSegment<STORAGE, TRANSFER>, Direction> entry = it.next();
        final EnergyNetworkSegment<STORAGE, TRANSFER> network = entry.getKey();
        final Direction requestSide = entry.getValue();

        final float sourced = network.requestEnergy(requestPosition, requestSide, share);

        if(sourced < share) {
          deficit += share - sourced;
          it.remove();
        }

        total += sourced;
      }

      if(deficit == 0.0f || this.extractNetworks.isEmpty()) {
        break;
      }

      share = deficit / this.extractNetworks.size();
      deficit = 0.0f;
    }

    this.extractNetworks.clear();

    return total;
  }

  private enum Action {
    CONNECT, DISCONNECT
  }

  private static final class QueuedAction {
    private final Action action;
    private final BlockPos pos;
    @Nullable
    private final TileEntity te;

    private QueuedAction(final Action action, final BlockPos pos, final TileEntity te) {
      this.action = action;
      this.pos = pos;
      this.te = te;
    }

    private QueuedAction(final Action action, final BlockPos pos) {
      this.action = action;
      this.pos = pos;
      this.te = null;
    }
  }
}
