package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.progression.Stage;
import lofimodding.progression.StageUtils;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class GrindingRecipe implements IRecipe<IInventory> {
  public static final IRecipeType<GrindingRecipe> TYPE = IRecipeType.register("grinding");

  private final ResourceLocation id;
  private final String group;
  private final NonNullList<Stage> stages;
  private final int ticks;
  private final ItemStack result;
  private final NonNullList<Ingredient> ingredients;
  private final boolean simple;

  public GrindingRecipe(final ResourceLocation id, final String group, final NonNullList<Stage> stages, final int ticks, final ItemStack result, final NonNullList<Ingredient> ingredients) {
    this.id = id;
    this.group = group;
    this.stages = stages;
    this.ticks = ticks;
    this.result = result;
    this.ingredients = ingredients;
    this.simple = ingredients.stream().allMatch(Ingredient::isSimple);
  }

  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public String getGroup() {
    return this.group;
  }

  public NonNullList<Stage> getStages() {
    return this.stages;
  }

  public int getTicks() {
    return this.ticks;
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
  public boolean matches(final IInventory inv, final World worldIn) {

  }

  public boolean matches(final IItemHandler inv) {
    if(!StageUtils.hasStage(inv, this.stages.toArray(new Stage[0]))) {
      return false;
    }

    final RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
    final List<ItemStack> inputs = new ArrayList<>();
    int i = 0;

    for(int j = 0; j < inv.getSizeInventory(); ++j) {
      final ItemStack itemstack = inv.getStackInSlot(j);
      if(!itemstack.isEmpty()) {
        ++i;
        if(this.simple) {
          recipeitemhelper.func_221264_a(itemstack, 1);
        } else {
          inputs.add(itemstack);
        }
      }
    }

    return i == this.ingredients.size() && (this.simple ? recipeitemhelper.canCraft(this, null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.ingredients) != null);
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
  public ItemStack getIcon() {
    return new ItemStack(GradientBlocks.GRINDSTONE.get());
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.GRINDING.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrindingRecipe> {
    @Override
    public GrindingRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");

      final NonNullList<Stage> stages = NonNullList.create();
      for(final JsonElement element : JSONUtils.getJsonArray(json, "stages", new JsonArray())) {
        stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(element.getAsString())));
      }

      final int ticks = JSONUtils.getInt(json, "ticks");

      final NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
      if(ingredients.isEmpty()) {
        throw new JsonParseException("No ingredients for grinding recipe");
      }

      final ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
      return new GrindingRecipe(id, group, stages, ticks, result, ingredients);
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
    public GrindingRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);

      final NonNullList<Stage> stages = NonNullList.create();

      final int stageCount = buffer.readVarInt();
      for(int i = 0; i < stageCount; i++) {
        stages.add(buffer.readRegistryIdSafe(Stage.class));
      }

      final int ticks = buffer.readVarInt();

      final int ingredientCount = buffer.readVarInt();
      final NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
      for(int i = 0; i < ingredients.size(); ++i) {
        ingredients.set(i, Ingredient.read(buffer));
      }

      final ItemStack result = buffer.readItemStack();
      return new GrindingRecipe(id, group, stages, ticks, result, ingredients);
    }

    @Override
    public void write(final PacketBuffer buffer, final GrindingRecipe recipe) {
      buffer.writeString(recipe.group);

      buffer.writeVarInt(recipe.stages.size());
      for(final Stage stage : recipe.stages) {
        buffer.writeRegistryId(stage);
      }

      buffer.writeVarInt(recipe.ticks);

      buffer.writeVarInt(recipe.ingredients.size());
      for(final Ingredient ingredient : recipe.ingredients) {
        ingredient.write(buffer);
      }

      buffer.writeItemStack(recipe.result);
    }
  }
}
