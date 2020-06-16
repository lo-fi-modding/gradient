package lofimodding.gradient.items;

import com.google.common.collect.Sets;
import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientToolTypes;
import lofimodding.gradient.science.Metal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MetalHammerItem extends ToolItem {
  private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.COBBLESTONE);

  public final Metal metal;

  public MetalHammerItem(final Metal metal) {
    super(-GradientItemTiers.METALS.get(metal).getAttackDamage() * 0.5f, -3.5f + 25.0f / metal.weight, GradientItemTiers.METALS.get(metal), EFFECTIVE_BLOCKS, new Item.Properties().group(GradientItems.GROUP).addToolType(GradientToolTypes.HAMMER, metal.harvestLevel));
    this.metal = metal;
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