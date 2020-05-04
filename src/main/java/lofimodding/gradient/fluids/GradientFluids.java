package lofimodding.gradient.fluids;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.utils.DeferredRegister2;
import lofimodding.gradient.utils.RegistryObject2;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.HashMap;
import java.util.Map;

public final class GradientFluids {
  private GradientFluids() { }

  private static final DeferredRegister2<GradientFluid> REGISTRY = new DeferredRegister2<>(GradientFluid.REGISTRY, Gradient.MOD_ID);

  public static final RegistryObject2<GradientFluid> EMPTY = REGISTRY.register("empty", GradientFluid::new);

  private static final Map<Metal, RegistryObject2<GradientFluid>> METALS = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      METALS.put(metal, REGISTRY.register(metal.name, () -> new MetalFluid(metal)));
    }
  }

  public static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering Gradient fluids...");
    REGISTRY.register(bus);
  }

  public static RegistryObject2<GradientFluid> METAL(final Metal metal) {
    return METALS.get(metal);
  }
}
