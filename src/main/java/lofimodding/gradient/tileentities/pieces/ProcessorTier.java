package lofimodding.gradient.tileentities.pieces;

public enum ProcessorTier implements IProcessorTier {
  BASIC(3.0f),
  ;

  private final float recipeTimeMultiplier;

  ProcessorTier(final float recipeTimeMultiplier) {
    this.recipeTimeMultiplier = recipeTimeMultiplier;
  }

  @Override
  public float getRecipeTimeMultiplier() {
    return this.recipeTimeMultiplier;
  }
}
