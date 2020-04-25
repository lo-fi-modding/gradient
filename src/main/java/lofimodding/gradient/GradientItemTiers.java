package lofimodding.gradient;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public final class GradientItemTiers {
  private GradientItemTiers() { }

  public static final IItemTier PEBBLE = new ItemTier(1, 20, 0.5f, 1.0f, 5, () -> Ingredient.fromItems(GradientItems.PEBBLE.get()));
  public static final IItemTier FLINT = new ItemTier(1, 50, 1.0f, 2.0f, 5, () -> Ingredient.fromItems(Items.FLINT));

  public static class ItemTier implements IItemTier {
    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final LazyValue<Ingredient> repairMaterial;

    public ItemTier(final int harvestLevel, final int maxUses, final float efficiency, final float attackDamage, final int enchantability, final Supplier<Ingredient> repairMaterial) {
      this.harvestLevel = harvestLevel;
      this.maxUses = maxUses;
      this.efficiency = efficiency;
      this.attackDamage = attackDamage;
      this.enchantability = enchantability;
      this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    @Override
    public int getMaxUses() {
      return this.maxUses;
    }

    @Override
    public float getEfficiency() {
      return this.efficiency;
    }

    @Override
    public float getAttackDamage() {
      return this.attackDamage;
    }

    @Override
    public int getHarvestLevel() {
      return this.harvestLevel;
    }

    @Override
    public int getEnchantability() {
      return this.enchantability;
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.repairMaterial.getValue();
    }
  }
}