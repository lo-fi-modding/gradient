package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.progression.Stage;
import lofimodding.progression.recipes.IStagedRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class CookingRecipe implements IRecipe<IInventory>, IStagedRecipe {
  public static final IRecipeType<CookingRecipe> TYPE = IRecipeType.register("cooking");

  private static final RecipeItemHelper RECIPE_ITEM_HELPER = new RecipeItemHelper();
  private static final List<ItemStack> INPUT_STACKS = new ArrayList<>();

  private final ResourceLocation id;
  private final String group;
  private final NonNullList<Stage> stages;
  public final int ticks;
  public final float temperature;
  private final ItemStack result;
  private final NonNullList<Ingredient> ingredients;
  private final boolean simple;

  public CookingRecipe(final ResourceLocation id, final String group, final NonNullList<Stage> stages, final int ticks, final float temperature, final ItemStack result, final NonNullList<Ingredient> ingredients) {
    this.id = id;
    this.group = group;
    this.stages = stages;
    this.ticks = ticks;
    this.temperature = temperature;
    this.result = result;
    this.ingredients = ingredients;
    this.simple = ingredients.stream().allMatch(Ingredient::isSimple);
  }

  @Override
  public boolean matches(final IInventory inv, final World world) {
    return false;
  }

  public boolean matches(final IItemHandler inv, final NonNullList<Stage> stages, final int firstSlot, final int lastSlot) {
    for(final Stage stage : this.stages) {
      if(!stages.contains(stage)) {
        return false;
      }
    }

    return this.matches(inv, firstSlot, lastSlot);
  }

  public boolean matches(final IItemHandler inv, final int firstSlot, final int lastSlot) {
    RECIPE_ITEM_HELPER.clear();
    INPUT_STACKS.clear();

    int ingredientCount = 0;
    for(int slot = firstSlot; slot <= lastSlot; ++slot) {
      final ItemStack itemstack = inv.getStackInSlot(slot);

      if(!itemstack.isEmpty()) {
        ++ingredientCount;

        if(this.simple) {
          RECIPE_ITEM_HELPER.accountStack(itemstack);
        } else {
          INPUT_STACKS.add(itemstack);
        }
      }
    }

    if(ingredientCount != this.ingredients.size()) {
      return false;
    }

    if(this.simple) {
      return RECIPE_ITEM_HELPER.canCraft(this, null);
    }

    return RecipeMatcher.findMatches(INPUT_STACKS, this.ingredients) != null;
  }

  @Override
  public ItemStack getCraftingResult(final IInventory inv) {
    return this.result.copy();
  }

  @Override
  public boolean canFit(final int width, final int height) {
    return width * height >= this.ingredients.size();
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.ingredients;
  }

  @Override
  public String getGroup() {
    return this.group;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(GradientItems.FIREPIT.get());
  }

  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.COOKING.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  @Override
  public NonNullList<Stage> getStages() {
    return this.stages;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CookingRecipe> {
    @Override
    public CookingRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");

      final NonNullList<Stage> stages = NonNullList.create();
      for(final JsonElement element : JSONUtils.getJsonArray(json, "stages", new JsonArray())) {
        stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(element.getAsString())));
      }

      final int ticks = JSONUtils.getInt(json, "ticks");
      final float temperature = JSONUtils.getFloat(json, "temperature");

      final NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
      if(ingredients.isEmpty()) {
        throw new JsonParseException("No ingredients for cooking recipe");
      }

      final ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
      return new CookingRecipe(id, group, stages, ticks, temperature, result, ingredients);
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

    @Override
    public CookingRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);

      final NonNullList<Stage> stages = NonNullList.create();

      final int stageCount = buffer.readVarInt();
      for(int i = 0; i < stageCount; i++) {
        stages.add(buffer.readRegistryIdSafe(Stage.class));
      }

      final int ticks = buffer.readVarInt();
      final float temperature = buffer.readFloat();

      final int ingredientCount = buffer.readVarInt();
      final NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
      for(int i = 0; i < ingredients.size(); ++i) {
        ingredients.set(i, Ingredient.read(buffer));
      }

      final ItemStack result = buffer.readItemStack();
      return new CookingRecipe(id, group, stages, ticks, temperature, result, ingredients);
    }

    @Override
    public void write(final PacketBuffer buffer, final CookingRecipe recipe) {
      buffer.writeString(recipe.group);

      buffer.writeVarInt(recipe.stages.size());
      for(final Stage stage : recipe.stages) {
        buffer.writeRegistryId(stage);
      }

      buffer.writeVarInt(recipe.ticks);
      buffer.writeFloat(recipe.temperature);

      buffer.writeVarInt(recipe.ingredients.size());
      for(final Ingredient ingredient : recipe.ingredients) {
        ingredient.write(buffer);
      }

      buffer.writeItemStack(recipe.result);
    }
  }
}
