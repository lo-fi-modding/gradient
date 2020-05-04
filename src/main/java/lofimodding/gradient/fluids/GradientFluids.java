package lofimodding.gradient.fluids;

import lofimodding.gradient.Gradient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
@ObjectHolder(Gradient.MOD_ID)
public final class GradientFluids {
  private GradientFluids() { }

  public static final GradientFluid EMPTY = null;

  @SubscribeEvent
  public static void registerFluids(final RegistryEvent.Register<GradientFluid> event) {
    Gradient.LOGGER.info("Registering Gradient fluids...");

    event.getRegistry().registerAll(
      new GradientFluid().setRegistryName("empty")
    );
  }
}
