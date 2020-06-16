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
    super(GradientItemTiers.METALS.get(metal), (int)(-GradientItemTiers.METALS.get(metal).getAttackDamage() * 0.5f), -3.6f + 33.0f / metal.weight, new Item.Properties().group(GradientItems.GROUP).addToolType(ToolType.PICKAXE, metal.harvestLevel));
    this.metal = metal;
  }
}
