package lofimodding.gradient;

import lofimodding.progression.Stage;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class GradientStages {
  private GradientStages() { }

  private static final DeferredRegister<Stage> REGISTRY = DeferredRegister.create(Stage.class, Gradient.MOD_ID);

  public static final RegistryObject<Stage> AGE_1 = REGISTRY.register("age_1", () -> new Stage() {
    @Override
    public boolean isDefault() {
      return true;
    }
  });

  public static final RegistryObject<Stage> AGE_2 = REGISTRY.register("age_2", Stage::new);
  public static final RegistryObject<Stage> AGE_3 = REGISTRY.register("age_3", Stage::new);

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering stages...");
    REGISTRY.register(bus);
  }
}
