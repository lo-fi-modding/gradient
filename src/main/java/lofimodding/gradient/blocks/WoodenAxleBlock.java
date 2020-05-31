package lofimodding.gradient.blocks;

import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.tileentities.WoodenAxleTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class WoodenAxleBlock extends RotatedPillarBlock {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private static final VoxelShape SHAPE_X = makeCuboidShape(0.0d, 5.0d, 5.0d, 16.0d, 11.0d, 11.0d);
  private static final VoxelShape SHAPE_Y = makeCuboidShape(5.0d, 0.0d, 5.0d, 11.0d, 16.0d, 11.0d);
  private static final VoxelShape SHAPE_Z = makeCuboidShape(5.0d, 5.0d, 0.0d, 11.0d, 11.0d, 16.0d);

  public WoodenAxleBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD).notSolid());
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockRenderType getRenderType(final BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);
    EnergyNetworkManager.getManager(world, STORAGE, TRANSFER).queueDisconnection(pos);
  }

  @Override
  public WoodenAxleTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenAxleTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    switch(state.get(AXIS)) {
      case X:
        return SHAPE_X;

      case Z:
        return SHAPE_Z;
    }

    return SHAPE_Y;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public boolean allowsMovement(final BlockState state, final IBlockReader world, final BlockPos pos, final PathType type) {
    return false;
  }
}
