package lofimodding.gradient.advancements.criterion;

import net.minecraft.advancements.CriteriaTriggers;

public class GradientCriteriaTriggers {
  public static final AdvancementUnlockedTrigger ADVANCEMENT_UNLOCKED = CriteriaTriggers.register(new AdvancementUnlockedTrigger());
  public static final BlockRightClickedTrigger BLOCK_RIGHT_CLICKED = CriteriaTriggers.register(new BlockRightClickedTrigger());
}
