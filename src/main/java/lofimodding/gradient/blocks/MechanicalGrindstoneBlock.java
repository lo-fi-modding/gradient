package lofimodding.gradient.blocks;

import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.MechanicalGrindstoneTile;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
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

public class MechanicalGrindstoneBlock extends ProcessorBlock<GrindingRecipe, KineticEnergySource<GrindingRecipe, MechanicalGrindstoneTile>, MechanicalGrindstoneTile> {
  private static final VoxelShape SHAPE = Block.makeCuboidShape(2.0d, 0.0d, 0.0d, 14.0d, 4.0d, 16.0d);

  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

  public MechanicalGrindstoneBlock() {
    super(MechanicalGrindstoneTile.class, Properties.create(Material.ROCK).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @Override
  public MechanicalGrindstoneTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new MechanicalGrindstoneTile();
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
    return SHAPE;
  }
}
