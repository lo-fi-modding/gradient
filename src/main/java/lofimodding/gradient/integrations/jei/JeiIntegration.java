package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.recipes.CookingRecipe;
import lofimodding.gradient.recipes.DryingRecipe;
import lofimodding.gradient.recipes.FuelRecipe;
import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.recipes.HardeningRecipe;
import lofimodding.gradient.recipes.MixingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.stream.Collectors;

@JeiPlugin
public class JeiIntegration implements IModPlugin {
  @Override
  public ResourceLocation getPluginUid() {
    return Gradient.loc("gradient");
  }

  @Override
  public void registerCategories(final IRecipeCategoryRegistration registration) {
    final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
    registration.addRecipeCategories(new CookingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new DryingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new FuelRecipeCategory(guiHelper));
    registration.addRecipeCategories(new GrindingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new HardeningRecipeCategory(guiHelper));
    registration.addRecipeCategories(new MixingRecipeCategory(guiHelper));
  }

  @Override
  public void registerRecipes(final IRecipeRegistration registration) {
    //TODO: blacklist
/*
    final IIngredientBlacklist blacklist = registration.getJeiHelpers().getIngredientBlacklist();

    for(final Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
      if(item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemSword) {
        if("minecraft".equals(item.getRegistryName().getNamespace())) {
          blacklist.addIngredientToBlacklist(new ItemStack(item));
        }
      }
    }
*/

    registration.addRecipes(filterRecipes(CookingRecipe.class), GradientRecipeSerializers.COOKING.getId());
    registration.addRecipes(filterRecipes(MixingRecipe.class), GradientRecipeSerializers.MIXING.getId());
    registration.addRecipes(filterRecipes(GrindingRecipe.class), GradientRecipeSerializers.GRINDING.getId());
    registration.addRecipes(filterRecipes(HardeningRecipe.class), GradientRecipeSerializers.HARDENING.getId());
    registration.addRecipes(filterRecipes(DryingRecipe.class), GradientRecipeSerializers.DRYING.getId());
    registration.addRecipes(filterRecipes(FuelRecipe.class), GradientRecipeSerializers.FUEL.getId());
  }

  @Override
  public void registerRecipeCatalysts(final IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.FIREPIT.get()), GradientRecipeSerializers.COOKING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.MIXING_BASIN.get()), GradientRecipeSerializers.MIXING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.GRINDSTONE.get()), GradientRecipeSerializers.GRINDING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.FIREPIT.get()), GradientRecipeSerializers.HARDENING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.DRYING_RACK.get()), GradientRecipeSerializers.DRYING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.FIREPIT.get()), GradientRecipeSerializers.FUEL.getId());
  }

  @Override
  public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registration) {
    //TODO
//    registration.getCraftingCategory().addCategoryExtension(AgeGatedShapedToolRecipe.class, ShapedAgeCraftingExtension::new);
//    registration.getCraftingCategory().addCategoryExtension(AgeGatedShapelessToolRecipe.class, ShapelessAgeCraftingExtension::new);
  }

  private static <T extends IRecipe<?>> Collection<T> filterRecipes(final Class<T> recipeClass) {
    return Gradient.getRecipeManager().getRecipes().stream()
      .filter(recipe -> recipe.getClass().isAssignableFrom(recipeClass))
      .map(recipeClass::cast)
      .collect(Collectors.toList());
  }
}
