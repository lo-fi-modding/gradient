package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.WoodenConveyorBeltDriverTile;
import lofimodding.gradient.tileentities.WoodenConveyorBeltTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class WoodenConveyorBeltBlock extends Block {
  private static final VoxelShape SHAPE = makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 4.0d, 16.0d);
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public WoodenConveyorBeltBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    final WoodenConveyorBeltTile belt = WorldUtils.getTileEntity(world, pos, WoodenConveyorBeltTile.class);

    if(belt != null) {
      belt.onRemove();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbour, final boolean isMoving) {
    super.neighborChanged(state, world, pos, block, neighbour, isMoving);

    final WoodenConveyorBeltTile belt = WorldUtils.getTileEntity(world, pos, WoodenConveyorBeltTile.class);

    if(belt != null) {
      final Map<WoodenConveyorBeltDriverTile, Direction> drivers = new HashMap<>(belt.getDrivers());

      for(final Map.Entry<WoodenConveyorBeltDriverTile, Direction> entry : drivers.entrySet()) {
        entry.getKey().removeBelt(entry.getValue());
        entry.getKey().addBelt(entry.getValue());
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return super.getStateForPlacement(context).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenConveyorBeltTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState rotate(final BlockState state, final Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(final BlockState state, final Mirror mirror) {
    return state.rotate(mirror.toRotation(state.get(FACING)));
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(FACING);
  }
}
