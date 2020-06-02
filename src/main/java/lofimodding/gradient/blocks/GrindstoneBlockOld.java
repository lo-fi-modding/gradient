package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.GrindstoneTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
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

public class GrindstoneBlockOld extends Block {
  private static final VoxelShape SHAPE = Block.makeCuboidShape(1.0d, 0.0d, 1.0d, 15.0d, 2.0d, 15.0d);

  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

  public GrindstoneBlockOld() {
    super(Properties.create(Material.ROCK).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @Override
  public boolean canHarvestBlock(final BlockState state, final IBlockReader world, final BlockPos pos, final PlayerEntity player) {
    return true;
  }

  @Override
  public GrindstoneTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new GrindstoneTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(!world.isRemote) {
      final GrindstoneTile grindstone = WorldUtils.getTileEntity(world, pos, GrindstoneTile.class);

      if(grindstone == null) {
        return ActionResultType.SUCCESS;
      }

      //TODO

      // Remove input
      if(player.isSneaking()) {
//        if(grindstone.hasInput()) {
//          final ItemStack stack = grindstone.takeInput();
//          ItemHandlerHelper.giveItemToPlayer(player, stack);
//        }

        return ActionResultType.SUCCESS;
      }

      // Take stuff out
//      if(grindstone.hasOutput()) {
//        final ItemStack stack = grindstone.takeOutput();
//        ItemHandlerHelper.giveItemToPlayer(player, stack);
//
//        return ActionResultType.SUCCESS;
//      }

      final ItemStack held = player.getHeldItem(hand);

      // Put stuff in
      if(!held.isEmpty()) {
//        final ItemStack remaining = grindstone.insertItem(held.copy(), player);
//
//        if(!player.isCreative()) {
//          player.setHeldItem(hand, remaining);
//        }

        return ActionResultType.SUCCESS;
      }

//      return grindstone.crank(state, world, pos, player, hand, hit);
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    WorldUtils.dropInventory(world, pos);
    super.onReplaced(state, world, pos, newState, isMoving);
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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
    builder.add(FACING);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader source, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }
}
