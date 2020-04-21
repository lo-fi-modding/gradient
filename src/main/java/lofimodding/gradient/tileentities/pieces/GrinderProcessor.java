package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.GrindingRecipe;

import javax.annotation.Nullable;

public class GrinderProcessor implements IProcessor<GrindingRecipe> {
  @Nullable
  private GrindingRecipe recipe;
  private int ticks;

  @Override
  public void setRecipe(@Nullable final GrindingRecipe recipe) {
    this.recipe = recipe;
    this.ticks = 0;
  }

  @Override
  public boolean tick() {
    if(this.recipe != null) {
      this.ticks++;
      return true;
    }

    return false;
  }

  @Override
  public boolean isFinished() {
    return this.recipe != null && this.ticks >= this.recipe.getTicks();
  }
}
