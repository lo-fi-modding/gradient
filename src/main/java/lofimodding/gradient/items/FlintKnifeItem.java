package lofimodding.gradient.items;

import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import net.minecraft.item.ToolItem;

import java.util.Collections;

public class FlintKnifeItem extends ToolItem {
  public FlintKnifeItem() {
    super(1.0f, -1.0f, GradientItemTiers.FLINT, Collections.emptySet(), new Properties().group(GradientItems.GROUP));
  }
}
