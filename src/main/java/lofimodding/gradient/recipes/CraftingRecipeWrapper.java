package lofimodding.gradient.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

public class CraftingRecipeWrapper extends RecipeWrapper<CraftingInventory> implements ICraftingRecipe {
  public CraftingRecipeWrapper(final IRecipe<CraftingInventory> original, final BiPredicate<CraftingInventory, World> matcher) {
    super(original, matcher);
  }
}
