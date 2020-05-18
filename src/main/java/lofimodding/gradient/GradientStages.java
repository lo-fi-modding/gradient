package lofimodding.gradient;

import lofimodding.gradient.utils.DeferredRegister2;
import lofimodding.gradient.utils.RegistryObject2;
import lofimodding.progression.Stage;
import net.minecraftforge.eventbus.api.IEventBus;

public final class GradientStages {
  private GradientStages() { }

  private static final DeferredRegister2<Stage> REGISTRY = new DeferredRegister2<>(Stage.REGISTRY, Gradient.MOD_ID);

  public static final RegistryObject2<Stage> AGE_1 = REGISTRY.register("age_1", () -> new Stage() {
    @Override
    public boolean isDefault() {
      return true;
    }
  });

  public static final RegistryObject2<Stage> AGE_2 = REGISTRY.register("age_2", Stage::new);

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering stages...");
    REGISTRY.register(bus);
  }
}
