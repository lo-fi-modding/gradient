package lofimodding.gradient.utils;

import lofimodding.gradient.Gradient;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.function.Predicate;

public final class RecipeUtils {
  private RecipeUtils() { }

  public static <I extends IInventory, T extends IRecipe<I>> Optional<T> getRecipe(final IRecipeType<T> type, final Predicate<T> matcher) {
    return Gradient.getRecipeManager().getRecipes(type).values().stream().flatMap(r -> Util.streamOptional(matcher.test((T)r) ? Optional.of((T)r) : Optional.empty())).findFirst();
  }
}
