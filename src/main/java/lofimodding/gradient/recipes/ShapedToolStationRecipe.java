package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Map;

public class ShapedToolStationRecipe implements IToolStationRecipe {
  private final ResourceLocation id;
  private final String group;
  private final int width;
  private final int height;
  private final NonNullList<Ingredient> ingredients;
  private final NonNullList<ToolType> tools;
  private final NonNullList<ItemStack> outputs;

  public ShapedToolStationRecipe(final ResourceLocation id, final String group, final int width, final int height, final NonNullList<Ingredient> ingredients, final NonNullList<ToolType> tools, final NonNullList<ItemStack> outputs) {
    if(ingredients.size() > 5 * 5) {
      throw new JsonSyntaxException("Recipe has too many ingredients");
    }

    this.id = id;
    this.group = group;
    this.width = width;
    this.height = height;
    this.ingredients = ingredients;
    this.tools = tools;
    this.outputs = outputs;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public boolean canFit(final int width, final int height) {
    return width >= this.width && height >= this.height;
  }

  @Override
  public boolean recipeMatches(final IItemHandler recipe, final int width, final int height) {
    for(int x = 0; x <= width - this.width; ++x) {
      for(int y = 0; y <= height - this.height; ++y) {
        if(this.checkMatch(recipe, width, height, x, y, true)) {
          return true;
        }

        if(this.checkMatch(recipe, width, height, x, y, false)) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean checkMatch(final IItemHandler inv, final int width, final int height, final int xOffset, final int yOffset, final boolean reversed) {
    for(int x = 0; x < width; ++x) {
      for(int y = 0; y < height; ++y) {
        final int k = x - xOffset;
        final int l = y - yOffset;
        Ingredient ingredient = Ingredient.EMPTY;

        if(k >= 0 && l >= 0 && k < this.width && l < this.height) {
          if(reversed) {
            ingredient = this.ingredients.get(this.width - k - 1 + l * this.width);
          } else {
            ingredient = this.ingredients.get(k + l * this.width);
          }
        }

        if(!ingredient.test(inv.getStackInSlot(x + y * width))) {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.ingredients;
  }

  @Override
  public NonNullList<ToolType> getTools() {
    return this.tools;
  }

  @Override
  public NonNullList<ItemStack> getOutputs() {
    return this.outputs;
  }

  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public String getGroup() {
    return this.group;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(GradientItems.TOOL_STATION.get());
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.SHAPED_TOOL_STATION.get();
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedToolStationRecipe> {
    @Override
    public ShapedToolStationRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");

      final Map<String, Ingredient> map = ShapedRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
      final String[] pattern = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
      final int width = pattern[0].length();
      final int height = pattern.length;
      final NonNullList<Ingredient> ingredients = ShapedRecipe.deserializeIngredients(pattern, map, width, height);

      final NonNullList<ToolType> tools = readToolTypes(JSONUtils.getJsonArray(json, "tools"));

      final JsonArray outputsJson = JSONUtils.getJsonArray(json, "outputs");
      final NonNullList<ItemStack> outputs = NonNullList.create();

      for(int i = 0; i < outputsJson.size(); i++) {
        outputs.add(ShapedRecipe.deserializeItem(outputsJson.get(i).getAsJsonObject()));
      }

      if(outputs.isEmpty()) {
        throw new JsonParseException("No outputs for shapeless tool station recipe");
      }

      return new ShapedToolStationRecipe(id, group, width, height, ingredients, tools, outputs);
    }

    private static NonNullList<ToolType> readToolTypes(final JsonArray json) {
      final NonNullList<ToolType> types = NonNullList.create();

      for(int i = 0; i < json.size(); ++i) {
        final String typeStr = json.getAsString();
        final ToolType type;

        try {
          type = ToolType.get(typeStr);
        } catch(final IllegalArgumentException e) {
          throw new JsonSyntaxException("Invalid tool type " + typeStr, e);
        }

        types.add(type);
      }

      return types;
    }

    @Override
    public ShapedToolStationRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);

      final int width = buffer.readVarInt();
      final int height = buffer.readVarInt();
      final NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

      for(int i = 0; i < ingredients.size(); ++i) {
        ingredients.set(i, Ingredient.read(buffer));
      }

      final NonNullList<ToolType> tools = NonNullList.withSize(buffer.readVarInt(), ToolType.PICKAXE);

      for(int i = 0; i < tools.size(); ++i) {
        tools.set(i, ToolType.get(buffer.readString(100)));
      }

      final NonNullList<ItemStack> outputs = NonNullList.withSize(buffer.readVarInt(), ItemStack.EMPTY);

      for(int i = 0; i < outputs.size(); ++i) {
        outputs.set(i, buffer.readItemStack());
      }

      return new ShapedToolStationRecipe(id, group, width, height, ingredients, tools, outputs);
    }

    @Override
    public void write(final PacketBuffer buffer, final ShapedToolStationRecipe recipe) {
      buffer.writeString(recipe.group);

      buffer.writeVarInt(recipe.width);
      buffer.writeVarInt(recipe.height);
      for(final Ingredient ingredient : recipe.ingredients) {
        ingredient.write(buffer);
      }

      buffer.writeVarInt(recipe.tools.size());
      for(final ToolType tool : recipe.tools) {
        buffer.writeString(tool.getName(), 100);
      }

      buffer.writeVarInt(recipe.outputs.size());
      for(final ItemStack output : recipe.outputs) {
        buffer.writeItemStack(output);
      }
    }
  }
}
