package lofimodding.gradient.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lofimodding.gradient.Gradient;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class UsedHoeTrigger extends AbstractCriterionTrigger<UsedHoeTrigger.Instance> {
  private static final ResourceLocation ID = Gradient.loc("used_hoe");

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  public Instance deserializeInstance(final JsonObject json, final JsonDeserializationContext context) {
    return new Instance();
  }

  public void trigger(final ServerPlayerEntity player) {
    this.func_227070_a_(player.getAdvancements(), instance -> true);
  }

  public static class Instance extends CriterionInstance {
    public Instance() {
      super(UsedHoeTrigger.ID);
    }
  }
}
