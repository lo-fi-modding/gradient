package lofimodding.gradient.items;

import com.google.common.collect.Sets;
import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientToolTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class StoneHammerItem extends ToolItem {
  private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.COBBLESTONE);

  public StoneHammerItem() {
    super(1.0f, -2.4f, GradientItemTiers.PEBBLE, EFFECTIVE_BLOCKS, new Properties().group(GradientItems.GROUP).addToolType(GradientToolTypes.HAMMER, 1));
  }

  @Override
  public boolean canHarvestBlock(final BlockState state) {
    return EFFECTIVE_BLOCKS.contains(state.getBlock()) || super.canHarvestBlock(state);
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }
}
