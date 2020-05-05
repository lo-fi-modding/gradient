package lofimodding.gradient;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum GradientCasts {
  MATTOCK_HEAD(1.0f),
  PICKAXE_HEAD(1.0f),
  HAMMER_HEAD(1.0f),
  SWORD_BLADE(1.0f);

  public final String name;
  public final float metalAmount;

  GradientCasts(final float metalAmount) {
    this.name = this.name().toLowerCase();
    this.metalAmount = metalAmount;
  }

  public String getTranslationKey() {
    return "cast.gradient." + this.name;
  }

  public ITextComponent getUnlocalizedName() {
    return new TranslationTextComponent(this.getTranslationKey());
  }
}
