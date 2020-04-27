package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientTags;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public class UnlitFibreWallTorchBlock extends WallTorchBlock {
  private final Supplier<TorchBlock> lit;

  public UnlitFibreWallTorchBlock(final Supplier<TorchBlock> lit, final Properties properties) {
    super(properties);
    this.lit = lit;
  }

  @Override
  public void animateTick(final BlockState state, final World world, final BlockPos pos, final Random rand) {
    // no particles
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(!world.isRemote || !player.isShiftKeyDown()) {
      if(GradientTags.Items.FIBRE_TORCH_LIGHTERS.contains(player.getHeldItemMainhand().getItem()) || GradientTags.Items.FIBRE_TORCH_LIGHTERS.contains(player.getHeldItemOffhand().getItem())) {
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.15f, world.rand.nextFloat() * 0.1f + 0.9f);
        world.setBlockState(pos, WorldUtils.copyStateProperties(state, this.lit.get().getDefaultState()));
        return ActionResultType.SUCCESS;
      }
    }

    return ActionResultType.FAIL;
  }
}
