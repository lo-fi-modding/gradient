package lofimodding.gradient.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;

public class Tool {
  public boolean hasToolType(final ItemStack stack, final ToolType type) {
    return stack.getToolTypes().contains(type);
  }

  public void useTool(final ItemStack stack) {
    if(stack.isDamageable()) {
//      stack.damageItem();
    }
  }
}
