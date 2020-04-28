package lofimodding.gradient.advancements;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.advancements.criterion.GradientCriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public final class AdvancementEvents {
  private AdvancementEvents() { }

  @SubscribeEvent
  public static void onAdvancement(final AdvancementEvent event) {
    GradientCriteriaTriggers.ADVANCEMENT_UNLOCKED.trigger((ServerPlayerEntity)event.getPlayer());
  }
}
