package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.WoodenHopperTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class WoodenHopperBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.FACING_EXCEPT_UP;
  public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

  private static final VoxelShape INPUT_SHAPE = Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  private static final VoxelShape MIDDLE_SHAPE = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
  private static final VoxelShape INPUT_MIDDLE_SHAPE = VoxelShapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
  private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(INPUT_MIDDLE_SHAPE, IHopper.INSIDE_BOWL_SHAPE, IBooleanFunction.ONLY_FIRST);
  private static final VoxelShape DOWN_SHAPE = VoxelShapes.or(DEFAULT_SHAPE, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
  private static final VoxelShape EAST_SHAPE = VoxelShapes.or(DEFAULT_SHAPE, Block.makeCuboidShape(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
  private static final VoxelShape NORTH_SHAPE = VoxelShapes.or(DEFAULT_SHAPE, Block.makeCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
  private static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(DEFAULT_SHAPE, Block.makeCuboidShape(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
  private static final VoxelShape WEST_SHAPE = VoxelShapes.or(DEFAULT_SHAPE, Block.makeCuboidShape(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
  private static final VoxelShape DOWN_RAYTRACE_SHAPE = IHopper.INSIDE_BOWL_SHAPE;
  private static final VoxelShape EAST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
  private static final VoxelShape NORTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
  private static final VoxelShape SOUTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
  private static final VoxelShape WEST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

  public WoodenHopperBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    switch(state.get(FACING)) {
      case DOWN:
        return DOWN_SHAPE;
      case NORTH:
        return NORTH_SHAPE;
      case SOUTH:
        return SOUTH_SHAPE;
      case WEST:
        return WEST_SHAPE;
      case EAST:
        return EAST_SHAPE;
      default:
        return DEFAULT_SHAPE;
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getRaytraceShape(final BlockState state, final IBlockReader world, final BlockPos pos) {
    switch(state.get(FACING)) {
      case DOWN:
        return DOWN_RAYTRACE_SHAPE;
      case NORTH:
        return NORTH_RAYTRACE_SHAPE;
      case SOUTH:
        return SOUTH_RAYTRACE_SHAPE;
      case WEST:
        return WEST_RAYTRACE_SHAPE;
      case EAST:
        return EAST_RAYTRACE_SHAPE;
      default:
        return IHopper.INSIDE_BOWL_SHAPE;
    }
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    final Direction direction = context.getFace().getOpposite();
    return this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).with(ENABLED, Boolean.TRUE);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenHopperTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    if(oldState.getBlock() != state.getBlock()) {
      this.updateState(world, pos, state);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    final WoodenHopperTile hopper = WorldUtils.getTileEntity(world, pos, WoodenHopperTile.class);

    if(hopper != null) {
      NetworkHooks.openGui((ServerPlayerEntity)player, hopper, pos);
      player.addStat(Stats.INSPECT_HOPPER);
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos fromPos, final boolean isMoving) {
    this.updateState(world, pos, state);
  }

  private void updateState(final World world, final BlockPos pos, final BlockState state) {
    final boolean unpowered = !world.isBlockPowered(pos);

    if(unpowered != state.get(ENABLED)) {
      world.setBlockState(pos, state.with(ENABLED, unpowered), 4);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    if(state.getBlock() != newState.getBlock()) {
      final WoodenHopperTile hopper = WorldUtils.getTileEntity(world, pos, WoodenHopperTile.class);

      if(hopper != null) {
        InventoryHelper.dropInventoryItems(world, pos, hopper);
        world.updateComparatorOutputLevel(pos, this);
      }

      super.onReplaced(state, world, pos, newState, isMoving);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public boolean hasComparatorInputOverride(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public int getComparatorInputOverride(final BlockState state, final World world, final BlockPos pos) {
    return Container.calcRedstone(world.getTileEntity(pos));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState rotate(final BlockState state, final Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState mirror(final BlockState state, final Mirror mirror) {
    return state.rotate(mirror.toRotation(state.get(FACING)));
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, ENABLED);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entityIn) {
    final WoodenHopperTile hopper = WorldUtils.getTileEntity(world, pos, WoodenHopperTile.class);

    if(hopper != null) {
      hopper.onEntityCollision(entityIn);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public boolean allowsMovement(final BlockState state, final IBlockReader world, final BlockPos pos, final PathType type) {
    return false;
  }
}
