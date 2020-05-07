package lofimodding.gradient.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lofimodding.gradient.Gradient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntityCondition implements ILootCondition {
  @Nullable
  final EntityType<?> type;

  public EntityCondition(@Nullable final EntityType<?> type) {
    this.type = type;
  }

  @Override
  public boolean test(final LootContext lootContext) {
    final Entity entity = lootContext.get(LootParameters.THIS_ENTITY);
    if(entity == null) {
      return false;
    }

    return this.type == null || this.type == entity.getType();
  }

  public static class Serializer extends ILootCondition.AbstractSerializer<EntityCondition> {
    public Serializer() {
      super(Gradient.loc("entity"), EntityCondition.class);
    }

    @Override
    public void serialize(final JsonObject json, final EntityCondition value, final JsonSerializationContext context) {
      if(value.type != null) {
        json.addProperty("type", value.type.getRegistryName().toString());
      }
    }

    @Override
    public EntityCondition deserialize(final JsonObject json, final JsonDeserializationContext context) {
      final String raw = JSONUtils.getString(json, "type", "");

      if(raw.isEmpty()) {
        return new EntityCondition(null);
      }

      final ResourceLocation type = new ResourceLocation(raw);
      return new EntityCondition(Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(type), "Entity type " + type + " not found"));
    }
  }
}
