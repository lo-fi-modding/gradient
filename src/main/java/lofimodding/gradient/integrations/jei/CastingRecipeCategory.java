package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.fluids.GradientFluidStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
    ingredients.setInput(IngredientTypes.GRADIENT_FLUID, recipe.fluid);
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final JeiIntegration.CastingRecipe recipe, final IIngredients ingredients) {
    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final IGuiIngredientGroup<GradientFluidStack> guiFluidStacks = recipeLayout.getIngredientsGroup(IngredientTypes.GRADIENT_FLUID);
    final List<List<ItemStack>> inputItems = ingredients.getInputs(VanillaTypes.ITEM);
    final List<List<GradientFluidStack>> inputFluids = ingredients.getInputs(IngredientTypes.GRADIENT_FLUID);

    final IIngredientRenderer<GradientFluidStack> fluidStackRenderer = new GradientFluidStackRenderer(1.0f, false, 16, 16, null);

    guiItemStacks.init(0, true, 0, 0);
    guiItemStacks.set(0, inputItems.get(0));

    guiFluidStacks.init(1, true, fluidStackRenderer, 20, 0, 18, 18, 1, 1);
    guiFluidStacks.set(1, inputFluids.get(0));

    guiItemStacks.init(2, true, 58, 0);
    guiItemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
  }
}
