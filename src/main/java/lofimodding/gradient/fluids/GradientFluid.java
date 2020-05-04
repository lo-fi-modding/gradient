package lofimodding.gradient.fluids;

import lofimodding.gradient.Gradient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GradientFluid extends ForgeRegistryEntry<GradientFluid> {
  private static IForgeRegistry<GradientFluid> registry;

  public static final Supplier<IForgeRegistry<GradientFluid>> REGISTRY = new Supplier<IForgeRegistry<GradientFluid>>() {
    @Override
    public IForgeRegistry<GradientFluid> get() {
      return registry;
    }
  };

  @SubscribeEvent
  public static void createRegistry(final RegistryEvent.NewRegistry event) {
    Gradient.LOGGER.info("Creating Gradient fluid registry...");

    registry = new RegistryBuilder<GradientFluid>()
      .setName(Gradient.loc("fluid"))
      .setType(GradientFluid.class)
      .create();
  }
}
