package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientTags;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class UnlitFibreTorchBlock extends TorchBlock {
  private final Supplier<TorchBlock> lit;

  public UnlitFibreTorchBlock(final Supplier<TorchBlock> lit, final Properties properties) {
    super(properties);
    this.lit = lit;
  }

  public TorchBlock getLit() {
    return this.lit.get();
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }

  @Override
  public void animateTick(final BlockState state, final World world, final BlockPos pos, final Random rand) {
    // no particles
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(!world.isRemote || !player.isSneaking()) {
      if(GradientTags.Items.FIBRE_TORCH_LIGHTERS.contains(player.getHeldItemMainhand().getItem()) || GradientTags.Items.FIBRE_TORCH_LIGHTERS.contains(player.getHeldItemOffhand().getItem())) {
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.15f, world.rand.nextFloat() * 0.1f + 0.9f);
        world.setBlockState(pos, WorldUtils.copyStateProperties(state, this.lit.get().getDefaultState()));
        return ActionResultType.SUCCESS;
      }
    }

    return ActionResultType.FAIL;
  }
}
