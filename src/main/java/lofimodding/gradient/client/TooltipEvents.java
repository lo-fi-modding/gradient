package lofimodding.gradient.client;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.recipes.MeltingRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, value = Dist.CLIENT)
public final class TooltipEvents {
  private TooltipEvents() { }

  @SubscribeEvent
  public static void meltableTooltips(final ItemTooltipEvent event) {
    if(event.getPlayer() == null) {
      // Only run on the client
      return;
    }

    final ItemStack stack = event.getItemStack();

    if(stack.isEmpty()) {
      return;
    }

    RecipeUtils.getRecipe(MeltingRecipe.TYPE, r -> r.matches(stack)).ifPresent(recipe -> {
      final List<ITextComponent> tooltip = event.getToolTip();
      tooltip.add(new TranslationTextComponent("meltable.melt_temp", recipe.getTemperature()));
      tooltip.add(new TranslationTextComponent("meltable.melt_time", recipe.getTicks()));
      tooltip.add(new TranslationTextComponent("meltable.amount", recipe.getFluidOutput().getAmount()));
      tooltip.add(new TranslationTextComponent("meltable.fluid", recipe.getFluidOutput().getName()));
    });
  }
}
