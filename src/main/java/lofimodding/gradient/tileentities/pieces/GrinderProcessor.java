package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.GrindingRecipe;
import net.minecraft.nbt.CompoundNBT;

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
  public int getTicks() {
    return this.ticks;
  }

  @Override
  public boolean isFinished() {
    return this.recipe != null && this.ticks >= this.recipe.getTicks();
  }

  @Override
  public void restart() {
    this.ticks = 0;
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putInt("ticks", this.ticks);
    return compound;
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.ticks = compound.getInt("ticks");
  }
}
