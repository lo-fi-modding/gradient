package lofimodding.gradient.energy;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lofimodding.gradient.Config;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.utils.Tuple;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnergyNetworkSegment<STORAGE extends IEnergyStorage, TRANSFER extends IEnergyTransfer> {
  private final Capability<STORAGE> storage;
  private final Capability<TRANSFER> transfer;

  final Map<BlockPos, EnergyNode> nodes = new HashMap<>();

  private boolean invalidated;

  public EnergyNetworkSegment(final Capability<STORAGE> storage, final Capability<TRANSFER> transfer) {
    this.storage = storage;
    this.transfer = transfer;
  }

  public void invalidate() {
    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Invalidating {}", this);
    }

    this.invalidated = true;
  }

  public int size() {
    this.checkValidity();
    return this.nodes.size();
  }

  public boolean isEmpty() {
    this.checkValidity();
    return this.nodes.isEmpty();
  }

  public boolean contains(final BlockPos pos) {
    this.checkValidity();
    return this.nodes.containsKey(pos);
  }

  public boolean connect(final BlockPos newNodePos, final TileEntity te) {
    this.checkValidity();
    return this.connect(newNodePos, te, false);
  }

  private boolean connect(final BlockPos newNodePos, final TileEntity te, final boolean force) {
    this.checkValidity();

    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Adding node {} to enet {} @ {}", te, this, newNodePos);
    }

    // First node is always accepted
    if(this.nodes.isEmpty()) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("First node, adding");
      }

      this.nodes.put(newNodePos, new EnergyNode(newNodePos, te));
      return true;
    }

    // If we have a node here already, check to see if it's the same one
    if(this.contains(newNodePos)) {
      final EnergyNode existing = this.getNode(newNodePos);

      if(existing.te == te) {
        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("{} is already connected at {}", te, newNodePos);
        }

        return true;
      }

      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("There is already a different node connected at {}: {}", newNodePos, existing.te);
      }

      return false;
    }

    EnergyNode newNode = null;

    Map<Direction, EnergyNode> conditionalConnections = null;

    for(final Map.Entry<BlockPos, EnergyNode> entry : this.nodes.entrySet()) {
      final BlockPos nodePos = entry.getKey();
      final EnergyNode node = entry.getValue();

      final Direction facing = WorldUtils.areBlocksAdjacent(newNodePos, nodePos);

      if(facing != null) {
        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("New node is adjacent to {} on facing {}", node, facing);
        }

        final IEnergyNode teNode;

        final LazyOptional<STORAGE> storage = te.getCapability(this.storage, facing);

        if(storage.isPresent()) {
          // Storage nodes can't connect to other storage nodes unless it's the only one
          if(!force && node.te.getCapability(this.storage, facing.getOpposite()).isPresent() && this.nodes.size() > 1) {
            if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
              Gradient.LOGGER.info("Adjacent node is storage - moving on");
            }

            continue;
          }

          teNode = storage.orElseThrow(RuntimeException::new);
        } else {
          final LazyOptional<TRANSFER> transfer = te.getCapability(this.transfer, facing);

          if(transfer.isPresent()) {
            // Networks are split by storage nodes (a transfer node can connect to a storage node if it is the only node)
            // Transfer nodes can also connect to storage nodes if the transfer node will be connecting to another transfer node
            if(!force && node.te.getCapability(this.storage, facing.getOpposite()).isPresent() && this.nodes.size() > 1) {
              if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
                Gradient.LOGGER.info("Adjacent node is storage - deferring");
              }

              if(conditionalConnections == null) {
                conditionalConnections = new EnumMap<>(Direction.class);
              }

              conditionalConnections.put(facing, node);
              continue;
            }

            teNode = transfer.orElseThrow(RuntimeException::new);
          } else {
            continue;
          }
        }

        final boolean canConnect = this.canConnect(teNode, node, facing.getOpposite());

        if(!force && !canConnect) {
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Adjacent node is not connectable");
          }

          continue;
        }

        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("Connecting!");
        }

        if(newNode == null) {
          newNode = new EnergyNode(newNodePos, te);
        }

        if(canConnect) {
          newNode.connections.put(facing, node);
          node.connections.put(facing.getOpposite(), newNode);
        }
      }
    }

    // If we made a connection, attempt to link up any deferred storages
    if(newNode != null && conditionalConnections != null) {
      for(final Map.Entry<Direction, EnergyNode> entry : conditionalConnections.entrySet()) {
        final Direction facing = entry.getKey();
        final EnergyNode node = entry.getValue();

        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("Checking deferred connection {}", facing);
        }

        if(!this.canConnect(te.getCapability(this.transfer, facing).orElseThrow(RuntimeException::new), node, facing.getOpposite())) {
          if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
            Gradient.LOGGER.info("Adjacent node is not connectable");
          }

          continue;
        }

        if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
          Gradient.LOGGER.info("Connecting!");
        }

        newNode.connections.put(facing, node);
        node.connections.put(facing.getOpposite(), newNode);
      }
    }

    if(force && newNode == null) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("No connections possible but force is true, connecting anyway");
      }

      newNode = new EnergyNode(newNodePos, te);
    }

    if(newNode != null) {
      this.nodes.put(newNodePos, newNode);
      return true;
    }

    return false;
  }

  /**
   * @return true if this network needs to be rebuilt or deleted (i.e. empty) by the manager
   */
  public boolean disconnect(final BlockPos pos) {
    this.checkValidity();

    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Removing {} from {}", pos, this);
    }

    if(!this.nodes.containsKey(pos)) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("Node {} did not exist in {}", pos, this);
      }

      return false;
    }

    final EnergyNode node = this.getNode(pos);
    this.nodes.remove(pos);

    if(node.connections.isEmpty()) {
      return true;
    }

    // Remove the neighbour's connection to this node
    for(final Map.Entry<Direction, EnergyNode> connection : node.connections.entrySet()) {
      connection.getValue().connections.remove(connection.getKey().getOpposite());
    }

    final EnergyNode firstNeighbour = node.connections.values().iterator().next();

    // See if we can still access the other nodes
    connectionLoop:
    for(final Map.Entry<Direction, EnergyNode> connection : node.connections.entrySet()) {
      if(connection.getValue() != firstNeighbour) {
        for(final Direction startFacing : Direction.values()) {
          if(!firstNeighbour.connections.containsKey(startFacing)) {
            continue;
          }

          for(final Direction goalFacing : Direction.values()) {
            if(!connection.getValue().connections.containsKey(goalFacing)) {
              continue;
            }

            // If we path successfully, it's connected
            if(!this.pathFind(firstNeighbour.pos, startFacing, connection.getValue().pos, goalFacing).isEmpty()) {
              // Continue on to the next connection
              continue connectionLoop;
            }
          }
        }

        return true;
      }
    }

    return false;
  }

  private boolean canConnect(final IEnergyNode newNode, final EnergyNode existingNode, final Direction facing) {
    final IEnergyNode existing;

    final LazyOptional<STORAGE> storage = existingNode.te.getCapability(this.storage, facing);

    if(storage.isPresent()) {
      existing = storage.orElseThrow(RuntimeException::new);
    } else {
      final LazyOptional<TRANSFER> transfer = existingNode.te.getCapability(this.transfer, facing);

      if(transfer.isPresent()) {
        existing = transfer.orElseThrow(RuntimeException::new);
      } else {
        return false;
      }
    }

    return newNode.canSink() && existing.canSource() || newNode.canSource() && existing.canSink();
  }

  public EnergyNode getNode(final BlockPos pos) {
    return this.nodes.get(pos);
  }

  private final Set<STORAGE> availableEnergySources = new HashSet<>();

  public float getAvailableEnergy() {
    this.checkValidity();

    float available = 0.0f;

    for(final EnergyNode node : this.nodes.values()) {
      for(final Map.Entry<Direction, EnergyNode> connection : node.connections.entrySet()) {
        available += node.te.getCapability(this.storage, connection.getKey()).map(storage -> {
          if(storage.canSource() && connection.getValue() != null && !this.availableEnergySources.contains(storage)) {
            this.availableEnergySources.add(storage);
            return storage.sourceEnergy(storage.getEnergy(), true);
          }

          return 0.0f;
        }).orElse(0.0f);
      }
    }

    this.availableEnergySources.clear();

    return available;
  }

  private final Map<STORAGE, List<BlockPos>> extractEnergySources = new HashMap<>();

  public float requestEnergy(final BlockPos sink, final Direction sinkSide, final float amount) {
    this.checkValidity();

    // Find all of the energy sources
    for(final EnergyNode node : this.nodes.values()) {
      for(final Map.Entry<Direction, EnergyNode> connection : node.connections.entrySet()) {
        if(sink.equals(node.pos) && sinkSide == connection.getKey()) {
          continue;
        }

        node.te.getCapability(this.storage, connection.getKey()).ifPresent(storage -> {
          if(storage.canSource() && storage.getEnergy() != 0.0f && connection.getValue() != null) {
            final List<BlockPos> path = this.pathFind(sink, sinkSide, node.pos, connection.getKey());

            if(path.isEmpty()) {
              return;
            }

            // Don't overwrite shorter paths
            if(this.extractEnergySources.containsKey(storage)) {
              if(this.extractEnergySources.get(storage).size() <= path.size()) {
                return;
              }
            }

            this.extractEnergySources.put(storage, path);
          }
        });
      }
    }

    if(this.extractEnergySources.isEmpty()) {
      return 0.0f;
    }

    float share = amount / this.extractEnergySources.size();
    float deficit = 0.0f;
    float total = 0.0f;

    while(total < amount) {
      for(final Iterator<Map.Entry<STORAGE, List<BlockPos>>> it = this.extractEnergySources.entrySet().iterator(); it.hasNext(); ) {
        final Map.Entry<STORAGE, List<BlockPos>> entry = it.next();

        final STORAGE source = entry.getKey();
        final List<BlockPos> path = entry.getValue();

        final float sourced = source.sourceEnergy(share, false);

        if(sourced < share) {
          deficit += share - sourced;
          it.remove();
        }

        for(int i = 1; i < path.size() - 1; i++) {
          final BlockPos pathPos = path.get(i);
          final Direction facingFrom = WorldUtils.getFacingTowards(pathPos, path.get(i - 1));
          final Direction facingTo = WorldUtils.getFacingTowards(pathPos, path.get(i + 1));
          final TileEntity transferEntity = this.getNode(pathPos).te;

          transferEntity.getCapability(this.transfer, facingFrom).ifPresent(transfer -> {
            if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
              Gradient.LOGGER.info("Routing {} through {}", sourced, pathPos);
            }

            transfer.transfer(sourced, facingFrom, facingTo);
          });
        }

        total += sourced;
      }

      if(deficit == 0.0f || this.extractEnergySources.isEmpty()) {
        break;
      }

      share = deficit / this.extractEnergySources.size();
      deficit = 0.0f;
    }

    this.extractEnergySources.clear();

    return total;
  }

  private final Set<Tuple<BlockPos, Direction>> closed = new HashSet<>();
  private final Set<Tuple<BlockPos, Direction>> open = new HashSet<>();

  private final Map<Tuple<BlockPos, Direction>, Tuple<BlockPos, Direction>> cameFrom = new HashMap<>();
  private final Object2IntMap<Tuple<BlockPos, Direction>> gScore = new Object2IntOpenHashMap<>();
  private final Object2IntMap<Tuple<BlockPos, Direction>> fScore = new Object2IntLinkedOpenHashMap<>();

  public List<BlockPos> pathFind(final BlockPos start, final Direction startFacing, final BlockPos goal, final Direction goalFacing) {
    this.checkValidity();

    this.closed.clear();
    this.open.clear();
    this.cameFrom.clear();
    this.gScore.clear();
    this.fScore.clear();

    this.gScore.defaultReturnValue(Integer.MAX_VALUE);
    this.fScore.defaultReturnValue(Integer.MAX_VALUE);

    final Tuple<BlockPos, Direction> startTuple = new Tuple<>(start, startFacing);
    final Tuple<BlockPos, Direction> goalTuple = new Tuple<>(goal, goalFacing);

    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("Starting pathfind at {} {}, goal {} {}", start, startFacing, goal, goalFacing);
    }

    this.closed.add(startTuple);
    this.gScore.put(startTuple, 0);
    this.pathFindSide(startFacing, this.getNode(start), startTuple, goalTuple);

    while(!this.open.isEmpty()) {
      final Tuple<BlockPos, Direction> current = this.getLowest(this.fScore);

      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("Current = {} {}", current.a, current.b);
      }

      if(current.equals(goalTuple)) {
        if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
          Gradient.LOGGER.info("GOAL!");
        }

        return this.reconstructPath(this.cameFrom, goalTuple);
      }

      this.open.remove(current);
      this.fScore.removeInt(current);
      this.closed.add(current);

      final EnergyNode currentNode = this.getNode(current.a);

      for(final Direction side : Direction.values()) {
        this.pathFindSide(side, currentNode, current, goalTuple);
      }
    }

    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("Pathfinding failed");
    }

    return new ArrayList<>();
  }

  private void pathFindSide(final Direction side, final EnergyNode currentNode, final Tuple<BlockPos, Direction> currentTuple, final Tuple<BlockPos, Direction> goalTuple) {
    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("Checking side {}, came from {} {}", side, currentTuple.a, currentTuple.b);
    }

    final EnergyNode neighbourNode = currentNode.connection(side);

    if(neighbourNode == null) {
      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("No node, skipping");
      }

      return;
    }

    final BlockPos neighbour = currentTuple.a.offset(side);

    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("Found {}", neighbour);
    }

    final Direction opposite = side.getOpposite();
    final Tuple<BlockPos, Direction> neighbourTuple = new Tuple<>(neighbour, opposite);

    if(!neighbourNode.te.getCapability(this.transfer, opposite).isPresent() && !neighbourTuple.equals(goalTuple)) {
      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("Not a transfer node, skipping");
      }

      return;
    }

    if(this.closed.contains(neighbourTuple)) {
      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("Already visited, skipping");
      }

      return;
    }

    // Make sure the side we're trying to leave from is the same transfer node that we entered from
    if(currentNode.te.getCapability(this.transfer, currentTuple.b) != currentNode.te.getCapability(this.transfer, side)) {
      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("Sides have different transfer nodes, skipping");
      }

      return;
    }

    final int g = this.gScore.getInt(currentTuple) + 1; // 1 = distance

    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("New G = {}, current G = {}", g, this.gScore.getInt(neighbourTuple));
    }

    if(g >= this.gScore.getInt(neighbourTuple)) {
      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("G >= neighbour");
      }

      return;
    }

    this.open.add(neighbourTuple);
    this.closed.add(new Tuple<>(currentTuple.a, side));
    this.cameFrom.put(neighbourTuple, currentTuple);
    this.gScore.put(neighbourTuple, g);
    this.fScore.put(neighbourTuple, g + this.pathFindHeuristic(neighbour, goalTuple.a));

    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("Adding node {} G = {} F = {}", neighbour, this.gScore.getInt(neighbourTuple), this.fScore.getInt(neighbourTuple));
    }
  }

  private List<BlockPos> reconstructPath(final Map<Tuple<BlockPos, Direction>, Tuple<BlockPos, Direction>> cameFrom, final Tuple<BlockPos, Direction> goal) {
    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("Path:");
    }

    final List<BlockPos> path = new ArrayList<>();
    path.add(goal.a);

    if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
      Gradient.LOGGER.info("{} {}", goal.a, goal.b);
    }

    Tuple<BlockPos, Direction> current = goal;

    while(cameFrom.containsKey(current)) {
      current = cameFrom.get(current);
      path.add(current.a);

      if(Config.ENET.ENABLE_PATH_DEBUG.get()) {
        Gradient.LOGGER.info("{} {}", current.a, current.b);
      }
    }

    return path;
  }

  private int pathFindHeuristic(final BlockPos current, final BlockPos goal) {
    return (int)current.distanceSq(goal);
  }

  private Tuple<BlockPos, Direction> getLowest(final Object2IntMap<Tuple<BlockPos, Direction>> values) {
    int lowest = Integer.MAX_VALUE;
    Tuple<BlockPos, Direction> pos = null;

    for(final Object2IntMap.Entry<Tuple<BlockPos, Direction>> entry : values.object2IntEntrySet()) {
      if(entry.getIntValue() <= lowest) {
        lowest = entry.getIntValue();
        pos = entry.getKey();
      }
    }

    return pos;
  }

  private void checkValidity() {
    if(this.invalidated) {
      throw new RuntimeException("Trying to use invalidated energy network " + this);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " (" + this.nodes.size() + " nodes)";
  }

  public static final class EnergyNode {
    public final BlockPos pos;
    public final TileEntity te;
    private final Map<Direction, EnergyNode> connections = new EnumMap<>(Direction.class);

    private EnergyNode(final BlockPos pos, final TileEntity te) {
      this.pos = pos;
      this.te  = te;
    }

    @Nullable
    public EnergyNode connection(final Direction side) {
      return this.connections.get(side);
    }

    @Override
    public String toString() {
      return "Node holder {" + this.te + "} @ " + this.pos + " connections {" + String.join(", ", this.connections.keySet().stream().map(Direction::toString).toArray(String[]::new)) + '}';
    }
  }

  void merge(final EnergyNetworkSegment<STORAGE, TRANSFER> other) {
    if(this == other) {
      if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
        Gradient.LOGGER.info("Skipping merge - same network");
      }

      return;
    }

    if(Config.ENET.ENABLE_NODE_DEBUG.get()) {
      Gradient.LOGGER.info("Merging network {} into {}", other, this);
    }

    for(final Map.Entry<BlockPos, EnergyNode> node : other.nodes.entrySet()) {
      this.connect(node.getKey(), node.getValue().te, true);
    }
  }
}
