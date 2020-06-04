package lofimodding.gradient.recipes;

import lofimodding.progression.Stage;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Set;

public interface IGradientRecipe extends IRecipe<IInventory> {
  int getTicks();
  int getItemInputCount();
  int getItemOutputCount();
  boolean matchesStages(final Set<Stage> stages);
  boolean matchesItems(final NonNullList<ItemStack> stacks);
  ItemStack getItemOutput(final int slot);

  default int getFluidInputCount() { return 0; }
  default int getFluidOutputCount() { return 0; }
  default boolean matchesFluids(final IFluidHandler fluidHandler) { return true; }
  default FluidStack getFluidInput(final int slot) { return FluidStack.EMPTY; }
  default FluidStack getFluidOutput(final int slot) { return FluidStack.EMPTY; }

  @Override
  @Deprecated
  default ItemStack getRecipeOutput() {
    return this.getItemOutput(0);
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
