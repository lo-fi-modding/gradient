package lofimodding.gradient.advancements;

import betteradvancements.api.event.AdvancementDrawConnectionsEvent;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.advancements.criterion.AdvancementUnlockedTrigger;
import lofimodding.gradient.advancements.criterion.GradientCriteriaTriggers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public final class AdvancementEvents {
  private AdvancementEvents() { }

  @SubscribeEvent
  public static void onAdvancement(final AdvancementEvent event) {
    GradientCriteriaTriggers.ADVANCEMENT_UNLOCKED.trigger((ServerPlayerEntity)event.getPlayer());
  }

  @SubscribeEvent
  public static void onBlockRightClicked(final PlayerInteractEvent.RightClickBlock event) {
    if(event.getSide() == LogicalSide.CLIENT) {
      return;
    }

    GradientCriteriaTriggers.BLOCK_RIGHT_CLICKED.trigger((ServerPlayerEntity)event.getPlayer(), event.getWorld().getBlockState(event.getPos()).getBlock(), event.getItemStack());
  }

  @SubscribeEvent
  public static void onUseHoe(final UseHoeEvent event) {
    if(!event.getContext().getWorld().isRemote && event.getEntity() instanceof ServerPlayerEntity) {
      GradientCriteriaTriggers.USED_HOE.trigger((ServerPlayerEntity)event.getEntity());
    }
  }

  @SubscribeEvent
  public static void onDrawConnections(final AdvancementDrawConnectionsEvent event) {
    final Advancement advancement = event.getAdvancement();

    for(final Map.Entry<String, Criterion> entry : advancement.getCriteria().entrySet()) {
      if(entry.getValue().getCriterionInstance() instanceof AdvancementUnlockedTrigger.Instance) {
        final AdvancementUnlockedTrigger.Instance instance = (AdvancementUnlockedTrigger.Instance)entry.getValue().getCriterionInstance();

        if(advancement.getParent() != null && !instance.advancement.equals(advancement.getParent().getId())) {
          final Advancement extraParent = Minecraft.getInstance().getConnection().getAdvancementManager().getAdvancementList().getAdvancement(instance.advancement);
          event.getExtraConnections().add(extraParent);
        }
      }
    }
  }
}
