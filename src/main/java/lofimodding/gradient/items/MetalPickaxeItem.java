package lofimodding.gradient.items;

import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.science.Metal;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraftforge.common.ToolType;

public class MetalPickaxeItem extends PickaxeItem {
  public final Metal metal;

  public MetalPickaxeItem(final Metal metal) {
    super(GradientItemTiers.METALS.get(metal), 2, -3.5f + 50.0f / metal.weight, new Item.Properties().group(GradientItems.GROUP).addToolType(ToolType.PICKAXE, metal.harvestLevel));
    this.metal = metal;
  }
}
