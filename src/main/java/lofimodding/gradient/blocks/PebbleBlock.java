package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PebbleBlock extends Block {
  private static final VoxelShape SHAPE = Block.makeCuboidShape(4.0d, 0.0d, 4.0d, 12.0d, 4.0d, 12.0d);

  public PebbleBlock(final Properties properties) {
    super(properties);
  }

  @Override
  public Item asItem() {
    return GradientItems.PEBBLE.get();
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader source, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isValidPosition(final BlockState state, final IWorldReader world, final BlockPos pos) {
    final BlockState down = world.getBlockState(pos.down());

    return
      this.isAir(world.getBlockState(pos), world, pos) && (
        down.getMaterial() == Material.CLAY ||
        down.getMaterial() == Material.ORGANIC ||
        down.getMaterial() == Material.EARTH ||
        down.getMaterial() == Material.ICE ||
        down.getMaterial() == Material.PACKED_ICE ||
        down.getMaterial() == Material.ROCK ||
        down.getMaterial() == Material.SAND
      ) && down.isSolid();
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos fromPos, final boolean isMoving) {
    if(world.isAirBlock(pos.down())) {
      world.destroyBlock(pos, true);
    }
  }
}
