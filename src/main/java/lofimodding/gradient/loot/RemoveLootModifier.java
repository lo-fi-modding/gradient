package lofimodding.gradient.loot;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class RemoveLootModifier extends LootModifier {
  private final Ingredient ingredient;

  protected RemoveLootModifier(final ILootCondition[] conditions, final Ingredient ingredient) {
    super(conditions);
    this.ingredient = ingredient;
  }

  @Nonnull
  @Override
  protected List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
    generatedLoot.removeIf(this.ingredient);
    return generatedLoot;
  }

  public static class Serializer extends GlobalLootModifierSerializer<RemoveLootModifier> {
    @Override
    public RemoveLootModifier read(final ResourceLocation location, final JsonObject object, final ILootCondition[] conditions) {
      final Ingredient ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(object, "ingredient"));
      return new RemoveLootModifier(conditions, ingredient);
    }
  }
}
