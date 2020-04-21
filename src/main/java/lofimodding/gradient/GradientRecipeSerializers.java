package lofimodding.gradient;

import lofimodding.gradient.recipes.GrindingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientRecipeSerializers {
  private GradientRecipeSerializers() { }

  private static final DeferredRegister<IRecipeSerializer<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, Gradient.MOD_ID);

  public static final RegistryObject<IRecipeSerializer<GrindingRecipe>> GRINDING = REGISTRY.register("grinding", GrindingRecipe.Serializer::new);

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering recipe serializers...");
    REGISTRY.register(bus);
  }
}
