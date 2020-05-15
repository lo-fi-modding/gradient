package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.fluids.GradientFluid;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.GradientFluids;
import lofimodding.gradient.recipes.AlloyRecipe;
import lofimodding.gradient.recipes.CookingRecipe;
import lofimodding.gradient.recipes.DryingRecipe;
import lofimodding.gradient.recipes.FuelRecipe;
import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.recipes.HardeningRecipe;
import lofimodding.gradient.recipes.MeltingRecipe;
import lofimodding.gradient.recipes.MixingRecipe;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Minerals;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
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
    registration.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
    registration.addRecipeCategories(new CookingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new DryingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new FuelRecipeCategory(guiHelper));
    registration.addRecipeCategories(new GrindingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new HardeningRecipeCategory(guiHelper));
    registration.addRecipeCategories(new MeltingRecipeCategory(guiHelper));
    registration.addRecipeCategories(new MixingRecipeCategory(guiHelper));

    registration.addRecipeCategories(new CastingRecipeCategory(guiHelper));
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

    registration.addRecipes(filterRecipes(AlloyRecipe.class), GradientRecipeSerializers.ALLOY.getId());
    registration.addRecipes(filterRecipes(CookingRecipe.class), GradientRecipeSerializers.COOKING.getId());
    registration.addRecipes(filterRecipes(DryingRecipe.class), GradientRecipeSerializers.DRYING.getId());
    registration.addRecipes(filterRecipes(FuelRecipe.class), GradientRecipeSerializers.FUEL.getId());
    registration.addRecipes(filterRecipes(GrindingRecipe.class), GradientRecipeSerializers.GRINDING.getId());
    registration.addRecipes(filterRecipes(HardeningRecipe.class), GradientRecipeSerializers.HARDENING.getId());
    registration.addRecipes(filterRecipes(MeltingRecipe.class), GradientRecipeSerializers.MELTING.getId());
    registration.addRecipes(filterRecipes(MixingRecipe.class), GradientRecipeSerializers.MIXING.getId());

    registration.addRecipes(getCastingRecipes(), Gradient.loc("casting"));
  }

  @Override
  public void registerRecipeCatalysts(final IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.CLAY_METAL_MIXER.get()), GradientRecipeSerializers.ALLOY.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.FIREPIT.get()), GradientRecipeSerializers.COOKING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.DRYING_RACK.get()), GradientRecipeSerializers.DRYING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.FIREPIT.get()), GradientRecipeSerializers.FUEL.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.GRINDSTONE.get()), GradientRecipeSerializers.GRINDING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.FIREPIT.get()), GradientRecipeSerializers.HARDENING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.CLAY_CRUCIBLE.get()), GradientRecipeSerializers.MELTING.getId());
    registration.addRecipeCatalyst(new ItemStack(GradientBlocks.MIXING_BASIN.get()), GradientRecipeSerializers.MIXING.getId());
  }

  @Override
  public void registerIngredients(final IModIngredientRegistration registration) {
    registration.register(
      IngredientTypes.GRADIENT_FLUID,
      GradientFluid.REGISTRY.get().getValues().stream().map(fluid -> new GradientFluidStack(fluid, 1.0f, Float.NaN)).filter(((Predicate<GradientFluidStack>)GradientFluidStack::isEmpty).negate()).collect(Collectors.toList()),
      new GradientFluidStackHelper(),
      new GradientFluidStackRenderer()
    );
  }

  private static <T extends IRecipe<?>> Collection<T> filterRecipes(final Class<T> recipeClass) {
    return Gradient.getRecipeManager().getRecipes().stream()
      .filter(recipe -> recipe.getClass().isAssignableFrom(recipeClass))
      .map(recipeClass::cast)
      .collect(Collectors.toList());
  }

  private static Collection<IRecipe<IInventory>> getCastingRecipes() {
    final List<IRecipe<IInventory>> recipes = new ArrayList<>();

    for(final Metal metal : Minerals.metals()) {
      for(final GradientCasts cast : GradientCasts.values()) {
        recipes.add(new CastingRecipe(cast, metal));
      }
    }

    return recipes;
  }

  protected static class CastingRecipe implements IRecipe<IInventory> {
    public final GradientCasts cast;
    public final Metal metal;
    public final GradientFluidStack fluid;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;

    public CastingRecipe(final GradientCasts cast, final Metal metal) {
      this.cast = cast;
      this.metal = metal;
      this.fluid = new GradientFluidStack(GradientFluids.METAL(metal).get(), 1.0f, Float.NaN);
      this.ingredients = NonNullList.withSize(1, Ingredient.fromItems(GradientItems.CLAY_CAST(cast).get()));
      this.output = new ItemStack(cast.getItem(metal));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
    }

    @Override
    public boolean matches(final IInventory inv, final World world) {
      return false;
    }

    @Override
    public ItemStack getCraftingResult(final IInventory inv) {
      return this.output.copy();
    }

    @Override
    public boolean canFit(final int width, final int height) {
      return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
      return this.output;
    }

    @Override
    public ResourceLocation getId() {
      return this.cast.getItem(this.metal).getRegistryName();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return null;
    }

    @Override
    public IRecipeType<?> getType() {
      return null;
    }
  }
}
