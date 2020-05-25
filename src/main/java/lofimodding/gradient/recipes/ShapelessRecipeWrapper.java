package lofimodding.gradient.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

public class ShapelessRecipeWrapper extends ShapelessRecipe {
  private final IRecipe<CraftingInventory> original;
  private final BiPredicate<CraftingInventory, World> matcher;

  public ShapelessRecipeWrapper(final IRecipe<CraftingInventory> original, final BiPredicate<CraftingInventory, World> matcher) {
    super(original.getId(), original.getGroup(), original.getRecipeOutput(), original.getIngredients());
    this.original = original;
    this.matcher = matcher;
  }

  @Override
  public boolean matches(final CraftingInventory inv, final World world) {
    return this.matcher.test(inv, world) && this.original.matches(inv, world);
  }

  @Override
  public ItemStack getCraftingResult(final CraftingInventory inv) {
    return this.original.getCraftingResult(inv);
  }

  @Override
  public boolean canFit(final int width, final int height) {
    return this.original.canFit(width, height);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.original.getRecipeOutput();
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(final CraftingInventory inv) {
    return this.original.getRemainingItems(inv);
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.original.getIngredients();
  }

  @Override
  public boolean isDynamic() {
    return this.original.isDynamic();
  }

  @Override
  public String getGroup() {
    return this.original.getGroup();
  }

  @Override
  public ItemStack getIcon() {
    return this.original.getIcon();
  }

  @Override
  public ResourceLocation getId() {
    return this.original.getId();
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return this.original.getSerializer();
  }

  @Override
  public IRecipeType<?> getType() {
    return this.original.getType();
  }
}
