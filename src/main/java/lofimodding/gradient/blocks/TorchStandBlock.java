package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TorchStandBlock extends Block {
  private static final VoxelShape SHAPE = makeCuboidShape(6.0d, 0.0d, 6.0d, 10.0d, 16.0d, 10.0d);

  public TorchStandBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f));
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    final BlockPos up = context.getPos().up();

    if(!context.getWorld().getBlockState(up).isReplaceable(context)) {
      return Blocks.AIR.getDefaultState();
    }

    return super.getStateForPlacement(context);
  }

  @Override
  public void onBlockPlacedBy(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, placer, stack);
    world.setBlockState(pos.up(), GradientBlocks.UNLIT_TORCH_STAND_TORCH.get().getDefaultState());
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }
}
