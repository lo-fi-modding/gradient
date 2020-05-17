package lofimodding.gradient.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lofimodding.gradient.Gradient;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRightClickedTrigger extends AbstractCriterionTrigger<BlockRightClickedTrigger.Instance> {
  private static final ResourceLocation ID = Gradient.loc("block_right_clicked");

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  public Instance deserializeInstance(final JsonObject json, final JsonDeserializationContext context) {
    final Block clickedBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getString(json, "clicked_block")));
    final Ingredient heldItem = Ingredient.deserialize(JSONUtils.getJsonObject(json, "held_item"));
    return new Instance(ID, clickedBlock, heldItem);
  }

  public void trigger(final ServerPlayerEntity player, final Block clickedBlock, final ItemStack heldItem) {
    this.func_227070_a_(player.getAdvancements(), instance -> instance.test(clickedBlock, heldItem));
  }

  public static class Instance extends CriterionInstance {
    public final Block clickedBlock;
    public final Ingredient heldItem;

    public Instance(final ResourceLocation criterion, final Block clickedBlock, final Ingredient heldItem) {
      super(criterion);
      this.clickedBlock = clickedBlock;
      this.heldItem = heldItem;
    }

    public static Instance of(final Block block, final Ingredient held) {
      return new Instance(ID, block, held);
    }

    public boolean test(final Block clickedBlock, final ItemStack heldItem) {
      return clickedBlock == this.clickedBlock && this.heldItem.test(heldItem);
    }

    @Override
    public JsonElement serialize() {
      final JsonObject json = new JsonObject();
      json.addProperty("clicked_block", this.clickedBlock.getRegistryName().toString());
      json.add("held_item", this.heldItem.serialize());
      return json;
    }
  }
}
