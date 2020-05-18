package lofimodding.gradient.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lofimodding.gradient.Gradient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

// Yeah I'm hard-coding it. Sue me.
public class EntityBoneDropsCondition implements ILootCondition {
  @Override
  public boolean test(final LootContext lootContext) {
    final Entity entity = lootContext.get(LootParameters.THIS_ENTITY);
    if(entity == null) {
      return false;
    }

    return entity instanceof ZombieEntity ||
      entity instanceof AbstractIllagerEntity ||
      entity instanceof CreeperEntity ||
      entity instanceof WitchEntity ||
      entity instanceof CowEntity ||
      entity instanceof AbstractHorseEntity ||
      entity instanceof OcelotEntity ||
      entity instanceof PigEntity ||
      entity instanceof PolarBearEntity ||
      entity instanceof SheepEntity ||
      entity instanceof WolfEntity ||
      entity instanceof PandaEntity ||
      entity instanceof CatEntity ||
      entity instanceof FoxEntity ||
      entity instanceof RavagerEntity ||
      entity instanceof VillagerEntity;
  }

  public static class Serializer extends ILootCondition.AbstractSerializer<EntityBoneDropsCondition> {
    public Serializer() {
      super(Gradient.loc("entity_bone_drops"), EntityBoneDropsCondition.class);
    }

    @Override
    public void serialize(final JsonObject json, final EntityBoneDropsCondition value, final JsonSerializationContext context) {

    }

    @Override
    public EntityBoneDropsCondition deserialize(final JsonObject json, final JsonDeserializationContext context) {
      return new EntityBoneDropsCondition();
    }
  }
}
