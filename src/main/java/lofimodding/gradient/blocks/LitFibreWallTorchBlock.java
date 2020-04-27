package lofimodding.gradient.blocks;

import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.function.Supplier;

public class LitFibreWallTorchBlock extends WallTorchBlock {
  private final Supplier<TorchBlock> unlit;

  public LitFibreWallTorchBlock(final Supplier<TorchBlock> unlit, final Properties properties) {
    super(properties);
    this.unlit = unlit;
  }

  @Override
  public int tickRate(final IWorldReader world) {
    return 9600;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void tick(final BlockState state, final ServerWorld world, final BlockPos pos, final Random rand) {
    super.tick(state, world, pos, rand);
    world.setBlockState(pos, WorldUtils.copyStateProperties(state, this.unlit.get().getDefaultState()));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
    super.onBlockAdded(state, world, pos, oldState, isMoving);
  }
}
