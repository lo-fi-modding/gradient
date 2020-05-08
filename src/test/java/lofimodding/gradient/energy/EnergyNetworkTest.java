package lofimodding.gradient.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class EnergyNetworkTest {
  private static final EnergyNetworkSegment[] EMPTY_ENERGY_NETWORKS = new EnergyNetworkSegment[0];
  private World world;
  private EnergyNetwork<IEnergyStorage, IEnergyTransfer> network;

  @BeforeEach
  void setUp() {
    this.world = new World();
    this.network = new EnergyNetwork<>(new DimensionType(0, "", "", null, false, null, null, null), this.world, EnergyNetworkSegmentTest.STORAGE, EnergyNetworkSegmentTest.TRANSFER);
  }

  @Test
  void testAddingOneTransferNode() {
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> added = this.network.connect(BlockPos.ZERO, TileEntityWithCapabilities.transfer());

    Assertions.assertEquals(1, added.size(), "There should only be one network");

    for(final Map.Entry<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> network : added.entrySet()) {
      Assertions.assertNull(network.getKey());
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getValue().getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, null, null, null, null), "Node did not match");
    }
  }

  @Test
  void testAddingOneStorageNode() {
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> added = this.network.connect(BlockPos.ZERO, TileEntityWithCapabilities.storage());

    Assertions.assertEquals(1, added.size(), "There should only be one network");

    for(final Map.Entry<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> network : added.entrySet()) {
      Assertions.assertNull(network.getKey());
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getValue().getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, null, null, null, null), "Node did not match");
    }
  }

  @Test
  void testMergingStorageNetworks() {
    final TileEntity teEast = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.storage());
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> east = this.network.connect(BlockPos.ZERO.east(), teEast);

    final TileEntity teWest = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.storage());
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> west = this.network.connect(BlockPos.ZERO.west(), teWest);

    Assertions.assertEquals(1, east.size(), "There should only be one network");
    Assertions.assertEquals(1, west.size(), "There should only be one network");
    Assertions.assertEquals(2, this.network.size(), "There should be two networks total");

    for(final Map.Entry<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> network : east.entrySet()) {
      Assertions.assertNull(network.getKey());
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getValue().getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, null, null, null, null, null), "Node did not match");
    }

    for(final Map.Entry<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> network : west.entrySet()) {
      Assertions.assertNull(network.getKey());
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getValue().getNode(BlockPos.ZERO.west()), BlockPos.ZERO.west(), null, null, null, null, null, null), "Node did not match");
    }

    final TileEntity teOrigin = TileEntityWithCapabilities.storage();

    this.world.addTileEntity(BlockPos.ZERO, teOrigin);

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> origin = this.network.connect(BlockPos.ZERO, teOrigin);

    Assertions.assertEquals(2, origin.size(), "There should be two networks");
    Assertions.assertEquals(2, this.network.size(), "There should be two networks total");

    final EnergyNetworkSegment[] originNetworks = origin.values().toArray(EMPTY_ENERGY_NETWORKS);

    if(originNetworks[0].contains(BlockPos.ZERO.east())) {
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[0].getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, originNetworks[0].getNode(BlockPos.ZERO.east()), null, null, null), () -> "Node did not match: " + originNetworks[0].getNode(BlockPos.ZERO));
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[0].getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, null, null, originNetworks[0].getNode(BlockPos.ZERO), null, null), () -> "Node did not match: " + originNetworks[0].getNode(BlockPos.ZERO.east()));
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[1].getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, null, originNetworks[1].getNode(BlockPos.ZERO.west()), null, null), () -> "Node did not match: " + originNetworks[1].getNode(BlockPos.ZERO));
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[1].getNode(BlockPos.ZERO.west()), BlockPos.ZERO.west(), null, null, originNetworks[1].getNode(BlockPos.ZERO), null, null, null), () -> "Node did not match: " + originNetworks[1].getNode(BlockPos.ZERO.west()));
    } else {
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[0].getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, null, originNetworks[0].getNode(BlockPos.ZERO.west()), null, null), () -> "Node did not match: " + originNetworks[0].getNode(BlockPos.ZERO));
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[0].getNode(BlockPos.ZERO.west()), BlockPos.ZERO.west(), null, null, originNetworks[0].getNode(BlockPos.ZERO), null, null, null), () -> "Node did not match: " + originNetworks[0].getNode(BlockPos.ZERO.west()));
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[1].getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, originNetworks[1].getNode(BlockPos.ZERO.east()), null, null, null), () -> "Node did not match: " + originNetworks[1].getNode(BlockPos.ZERO));
      Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(originNetworks[1].getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, null, null, originNetworks[1].getNode(BlockPos.ZERO), null, null), () -> "Node did not match: " + originNetworks[1].getNode(BlockPos.ZERO.east()));
    }
  }

  @Test
  void testAddingStorageToTransfer() {
    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO, transfer).size());

    final TileEntity storage1 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), storage1).size());

    final TileEntity storage2 = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.west(), storage2).size());

    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");
    Assertions.assertEquals(3, this.network.getNetworksForBlock(BlockPos.ZERO).get(0).size(), "Network should have 3 nodes");
  }

  @Test
  void testAddingTransferBetweenStoragesMerges() {
    final TileEntity storage1 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), storage1).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity storage2 = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.west(), storage2).size());
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> transferNetworks = this.network.connect(BlockPos.ZERO, transfer);
    Assertions.assertEquals(2, transferNetworks.size());
    Assertions.assertTrue(transferNetworks.containsKey(Direction.EAST));
    Assertions.assertTrue(transferNetworks.containsKey(Direction.WEST));
    Assertions.assertFalse(transferNetworks.containsKey(null));
    Assertions.assertEquals(transferNetworks.get(Direction.EAST), transferNetworks.get(Direction.WEST));
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);
    Assertions.assertEquals(3, network.size(), "Network should have 3 nodes");

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, network.getNode(BlockPos.ZERO.east()), network.getNode(BlockPos.ZERO.west()), null, null), () -> "Node did not match: " + network.getNode(BlockPos.ZERO));
  }

  @Test
  void testAddingTransferToStorage() {
    final TileEntity storage = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), storage).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO, transfer).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);
    Assertions.assertEquals(2, network.size(), "Network should have 3 nodes");

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO), BlockPos.ZERO, null, null, network.getNode(BlockPos.ZERO.east()), null, null, null), () -> "Node did not match: " + network.getNode(BlockPos.ZERO));
  }

  @Test
  void testTransferStarWithStorageTipsIsMergedByCentralTransfer() {
    final TileEntity transferNorth = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.north(), transferNorth).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transferSouth = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south(), transferSouth).size());
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final TileEntity transferEast = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), transferEast).size());
    Assertions.assertEquals(3, this.network.size(), "Manager should have three networks");

    final TileEntity transferWest = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.west(), transferWest).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageNorth = this.world.addTileEntity(BlockPos.ZERO.north().north(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.north().north(), storageNorth).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageSouth = this.world.addTileEntity(BlockPos.ZERO.south().south(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south().south(), storageSouth).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageEast = this.world.addTileEntity(BlockPos.ZERO.east().east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east().east(), storageEast).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageWest = this.world.addTileEntity(BlockPos.ZERO.west().west(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.west().west(), storageWest).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity origin = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> networks = this.network.connect(BlockPos.ZERO, origin);
    Assertions.assertEquals(4, networks.size());
    Assertions.assertTrue(networks.containsKey(Direction.NORTH));
    Assertions.assertTrue(networks.containsKey(Direction.SOUTH));
    Assertions.assertTrue(networks.containsKey(Direction.EAST));
    Assertions.assertTrue(networks.containsKey(Direction.WEST));
    Assertions.assertFalse(networks.containsKey(null));
    Assertions.assertEquals(networks.get(Direction.NORTH), networks.get(Direction.EAST));
    Assertions.assertEquals(networks.get(Direction.EAST), networks.get(Direction.SOUTH));
    Assertions.assertEquals(networks.get(Direction.SOUTH), networks.get(Direction.WEST));
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO), BlockPos.ZERO, network.getNode(BlockPos.ZERO.north()), network.getNode(BlockPos.ZERO.south()), network.getNode(BlockPos.ZERO.east()), network.getNode(BlockPos.ZERO.west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.north()), BlockPos.ZERO.north(), network.getNode(BlockPos.ZERO.north().north()), network.getNode(BlockPos.ZERO), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south()), BlockPos.ZERO.south(), network.getNode(BlockPos.ZERO), network.getNode(BlockPos.ZERO.south().south()), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, null, network.getNode(BlockPos.ZERO.east().east()), network.getNode(BlockPos.ZERO), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.west()), BlockPos.ZERO.west(), null, null, network.getNode(BlockPos.ZERO), network.getNode(BlockPos.ZERO.west().west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.north().north()), BlockPos.ZERO.north().north(), null, network.getNode(BlockPos.ZERO.north()), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south().south()), BlockPos.ZERO.south().south(), network.getNode(BlockPos.ZERO.south()), null, null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.east().east()), BlockPos.ZERO.east().east(), null, null, null, network.getNode(BlockPos.ZERO.east()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.west().west()), BlockPos.ZERO.west().west(), null, null, network.getNode(BlockPos.ZERO.west()), null, null, null));
  }

  @Test
  void testTransferStarWithStorageTipsIsSplitByCentralStorage() {
    final TileEntity transferNorth = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.north(), transferNorth).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transferSouth = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south(), transferSouth).size());
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final TileEntity transferEast = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), transferEast).size());
    Assertions.assertEquals(3, this.network.size(), "Manager should have three networks");

    final TileEntity transferWest = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.west(), transferWest).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageNorth = this.world.addTileEntity(BlockPos.ZERO.north().north(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.north().north(), storageNorth).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageSouth = this.world.addTileEntity(BlockPos.ZERO.south().south(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south().south(), storageSouth).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageEast = this.world.addTileEntity(BlockPos.ZERO.east().east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east().east(), storageEast).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity storageWest = this.world.addTileEntity(BlockPos.ZERO.west().west(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.west().west(), storageWest).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity origin = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.storage());
    Assertions.assertEquals(4, this.network.connect(BlockPos.ZERO, origin).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> networkNorth = this.network.getNetworksForBlock(BlockPos.ZERO.north()).get(0);
    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> networkSouth = this.network.getNetworksForBlock(BlockPos.ZERO.south()).get(0);
    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> networkEast = this.network.getNetworksForBlock(BlockPos.ZERO.east()).get(0);
    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> networkWest = this.network.getNetworksForBlock(BlockPos.ZERO.west()).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkNorth.getNode(BlockPos.ZERO), BlockPos.ZERO, networkNorth.getNode(BlockPos.ZERO.north()), networkNorth.getNode(BlockPos.ZERO.south()), networkNorth.getNode(BlockPos.ZERO.east()), networkNorth.getNode(BlockPos.ZERO.west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkNorth.getNode(BlockPos.ZERO.north()), BlockPos.ZERO.north(), networkNorth.getNode(BlockPos.ZERO.north().north()), networkNorth.getNode(BlockPos.ZERO), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkNorth.getNode(BlockPos.ZERO.north().north()), BlockPos.ZERO.north().north(), null, networkNorth.getNode(BlockPos.ZERO.north()), null, null, null, null));

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkSouth.getNode(BlockPos.ZERO), BlockPos.ZERO, networkSouth.getNode(BlockPos.ZERO.north()), networkSouth.getNode(BlockPos.ZERO.south()), networkSouth.getNode(BlockPos.ZERO.east()), networkSouth.getNode(BlockPos.ZERO.west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkSouth.getNode(BlockPos.ZERO.south()), BlockPos.ZERO.south(), networkSouth.getNode(BlockPos.ZERO), networkSouth.getNode(BlockPos.ZERO.south().south()), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkSouth.getNode(BlockPos.ZERO.south().south()), BlockPos.ZERO.south().south(), networkSouth.getNode(BlockPos.ZERO.south()), null, null, null, null, null));

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkEast.getNode(BlockPos.ZERO), BlockPos.ZERO, networkEast.getNode(BlockPos.ZERO.north()), networkEast.getNode(BlockPos.ZERO.south()), networkEast.getNode(BlockPos.ZERO.east()), networkEast.getNode(BlockPos.ZERO.west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkEast.getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, null, networkEast.getNode(BlockPos.ZERO.east().east()), networkEast.getNode(BlockPos.ZERO), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkEast.getNode(BlockPos.ZERO.east().east()), BlockPos.ZERO.east().east(), null, null, null, networkEast.getNode(BlockPos.ZERO.east()), null, null));

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkWest.getNode(BlockPos.ZERO), BlockPos.ZERO, networkWest.getNode(BlockPos.ZERO.north()), networkWest.getNode(BlockPos.ZERO.south()), networkWest.getNode(BlockPos.ZERO.east()), networkWest.getNode(BlockPos.ZERO.west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkWest.getNode(BlockPos.ZERO.west()), BlockPos.ZERO.west(), null, null, networkWest.getNode(BlockPos.ZERO), networkWest.getNode(BlockPos.ZERO.west().west()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(networkWest.getNode(BlockPos.ZERO.west().west()), BlockPos.ZERO.west().west(), null, null, networkWest.getNode(BlockPos.ZERO.west()), null, null, null));
  }

  @Test
  void testStorageAtCornerOfTransferSquareDoesNotCreateNewNetwork() {
    final TileEntity transfer1 = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO, transfer1).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer2 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), transfer2).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer3 = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south(), transfer3).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity storage = this.world.addTileEntity(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(2, this.network.connect(BlockPos.ZERO.south().east(), storage).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO), BlockPos.ZERO, null, network.getNode(BlockPos.ZERO.south()), network.getNode(BlockPos.ZERO.east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south()), BlockPos.ZERO.south(), network.getNode(BlockPos.ZERO), null, network.getNode(BlockPos.ZERO.south().east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, network.getNode(BlockPos.ZERO.south().east()), null, network.getNode(BlockPos.ZERO), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south().east()), BlockPos.ZERO.south().east(), network.getNode(BlockPos.ZERO.east()), null, null, network.getNode(BlockPos.ZERO.south()), null, null));
  }

  @Test
  void testStorageAtCornerOfTransferSquareWithAdjacentTransferNetworkDoesNotCreateNewNetworkOrMergeNetworks() {
    final TileEntity transfer1 = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO, transfer1).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer2 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), transfer2).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer3 = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south(), transfer3).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer4 = this.world.addTileEntity(BlockPos.ZERO.south().east().east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south().east().east(), transfer4).size());
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final TileEntity storage = this.world.addTileEntity(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(3, this.network.connect(BlockPos.ZERO.south().east(), storage).size(), "Storage should have been added to two networks");
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network1 = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);
    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network2 = this.network.getNetworksForBlock(BlockPos.ZERO.south().east().east()).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network1.getNode(BlockPos.ZERO), BlockPos.ZERO, null, network1.getNode(BlockPos.ZERO.south()), network1.getNode(BlockPos.ZERO.east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network1.getNode(BlockPos.ZERO.south()), BlockPos.ZERO.south(), network1.getNode(BlockPos.ZERO), null, network1.getNode(BlockPos.ZERO.south().east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network1.getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, network1.getNode(BlockPos.ZERO.south().east()), null, network1.getNode(BlockPos.ZERO), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network1.getNode(BlockPos.ZERO.south().east()), BlockPos.ZERO.south().east(), network1.getNode(BlockPos.ZERO.east()), null, null, network1.getNode(BlockPos.ZERO.south()), null, null));

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network2.getNode(BlockPos.ZERO.south().east()), BlockPos.ZERO.south().east(), null, null, network2.getNode(BlockPos.ZERO.south().east().east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network2.getNode(BlockPos.ZERO.south().east().east()), BlockPos.ZERO.south().east().east(), null, null, null, network2.getNode(BlockPos.ZERO.south().east()), null, null));
  }

  @Test
  void testTransferAtCornerOfTransferSquareDoesNotCreateNewNetwork() {
    final TileEntity transfer1 = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO, transfer1).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer2 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), transfer2).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer3 = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south(), transfer3).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer4 = this.world.addTileEntity(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(2, this.network.connect(BlockPos.ZERO.south().east(), transfer4).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO), BlockPos.ZERO, null, network.getNode(BlockPos.ZERO.south()), network.getNode(BlockPos.ZERO.east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south()), BlockPos.ZERO.south(), network.getNode(BlockPos.ZERO), null, network.getNode(BlockPos.ZERO.south().east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, network.getNode(BlockPos.ZERO.south().east()), null, network.getNode(BlockPos.ZERO), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south().east()), BlockPos.ZERO.south().east(), network.getNode(BlockPos.ZERO.east()), null, null, network.getNode(BlockPos.ZERO.south()), null, null));
  }

  @Test
  void testTransferAtCornerOfTransferSquareWithAdjacentTransferNetworkMergesNetworks() {
    final TileEntity transfer1 = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO, transfer1).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer2 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.east(), transfer2).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer3 = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south(), transfer3).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity transfer4 = this.world.addTileEntity(BlockPos.ZERO.south().east().east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(BlockPos.ZERO.south().east().east(), transfer4).size());
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final TileEntity transferMerge = this.world.addTileEntity(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.transfer());
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> networks = this.network.connect(BlockPos.ZERO.south().east(), transferMerge);
    Assertions.assertEquals(3, networks.size(), "transferMerge should have been added to one network");
    Assertions.assertFalse(networks.containsKey(Direction.UP));
    Assertions.assertFalse(networks.containsKey(Direction.DOWN));
    Assertions.assertTrue(networks.containsKey(Direction.NORTH));
    Assertions.assertFalse(networks.containsKey(Direction.SOUTH));
    Assertions.assertTrue(networks.containsKey(Direction.EAST));
    Assertions.assertTrue(networks.containsKey(Direction.WEST));
    Assertions.assertFalse(networks.containsKey(null));
    Assertions.assertEquals(networks.get(Direction.NORTH), networks.get(Direction.WEST));
    Assertions.assertEquals(networks.get(Direction.EAST), networks.get(Direction.WEST));
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO), BlockPos.ZERO, null, network.getNode(BlockPos.ZERO.south()), network.getNode(BlockPos.ZERO.east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south()), BlockPos.ZERO.south(), network.getNode(BlockPos.ZERO), null, network.getNode(BlockPos.ZERO.south().east()), null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.east()), BlockPos.ZERO.east(), null, network.getNode(BlockPos.ZERO.south().east()), null, network.getNode(BlockPos.ZERO), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south().east()), BlockPos.ZERO.south().east(), network.getNode(BlockPos.ZERO.east()), null, network.getNode(BlockPos.ZERO.south().east().east()), network.getNode(BlockPos.ZERO.south()), null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(BlockPos.ZERO.south().east().east()), BlockPos.ZERO.south().east().east(), null, null, null, network.getNode(BlockPos.ZERO.south().east()), null, null));
  }

  @Test
  void testTwoWayTransferDoesNotConnectInvalidSides() {
    final TileEntity north = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(north.getPos(), north).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity south = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(south.getPos(), south).size());
    Assertions.assertEquals(2, this.network.size(), "Manager should have two networks");

    final TileEntity east = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(east.getPos(), east).size());
    Assertions.assertEquals(3, this.network.size(), "Manager should have three networks");

    final TileEntity west = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(west.getPos(), west).size());
    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer(Direction.NORTH, Direction.SOUTH));
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> networks = this.network.connect(transfer.getPos(), transfer);
    Assertions.assertEquals(2, networks.size());
    Assertions.assertFalse(networks.containsKey(Direction.UP));
    Assertions.assertFalse(networks.containsKey(Direction.DOWN));
    Assertions.assertTrue(networks.containsKey(Direction.NORTH));
    Assertions.assertTrue(networks.containsKey(Direction.SOUTH));
    Assertions.assertFalse(networks.containsKey(Direction.EAST));
    Assertions.assertFalse(networks.containsKey(Direction.WEST));
    Assertions.assertFalse(networks.containsKey(null));
    Assertions.assertEquals(networks.get(Direction.NORTH), networks.get(Direction.SOUTH));
    Assertions.assertEquals(3, this.network.size(), "Manager should have three networks");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> netMiddle = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);
    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> netEast = this.network.getNetworksForBlock(BlockPos.ZERO.east()).get(0);
    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> netWest = this.network.getNetworksForBlock(BlockPos.ZERO.west()).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(netMiddle.getNode(transfer.getPos()), transfer.getPos(), netMiddle.getNode(north.getPos()), netMiddle.getNode(south.getPos()), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(netMiddle.getNode(north.getPos()), north.getPos(), null, netMiddle.getNode(transfer.getPos()), null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(netMiddle.getNode(south.getPos()), south.getPos(), netMiddle.getNode(transfer.getPos()), null, null, null, null, null));

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(netEast.getNode(east.getPos()), east.getPos(), null, null, null, null, null, null));
    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(netWest.getNode(west.getPos()), west.getPos(), null, null, null, null, null, null));
  }

  @Test
  void testTileEntityWithMultipleStorages() {
    final TileEntity origin = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(origin.getPos(), origin).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity east = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(east.getPos(), east).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity southEast = this.world.addTileEntity(BlockPos.ZERO.south().east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(southEast.getPos(), southEast).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity west = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(west.getPos(), west).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity southWest = this.world.addTileEntity(BlockPos.ZERO.south().west(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(southWest.getPos(), southWest).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final TileEntity storage = this.world.addTileEntity(BlockPos.ZERO.south(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(), Direction.NORTH).addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(), Direction.EAST).addCapability(EnergyNetworkSegmentTest.STORAGE, new StorageNode(), Direction.WEST));
    Assertions.assertEquals(3, this.network.connect(storage.getPos(), storage).size());
    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    final EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer> network = this.network.getNetworksForBlock(BlockPos.ZERO).get(0);

    Assertions.assertTrue(EnergyNetworkSegmentTest.checkNode(network.getNode(storage.getPos()), storage.getPos(), network.getNode(origin.getPos()), null, network.getNode(southEast.getPos()), network.getNode(southWest.getPos()), null, null));
  }

  @Test
  void testDirectionalTransferNodeCreatesTwoNetworks() {
    final TileEntity north = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.network.connect(north.getPos(), north);

    final TileEntity south = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.network.connect(south.getPos(), south);

    final TileEntity east = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    this.network.connect(east.getPos(), east);

    final TileEntity west = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    this.network.connect(west.getPos(), west);

    Assertions.assertEquals(4, this.network.size());

    final TransferNode x = new TransferNode();
    final TransferNode z = new TransferNode();
    final TileEntity origin = this.world.addTileEntity(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.TRANSFER, x, Direction.EAST, Direction.WEST).addCapability(EnergyNetworkSegmentTest.TRANSFER, z, Direction.NORTH, Direction.SOUTH));
    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> networks = this.network.connect(origin.getPos(), origin);
    Assertions.assertEquals(4, networks.size());
    Assertions.assertTrue(networks.containsKey(Direction.NORTH));
    Assertions.assertTrue(networks.containsKey(Direction.SOUTH));
    Assertions.assertTrue(networks.containsKey(Direction.EAST));
    Assertions.assertTrue(networks.containsKey(Direction.WEST));
    Assertions.assertFalse(networks.containsKey(null));
    Assertions.assertEquals(networks.get(Direction.NORTH), networks.get(Direction.SOUTH));
    Assertions.assertEquals(networks.get(Direction.EAST), networks.get(Direction.WEST));
    Assertions.assertNotEquals(networks.get(Direction.NORTH), networks.get(Direction.EAST));

    Assertions.assertEquals(2, this.network.size());
  }

  /**
   * #485
   */
  @Test
  void testTileEntitiesAlreadyInWorldBuildNetworkAndDoNotDuplicate() {
    final TileEntity t1 = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.storage());
    final TileEntity t2 = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.storage());

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> networks1 = this.network.connect(t1.getPos(), t1);

    Assertions.assertEquals(1, networks1.size());
    Assertions.assertTrue(networks1.containsKey(Direction.EAST));
    Assertions.assertEquals(1, this.network.size());

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> networks2 = this.network.connect(t2.getPos(), t2);

    Assertions.assertEquals(1, networks2.size());
    Assertions.assertTrue(networks2.containsKey(Direction.WEST));
    Assertions.assertEquals(1, this.network.size());

    Assertions.assertEquals(networks1.get(Direction.EAST), networks2.get(Direction.WEST));
  }

  /**
   * #500
   */
  @Test
  void testIncompatibleDirectionalTransfersDoNotMergeNetworks() {
    final TileEntity sink   = this.world.addTileEntity(new BlockPos(160, 72, 67), TileEntityWithCapabilities.sink());
    final TileEntity tx     = this.world.addTileEntity(new BlockPos(161, 72, 68), TileEntityWithCapabilities.transfer(Direction.EAST, Direction.WEST));
    final TileEntity source = this.world.addTileEntity(new BlockPos(162, 72, 68), TileEntityWithCapabilities.storage(Direction.WEST));
    final TileEntity tz     = this.world.addTileEntity(new BlockPos(160, 72, 68), TileEntityWithCapabilities.transfer(Direction.NORTH, Direction.SOUTH));

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> sinkMap = this.network.connect(sink.getPos(), sink);
    Assertions.assertEquals(1, sinkMap.size());
    Assertions.assertTrue(sinkMap.containsKey(Direction.SOUTH));
    Assertions.assertEquals(1, this.network.size());

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> txMap = this.network.connect(tx.getPos(), tx);
    Assertions.assertEquals(1, txMap.size());
    Assertions.assertTrue(txMap.containsKey(Direction.EAST));
    Assertions.assertEquals(2, this.network.size());

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> sourceMap = this.network.connect(source.getPos(), source);
    Assertions.assertEquals(1, sourceMap.size());
    Assertions.assertTrue(sourceMap.containsKey(Direction.WEST));
    Assertions.assertEquals(2, this.network.size());

    final Map<Direction, EnergyNetworkSegment<IEnergyStorage, IEnergyTransfer>> tzMap = this.network.connect(tz.getPos(), tz);
    Assertions.assertEquals(1, tzMap.size());
    Assertions.assertTrue(tzMap.containsKey(Direction.NORTH));
    Assertions.assertEquals(2, this.network.size());
  }

  @Test
  void testExtractingEnergySingleNetwork() {
    final IEnergyStorage sourceStorage = new StorageNode(10000.0f, 0.0f, 32.0f, 1000.0f);
    final TileEntity sourceTile = this.world.addTileEntity(BlockPos.ZERO.west(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorage));
    this.network.connect(sourceTile.getPos(), sourceTile);

    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    this.network.connect(transfer.getPos(), transfer);

    final IEnergyStorage sinkStorage = new StorageNode(10000.0f, 32.0f, 0.0f, 100.0f);
    final TileEntity sinkTile = this.world.addTileEntity(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sinkStorage));
    this.network.connect(sinkTile.getPos(), sinkTile);

    Assertions.assertEquals(1, this.network.size(), "Manager should have one network");

    Assertions.assertEquals(32.0f, this.network.requestEnergy(sinkTile.getPos(), 100.0f), 0.0001f, "Extracted energy did not match");
    Assertions.assertEquals(968.0f, sourceStorage.getEnergy(), 0.0001f, "Source energy does not match");
  }

  @Test
  void testExtractingEnergyMultipleNetworksBalanced() {
    final IEnergyStorage sourceStorageNorth = new StorageNode(10000.0f, 0.0f, 32.0f, 1000.0f);
    final TileEntity sourceNorth = this.world.addTileEntity(BlockPos.ZERO.north().north(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageNorth));
    this.network.connect(sourceNorth.getPos(), sourceNorth);

    final TileEntity transferNorth = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferNorth.getPos(), transferNorth);

    final IEnergyStorage sourceStorageSouth = new StorageNode(10000.0f, 0.0f, 32.0f, 1000.0f);
    final TileEntity sourceSouth = this.world.addTileEntity(BlockPos.ZERO.south().south(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageSouth));
    this.network.connect(sourceSouth.getPos(), sourceSouth);

    final TileEntity transferSouth = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferSouth.getPos(), transferSouth);

    final IEnergyStorage sourceStorageEast = new StorageNode(10000.0f, 0.0f, 32.0f, 1000.0f);
    final TileEntity sourceEast = this.world.addTileEntity(BlockPos.ZERO.east().east(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageEast));
    this.network.connect(sourceEast.getPos(), sourceEast);

    final TileEntity transferEast = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferEast.getPos(), transferEast);

    final IEnergyStorage sourceStorageWest = new StorageNode(10000.0f, 0.0f, 32.0f, 1000.0f);
    final TileEntity sourceWest = this.world.addTileEntity(BlockPos.ZERO.west().west(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageWest));
    this.network.connect(sourceWest.getPos(), sourceWest);

    final TileEntity transferWest = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferWest.getPos(), transferWest);

    final IEnergyStorage sinkStorage = new StorageNode(10000.0f, 32.0f, 0.0f, 100.0f);
    final TileEntity sink = this.world.addTileEntity(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sinkStorage));
    this.network.connect(sink.getPos(), sink);

    Assertions.assertEquals(4, this.network.size(), "Manager should have one network");

    Assertions.assertEquals(128.0f, this.network.requestEnergy(sink.getPos(), 1000.0f), 0.0001f, "Extracted energy did not match");
    Assertions.assertEquals(968.0f, sourceStorageNorth.getEnergy(), 0.0001f, "Source north energy does not match");
    Assertions.assertEquals(968.0f, sourceStorageSouth.getEnergy(), 0.0001f, "Source south energy does not match");
    Assertions.assertEquals(968.0f, sourceStorageEast.getEnergy(), 0.0001f, "Source east energy does not match");
    Assertions.assertEquals(968.0f, sourceStorageWest.getEnergy(), 0.0001f, "Source west energy does not match");
  }

  @Test
  void testExtractingEnergyMultipleNetworksImbalanced() {
    final IEnergyStorage sourceStorageNorth = new StorageNode(10000.0f, 0.0f, 20.0f, 1000.0f);
    final TileEntity sourceNorth = this.world.addTileEntity(BlockPos.ZERO.north().north(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageNorth));
    this.network.connect(sourceNorth.getPos(), sourceNorth);

    final TileEntity transferNorth = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferNorth.getPos(), transferNorth);

    final IEnergyStorage sourceStorageSouth = new StorageNode(10000.0f, 0.0f, 64.0f, 1000.0f);
    final TileEntity sourceSouth = this.world.addTileEntity(BlockPos.ZERO.south().south(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageSouth));
    this.network.connect(sourceSouth.getPos(), sourceSouth);

    final TileEntity transferSouth = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferSouth.getPos(), transferSouth);

    final IEnergyStorage sourceStorageEast = new StorageNode(10000.0f, 0.0f, 64.0f, 1000.0f);
    final TileEntity sourceEast = this.world.addTileEntity(BlockPos.ZERO.east().east(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageEast));
    this.network.connect(sourceEast.getPos(), sourceEast);

    final TileEntity transferEast = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferEast.getPos(), transferEast);

    final IEnergyStorage sourceStorageWest = new StorageNode(10000.0f, 0.0f, 64.0f, 1000.0f);
    final TileEntity sourceWest = this.world.addTileEntity(BlockPos.ZERO.west().west(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sourceStorageWest));
    this.network.connect(sourceWest.getPos(), sourceWest);

    final TileEntity transferWest = this.world.addTileEntity(BlockPos.ZERO.west(), TileEntityWithCapabilities.transfer());
    this.network.connect(transferWest.getPos(), transferWest);

    final IEnergyStorage sinkStorage = new StorageNode(10000.0f, 32.0f, 0.0f, 100.0f);
    final TileEntity sink = this.world.addTileEntity(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sinkStorage));
    this.network.connect(sink.getPos(), sink);

    Assertions.assertEquals(4, this.network.size(), "Manager should have four networks");

    Assertions.assertEquals(128.0f, this.network.requestEnergy(sink.getPos(), 128.0f), 0.0001f, "Extracted energy did not match");
    Assertions.assertEquals(980.0f, sourceStorageNorth.getEnergy(), 0.0001f, "Source north energy does not match");
    Assertions.assertEquals(964.0f, sourceStorageSouth.getEnergy(), 0.0001f, "Source south energy does not match");
    Assertions.assertEquals(964.0f, sourceStorageEast.getEnergy(), 0.0001f, "Source east energy does not match");
    Assertions.assertEquals(964.0f, sourceStorageWest.getEnergy(), 0.0001f, "Source west energy does not match");
  }

  @Test
  void testTileEntityWithMultipleSinksOnSameNetwork() {
    final StorageNode sink1 = new StorageNode(10000.0f, 10.0f, 0.0f, 0.0f);
    final StorageNode sink2 = new StorageNode(10000.0f, 10.0f, 0.0f, 0.0f);
    final StorageNode source = new StorageNode(10000.0f, 0.0f, 100.0f, 10000.0f);

    final TileEntity teTransfer1 = this.world.addTileEntity(new BlockPos(0, 0, 0), TileEntityWithCapabilities.transfer());
    final TileEntity teTransfer2 = this.world.addTileEntity(new BlockPos(1, 0, 0), TileEntityWithCapabilities.transfer());
    final TileEntity teTransfer3 = this.world.addTileEntity(new BlockPos(1, 0, 1), TileEntityWithCapabilities.transfer());
    final TileEntity teSink = this.world.addTileEntity(new BlockPos(0, 0, 1), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sink1, Direction.NORTH).addCapability(EnergyNetworkSegmentTest.STORAGE, sink2, Direction.EAST));
    final TileEntity teSource = this.world.addTileEntity(new BlockPos(-1, 0, 0), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, source));

    this.network.connect(teTransfer1.getPos(), teTransfer1);
    this.network.connect(teTransfer2.getPos(), teTransfer2);
    this.network.connect(teTransfer3.getPos(), teTransfer3);
    this.network.connect(teSink.getPos(), teSink);
    this.network.connect(teSource.getPos(), teSource);

    Assertions.assertEquals(1, this.network.size());

    this.network.tick();

    Assertions.assertEquals(10.0f, sink1.getEnergy());
    Assertions.assertEquals(10.0f, sink2.getEnergy());
    Assertions.assertEquals(9980.0f, source.getEnergy());
  }

  @Test
  void testTick() {
    final IEnergyTransfer transfer = new TransferNode();
    final IEnergyStorage source1 = new StorageNode(10000.0f, 0.0f, 10.0f, 10000.0f);
    final IEnergyStorage source2 = new StorageNode(10000.0f, 0.0f, 10.0f, 20.0f);
    final IEnergyStorage sink = new StorageNode(10000.0f, 32.0f, 0.0f, 0.0f);

    final TileEntity teTransfer = this.world.addTileEntity(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.TRANSFER, transfer));
    this.network.connect(teTransfer.getPos(), teTransfer);

    final TileEntity teSource1 = this.world.addTileEntity(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, source1));
    this.network.connect(teSource1.getPos(), teSource1);

    final TileEntity teSource2 = this.world.addTileEntity(BlockPos.ZERO.west(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, source2));
    this.network.connect(teSource2.getPos(), teSource2);

    final TileEntity teSink = this.world.addTileEntity(BlockPos.ZERO.north(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sink));
    this.network.connect(teSink.getPos(), teSink);

    Assertions.assertEquals(1, this.network.size());

    this.network.tick();

    Assertions.assertEquals(9990.0f, source1.getEnergy(), 0.0001f);
    Assertions.assertEquals(  10.0f, source2.getEnergy(), 0.0001f);
    Assertions.assertEquals(  20.0f, sink.getEnergy(), 0.0001f);

    Assertions.assertEquals(20.0f, transfer.getEnergyTransferred(), 0.0001f);

    this.network.tick();

    Assertions.assertEquals(9980.0f, source1.getEnergy(), 0.0001f);
    Assertions.assertEquals(   0.0f, source2.getEnergy(), 0.0001f);
    Assertions.assertEquals(  40.0f, sink.getEnergy(), 0.0001f);

    Assertions.assertEquals(20.0f, transfer.getEnergyTransferred(), 0.0001f);

    this.network.tick();

    Assertions.assertEquals(9970.0f, source1.getEnergy(), 0.0001f);
    Assertions.assertEquals(   0.0f, source2.getEnergy(), 0.0001f);
    Assertions.assertEquals(  50.0f, sink.getEnergy(), 0.0001f);

    Assertions.assertEquals(10.0f, transfer.getEnergyTransferred(), 0.0001f);
  }

  @Test
  void testPowerIsNotExtractedFromEmptyStorage() {
    final IEnergyTransfer transfer = new TransferNode() {
      @Override
      public void transfer(final float amount, final Direction from, final Direction to) {
        Assertions.fail("Power should not have been routed");
        super.transfer(amount, from, to);
      }
    };
    final IEnergyStorage source = new StorageNode(10000.0f, 0.0f, 10.0f, 0.0f);
    final IEnergyStorage sink = new StorageNode(10000.0f, 32.0f, 0.0f, 0.0f) {
      @Override
      public float sinkEnergy(final float maxSink, final boolean simulate) {
        Assertions.fail("Should not have received energy");
        return super.sinkEnergy(maxSink, simulate);
      }
    };

    final TileEntity teTransfer = this.world.addTileEntity(BlockPos.ZERO, new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.TRANSFER, transfer));
    this.network.connect(teTransfer.getPos(), teTransfer);

    final TileEntity teSource = this.world.addTileEntity(BlockPos.ZERO.east(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, source));
    this.network.connect(teSource.getPos(), teSource);

    final TileEntity teSink = this.world.addTileEntity(BlockPos.ZERO.north(), new TileEntityWithCapabilities().addCapability(EnergyNetworkSegmentTest.STORAGE, sink));
    this.network.connect(teSink.getPos(), teSink);

    Assertions.assertEquals(this.network.size(), 1);

    this.network.tick();

    Assertions.assertEquals(0.0f, source.getEnergy(), 0.0001f);
    Assertions.assertEquals(0.0f, sink.getEnergy(), 0.0001f);

    Assertions.assertEquals(0.0f, transfer.getEnergyTransferred(), 0.0001f);
  }

  @Test
  void testRemoveBasic() {
    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(transfer.getPos(), transfer).size());

    final TileEntity north = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(north.getPos(), north).size());

    final TileEntity south = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(south.getPos(), south).size());

    Assertions.assertEquals(1, this.network.size());

    this.world.removeTileEntity(north.getPos());
    this.network.disconnect(north.getPos());
    Assertions.assertEquals(1, this.network.size());
    Assertions.assertEquals(0, this.network.getNetworksForBlock(north.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(south.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(transfer.getPos()).size());

    this.world.removeTileEntity(south.getPos());
    this.network.disconnect(south.getPos());
    Assertions.assertEquals(1, this.network.size());
    Assertions.assertEquals(0, this.network.getNetworksForBlock(south.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(transfer.getPos()).size());

    this.world.removeTileEntity(transfer.getPos());
    this.network.disconnect(transfer.getPos());
    Assertions.assertEquals(0, this.network.size());
    Assertions.assertEquals(0, this.network.getNetworksForBlock(transfer.getPos()).size());
  }

  @Test
  void testRemoveSplitsNetwork() {
    final TileEntity transfer = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(transfer.getPos(), transfer).size());

    final TileEntity north = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(north.getPos(), north).size());

    final TileEntity south = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(south.getPos(), south).size());

    Assertions.assertEquals(1, this.network.size());

    this.world.removeTileEntity(transfer.getPos());
    this.network.disconnect(transfer.getPos());
    Assertions.assertEquals(2, this.network.size());
    Assertions.assertEquals(0, this.network.getNetworksForBlock(transfer.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(north.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(south.getPos()).size());
    Assertions.assertNotEquals(this.network.getNetworksForBlock(north.getPos()), this.network.getNetworksForBlock(south.getPos()));
  }

  @Test
  void testRemoveStorageNode() {
    final TileEntity storage = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(storage.getPos(), storage).size());

    final TileEntity north = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(north.getPos(), north).size());

    final TileEntity south = this.world.addTileEntity(BlockPos.ZERO.south(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(south.getPos(), south).size());

    Assertions.assertEquals(2, this.network.size());

    this.world.removeTileEntity(storage.getPos());
    this.network.disconnect(storage.getPos());
    Assertions.assertEquals(2, this.network.size());
    Assertions.assertEquals(0, this.network.getNetworksForBlock(storage.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(north.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(south.getPos()).size());
    Assertions.assertNotEquals(this.network.getNetworksForBlock(north.getPos()), this.network.getNetworksForBlock(south.getPos()));
  }

  @Test
  void testRemovePartOfCircularNetwork() {
    final TileEntity storage = this.world.addTileEntity(BlockPos.ZERO, TileEntityWithCapabilities.storage());
    Assertions.assertEquals(1, this.network.connect(storage.getPos(), storage).size());

    final TileEntity north = this.world.addTileEntity(BlockPos.ZERO.north(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(north.getPos(), north).size());

    final TileEntity east = this.world.addTileEntity(BlockPos.ZERO.east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(1, this.network.connect(east.getPos(), east).size());

    Assertions.assertEquals(2, this.network.size());

    final TileEntity ne = this.world.addTileEntity(BlockPos.ZERO.north().east(), TileEntityWithCapabilities.transfer());
    Assertions.assertEquals(2, this.network.connect(ne.getPos(), ne).size());

    Assertions.assertEquals(1, this.network.size());

    this.world.removeTileEntity(ne.getPos());
    this.network.disconnect(ne.getPos());
    Assertions.assertEquals(2, this.network.size());
    Assertions.assertEquals(0, this.network.getNetworksForBlock(ne.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(north.getPos()).size());
    Assertions.assertEquals(1, this.network.getNetworksForBlock(east.getPos()).size());
    Assertions.assertEquals(2, this.network.getNetworksForBlock(storage.getPos()).size());
    Assertions.assertNotEquals(this.network.getNetworksForBlock(north.getPos()), this.network.getNetworksForBlock(east.getPos()));
    Assertions.assertTrue(this.network.getNetworksForBlock(storage.getPos()).contains(this.network.getNetworksForBlock(north.getPos()).get(0)));
    Assertions.assertTrue(this.network.getNetworksForBlock(storage.getPos()).contains(this.network.getNetworksForBlock(east.getPos()).get(0)));
  }
}
