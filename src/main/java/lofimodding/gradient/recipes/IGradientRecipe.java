package lofimodding.gradient.recipes;

import lofimodding.gradient.tileentities.pieces.ProcessorTier;
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
  int getTier();
  int getTicks();
  int getItemInputCount();
  int getItemOutputCount();
  boolean matchesStages(final Set<Stage> stages);
  default boolean matchesTier(final int tier) { return this.getTier() >= tier; }
  default boolean matchesTier(final ProcessorTier tier) { return this.matchesTier(tier.getTier()); }
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
