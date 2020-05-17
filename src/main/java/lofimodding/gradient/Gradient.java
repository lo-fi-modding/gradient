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
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
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
    MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
    GradientClient.clientSetup(event);

    ScreenManager.registerFactory(GradientContainers.CLAY_CRUCIBLE.get(), ClayCrucibleScreen::new);
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    if(Config.INTEROP.REMOVE_LEATHER_RECIPES.get()) {
      InterModComms.sendTo("no-recipes", "remove_recipe", () -> (Predicate<IRecipe<?>>)recipe -> recipe.getRecipeOutput().getItem() == Items.LEATHER);
    }
  }

  public static ResourceLocation loc(final String path) {
    return new ResourceLocation(MOD_ID, path);
  }

  private static final ThreadLocal<RecipeManager> RECIPE_MANAGER = new ThreadLocal<>();

  private void onWorldLoad(final RecipesUpdatedEvent event) {
    // Set the recipe manager for clients
    LOGGER.info("Setting recipe manager from client {}", event.getRecipeManager());
    RECIPE_MANAGER.set(event.getRecipeManager());
  }

  private void serverStarting(final FMLServerStartingEvent event) {
    final RecipeManager recipeManager = event.getServer().getRecipeManager();

    // Set the recipe manager for servers
    LOGGER.info("Setting recipe manager from server {}", recipeManager);
    RECIPE_MANAGER.set(recipeManager);

    final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = new HashMap<>();

    for(final IRecipe<?> recipe : recipeManager.getRecipes()) {
      if(!recipe.getId().equals(new ResourceLocation("crafting_table"))) {
        recipes.computeIfAbsent(recipe.getType(), key -> new HashMap<>()).put(recipe.getId(), recipe);
      }
    }

    recipeManager.recipes = recipes;
  }

  public static RecipeManager getRecipeManager() {
    return RECIPE_MANAGER.get();
  }
}
