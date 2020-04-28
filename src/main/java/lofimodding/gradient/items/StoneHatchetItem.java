package lofimodding.gradient.items;

import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ToolType;

import java.util.Collections;

public class StoneHatchetItem extends ToolItem {
  protected StoneHatchetItem() {
    super(3.0f, -2.4f, GradientItemTiers.PEBBLE, Collections.emptySet(), new Item.Properties().group(GradientItems.GROUP).addToolType(ToolType.AXE, 1));
  }
}
