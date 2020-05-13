package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.fluids.GradientFluidStack;
import mezz.jei.api.ingredients.IIngredientType;

public final class IngredientTypes {
  private IngredientTypes() { }

  public static final IIngredientType<GradientFluidStack> GRADIENT_FLUID = () -> GradientFluidStack.class;
}
