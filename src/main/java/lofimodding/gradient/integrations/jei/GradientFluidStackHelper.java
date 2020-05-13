package lofimodding.gradient.integrations.jei;

import com.google.common.base.MoreObjects;
import lofimodding.gradient.fluids.GradientFluid;
import lofimodding.gradient.fluids.GradientFluidStack;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Iterator;

public class GradientFluidStackHelper implements IIngredientHelper<GradientFluidStack> {
  @Override
  @Nullable
  public GradientFluidStack getMatch(final Iterable<GradientFluidStack> ingredients, final GradientFluidStack toMatch) {
    final Iterator<GradientFluidStack> var3 = ingredients.iterator();

    GradientFluidStack fluidStack;
    do {
      if(!var3.hasNext()) {
        return null;
      }

      fluidStack = var3.next();
    } while(toMatch.getFluid() != fluidStack.getFluid());

    return fluidStack;
  }

  @Override
  public String getDisplayName(final GradientFluidStack ingredient) {
    final ITextComponent displayName = ingredient.getName();
    return displayName.getFormattedText();
  }

  @Override
  public String getUniqueId(final GradientFluidStack ingredient) {
    final GradientFluid fluid = ingredient.getFluid();
    final ResourceLocation registryName = fluid.getRegistryName();
    return "fluid:" + registryName;
  }

  @Override
  public String getWildcardId(final GradientFluidStack ingredient) {
    return this.getUniqueId(ingredient);
  }

  @Override
  public String getModId(final GradientFluidStack ingredient) {
    final GradientFluid fluid = ingredient.getFluid();
    final ResourceLocation registryName = fluid.getRegistryName();
    if(registryName == null) {
      final String ingredientInfo = this.getErrorInfo(ingredient);
      throw new IllegalStateException("fluid.getRegistryName() returned null for: " + ingredientInfo);
    }
    return registryName.getNamespace();
  }

  @Override
  public String getResourceId(final GradientFluidStack ingredient) {
    final GradientFluid fluid = ingredient.getFluid();
    final ResourceLocation registryName = fluid.getRegistryName();

    if(registryName == null) {
      final String ingredientInfo = this.getErrorInfo(ingredient);
      throw new IllegalStateException("fluid.getRegistryName() returned null for: " + ingredientInfo);
    }

    return registryName.getPath();
  }

  @Override
  public GradientFluidStack copyIngredient(final GradientFluidStack ingredient) {
    return ingredient.copy();
  }

  @Override
  public GradientFluidStack normalizeIngredient(final GradientFluidStack ingredient) {
    final GradientFluidStack copy = this.copyIngredient(ingredient);
    copy.setAmount(1000);
    return copy;
  }

  @Override
  public String getErrorInfo(@Nullable final GradientFluidStack ingredient) {
    if(ingredient == null) {
      return "null";
    }

    final MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(GradientFluidStack.class);
    final ITextComponent displayName = ingredient.getName();
    toStringHelper.add("Fluid", displayName.getFormattedText());
    toStringHelper.add("Amount", ingredient.getAmount());
    toStringHelper.add("Temperature", ingredient.getTemperature());

    return toStringHelper.toString();
  }
}
