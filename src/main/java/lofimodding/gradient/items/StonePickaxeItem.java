package lofimodding.gradient.items;

import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraftforge.common.ToolType;

public class StonePickaxeItem extends PickaxeItem {
  public StonePickaxeItem() {
    super(GradientItemTiers.STONE, 3, -2.4f, new Item.Properties().group(GradientItems.GROUP).addToolType(ToolType.PICKAXE, 1));
  }
}
