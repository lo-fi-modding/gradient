package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.tileentities.WoodenConveyorBeltDriverTile;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WoodenConveyorBeltDriverBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public WoodenConveyorBeltDriverBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);

    final WoodenConveyorBeltDriverTile driver = WorldUtils.getTileEntity(world, pos, WoodenConveyorBeltDriverTile.class);

    if(driver != null) {
      driver.onRemove();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbour, final boolean isMoving) {
    super.neighborChanged(state, world, pos, block, neighbour, isMoving);

    final WoodenConveyorBeltDriverTile driver = WorldUtils.getTileEntity(world, pos, WoodenConveyorBeltDriverTile.class);

    if(driver != null) {
      final Direction side = WorldUtils.getFacingTowards(pos, neighbour);
      if(side.getAxis().isHorizontal()) {
        if(block == GradientBlocks.WOODEN_CONVEYOR_BELT.get()) {
          driver.addBelt(side);
        } else {
          driver.removeBelt(side);
        }
      }
    }
  }

  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenConveyorBeltDriverTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return super.getStateForPlacement(context).with(FACING, context.getFace().getOpposite());
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
