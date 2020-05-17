package lofimodding.gradient;

import lofimodding.gradient.advancements.criterion.GradientCriteriaTriggers;
import lofimodding.gradient.client.GradientClient;
import lofimodding.gradient.client.screens.ClayCrucibleScreen;
import lofimodding.gradient.energy.EnergyCapability;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.KineticEnergyTransfer;
import lofimodding.gradient.fluids.GradientFluidHandlerCapability;
import lofimodding.gradient.network.Packets;
import lofimodding.progression.recipes.ShapelessStagedRecipe;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.function.Predicate;

@Mod(Gradient.MOD_ID)
public class Gradient {
  public static final String MOD_ID = "gradient";
  public static final Logger LOGGER = LogManager.getLogger();

  public Gradient() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.ENET_SPEC);

    final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

    modBus.addListener(this::setup);
    modBus.addListener(this::enqueueIMC);
    modBus.addListener(this::clientSetup);
    forgeBus.addListener(this::serverStarting);

    GradientBlocks.init(modBus);
    GradientContainers.init(modBus);
    GradientFluids.init(modBus);
    lofimodding.gradient.fluids.GradientFluids.init(modBus);
    GradientItems.init(modBus);
    GradientEntities.init(modBus);
    GradientLoot.init(modBus);
    GradientRecipeSerializers.init(modBus);
    GradientSounds.init(modBus);
    GradientStages.init(modBus);
    GradientTileEntities.init(modBus);
  }

  private void setup(final FMLCommonSetupEvent event) {
    Packets.register();

    GradientFluidHandlerCapability.register();

    EnergyCapability.register(
      IKineticEnergyStorage.class,
      IKineticEnergyTransfer.class,
      () -> new KineticEnergyStorage(10000.0f),
      KineticEnergyTransfer::new
    );

    // Trigger loading
    Gradient.LOGGER.debug(GradientCriteriaTriggers.ADVANCEMENT_UNLOCKED);
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    LOGGER.info("Loading client-only features...");
    MinecraftForge.EVENT_BUS.addListener(this::recipesUpdated);
    GradientClient.clientSetup(event);

    ScreenManager.registerFactory(GradientContainers.CLAY_CRUCIBLE.get(), ClayCrucibleScreen::new);
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    if(Config.INTEROP.REMOVE_LEATHER_RECIPES.get()) {
      InterModComms.sendTo("no-recipes", "remove_recipe", () -> (Predicate<IRecipe<?>>)recipe -> recipe.getRecipeOutput().getItem() == Items.LEATHER);
    }

    if(Config.INTEROP.REPLACE_PLANK_RECIPES.get()) {
      InterModComms.sendTo("no-recipes", "replace_recipe", () -> new Tuple<Predicate<IRecipe<?>>, Function<IRecipe<?>, IRecipe<?>>>(
        recipe -> recipe.getType() == IRecipeType.CRAFTING && recipe.getRecipeOutput().getItem().isIn(ItemTags.PLANKS) && recipe.getIngredients().size() == 1,
        original -> {
          final NonNullList<Ingredient> ingredients = NonNullList.create();
          ingredients.addAll(original.getIngredients());
          ingredients.add(Ingredient.fromTag(GradientTags.Items.AXES));

          final ItemStack output = ItemHandlerHelper.copyStackWithSize(original.getRecipeOutput(), Config.INTEROP.HALVE_PLANK_RECIPE_OUTPUT.get() ? original.getRecipeOutput().getCount() / 2 : original.getRecipeOutput().getCount());
          final ShapelessRecipe recipe = new ShapelessRecipe(original.getId(), original.getGroup(), output, ingredients);
          return new ShapelessStagedRecipe(recipe, NonNullList.create(), true);
        })
      );
    }

    if(Config.INTEROP.REPLACE_STICK_RECIPES.get()) {
      InterModComms.sendTo("no-recipes", "replace_recipe", () -> new Tuple<Predicate<IRecipe<?>>, Function<IRecipe<?>, IRecipe<?>>>(
        recipe -> recipe.getType() == IRecipeType.CRAFTING && recipe.getRecipeOutput().getItem().isIn(Tags.Items.RODS_WOODEN) && recipe.getIngredients().size() == 2,
        original -> {
          final NonNullList<Ingredient> ingredients = NonNullList.create();
          ingredients.add(original.getIngredients().get(0));
          ingredients.add(Ingredient.fromTag(GradientTags.Items.AXES));

          final ItemStack output = ItemHandlerHelper.copyStackWithSize(original.getRecipeOutput(), Config.INTEROP.HALVE_STICK_RECIPE_OUTPUT.get() ? original.getRecipeOutput().getCount() / 2 : original.getRecipeOutput().getCount());
          final ShapelessRecipe recipe = new ShapelessRecipe(original.getId(), original.getGroup(), output, ingredients);
          return new ShapelessStagedRecipe(recipe, NonNullList.create(), true);
        })
      );
    }
  }

  public static ResourceLocation loc(final String path) {
    return new ResourceLocation(MOD_ID, path);
  }

  private static final ThreadLocal<RecipeManager> RECIPE_MANAGER = new ThreadLocal<>();

  private void recipesUpdated(final RecipesUpdatedEvent event) {
    LOGGER.info("Setting recipe manager for client {}", event.getRecipeManager());
    RECIPE_MANAGER.set(event.getRecipeManager());
  }

  private void serverStarting(final FMLServerStartingEvent event) {
    final RecipeManager recipeManager = event.getServer().getRecipeManager();

    LOGGER.info("Setting recipe manager for server {}", recipeManager);
    RECIPE_MANAGER.set(recipeManager);
  }

  public static RecipeManager getRecipeManager() {
    return RECIPE_MANAGER.get();
  }
}
