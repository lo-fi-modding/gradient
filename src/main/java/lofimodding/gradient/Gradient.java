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
import net.minecraft.advancements.AdvancementManager;
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
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
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
    forgeBus.addListener(this::setRecipeManagerServer);

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
    MinecraftForge.EVENT_BUS.addListener(this::setRecipeManagerClient);
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

  private void setRecipeManagerClient(final RecipesUpdatedEvent event) {
    final RecipeManager recipeManager = event.getRecipeManager();

    LOGGER.info("Setting recipe manager for client {}", recipeManager);
    RECIPE_MANAGER.set(recipeManager);
  }

  // Not ideal, but this event fires at just the right time - after data packs are loaded, but before worlds
  private void setRecipeManagerServer(final RegisterDimensionsEvent event) {
    final RecipeManager recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();

    final AdvancementManager advancements = ServerLifecycleHooks.getCurrentServer().getAdvancementManager();
    advancements.getAdvancement(loc("age1/root")).getDisplay().setPosition(0.0f, 3.0f);
    advancements.getAdvancement(loc("age1/pelt")).getDisplay().setPosition(1.0f, 2.5f);
    advancements.getAdvancement(loc("age1/bone_awl")).getDisplay().setPosition(2.0f, 1.375f);
    advancements.getAdvancement(loc("age1/hide_armour")).getDisplay().setPosition(3.0f, 0.375f);

    advancements.getAdvancement(loc("age1/basic_materials")).getDisplay().setPosition(1.0f, 3.5f);
    advancements.getAdvancement(loc("age1/wood")).getDisplay().setPosition(3.0f, 3.5f);
    advancements.getAdvancement(loc("age1/planks")).getDisplay().setPosition(4.0f, 3.5f);

    advancements.getAdvancement(loc("age1/firepit")).getDisplay().setPosition(3.0f, 4.75f);
    advancements.getAdvancement(loc("age1/fire_starter")).getDisplay().setPosition(4.0f, 4.75f);

    advancements.getAdvancement(loc("age1/waterskin")).getDisplay().setPosition(5.75f, 1.0f);
    advancements.getAdvancement(loc("age1/hide_bedding")).getDisplay().setPosition(5.5f, 2.0f);
    advancements.getAdvancement(loc("age1/grindstone")).getDisplay().setPosition(5.25f, 3.0f);
    advancements.getAdvancement(loc("age1/mixing_basin")).getDisplay().setPosition(5.5f, 4.0f);
    advancements.getAdvancement(loc("age1/fibre_torch")).getDisplay().setPosition(5.75f, 5.0f);
    advancements.getAdvancement(loc("age1/goal")).getDisplay().setPosition(7.0f, 3.0f);

    LOGGER.info("Setting recipe manager for server {}", recipeManager);
    RECIPE_MANAGER.set(recipeManager);
  }

  public static RecipeManager getRecipeManager() {
    return RECIPE_MANAGER.get();
  }
}
