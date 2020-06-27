package lofimodding.gradient.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;

public class Tool {
  public boolean hasToolType(final ItemStack stack, final ToolType type) {
    return stack.getToolTypes().contains(type);
  }

  public void useTool(final ItemStack stack, final PlayerEntity player) {
    if(stack.isDamageable()) {
      stack.damageItem(1, player, p -> {});
    }
  }
}
