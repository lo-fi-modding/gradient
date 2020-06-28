package lofimodding.gradient.tileentities.pieces;

public enum ProcessorTier {
  BASIC(3.0f),
  MECHANICAL(3.0f),
  ;

  private final float recipeTimeMultiplier;

  ProcessorTier(final float recipeTimeMultiplier) {
    this.recipeTimeMultiplier = recipeTimeMultiplier;
  }

  public float getRecipeTimeMultiplier() {
    return this.recipeTimeMultiplier;
  }
}
