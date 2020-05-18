package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.MixingRecipe;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public class MixerProcessor implements IProcessor<MixingRecipe> {
  @Nullable
  private MixingRecipe recipe;
  private int ticks;
  private int maxTicks;

  @Override
  public void setRecipe(@Nullable final MixingRecipe recipe) {
    this.recipe = recipe;
    this.ticks = 0;

    if(recipe != null) {
      this.maxTicks = recipe.getTicks() * 3;
    } else {
      this.maxTicks = Integer.MAX_VALUE;
    }
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
    return this.recipe != null && this.ticks >= this.maxTicks;
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
