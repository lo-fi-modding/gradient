package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CastingRecipeCategory implements IRecipeCategory<JeiIntegration.CastingRecipe> {
  private static final ResourceLocation BACKGROUND_LOCATION = Gradient.loc("textures/gui/recipe_casting.png");
  private final IGuiHelper guiHelper;

  public CastingRecipeCategory(final IGuiHelper guiHelper) {
    this.guiHelper = guiHelper;
  }

  @Override
  public ResourceLocation getUid() {
    return Gradient.loc("casting");
  }

  @Override
  public Class<? extends JeiIntegration.CastingRecipe> getRecipeClass() {
    return JeiIntegration.CastingRecipe.class;
  }

  @Override
  public String getTitle() {
    return I18n.format("jei.casting.name");
  }

  @Override
  public IDrawable getBackground() {
    return this.guiHelper.createDrawable(BACKGROUND_LOCATION, 0, 0, 76, 18);
  }

  @Override
  public IDrawable getIcon() {
    return this.guiHelper.createDrawableIngredient(new ItemStack(GradientItems.CLAY_CAST(GradientCasts.MATTOCK_HEAD).get()));
  }

  @Override
  public void setIngredients(final JeiIntegration.CastingRecipe recipe, final IIngredients ingredients) {
    ingredients.setInput(VanillaTypes.FLUID, recipe.fluid);
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final JeiIntegration.CastingRecipe recipe, final IIngredients ingredients) {
    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
    final List<List<ItemStack>> inputItems = ingredients.getInputs(VanillaTypes.ITEM);
    final List<List<FluidStack>> inputFluids = ingredients.getInputs(VanillaTypes.FLUID);

    guiItemStacks.init(0, true, 0, 0);
    guiItemStacks.set(0, inputItems.get(0));

    guiFluidStacks.init(1, true, 21, 1, 16, 16, recipe.cast.metalAmount, false, null);
    guiFluidStacks.set(1, inputFluids.get(0));

    guiItemStacks.init(2, true, 58, 0);
    guiItemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
  }
}
