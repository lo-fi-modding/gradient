package lofimodding.gradient.items;

import lofimodding.gradient.science.Ore;
import net.minecraft.item.Item;

public class OreItem extends Item {
  public final Ore ore;

  public OreItem(final Ore ore, final Properties properties) {
    super(properties);
    this.ore = ore;
  }
}
