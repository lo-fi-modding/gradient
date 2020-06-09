package lofimodding.gradient.items;

import lofimodding.gradient.GradientItems;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

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
      final PlayerEntity player = context.getPlayer();
      final String key = this.getTranslationKey(context.getItem());

      if(processor.areSlotsLocked()) {
        processor.unlockSlots();

        if(player != null) {
          player.sendMessage(new TranslationTextComponent(key + ".unlocked", processor.getBlockState().getBlock().getNameTextComponent()));
        }
      } else {
        processor.lockSlotsToCurrentContents();

        if(player != null) {
          player.sendMessage(new TranslationTextComponent(key + ".locked", processor.getBlockState().getBlock().getNameTextComponent()));

          final ItemStack[] items = processor.getAllItemInputs().toArray(ItemStack[]::new);
          final FluidStack[] fluids = processor.getAllFluidInputs().toArray(FluidStack[]::new);

          if(items.length != 0) {
            player.sendMessage(new TranslationTextComponent(key + ".items"));

            for(final ItemStack item : items) {
              player.sendMessage(new TranslationTextComponent(key + ".list", item.getDisplayName()));
            }
          }

          if(fluids.length != 0) {
            player.sendMessage(new TranslationTextComponent(key + ".fluids"));

            for(final FluidStack fluid : fluids) {
              player.sendMessage(new TranslationTextComponent(key + ".list", fluid.getDisplayName()));
            }
          }
        }
      }
    }

    return ActionResultType.SUCCESS;
  }
}
