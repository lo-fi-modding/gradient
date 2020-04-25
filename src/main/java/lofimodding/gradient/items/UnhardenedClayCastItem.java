package lofimodding.gradient.items;

import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.client.screens.UnhardenedClayCastScreen;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;

public class UnhardenedClayCastItem extends BlockItem {
  @Nullable
  private final GradientCasts cast;

  public UnhardenedClayCastItem(final Block block, final GradientCasts cast, final Properties builder) {
    super(block, builder);
    this.cast = cast;
  }

  public UnhardenedClayCastItem(final Block block, final Properties builder) {
    super(block, builder);
    this.cast = null;
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get().getTranslationKey() + ".tooltip"));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    final ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);

    if(result.getType() != ActionResultType.SUCCESS) {
      DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().displayGuiScreen(new UnhardenedClayCastScreen(this.cast)));
    }

    return result;
  }
}
