package lofimodding.gradient;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import lofimodding.gradient.advancements.criterion.GradientCriteriaTriggers;
import lofimodding.gradient.client.GradientClient;
import lofimodding.gradient.energy.EnergyCapability;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.KineticEnergyTransfer;
import lofimodding.gradient.fluids.GradientFluidHandlerCapability;
import lofimodding.gradient.network.Packets;
import lofimodding.gradient.recipes.FurnaceRecipeWrapper;
import lofimodding.gradient.recipes.ShapedRecipeWrapper;
import lofimodding.gradient.recipes.ShapelessRecipeWrapper;
import lofimodding.progression.recipes.ShapelessStagedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
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
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.INTEROP_SPEC, MOD_ID + "_interop-server.toml");
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.ENET_SPEC, MOD_ID + "_enet-server.toml");

    final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

    modBus.addListener(this::setup);
    modBus.addListener(this::enqueueIMC);
    modBus.addListener(this::clientSetup);
    modBus.addListener(this::loadComplete);
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

    // Take over advancement deserializer to allow custom positioning
    AdvancementManager.GSON = new GsonBuilder().registerTypeHierarchyAdapter(Advancement.Builder.class, (JsonDeserializer<Advancement.Builder>)(p_210124_0_, p_210124_1_, context) -> {
      final JsonObject advancementJson = JSONUtils.getJsonObject(p_210124_0_, "advancement");
      final Advancement.Builder builder = Advancement.Builder.deserialize(advancementJson, context);

      if(advancementJson.has("display") && builder.display != null) {
        final JsonObject displayJson = JSONUtils.getJsonObject(advancementJson, "display");

        if(displayJson.has("x") && displayJson.has("y")) {
          final float x = JSONUtils.getFloat(displayJson, "x");
          final float y = JSONUtils.getFloat(displayJson, "y");

          // Prevent auto-layout
          builder.display = new DisplayInfo(builder.display.getIcon(), builder.display.getTitle(), builder.display.getDescription(), builder.display.getBackground(), builder.display.getFrame(), builder.display.shouldShowToast(), builder.display.shouldAnnounceToChat(), builder.display.isHidden()) {
            @Override
            public float getX() {
              return x;
            }

            @Override
            public float getY() {
              return y;
            }
          };
        }
      }

      return builder;
    }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
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
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    if(Config.INTEROP.REMOVE_LEATHER_RECIPES.get()) {
      InterModComms.sendTo("no-recipes", "remove_recipe", () -> (Predicate<IRecipe<?>>)recipe -> recipe.getType() == IRecipeType.CRAFTING && recipe.getRecipeOutput().getItem() == Items.LEATHER);
    }

    InterModComms.sendTo("no-recipes", "remove_recipe", () -> (Predicate<IRecipe<?>>)recipe -> recipe.getType() == IRecipeType.CRAFTING && recipe.getRecipeOutput().getItem() == Items.BREAD || recipe.getRecipeOutput().getItem() == Items.SUGAR);

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

          final ItemStack output = ItemHandlerHelper.copyStackWithSize(original.getRecipeOutput(), Config.INTEROP.HALVE_STICK_RECIPE_OUTPUT.get() ? MathHelper.ceil(original.getRecipeOutput().getCount() / 2.0f) : original.getRecipeOutput().getCount());
          final ShapelessRecipe recipe = new ShapelessRecipe(original.getId(), original.getGroup(), output, ingredients);
          return new ShapelessStagedRecipe(recipe, NonNullList.create(), true);
        })
      );
    }

    if(Config.INTEROP.REMOVE_VANILLA_LEASH_RECIPE.get()) {
      InterModComms.sendTo("no-recipes", "remove_recipe", () -> (Predicate<IRecipe<?>>)recipe -> recipe.getId().equals(new ResourceLocation("lead")));
    }

    if(Config.INTEROP.DISABLE_VANILLA_CRAFTING_TABLE.get()) {
      // Wraps all crafting recipes in a special IRecipe implementation that delegates all methods to the wrapped IRecipe.
      // It has special handling to fail matches if the container is the vanilla workbench container.

      InterModComms.sendTo("no-recipes", "replace_recipe", () -> new Tuple<Predicate<IRecipe<?>>, Function<IRecipe<?>, IRecipe<?>>>(
        recipe -> recipe.getType() == IRecipeType.CRAFTING && recipe instanceof ShapelessRecipe,
        original -> new ShapelessRecipeWrapper((ShapelessRecipe)original, (inv, world) -> !(inv.eventHandler instanceof WorkbenchContainer)))
      );

      InterModComms.sendTo("no-recipes", "replace_recipe", () -> new Tuple<Predicate<IRecipe<?>>, Function<IRecipe<?>, IRecipe<?>>>(
        recipe -> recipe.getType() == IRecipeType.CRAFTING && recipe instanceof ShapedRecipe,
        original -> new ShapedRecipeWrapper((ShapedRecipe)original, (inv, world) -> !(inv.eventHandler instanceof WorkbenchContainer)))
      );
    }

    if(Config.INTEROP.DISABLE_VANILLA_FURNACE.get()) {
      // Wraps all furnace recipes in a special IRecipe implementation that delegates all methods to the wrapped IRecipe.
      // It has special handling to fail matches if the inventory is the vanilla furnace TE.

      InterModComms.sendTo("no-recipes", "replace_recipe", () -> new Tuple<Predicate<IRecipe<?>>, Function<IRecipe<?>, IRecipe<?>>>(
        recipe -> recipe.getType() == IRecipeType.SMELTING && recipe instanceof FurnaceRecipe,
        original -> new FurnaceRecipeWrapper((FurnaceRecipe)original, (inv, world) -> !(inv instanceof AbstractFurnaceTileEntity)))
      );
    }
  }

  private void loadComplete(final FMLLoadCompleteEvent event) {
    GradientWorldGen.addWorldGeneration();
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

    LOGGER.info("Setting recipe manager for server {}", recipeManager);
    RECIPE_MANAGER.set(recipeManager);
  }

  public static RecipeManager getRecipeManager() {
    return RECIPE_MANAGER.get();
  }
}
