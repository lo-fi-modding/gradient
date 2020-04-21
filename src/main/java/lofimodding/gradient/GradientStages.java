package lofimodding.gradient;

import lofimodding.progression.Stage;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Gradient.MOD_ID)
public final class GradientStages {
  private GradientStages() { }

  public static final Stage AGE_1 = null;
  public static final Stage AGE_2 = null;

  @SubscribeEvent
  public static void registerStages(final RegistryEvent.Register<Stage> event) {
    Gradient.LOGGER.info("Registering stages...");

    event.getRegistry().registerAll(
      new Stage().setRegistryName("age_1"),
      new Stage().setRegistryName("age_2")
    );
  }
}
