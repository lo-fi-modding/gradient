package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.DryingRackTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public class DryingRackBlock extends Block {
  private static final VoxelShape SHAPE_NORTH  = makeCuboidShape( 0.0d, 13.0d,  0.0d, 16.0d, 16.0d,  2.0d);
  private static final VoxelShape SHAPE_SOUTH  = makeCuboidShape( 0.0d, 13.0d, 14.0d, 16.0d, 16.0d, 16.0d);
  private static final VoxelShape SHAPE_EAST   = makeCuboidShape(14.0d, 13.0d,  0.0d, 16.0d, 16.0d, 16.0d);
  private static final VoxelShape SHAPE_WEST   = makeCuboidShape( 0.0d, 13.0d,  0.0d,  2.0d, 16.0d, 16.0d);
  private static final VoxelShape SHAPE_DOWN_Z = makeCuboidShape( 0.0d, 13.0d,  7.0d, 16.0d, 16.0d,  9.0d);
  private static final VoxelShape SHAPE_DOWN_X = makeCuboidShape( 7.0d, 13.0d,  0.0d,  9.0d, 16.0d, 16.0d);

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty ROOF = BooleanProperty.create("roof");

  public DryingRackBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).notSolid().doesNotBlockMovement());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ROOF, Boolean.FALSE));
  }

  @Override
  public DryingRackTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new DryingRackTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    if(!player.isSneaking()) {
      final DryingRackTile te = WorldUtils.getTileEntity(world, pos, DryingRackTile.class);

      if(te == null) {
        return ActionResultType.SUCCESS;
      }

      if(te.hasItem()) {
        ItemHandlerHelper.giveItemToPlayer(player, te.takeItem());
        return ActionResultType.SUCCESS;
      }

      final ItemStack held = player.getHeldItem(hand);

      if(!held.isEmpty()) {
        final ItemStack remaining = te.insertItem(held.copy(), player);

        if(!player.isCreative()) {
          player.setHeldItem(hand, remaining);
        }

        return ActionResultType.SUCCESS;
      }
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    if(state.getBlock() != newState.getBlock()) {
      WorldUtils.dropInventory(world, pos);
      super.onReplaced(state, world, pos, newState, isMoving);
    }
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    if(context.getFace() == Direction.UP) {
      return Blocks.AIR.getDefaultState();
    }

    if(context.getFace() != Direction.DOWN) {
      return super.getStateForPlacement(context).with(FACING, context.getFace().getOpposite()).with(ROOF, Boolean.FALSE);
    }

    return super.getStateForPlacement(context).with(FACING, context.getPlacementHorizontalFacing()).with(ROOF, Boolean.TRUE);
  }

  @SuppressWarnings("deprecation")
  @Override
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
    builder.add(FACING, ROOF);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    final Direction facing = state.get(FACING);

    if(!state.get(ROOF)) {
      switch(facing) {
        case NORTH:
          return SHAPE_NORTH;

        case SOUTH:
          return SHAPE_SOUTH;

        case EAST:
          return SHAPE_EAST;

        case WEST:
          return SHAPE_WEST;
      }
    } else {
      switch(facing) {
        case NORTH:
        case SOUTH:
          return SHAPE_DOWN_Z;

        case EAST:
        case WEST:
          return SHAPE_DOWN_X;
      }
    }

    return SHAPE_DOWN_Z;
  }
}
