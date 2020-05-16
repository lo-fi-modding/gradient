package lofimodding.gradient;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientSounds {
  private GradientSounds() { }

  private static final DeferredRegister<SoundEvent> REGISTRY = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Gradient.MOD_ID);

  public static final RegistryObject<SoundEvent> FIRE_STARTER = REGISTRY.register("fire_starter", () -> new SoundEvent(Gradient.loc("fire_starter")));
  public static final RegistryObject<SoundEvent> GRINDSTONE = REGISTRY.register("grindstone", () -> new SoundEvent(Gradient.loc("grindstone")));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering sounds...");
    REGISTRY.register(bus);
  }
}
