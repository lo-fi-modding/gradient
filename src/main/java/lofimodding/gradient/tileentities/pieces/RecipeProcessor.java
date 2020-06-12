package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.utils.RecipeUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class RecipeProcessor<Recipe extends IGradientRecipe> extends Processor {
  private final IRecipeType<Recipe> recipeType;

  @Nullable
  private Recipe recipe;
  private int ticks;
  private int maxTicks;

  public RecipeProcessor(final ProcessorTile<?> tile, final ProcessorItemHandler.Callback onItemChange, final ProcessorFluidTank.Callback onFluidChange, final IRecipeType<Recipe> recipeType, final Consumer<Builder> builder) {
    super(tile, onItemChange, onFluidChange, builder);
    this.recipeType = recipeType;
  }

  @Override
  public boolean tick(final boolean isClient) {
    if(this.hasWork()) {
      this.ticks++;

      if(!isClient && this.isFinished()) {
        this.finishRecipe();
        this.ticks = 0;
      }

      return true;
    }

    return false;
  }

  @Override
  public int getTicks() {
    return this.ticks;
  }

  private boolean isFinished() {
    return this.ticks >= this.maxTicks;
  }

  private void finishRecipe() {
    final Recipe recipe = this.recipe;

    for(final ItemSlot slot : this.itemInputSlots) {
      slot.extract(this.inv, 1, false); //TODO: don't hardcode to extract one item
    }

    this.inv.disableValidation();

    for(int slot = 0; slot < this.itemOutputSlots.size(); slot++) {
      this.itemOutputSlots.get(slot).insert(this.inv, recipe.getItemOutput(slot), false);
    }

    this.inv.enableValidation();

    this.tanksLocked = false;
    for(int i = 0; i < recipe.getFluidInputCount(); i++) {
      this.fluids.drain(recipe.getFluidInput(i), IFluidHandler.FluidAction.EXECUTE);
    }
    for(int i = 0; i < recipe.getFluidOutputCount(); i++) {
      this.fluids.fill(recipe.getFluidOutput(i), IFluidHandler.FluidAction.EXECUTE);
    }
    this.tanksLocked = true;
  }

  @Override
  public boolean hasWork() {
    return this.recipe != null;
  }

  @Override
  protected void onInputsChanged() {
    final Recipe recipe = RecipeUtils.getRecipe(this.recipeType, this::recipeMatches).orElse(null);

    if(recipe != this.recipe) {
      this.recipe = recipe;
      this.ticks = 0;

      if(this.hasWork()) {
        this.maxTicks = (int)(this.recipe.getTicks() * this.tier.getRecipeTimeMultiplier());
      } else {
        this.maxTicks = Integer.MAX_VALUE;
      }
    }
  }

  private boolean recipeMatches(final IGradientRecipe recipe) {
    final NonNullList<ItemStack> items = NonNullList.create();

    for(final ItemSlot slot : this.itemInputSlots) {
      items.add(slot.get(this.inv));
    }

    this.tanksLocked = false;
    final boolean matches =
      recipe.matchesStages(this.stages) &&
      recipe.matchesItems(items) &&
      recipe.matchesFluids(this.fluids);
    this.tanksLocked = true;

    return matches;
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    final CompoundNBT nbt = super.write(compound);
    nbt.putInt("Ticks", this.ticks);
    return nbt;
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.ticks = compound.getInt("Ticks");
    super.read(compound);
  }
}
