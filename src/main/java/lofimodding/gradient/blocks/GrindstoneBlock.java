package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.GrindstoneTile;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class GrindstoneBlock extends ProcessorBlock<ManualEnergySource, GrindstoneTile> {
  private static final VoxelShape SHAPE_X = Block.makeCuboidShape(1.0d, 0.0d, 4.0d, 15.0d, 3.0d, 12.0d);
  private static final VoxelShape SHAPE_Z = Block.makeCuboidShape(4.0d, 0.0d, 1.0d, 12.0d, 3.0d, 15.0d);

  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

  public GrindstoneBlock() {
    super(GrindstoneTile.class, ProcessorTier.BASIC, Properties.create(Material.ROCK).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @Override
  public GrindstoneTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new GrindstoneTile();
  }

  @Override
  public boolean canHarvestBlock(final BlockState state, final IBlockReader world, final BlockPos pos, final PlayerEntity player) {
    return true;
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
    if(state.get(FACING).getAxis() == Direction.Axis.X) {
      return SHAPE_X;
    }

    return SHAPE_Z;
  }
}
