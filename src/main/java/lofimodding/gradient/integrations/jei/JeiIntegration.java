package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientFluids;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
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
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

    registration.addIngredientInfo(new ItemStack(GradientItems.FIREPIT.get()), VanillaTypes.ITEM, I18n.format("jei.information." + GradientIds.FIREPIT + ".1"), I18n.format("jei.information." + GradientIds.FIREPIT + ".2"), I18n.format("jei.information." + GradientIds.FIREPIT + ".3"));
    registration.addIngredientInfo(new ItemStack(GradientItems.RECIPE_FILTER.get()), VanillaTypes.ITEM, I18n.format("jei.information." + GradientIds.RECIPE_FILTER + ".1"));
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
  public void registerGuiHandlers(final IGuiHandlerRegistration registration) {
    // Oh god this is bad
    try {
      final Class<?> cls = Class.forName("mezz.jei.load.registration.GuiHandlerRegistration");
      final Field guiHandlersField = cls.getDeclaredField("guiHandlers");
      guiHandlersField.setAccessible(true);

      final Object guiHandlers = guiHandlersField.get(registration);

      final Method keySet = Class.forName("mezz.jei.collect.MultiMap").getDeclaredMethod("keySet");

      ((Set<Class<? extends ContainerScreen<?>>>)keySet.invoke(guiHandlers)).removeIf(screenCls -> screenCls == InventoryScreen.class);
    } catch(final ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      Gradient.LOGGER.error("Failed to remove JEI's vanilla inventory click thing", e);
    }

    registration.addRecipeClickArea(InventoryScreen.class, 154, 18, 16, 16, new ResourceLocation("crafting"));
  }

  @Override
  public void registerRecipeTransferHandlers(final IRecipeTransferRegistration registration) {
    registration.addRecipeTransferHandler(new InventoryCraftingTransferInfo(new ResourceLocation("crafting")));

    //TODO not sure if this will be necessary or not. Removes the vanilla inv transfer handler.
//    try {
//      final Class<?> cls = Class.forName("mezz.jei.load.registration.RecipeTransferRegistration");
//      final HashBasedTable<Class<?>, ResourceLocation, IRecipeTransferHandler<?>> handlers = HashBasedTable.create((ImmutableTable<Class<?>, ResourceLocation, IRecipeTransferHandler<?>>)cls.getDeclaredMethod("getRecipeTransferHandlers").invoke(registration));
//
//      final Field recipeTransferHandlersMethod = cls.getDeclaredField("recipeTransferHandlers");
//      recipeTransferHandlersMethod.setAccessible(true);
//      final Object recipeTransferHandlers = recipeTransferHandlersMethod.get(registration);
//
//      final Class<?> tableClass = Class.forName("mezz.jei.collect.Table");
//      final Method clearMethod = tableClass.getDeclaredMethod("clear");
//      clearMethod.invoke(recipeTransferHandlers);
//
//      final Method putMethod = tableClass.getDeclaredMethod("put", Object.class, Object.class, Object.class);
//
//      for(final Table.Cell<Class<?>, ResourceLocation, IRecipeTransferHandler<?>> cell : handlers.cellSet()) {
//        if("mezz.jei.transfer.PlayerRecipeTransferHandler".equals(cell.getValue().getClass().getName())) {
//          continue;
//        }
//
//        putMethod.invoke(recipeTransferHandlers, cell.getRowKey(), cell.getColumnKey(), cell.getValue());
//      }
//    } catch(final ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//      e.printStackTrace();
//    }
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
    public final FluidStack fluid;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;

    public CastingRecipe(final GradientCasts cast, final Metal metal) {
      this.cast = cast;
      this.metal = metal;
      this.fluid = new FluidStack(GradientFluids.METAL(metal).get(), GradientFluids.INGOT_AMOUNT);
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

  private static final class InventoryCraftingTransferInfo implements IRecipeTransferInfo<PlayerContainer> {
    private final ResourceLocation uid;

    private InventoryCraftingTransferInfo(final ResourceLocation uid) {
      this.uid = uid;
    }

    @Override
    public Class<PlayerContainer> getContainerClass() {
      return PlayerContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
      return this.uid;
    }

    @Override
    public boolean canHandle(final PlayerContainer container) {
      return true;
    }

    @Override
    public List<Slot> getRecipeSlots(final PlayerContainer container) {
      final List<Slot> slots = new ArrayList<>();
      slots.add(container.getSlot(1));
      slots.add(container.getSlot(2));
      slots.add(container.getSlot(3));
      slots.add(container.getSlot(4));
      slots.add(container.getSlot(46));
      slots.add(container.getSlot(47));
      slots.add(container.getSlot(48));
      slots.add(container.getSlot(49));
      slots.add(container.getSlot(50));

      return slots;
    }

    @Override
    public List<Slot> getInventorySlots(final PlayerContainer container) {
      final List<Slot> slots = new ArrayList<>();

      for(int slot = 9; slot <= 45; slot++) {
        slots.add(container.getSlot(slot));
      }

      return slots;
    }
  }
}
