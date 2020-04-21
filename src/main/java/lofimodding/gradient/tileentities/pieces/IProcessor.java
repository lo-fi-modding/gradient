package lofimodding.gradient.tileentities.pieces;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public interface IProcessor<Recipe extends IRecipe<?>> {
  void setRecipe(@Nullable final Recipe recipe);
  boolean tick();
  int getTicks();
  boolean isFinished();
  void restart();
  CompoundNBT write(final CompoundNBT compound);
  void read(final CompoundNBT compound);
}
