package lofimodding.gradient.energy;

import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.Callable;

@DisplayName("Energy Network")
class EnergyNetworkSegmentTest {
  static final Capability<IEnergyStorage> STORAGE = newCap("STORAGE");
  static final Capability<IEnergyTransfer> TRANSFER = newCap("TRANSFER");

  private EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> net;

  @BeforeEach
  void setUp() {
    this.net = new EnergyNetworkSegment<>(STORAGE, TRANSFER);
  }

  @Test
  void testContainsEmptyNetwork() {
    final boolean contains = this.net.contains(BlockPos.ZERO);

    Assertions.assertFalse(contains, "Contains didn't return false for empty net");
  }

  @Test
  void testAddingTransferNodeToEmptyNetwork() {
    final BlockPos pos = BlockPos.ZERO;
    final boolean success = this.net.connect(pos, TileEntityWithCapabilities.transfer());

    Assertions.assertTrue(success, "Failed to add node to empty net");
    Assertions.assertTrue(this.net.contains(pos), "Net doesn't contain new node");
  }

  @Test
  void testAddingStorageNodeToEmptyNetwork() {
    final BlockPos pos = BlockPos.ZERO;
    final boolean success = this.net.connect(pos, TileEntityWithCapabilities.storage());

    Assertions.assertTrue(success, "Failed to add node to empty net");
    Assertions.assertTrue(this.net.contains(pos), "Net doesn't contain new node");
  }

  @Test
  void testNodeLinkage() {
    final BlockPos pos1 = BlockPos.ZERO;
    final BlockPos pos2 = pos1.east();
    final BlockPos pos3 = pos2.south();
    final BlockPos pos4 = pos3.west();
    final BlockPos pos5 = pos1.up();
    final BlockPos pos6 = pos1.down();

    Assertions.assertTrue(this.net.connect(pos1, TileEntityWithCapabilities.transfer()), "Failed to connect pos1");
    final EnergyNetworkSegment.EnergyNode energyNode1 = this.net.getNode(pos1);
    Assertions.assertTrue(checkNode(energyNode1, pos1, null, null, null, null, null, null), "Node 1 did not match expected");

    Assertions.assertTrue(this.net.connect(pos2, TileEntityWithCapabilities.transfer()), "Failed to connect pos2");
    final EnergyNetworkSegment.EnergyNode energyNode2 = this.net.getNode(pos2);
    Assertions.assertTrue(checkNode(energyNode2, pos2, null, null, null, energyNode1, null, null), "Node 2 did not match expected");
    Assertions.assertTrue(checkNode(energyNode1, pos1, null, null, energyNode2, null, null, null), "Node 1 did not match expected");

    Assertions.assertTrue(this.net.connect(pos3, TileEntityWithCapabilities.transfer()), "Failed to connect pos3");
    final EnergyNetworkSegment.EnergyNode energyNode3 = this.net.getNode(pos3);
    Assertions.assertTrue(checkNode(energyNode3, pos3, energyNode2, null, null, null, null, null), "Node 3 did not match expected");
    Assertions.assertTrue(checkNode(energyNode2, pos2, null, energyNode3, null, energyNode1, null, null), "Node 2 did not match expected");

    Assertions.assertTrue(this.net.connect(pos4, TileEntityWithCapabilities.transfer()), "Failed to connect pos4");
    final EnergyNetworkSegment.EnergyNode energyNode4 = this.net.getNode(pos4);
    Assertions.assertTrue(checkNode(energyNode4, pos4, energyNode1, null, energyNode3, null, null, null), "Node 4 did not match expected");
    Assertions.assertTrue(checkNode(energyNode3, pos3, energyNode2, null, null, energyNode4, null, null), "Node 3 did not match expected");
    Assertions.assertTrue(checkNode(energyNode1, pos1, null, energyNode4, energyNode2, null, null, null), "Node 1 did not match expected");

    Assertions.assertTrue(this.net.connect(pos5, TileEntityWithCapabilities.transfer()), "Failed to connect pos5");
    final EnergyNetworkSegment.EnergyNode energyNode5 = this.net.getNode(pos5);
    Assertions.assertTrue(checkNode(energyNode5, pos5, null, null, null, null, null, energyNode1), "Node 5 did not match expected");
    Assertions.assertTrue(checkNode(energyNode1, pos1, null, energyNode4, energyNode2, null, energyNode5, null), "Node 1 did not match expected");

    Assertions.assertTrue(this.net.connect(pos6, TileEntityWithCapabilities.transfer()), "Failed to connect pos6");
    final EnergyNetworkSegment.EnergyNode energyNode6 = this.net.getNode(pos6);
    Assertions.assertTrue(checkNode(energyNode6, pos6, null, null, null, null, energyNode1, null), "Node 6 did not match expected");
    Assertions.assertTrue(checkNode(energyNode1, pos1, null, energyNode4, energyNode2, null, energyNode5, energyNode6), "Node 1 did not match expected");
  }

