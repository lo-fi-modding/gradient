package lofimodding.gradient.recipes;

import lofimodding.progression.Stage;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.Set;

public interface IGradientRecipe extends IRecipe<IInventory> {
  int getTicks();
  boolean matchesStages(final Set<Stage> stages);
  boolean matchesItems(final NonNullList<ItemStack> stacks);
  ItemStack getOutput(final int slot);

  @Override
  default ItemStack getRecipeOutput() {
    return this.getOutput(0);
  }

  @Override
  @Deprecated
  default boolean matches(final IInventory inv, final World world) {
    return false;
  }

  @Override
  @Deprecated
  default ItemStack getCraftingResult(final IInventory inv) {
    return this.getRecipeOutput().copy();
  }
}
