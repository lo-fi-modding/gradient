package lofimodding.gradient.blocks;

import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.function.Supplier;

public class LitFibreTorchBlock extends TorchBlock {
  private final Supplier<TorchBlock> unlit;

  private static final IntegerProperty DURATION = IntegerProperty.create("duration", 0, 48);

  public LitFibreTorchBlock(final Supplier<TorchBlock> unlit, final Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(DURATION, 0));
    this.unlit = unlit;
  }

  @Override
  public int tickRate(final IWorldReader world) {
    return 200;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void tick(final BlockState state, final ServerWorld world, final BlockPos pos, final Random rand) {
    super.tick(state, world, pos, rand);

    final BlockState newState = state.cycle(DURATION);
    world.setBlockState(pos, newState);

    if(newState.get(DURATION) == 48) {
      world.setBlockState(pos, WorldUtils.copyStateProperties(state, this.unlit.get().getDefaultState()));
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
    super.onBlockAdded(state, world, pos, oldState, isMoving);
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(DURATION);
  }
}