  static boolean checkNode(@Nullable final EnergyNetworkSegment.EnergyNode node, final BlockPos pos, @Nullable final EnergyNetworkSegment.EnergyNode north, @Nullable final EnergyNetworkSegment.EnergyNode south, @Nullable final EnergyNetworkSegment.EnergyNode east, @Nullable final EnergyNetworkSegment.EnergyNode west, @Nullable final EnergyNetworkSegment.EnergyNode up, @Nullable final EnergyNetworkSegment.EnergyNode down) {
    return node != null && node.pos.equals(pos) && node.connection(Direction.NORTH) == north && node.connection(Direction.SOUTH) == south && node.connection(Direction.EAST) == east && node.connection(Direction.WEST) == west && node.connection(Direction.UP) == up && node.connection(Direction.DOWN) == down;
  }

  @Test
  void testInvalidConnection() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());

    Assertions.assertFalse(this.net.connect(new BlockPos(100.0f, 100.0f, 100.0f), TileEntityWithCapabilities.transfer()), "Invalid connection didn't return false");
  }

  @Test
  void testDuplicateConnection() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());

    Assertions.assertFalse(this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer()), "Duplicate connection didn't return false");
  }

  @Test
  void testStorageNodesCantConnectToOtherStorageNodesUnlessOnlyNode() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.storage());

    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.storage()));
    Assertions.assertFalse(this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.storage()));
  }

  @Test
  void testStorageNodesSplitNetwork() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.storage());

    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer()), "First transfer node should connect to storage node");
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north().north(), TileEntityWithCapabilities.transfer()), "Second transfer node should connect to first transfer node");
    Assertions.assertFalse(this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer()), "Third transfer node should not connect to storage node");
  }

  @Test
  void testConnectionsRestrictedBySides() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer(Direction.NORTH, Direction.SOUTH));

    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer(Direction.SOUTH)), "Failed to add north transfer node");
    Assertions.assertFalse(this.net.connect(BlockPos.ZERO.north().north(), TileEntityWithCapabilities.transfer()), "North transfer node 2 should not have been added");
    Assertions.assertFalse(this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer()), "East transfer node should not have been added");
    Assertions.assertFalse(this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer(Direction.SOUTH)), "South transfer node should not have been added");
  }

  @Test
  void testEnergyContained() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(1000.0f, 10.0f, 10.0f, 1000.0f)));
    this.net.connect(BlockPos.ZERO.south(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(1000.0f, 10.0f, 50.0f, 25.0f)));
    this.net.connect(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(1000.0f, 10.0f, 15.0f, 20.0f)));
    this.net.connect(BlockPos.ZERO.west(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(1000.0f, 10.0f, 100.0f, 100.0f)));

    Assertions.assertEquals(150.0f, this.net.getAvailableEnergy(), 0.001f, "Available energy did not match");
  }

  @Test
  void testTileEntityWithMultipleStorages() {
    this.net.connect(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(STORAGE, new EnergyStorage(10000.0f, 32.0f, 32.0f, 10000.0f), Direction.NORTH, Direction.SOUTH).addCapability(STORAGE, new EnergyStorage(10000.0f, 16.0f, 16.0f, 10000.0f), Direction.EAST, Direction.WEST));

    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer()), "Failed to add north transfer node");
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north().east(), TileEntityWithCapabilities.transfer()), "Failed to add north east transfer node");
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer()), "Failed to add east transfer node");

    Assertions.assertTrue(checkNode(this.net.getNode(BlockPos.ZERO), BlockPos.ZERO, this.net.getNode(BlockPos.ZERO.north()), null, this.net.getNode(BlockPos.ZERO.east()), null, null, null), () -> "Storage node did not match expected: " + this.net.getNode(BlockPos.ZERO));

    Assertions.assertEquals(48.0f, this.net.getAvailableEnergy(), 0.0001f, "Available energy did not match");
  }

  @Test
  void testExtractEnergyBalanced() {
    final StorageNode s1 = new StorageNode(1000.0f, 10.0f,  10.0f, 1000.0f);
    final StorageNode s2 = new StorageNode(1000.0f, 10.0f,  50.0f,   25.0f);
    final StorageNode s3 = new StorageNode(1000.0f, 10.0f,  15.0f,   20.0f);
    final StorageNode s4 = new StorageNode(1000.0f, 10.0f, 100.0f,  100.0f);

    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north(), new TileEntityWithCapabilities().addCapability(STORAGE, s1));
    this.net.connect(BlockPos.ZERO.south(), new TileEntityWithCapabilities().addCapability(STORAGE, s2));
    this.net.connect(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(STORAGE, s3));
    this.net.connect(BlockPos.ZERO.west(), new TileEntityWithCapabilities().addCapability(STORAGE, s4));

    Assertions.assertEquals(15.0f, this.net.requestEnergy(BlockPos.ZERO.north(), Direction.SOUTH, 15.0f), 0.001f, "Extracted energy did not match");
    Assertions.assertEquals( 20.0f, s2.getEnergy(), 0.001f, "s2 remaining energy did not match");
    Assertions.assertEquals( 15.0f, s3.getEnergy(), 0.001f, "s3 remaining energy did not match");
    Assertions.assertEquals( 95.0f, s4.getEnergy(), 0.001f, "s4 remaining energy did not match");
  }

  @Test
  void testExtractFromTileEntityWithMultipleStorages() {
    final StorageNode s1 = new StorageNode(10000.0f, 32.0f, 32.0f, 10000.0f);
    final StorageNode s2 = new StorageNode(10000.0f, 16.0f, 16.0f, 10000.0f);

    this.net.connect(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(STORAGE, s1, Direction.NORTH, Direction.SOUTH).addCapability(STORAGE, s2, Direction.EAST, Direction.WEST));

    this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north().east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());

    this.net.connect(BlockPos.ZERO.north().north(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(10000.0f, 100.0f, 0.0f, 0.0f)));

    Assertions.assertEquals(40.0f, this.net.requestEnergy(BlockPos.ZERO.north().north(), Direction.SOUTH, 40.0f), 0.0001f, "Extracted energy did not match");
    Assertions.assertEquals(9976.0f, s1.getEnergy(), 0.001f, "s1 remaining energy did not match");
    Assertions.assertEquals(9984.0f, s2.getEnergy(), 0.001f, "s2 remaining energy did not match");
  }

  @Test
  void testExtractEnergyImbalanced() {
    final StorageNode s1 = new StorageNode(1000.0f, 10.0f,  10.0f, 1000.0f);
    final StorageNode s2 = new StorageNode(1000.0f, 10.0f,  50.0f,   25.0f);
    final StorageNode s3 = new StorageNode(1000.0f, 10.0f,  15.0f,   20.0f);
    final StorageNode s4 = new StorageNode(1000.0f, 10.0f, 100.0f,  100.0f);

    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north(), new TileEntityWithCapabilities().addCapability(STORAGE, s1));
    this.net.connect(BlockPos.ZERO.south(), new TileEntityWithCapabilities().addCapability(STORAGE, s2));
    this.net.connect(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(STORAGE, s3));
    this.net.connect(BlockPos.ZERO.west(), new TileEntityWithCapabilities().addCapability(STORAGE, s4));

    this.net.connect(BlockPos.ZERO.up(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(10000.0f, 100.0f, 0.0f, 0.0f)));

    Assertions.assertEquals( 50.0000f, this.net.requestEnergy(BlockPos.ZERO.up(), Direction.DOWN, 50.0f), 0.001f, "Extracted energy did not match");
    Assertions.assertEquals(990.0000f, s1.getEnergy(), 0.001f, "s1 remaining energy did not match");
    Assertions.assertEquals( 11.6667f, s2.getEnergy(), 0.001f, "s2 remaining energy did not match");
    Assertions.assertEquals(  6.6667f, s3.getEnergy(), 0.001f, "s3 remaining energy did not match");
    Assertions.assertEquals( 86.6667f, s4.getEnergy(), 0.001f, "s4 remaining energy did not match");
  }

  @Test
  void testCantExtractFromSink() {
    final StorageNode s1 = new StorageNode(1000.0f, 10.0f,  0.0f, 1000.0f);

    this.net.connect(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(STORAGE, s1));
    this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.south().south(), new TileEntityWithCapabilities().addCapability(STORAGE, new StorageNode(10000.0f, 100.0f, 0.0f, 0.0f)));

    Assertions.assertEquals(0.0f, this.net.requestEnergy(BlockPos.ZERO.south().south(), Direction.NORTH, 50.0f), 0.001f, "Extracted energy did not match");
    Assertions.assertEquals(1000.0f, s1.getEnergy(), 0.0001f, "Sink energy does not match");
  }

  @Test
  void testMergingNetworks() {
    final TileEntity origin = TileEntityWithCapabilities.transfer();
    final TileEntity north = TileEntityWithCapabilities.sink();
    final TileEntity south = TileEntityWithCapabilities.sink();
    final TileEntity east = TileEntityWithCapabilities.sink();
    final TileEntity west = TileEntityWithCapabilities.sink();
    final TileEntity source1 = TileEntityWithCapabilities.source();

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> net1 = new EnergyNetworkSegment<>(STORAGE, TRANSFER);
    Assertions.assertTrue(net1.connect(BlockPos.ZERO, origin));
    Assertions.assertTrue(net1.connect(BlockPos.ZERO.north(), north));
    Assertions.assertTrue(net1.connect(BlockPos.ZERO.south(), south));
    Assertions.assertTrue(net1.connect(BlockPos.ZERO.east(), east));
    Assertions.assertTrue(net1.connect(BlockPos.ZERO.west(), west));
    Assertions.assertTrue(net1.connect(BlockPos.ZERO.down(), source1));

    final TileEntity origin2 = TileEntityWithCapabilities.transfer();
    final TileEntity north2 = TileEntityWithCapabilities.sink();
    final TileEntity south2 = TileEntityWithCapabilities.sink();
    final TileEntity east2 = TileEntityWithCapabilities.sink();
    final TileEntity west2 = TileEntityWithCapabilities.sink();
    final TileEntity source2 = TileEntityWithCapabilities.source();

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> net2 = new EnergyNetworkSegment<>(STORAGE, TRANSFER);
    Assertions.assertTrue(net2.connect(BlockPos.ZERO.up(), origin2));
    Assertions.assertTrue(net2.connect(BlockPos.ZERO.up().north(), north2));
    Assertions.assertTrue(net2.connect(BlockPos.ZERO.up().south(), south2));
    Assertions.assertTrue(net2.connect(BlockPos.ZERO.up().east(), east2));
    Assertions.assertTrue(net2.connect(BlockPos.ZERO.up().west(), west2));
    Assertions.assertTrue(net2.connect(BlockPos.ZERO.up().up(), source2));

    net1.merge(net2);

    final EnergyNetworkSegment.EnergyNode originNode = net1.getNode(BlockPos.ZERO);
    final EnergyNetworkSegment.EnergyNode northNode = net1.getNode(BlockPos.ZERO.north());
    final EnergyNetworkSegment.EnergyNode southNode = net1.getNode(BlockPos.ZERO.south());
    final EnergyNetworkSegment.EnergyNode eastNode = net1.getNode(BlockPos.ZERO.east());
    final EnergyNetworkSegment.EnergyNode westNode = net1.getNode(BlockPos.ZERO.west());
    final EnergyNetworkSegment.EnergyNode source1Node = net1.getNode(BlockPos.ZERO.down());
    final EnergyNetworkSegment.EnergyNode origin2Node = net1.getNode(BlockPos.ZERO.up());
    final EnergyNetworkSegment.EnergyNode north2Node = net1.getNode(BlockPos.ZERO.up().north());
    final EnergyNetworkSegment.EnergyNode south2Node = net1.getNode(BlockPos.ZERO.up().south());
    final EnergyNetworkSegment.EnergyNode east2Node = net1.getNode(BlockPos.ZERO.up().east());
    final EnergyNetworkSegment.EnergyNode west2Node = net1.getNode(BlockPos.ZERO.up().west());
    final EnergyNetworkSegment.EnergyNode source2Node = net1.getNode(BlockPos.ZERO.up().up());

    Assertions.assertTrue(checkNode(originNode, BlockPos.ZERO, northNode, southNode, eastNode, westNode, origin2Node, source1Node), () -> "origin1 did not match: " + originNode);
    Assertions.assertTrue(checkNode(northNode, BlockPos.ZERO.north(), null, originNode, null, null, null, null), () -> "north1 did not match: " + northNode);
    Assertions.assertTrue(checkNode(southNode, BlockPos.ZERO.south(), originNode, null, null, null, null, null), () -> "south1 did not match: " + southNode);
    Assertions.assertTrue(checkNode(eastNode, BlockPos.ZERO.east(), null, null, null, originNode, null, null), () -> "east1 did not match: " + eastNode);
    Assertions.assertTrue(checkNode(westNode, BlockPos.ZERO.west(), null, null, originNode, null, null, null), () -> "west1 did not match: " + westNode);
    Assertions.assertTrue(checkNode(source1Node, BlockPos.ZERO.down(), null, null, null, null, originNode, null), () -> "source1 did not match: " + source1Node);
    Assertions.assertTrue(checkNode(origin2Node, BlockPos.ZERO.up(), north2Node, south2Node, east2Node, west2Node, source2Node, originNode), () -> "origin2 did not match: " + origin2Node);
    Assertions.assertTrue(checkNode(north2Node, BlockPos.ZERO.up().north(), null, origin2Node, null, null, null, null), () -> "north2 did not match: " + north2Node);
    Assertions.assertTrue(checkNode(south2Node, BlockPos.ZERO.up().south(), origin2Node, null, null, null, null, null), () -> "south2 did not match: " + south2Node);
    Assertions.assertTrue(checkNode(east2Node, BlockPos.ZERO.up().east(), null, null, null, origin2Node, null, null), () -> "east2 did not match: " + east2Node);
    Assertions.assertTrue(checkNode(west2Node, BlockPos.ZERO.up().west(), null, null, origin2Node, null, null, null), () -> "west2 did not match: " + west2Node);
    Assertions.assertTrue(checkNode(source2Node, BlockPos.ZERO.up().up(), null, null, null, null, null, origin2Node), () -> "source2 did not match: " + source2Node);

    Assertions.assertEquals(64.0f, net1.getAvailableEnergy(), 0.0001f, "Available energy did not match");
  }

  @Test
  void testBasicPathfinding() {
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.storage()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.storage()));

    this.verifyPath(this.net.pathFind(BlockPos.ZERO.south(), Direction.NORTH, BlockPos.ZERO.north(), Direction.SOUTH));
  }

  @Test
  void testPathfindingMultiplePaths() {
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north().west(), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.north().east(), TileEntityWithCapabilities.storage()));
    Assertions.assertTrue(this.net.connect(BlockPos.ZERO.south().west(), TileEntityWithCapabilities.storage()));

    this.verifyPath(this.net.pathFind(BlockPos.ZERO.north().east(), Direction.SOUTH, BlockPos.ZERO.south().west(), Direction.NORTH));
    this.verifyPath(this.net.pathFind(BlockPos.ZERO.north().east(), Direction.WEST, BlockPos.ZERO.south().west(), Direction.EAST));
  }

  @Test
  void testPathfindingTwoBranches() {
    Assertions.assertTrue(this.net.connect(new BlockPos( 0, 0, -1), TileEntityWithCapabilities.storage()));
    Assertions.assertTrue(this.net.connect(new BlockPos( 0, 0,  0), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos(-1, 0,  0), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos(-1, 0,  1), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos(-1, 0,  2), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos(-1, 0,  3), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos( 1, 0,  0), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos( 1, 0,  1), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos( 1, 0,  2), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos( 1, 0,  3), TileEntityWithCapabilities.transfer()));
    Assertions.assertTrue(this.net.connect(new BlockPos( 0, 0,  3), TileEntityWithCapabilities.storage()));

    this.verifyPath(this.net.pathFind(new BlockPos(0, 0, -1), Direction.SOUTH, new BlockPos(0, 0, 3), Direction.EAST));
    this.verifyPath(this.net.pathFind(new BlockPos(0, 0, -1), Direction.SOUTH, new BlockPos(0, 0, 3), Direction.WEST));
  }

  @Test
  void testPathWithLoop() {
    final TransferNode transferX = new TransferNode();
    final TransferNode transferZ = new TransferNode();

    this.net.connect(BlockPos.ZERO.south().south(), TileEntityWithCapabilities.storage());
    this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(TRANSFER, transferX, Direction.EAST, Direction.WEST).addCapability(TRANSFER, transferZ, Direction.NORTH, Direction.SOUTH));
    this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north().east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.west().west(), TileEntityWithCapabilities.storage());

    final List<BlockPos> path = this.net.pathFind(BlockPos.ZERO.south().south(), Direction.NORTH, BlockPos.ZERO.west().west(), Direction.EAST);
    this.verifyPath(path);
    Assertions.assertEquals(9, path.size(), "Path did not loop");
  }

  @Test
  void testSeeminglyShorterPathDoesNotReachGoal() {
    this.net.connect(new BlockPos(0, 0,  1), TileEntityWithCapabilities.storage());

    this.net.connect(new BlockPos(0, 0,  0), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -1), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -2), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -3), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -4), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -5), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -6), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(0, 0, -7), TileEntityWithCapabilities.transfer());

    this.net.connect(new BlockPos(-1, 0, 0), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, 0), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -1), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -2), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -3), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -4), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -5), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -6), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -7), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -8), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-2, 0, -9), TileEntityWithCapabilities.transfer());
    this.net.connect(new BlockPos(-1, 0, -9), TileEntityWithCapabilities.transfer());

    this.net.connect(new BlockPos(0, 0,  -9), TileEntityWithCapabilities.storage());

    final List<BlockPos> path = this.net.pathFind(new BlockPos(0, 0, 1), Direction.NORTH, new BlockPos(0, 0, -9), Direction.WEST);
    this.verifyPath(path);
    Assertions.assertEquals(15, path.size(), "Path was the wrong size");
  }

  @Test
  void testEnergyTransferredThroughCorrectTransferNodes() {
    final StorageNode sourceEast = new StorageNode(10000.0f, 0.0f, 10.0f, 10000.0f);
    final StorageNode sourceWest = new StorageNode(10000.0f, 0.0f, 10.0f, 10000.0f);
    final TransferNode transferOrigin = new TransferNode();
    final TransferNode transferEast = new TransferNode();
    final TransferNode transferWest = new TransferNode();
    final TransferNode transferNorth = new TransferNode();
    final TransferNode transferEast2 = new TransferNode();
    final TransferNode transferWest2 = new TransferNode();
    final TransferNode transferEast3 = new TransferNode();
    final TransferNode transferWest3 = new TransferNode();
    final TransferNode transferWest4 = new TransferNode();
    final StorageNode sink = new StorageNode(10000.0f, 32.0f, 0.0f, 0.0f);

    //    -1 0 1
    // -3  T C
    // -2  T   T
    // -1  T T T
    //  0  T T T
    //  1  P   P
    this.net.connect(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(TRANSFER, transferOrigin));
    this.net.connect(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferEast));
    this.net.connect(BlockPos.ZERO.west(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferWest));
    this.net.connect(BlockPos.ZERO.north(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferNorth));
    this.net.connect(BlockPos.ZERO.north().east(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferEast2));
    this.net.connect(BlockPos.ZERO.north().west(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferWest2));
    this.net.connect(BlockPos.ZERO.north().north().east(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferEast3));
    this.net.connect(BlockPos.ZERO.north().north().west(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferWest3));
    this.net.connect(BlockPos.ZERO.north().north().north().west(), new TileEntityWithCapabilities().addCapability(TRANSFER, transferWest4));
    this.net.connect(BlockPos.ZERO.south().east(), new TileEntityWithCapabilities().addCapability(STORAGE, sourceEast));
    this.net.connect(BlockPos.ZERO.south().west(), new TileEntityWithCapabilities().addCapability(STORAGE, sourceWest));
    this.net.connect(BlockPos.ZERO.north().north().north(), new TileEntityWithCapabilities().addCapability(STORAGE, sink));

    Assertions.assertEquals(20.0f, this.net.requestEnergy(BlockPos.ZERO.north().north().north(), Direction.WEST, 32.0f), 0.0001f, "Extracted energy did not match");

    Assertions.assertEquals(10.0f, transferOrigin.getTransferred(), 0.0001f);
    Assertions.assertEquals(10.0f, transferEast.getTransferred(), 0.0001f);
    Assertions.assertEquals(10.0f, transferWest.getTransferred(), 0.0001f);
    Assertions.assertEquals(10.0f, transferNorth.getTransferred(), 0.0001f);
    Assertions.assertEquals( 0.0f, transferEast2.getTransferred(), 0.0001f);
    Assertions.assertEquals(20.0f, transferWest2.getTransferred(), 0.0001f);
    Assertions.assertEquals( 0.0f, transferEast3.getTransferred(), 0.0001f);
    Assertions.assertEquals(20.0f, transferWest3.getTransferred(), 0.0001f);
    Assertions.assertEquals(20.0f, transferWest4.getTransferred(), 0.0001f);
  }

  @Test
  void testRemoveBasic() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.storage());
    this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.storage());

    Assertions.assertEquals(3, this.net.size());

    Assertions.assertFalse(this.net.disconnect(BlockPos.ZERO.north()));
    Assertions.assertEquals(2, this.net.size());

    Assertions.assertFalse(this.net.disconnect(BlockPos.ZERO.south()));
    Assertions.assertEquals(1, this.net.size());
  }

  @Test
  void testRemoveLastNode() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());

    Assertions.assertTrue(this.net.disconnect(BlockPos.ZERO));
    Assertions.assertEquals(0, this.net.size());
  }

  @Test
  void testRemoveNodeWouldSplitNetwork() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());

    Assertions.assertTrue(this.net.disconnect(BlockPos.ZERO));
    Assertions.assertEquals(4, this.net.size());
  }

  @Test
  void testRemoveNodeWouldNotSplitNetwork() {
    this.net.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north().east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.north().west(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.south().west(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    this.net.connect(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());

    Assertions.assertFalse(this.net.disconnect(BlockPos.ZERO));
    Assertions.assertEquals(8, this.net.size());
  }

  private void verifyPath(final List<BlockPos> path) {
    Assertions.assertTrue(this.net.getNode(path.get(0)).te.getCapability(STORAGE, WorldUtils.getFacingTowards(path.get(0), path.get(1))).isPresent(), "Start node was not storage");
    Assertions.assertTrue(this.net.getNode(path.get(path.size() - 1)).te.getCapability(STORAGE, WorldUtils.getFacingTowards(path.get(path.size() - 1), path.get(path.size() - 2))).isPresent(), "End node was not storage");

    for(int i = 0; i < path.size() - 1; i++) {
      Assertions.assertNotNull(WorldUtils.areBlocksAdjacent(path.get(i), path.get(i + 1)), "Positions were not adjacent");
    }

    for(int i = 1; i < path.size() - 1; i++) {
      Assertions.assertTrue(this.net.getNode(path.get(i)).te.getCapability(TRANSFER, WorldUtils.getFacingTowards(path.get(i), path.get(i + 1))).isPresent(), "Intermediate node was not transfer");
      Assertions.assertTrue(this.net.getNode(path.get(i)).te.getCapability(TRANSFER, WorldUtils.getFacingTowards(path.get(i), path.get(i - 1))).isPresent(), "Intermediate node was not transfer");
    }
  }

  static <T> Capability<T> newCap(final String name) throws RuntimeException {
    try {
      final Constructor<Capability> constructor = Capability.class.getDeclaredConstructor(String.class, Capability.IStorage.class, Callable.class);
      constructor.setAccessible(true);
      return constructor.newInstance(name, null, null);
    } catch(final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
