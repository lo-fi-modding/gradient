package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.recipes.AlloyRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class AlloyRecipeCategory implements IRecipeCategory<AlloyRecipe> {
  private static final ResourceLocation BACKGROUND_LOCATION = Gradient.loc("textures/gui/recipe_alloy.png");
  private final IGuiHelper guiHelper;

  public AlloyRecipeCategory(final IGuiHelper guiHelper) {
    this.guiHelper = guiHelper;
  }

  @Override
  public ResourceLocation getUid() {
    return GradientRecipeSerializers.ALLOY.getId();
  }

  @Override
  public Class<? extends AlloyRecipe> getRecipeClass() {
    return AlloyRecipe.class;
  }

  @Override
  public String getTitle() {
    return I18n.format("jei.alloy.name");
  }

  @Override
  public IDrawable getBackground() {
    return this.guiHelper.createDrawable(BACKGROUND_LOCATION, 0, 0, 116, 18);
  }

  @Override
  public IDrawable getIcon() {
    return this.guiHelper.createDrawableIngredient(new ItemStack(GradientItems.CLAY_METAL_MIXER.get()));
  }

  @Override
  public void setIngredients(final AlloyRecipe recipe, final IIngredients ingredients) {
    ingredients.setInputs(VanillaTypes.FLUID, new ArrayList<>(recipe.getFluidInputs()));
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getFluidOutput());
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final AlloyRecipe recipe, final IIngredients ingredients) {
    final IGuiIngredientGroup<FluidStack> guiFluidStacks = recipeLayout.getIngredientsGroup(VanillaTypes.FLUID);
    final List<List<FluidStack>> inputs = ingredients.getInputs(VanillaTypes.FLUID);

    float largestSize = 0.0f;
    for(final FluidStack stack : recipe.getFluidInputs()) {
      if(stack.getAmount() > largestSize) {
        largestSize = stack.getAmount();
      }
    }

    if(recipe.getFluidOutput().getAmount() > largestSize) {
      largestSize = recipe.getFluidOutput().getAmount();
    }

    final IIngredientRenderer<FluidStack> fluidStackRenderer = new FluidStackRenderer(largestSize, false, 16, 16, null);

    for(int i = 0; i < recipe.getFluidInputs().size(); i++) {
      guiFluidStacks.init(i, true, fluidStackRenderer, i * 20, 0, 18, 18, 1, 1);
      guiFluidStacks.set(i, inputs.get(i));
    }

    guiFluidStacks.init(4, true, fluidStackRenderer, 98, 0, 18, 18, 1, 1);
    guiFluidStacks.set(4, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
  }
}
