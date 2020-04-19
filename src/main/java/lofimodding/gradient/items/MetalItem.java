package lofimodding.gradient.items;

import lofimodding.gradient.science.Metal;
import net.minecraft.item.Item;

public class MetalItem extends Item {
  public final Metal metal;

  public MetalItem(final Metal metal, final Properties properties) {
    super(properties);
    this.metal = metal;
  }
}
