package lofimodding.gradient.tileentities.pieces;

import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

public interface IProcessor<Recipe extends IRecipe<?>> {
  void setRecipe(@Nullable final Recipe recipe);
  boolean tick();
  boolean isFinished();
}
