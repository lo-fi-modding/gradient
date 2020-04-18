// From https://github.com/gigaherz/Survivalist

package lofimodding.gradient.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lofimodding.gradient.Gradient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Objects;

public class BlockTagCondition implements ILootCondition {
  final Tag<Block> blockTag;

  public BlockTagCondition(final Tag<Block> blockTag) {
    this.blockTag = blockTag;
  }

  @Override
  public boolean test(final LootContext lootContext) {
    final BlockState state = lootContext.get(LootParameters.BLOCK_STATE);
    if(state == null) {
      return false;
    }

    return this.blockTag.contains(state.getBlock());
  }

  public static class Serializer extends ILootCondition.AbstractSerializer<BlockTagCondition> {
    public Serializer() {
      super(Gradient.loc("block_tag"), BlockTagCondition.class);
    }

    @Override
    public void serialize(final JsonObject json, final BlockTagCondition value, final JsonSerializationContext context) {
      json.addProperty("tag", value.blockTag.getId().toString());
    }

    @Override
    public BlockTagCondition deserialize(final JsonObject json, final JsonDeserializationContext context) {
      final ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
      return new BlockTagCondition(Objects.requireNonNull(BlockTags.getCollection().get(tagName), "Block tag " + tagName + " not found"));
    }
  }
}
