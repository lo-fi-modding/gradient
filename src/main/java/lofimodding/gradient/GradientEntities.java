package lofimodding.gradient;

import lofimodding.gradient.entities.PebbleEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientEntities {
  private GradientEntities() { }

  private static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Gradient.MOD_ID);

  public static final RegistryObject<EntityType<PebbleEntity>> PEBBLE = REGISTRY.register(GradientIds.PEBBLE, () -> EntityType.Builder.<PebbleEntity>create(PebbleEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).build(GradientIds.PEBBLE));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering entities...");
    REGISTRY.register(bus);
  }
}
