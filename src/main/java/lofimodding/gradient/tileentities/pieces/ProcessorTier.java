package lofimodding.gradient.tileentities.pieces;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum ProcessorTier {
  BASIC(0, 3.0f),
  MECHANICAL(1, 3.0f),
  ;

  private final ITextComponent name;
  private final int tier;
  private final float recipeTimeMultiplier;

  ProcessorTier(final int tier, final float recipeTimeMultiplier) {
    this.name = new TranslationTextComponent("tier.gradient." + this.name().toLowerCase());
    this.tier = tier;
    this.recipeTimeMultiplier = recipeTimeMultiplier;
  }

  public ITextComponent getLocalizedName() {
    return this.name;
  }

  public int getTier() {
    return this.tier;
  }

  public float getRecipeTimeMultiplier() {
    return this.recipeTimeMultiplier;
  }
}
