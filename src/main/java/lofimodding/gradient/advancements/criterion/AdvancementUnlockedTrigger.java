package lofimodding.gradient.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lofimodding.gradient.Gradient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class AdvancementUnlockedTrigger extends AbstractCriterionTrigger<AdvancementUnlockedTrigger.Instance> {
  private static final ResourceLocation ID = Gradient.loc("advancement_unlocked");

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  public Instance deserializeInstance(final JsonObject json, final JsonDeserializationContext context) {
    final String id = JSONUtils.getString(json, "id");
    return new Instance(ID, new ResourceLocation(id));
  }

  public void trigger(final ServerPlayerEntity player) {
    this.func_227070_a_(player.getAdvancements(), instance -> instance.test(player));
  }

  public static class Instance extends CriterionInstance {
    public final ResourceLocation advancement;

    public Instance(final ResourceLocation criterion, final ResourceLocation advancement) {
      super(criterion);
      this.advancement = advancement;
    }

    public static Instance forAdvancement(final Advancement advancement) {
      return new Instance(ID, advancement.getId());
    }

    public boolean test(final ServerPlayerEntity player) {
      return player.getAdvancements().getProgress(ServerLifecycleHooks.getCurrentServer().getAdvancementManager().getAdvancement(this.advancement)).isDone();
    }

    @Override
    public JsonElement serialize() {
      final JsonObject json = new JsonObject();
      json.addProperty("id", this.advancement.toString());
      return json;
    }
  }
}
