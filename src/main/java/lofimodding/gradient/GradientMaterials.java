package lofimodding.gradient;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public final class GradientMaterials {
  private GradientMaterials() { }

  public static final Material CLAY_MACHINE = new Material.Builder(MaterialColor.BROWN).notSolid().build();

  public enum Armour implements IArmorMaterial {
    HIDE(Gradient.loc("hide").toString(), 3, new int[] {1, 1, 2, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, () -> {
      return Ingredient.fromTag(GradientTags.Items.PELTS);
    });

    private static final int[] MAX_DAMAGE_ARRAY = {13, 15, 16, 11};
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final LazyValue<Ingredient> repairMaterial;

    Armour(final String name, final int maxDamageFactor, final int[] damageReductionAmounts, final int enchantability, final SoundEvent equipSound, final float toughness, final Supplier<Ingredient> repairMaterialSupplier) {
      this.name = name;
      this.maxDamageFactor = maxDamageFactor;
      this.damageReductionAmountArray = damageReductionAmounts;
      this.enchantability = enchantability;
      this.soundEvent = equipSound;
      this.toughness = toughness;
      this.repairMaterial = new LazyValue<>(repairMaterialSupplier);
    }

    @Override
    public int getDurability(final EquipmentSlotType slotIn) {
      return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDamageReductionAmount(final EquipmentSlotType slotIn) {
      return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    @Override
    public int getEnchantability() {
      return this.enchantability;
    }

    @Override
    public SoundEvent getSoundEvent() {
      return this.soundEvent;
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.repairMaterial.getValue();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getName() {
      return this.name;
    }

    @Override
    public float getToughness() {
      return this.toughness;
    }
  }
}
