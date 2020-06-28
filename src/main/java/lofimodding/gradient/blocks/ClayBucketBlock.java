package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ClayBucketBlock extends Block {
  private static final VoxelShape SHAPE = makeCuboidShape(3.0d, 0.0d, 3.0d, 13.0d, 8.0d, 13.0d);

  public ClayBucketBlock() {
    super(Properties.create(GradientMaterials.CLAY_MACHINE).hardnessAndResistance(1.0f, 5.0f).notSolid());
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }
}
