package lofimodding.gradient;

import lofimodding.gradient.data.AppendLootTable;
import lofimodding.gradient.data.BlockTagCondition;
import lofimodding.gradient.data.EntityCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientLoot {
  private GradientLoot() { }

  private static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Gradient.MOD_ID);

  public static final RegistryObject<GlobalLootModifierSerializer<AppendLootTable>> APPEND_LOOT = REGISTRY.register("append_loot", AppendLootTable.Serializer::new);

  public static final ResourceLocation HIDE_ARMOUR_ADVANCEMENT = Gradient.loc("advancements/hide_armour");
  public static final ResourceLocation FIBRE_ADDITIONS = Gradient.loc("blocks/fibre_additions");
  public static final ResourceLocation PEBBLE_ADDITIONS = Gradient.loc("blocks/pebble_additions");

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering loot modifier serializers...");
    LootConditionManager.registerCondition(new BlockTagCondition.Serializer());
    LootConditionManager.registerCondition(new EntityCondition.Serializer());
    REGISTRY.register(bus);
  }
}
