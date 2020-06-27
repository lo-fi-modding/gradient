package lofimodding.gradient;

import lofimodding.gradient.recipes.AlloyRecipe;
import lofimodding.gradient.recipes.CookingRecipe;
import lofimodding.gradient.recipes.DryingRecipe;
import lofimodding.gradient.recipes.FuelRecipe;
import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.recipes.HardeningRecipe;
import lofimodding.gradient.recipes.MeltingRecipe;
import lofimodding.gradient.recipes.MixingRecipe;
import lofimodding.gradient.recipes.ShapelessToolStationRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientRecipeSerializers {
  private GradientRecipeSerializers() { }

  private static final DeferredRegister<IRecipeSerializer<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, Gradient.MOD_ID);

  public static final RegistryObject<IRecipeSerializer<AlloyRecipe>> ALLOY = REGISTRY.register("alloy", AlloyRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<CookingRecipe>> COOKING = REGISTRY.register("cooking", CookingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<DryingRecipe>> DRYING = REGISTRY.register("drying", DryingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<FuelRecipe>> FUEL = REGISTRY.register("fuel", FuelRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<GrindingRecipe>> GRINDING = REGISTRY.register("grinding", GrindingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<HardeningRecipe>> HARDENING = REGISTRY.register("hardening", HardeningRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> MELTING = REGISTRY.register("melting", MeltingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<MixingRecipe>> MIXING = REGISTRY.register("mixing", MixingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<ShapelessToolStationRecipe>> SHAPELESS_TOOL_STATION = REGISTRY.register("shapeless_tool_station", ShapelessToolStationRecipe.Serializer::new);

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering recipe serializers...");
    REGISTRY.register(bus);
  }
}
