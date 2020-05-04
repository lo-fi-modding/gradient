package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.IGradientFluidHandler;
import lofimodding.progression.Stage;
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
import java.util.Set;

public class MeltingRecipe implements IRecipe<IInventory> {
  public static final IRecipeType<MeltingRecipe> TYPE = IRecipeType.register("melting");

  private static final RecipeItemHelper RECIPE_ITEM_HELPER = new RecipeItemHelper();
  private static final List<ItemStack> INPUT_STACKS = new ArrayList<>();

  private final ResourceLocation id;
  private final String group;
  private final NonNullList<Stage> stages;
  private final int ticks;
  private final float temperature;
  private final ItemStack result;
  private final NonNullList<Ingredient> ingredients;
  private final GradientFluidStack output;
  private final boolean simple;

  public MeltingRecipe(final ResourceLocation id, final String group, final NonNullList<Stage> stages, final int ticks, final float temperature, final ItemStack result, final NonNullList<Ingredient> ingredients, final GradientFluidStack output) {
    this.id = id;
    this.group = group;
    this.stages = stages;
    this.ticks = ticks;
    this.temperature = temperature;
    this.result = result;
    this.ingredients = ingredients;
    this.simple = ingredients.stream().allMatch(Ingredient::isSimple);
    this.output = output;
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

  public float getTemperature() {
    return this.temperature;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.ingredients;
  }

  public GradientFluidStack getFluidOutput() {
    return this.output;
  }

  @Override
  @Deprecated
  public boolean matches(final IInventory inv, final World world) {
    return false;
  }

  public boolean matches(final IItemHandler inv, final Set<Stage> stages, final int firstSlot, final int lastSlot, final IGradientFluidHandler fluidHandler) {
    for(final Stage stage : this.stages) {
      if(!stages.contains(stage)) {
        return false;
      }
    }

    return this.matches(inv, firstSlot, lastSlot, fluidHandler);
  }

  public boolean matches(final IItemHandler inv, final int firstSlot, final int lastSlot, final IGradientFluidHandler fluidHandler) {
    if(fluidHandler.drain(this.output, IGradientFluidHandler.FluidAction.SIMULATE).getAmount() < this.output.getAmount()) {
      return false;
    }

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
  public ItemStack getIcon() {
    return new ItemStack(GradientBlocks.CLAY_CRUCIBLE.get());
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.MELTING.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MeltingRecipe> {
    @Override
    public MeltingRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");

      final NonNullList<Stage> stages = NonNullList.create();
      for(final JsonElement element : JSONUtils.getJsonArray(json, "stages", new JsonArray())) {
        stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(element.getAsString())));
      }

      final int ticks = JSONUtils.getInt(json, "ticks");
      final float temperature = JSONUtils.getFloat(json, "temperature");

      final NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
      if(ingredients.isEmpty()) {
        throw new JsonParseException("No ingredients for melting recipe");
      }

      final GradientFluidStack fluid = GradientFluidStack.read(JSONUtils.getJsonObject(json, "fluid"));
      final ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
      return new MeltingRecipe(id, group, stages, ticks, temperature, result, ingredients, fluid);
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
    public MeltingRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
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

      final GradientFluidStack fluid = GradientFluidStack.read(buffer);
      final ItemStack result = buffer.readItemStack();

      return new MeltingRecipe(id, group, stages, ticks, temperature, result, ingredients, fluid);
    }

    @Override
    public void write(final PacketBuffer buffer, final MeltingRecipe recipe) {
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

      recipe.output.write(buffer);
      buffer.writeItemStack(recipe.result);
    }
  }
}
