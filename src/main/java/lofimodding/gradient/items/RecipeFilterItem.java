package lofimodding.gradient.items;

import lofimodding.gradient.GradientItems;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RecipeFilterItem extends Item {
  public RecipeFilterItem() {
    super(new Properties().group(GradientItems.GROUP));
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }

  @Override
  public ActionResultType onItemUse(final ItemUseContext context) {
    if(context.getWorld().isRemote) {
      return ActionResultType.SUCCESS;
    }

    final ProcessorTile<?, ?, ?> processor = WorldUtils.getTileEntity(context.getWorld(), context.getPos(), ProcessorTile.class);

    if(processor != null) {
      if(processor.areSlotsLocked()) {
        if(context.getPlayer() != null) {
          context.getPlayer().sendMessage(new TranslationTextComponent(this.getTranslationKey(context.getItem()) + ".unlocked", processor.getBlockState().getBlock().getNameTextComponent()));
        }

        processor.unlockSlots();
      } else {
        if(context.getPlayer() != null) {
          context.getPlayer().sendMessage(new TranslationTextComponent(this.getTranslationKey(context.getItem()) + ".locked", processor.getBlockState().getBlock().getNameTextComponent()));
        }

        processor.lockSlotsToCurrentContents();
      }
    }

    return ActionResultType.SUCCESS;
  }
}
