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
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class ShapelessToolStationRecipe implements IToolStationRecipe {
  private static final RecipeItemHelper RECIPE_ITEM_HELPER = new RecipeItemHelper();
  private static final List<ItemStack> INPUT_STACKS = new ArrayList<>();

  private final ResourceLocation id;
  private final String group;
  private final int width;
  private final NonNullList<Ingredient> ingredients;
  private final NonNullList<ToolType> tools;
  private final NonNullList<ItemStack> outputs;
  private final boolean simple;

  public ShapelessToolStationRecipe(final ResourceLocation id, final String group, final NonNullList<Ingredient> ingredients, final NonNullList<ToolType> tools, final NonNullList<ItemStack> outputs) {
    if(ingredients.size() > 5 * 5) {
      throw new JsonSyntaxException("Recipe has too many ingredients");
    }

    if(ingredients.size() > 4 * 4) {
      this.width = 5;
    } else if(ingredients.size() > 3 * 3) {
      this.width = 4;
    } else {
      this.width = 3;
    }

    this.id = id;
    this.group = group;
    this.ingredients = ingredients;
    this.tools = tools;
    this.outputs = outputs;
    this.simple = ingredients.stream().allMatch(Ingredient::isSimple);
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public boolean recipeMatches(final IItemHandler recipe, final int width, final int height) {
    return this.handlerContainsIngredients(recipe, this.ingredients);
  }

  private boolean handlerContainsIngredients(final IItemHandler tools, final NonNullList<Ingredient> ingredients) {
    RECIPE_ITEM_HELPER.clear();
    INPUT_STACKS.clear();

    int ingredientCount = 0;
    for(int slot = 0; slot < tools.getSlots(); ++slot) {
      final ItemStack stack = tools.getStackInSlot(slot);

      if(!stack.isEmpty()) {
        ++ingredientCount;

        if(this.simple) {
          RECIPE_ITEM_HELPER.accountStack(stack);
        } else {
          INPUT_STACKS.add(stack);
        }
      }
    }

    if(ingredientCount != ingredients.size()) {
      return false;
    }

    if(this.simple) {
      return RECIPE_ITEM_HELPER.canCraft(this, null);
    }

    return RecipeMatcher.findMatches(INPUT_STACKS, ingredients) != null;
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
    return GradientRecipeSerializers.SHAPELESS_TOOL_STATION.get();
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessToolStationRecipe> {
    @Override
    public ShapelessToolStationRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");

      final NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
      if(ingredients.isEmpty()) {
        throw new JsonParseException("No ingredients for shapeless tool station recipe");
      }

      final NonNullList<ToolType> tools = readToolTypes(JSONUtils.getJsonArray(json, "tools"));

      final JsonArray outputsJson = JSONUtils.getJsonArray(json, "outputs");
      final NonNullList<ItemStack> outputs = NonNullList.create();

      for(int i = 0; i < outputsJson.size(); i++) {
        outputs.add(ShapedRecipe.deserializeItem(outputsJson.get(i).getAsJsonObject()));
      }

      if(outputs.isEmpty()) {
        throw new JsonParseException("No outputs for shapeless tool station recipe");
      }

      return new ShapelessToolStationRecipe(id, group, ingredients, tools, outputs);
    }

    private static NonNullList<Ingredient> readIngredients(final JsonArray json) {
      final NonNullList<Ingredient> ingredients = NonNullList.create();

      for(int i = 0; i < json.size(); ++i) {
        final Ingredient ingredient = Ingredient.deserialize(json.get(i));
        if(!ingredient.hasNoMatchingItems()) {
          ingredients.add(ingredient);
        }
      }

      return ingredients;
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
    public ShapelessToolStationRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);

      final NonNullList<Ingredient> ingredients = NonNullList.withSize(buffer.readVarInt(), Ingredient.EMPTY);

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

      return new ShapelessToolStationRecipe(id, group, ingredients, tools, outputs);
    }

    @Override
    public void write(final PacketBuffer buffer, final ShapelessToolStationRecipe recipe) {
      buffer.writeString(recipe.group);

      buffer.writeVarInt(recipe.ingredients.size());
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
