package lofimodding.gradient.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.IItemHandler;

public interface IToolStationRecipe extends IRecipe<CraftingInventory> {
  IRecipeType<IToolStationRecipe> TYPE = IRecipeType.register("tool_station");

  int getWidth();
  boolean recipeMatches(final IItemHandler recipe);
  NonNullList<ToolType> getTools();
  NonNullList<ItemStack> getOutputs();

  @Override
  @Deprecated
  default boolean matches(final CraftingInventory inv, final World world) {
    return false;
  }

  @Override
  @Deprecated
  default ItemStack getCraftingResult(final CraftingInventory inv) {
    return this.getRecipeOutput();
  }

  @Override
  @Deprecated
  default boolean canFit(final int width, final int height) {
    return width * height >= this.getIngredients().size();
  }

  @Override
  default ItemStack getRecipeOutput() {
    return this.getOutputs().get(0);
  }
}
