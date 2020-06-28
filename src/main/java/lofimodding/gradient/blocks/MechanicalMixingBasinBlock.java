package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.MechanicalMixingBasinTile;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class MechanicalMixingBasinBlock extends ProcessorBlock<KineticEnergySource, MechanicalMixingBasinTile> {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty HAS_WATER = BooleanProperty.create("has_water");

  private static final VoxelShape SHAPE = makeCuboidShape(1.0d, 0.0d, 1.0d, 15.0d, 8.0d, 15.0d);

  public MechanicalMixingBasinBlock() {
    super(MechanicalMixingBasinTile.class, ProcessorTier.MECHANICAL, Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HAS_WATER, Boolean.FALSE));
  }

  @Override
  public MechanicalMixingBasinTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new MechanicalMixingBasinTile();
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
    builder.add(FACING, HAS_WATER);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }
}
