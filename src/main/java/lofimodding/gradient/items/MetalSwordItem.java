package lofimodding.gradient.items;

import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.science.Metal;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

public class MetalSwordItem extends SwordItem {
  public final Metal metal;

  public MetalSwordItem(final Metal metal) {
    super(GradientItemTiers.METALS.get(metal), 3, -3.5f + 50.0f / metal.weight, new Item.Properties().group(GradientItems.GROUP));
    this.metal = metal;
  }
}
