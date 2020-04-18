// From https://github.com/gigaherz/Survivalist

package lofimodding.gradient.data;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class AppendLootTable extends LootModifier {
  private final ResourceLocation lootTable;

  public AppendLootTable(final ILootCondition[] lootConditions, final ResourceLocation lootTable) {
    super(lootConditions);
    this.lootTable = lootTable;
  }

  boolean reentryPrevention = false;

  @Nonnull
  @Override
  public List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
    if(this.reentryPrevention) {
      return generatedLoot;
    }

    this.reentryPrevention = true;
    final LootTable lootTable = context.func_227502_a_(this.lootTable);
    final List<ItemStack> extras = lootTable.generate(context);
    generatedLoot.addAll(extras);
    this.reentryPrevention = false;

    return generatedLoot;
  }

  public static class Serializer extends GlobalLootModifierSerializer<AppendLootTable> {
    @Override
    public AppendLootTable read(final ResourceLocation location, final JsonObject object, final ILootCondition[] conditions) {
      final ResourceLocation lootTable = new ResourceLocation(JSONUtils.getString(object, "add_loot"));
      return new AppendLootTable(conditions, lootTable);
    }
  }
}
