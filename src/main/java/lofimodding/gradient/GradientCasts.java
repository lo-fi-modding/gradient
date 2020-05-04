package lofimodding.gradient;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum GradientCasts {
  MATTOCK_HEAD,
  PICKAXE_HEAD,
  HAMMER_HEAD,
  SWORD_BLADE;

  public final String name;

  GradientCasts() {
    this.name = this.name().toLowerCase();
  }

  public String getTranslationKey() {
    return "cast.gradient." + this.name;
  }

  public ITextComponent getUnlocalizedName() {
    return new TranslationTextComponent(this.getTranslationKey());
  }
}
