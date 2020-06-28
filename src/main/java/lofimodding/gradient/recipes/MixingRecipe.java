package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.progression.Stage;
import net.minecraft.fluid.Fluid;
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
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MixingRecipe implements IGradientRecipe {
  public static final IRecipeType<MixingRecipe> TYPE = IRecipeType.register("mixing");

  private static final RecipeItemHelper RECIPE_ITEM_HELPER = new RecipeItemHelper();
  private static final List<ItemStack> INPUT_STACKS = new ArrayList<>();

  private final ResourceLocation id;
  private final String group;
  private final NonNullList<Stage> stages;
  private final int tier;
  private final int ticks;
  private final ItemStack result;
  private final NonNullList<Ingredient> ingredients;
  private final FluidStack fluid;
  private final boolean simple;

  public MixingRecipe(final ResourceLocation id, final String group, final NonNullList<Stage> stages, final int tier, final int ticks, final ItemStack result, final NonNullList<Ingredient> ingredients, final FluidStack fluid) {
    this.id = id;
    this.group = group;
    this.stages = stages;
    this.tier = tier;
    this.ticks = ticks;
    this.result = result;
    this.ingredients = ingredients;
    this.simple = ingredients.stream().allMatch(Ingredient::isSimple);
    this.fluid = fluid;
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

  @Override
  public int getTier() {
    return this.tier;
  }

  @Override
  public int getTicks() {
    return this.ticks;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.ingredients;
  }

  @Override
  public int getItemInputCount() {
    return this.ingredients.size();
  }

  @Override
  public int getItemOutputCount() {
    return 1;
  }

  @Override
  public int getFluidInputCount() {
    return 1;
  }

  @Override
  public FluidStack getFluidInput(final int slot) {
    if(slot == 0) {
      return this.fluid.copy();
    }

    return FluidStack.EMPTY;
  }

  @Override
  public ItemStack getItemOutput(final int slot) {
    if(slot == 0) {
      return this.result.copy();
    }

    return ItemStack.EMPTY;
  }

  @Override
  public boolean matchesStages(final Set<Stage> stages) {
    for(final Stage stage : this.stages) {
      if(!stages.contains(stage)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean matchesItems(final NonNullList<ItemStack> stacks) {
    RECIPE_ITEM_HELPER.clear();
    INPUT_STACKS.clear();

    int ingredientCount = 0;
    for(final ItemStack stack : stacks) {
      if(!stack.isEmpty()) {
        ++ingredientCount;

        if(this.simple) {
          RECIPE_ITEM_HELPER.accountStack(stack);
        } else {
          INPUT_STACKS.add(stack);
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
  public boolean matchesFluids(final IFluidHandler fluidHandler) {
    return fluidHandler.drain(this.fluid, IFluidHandler.FluidAction.SIMULATE).getAmount() >= this.fluid.getAmount();
  }

  @Override
  public boolean canFit(final int width, final int height) {
    return width * height >= this.ingredients.size();
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(GradientBlocks.MIXING_BASIN.get());
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.MIXING.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MixingRecipe> {
    @Override
    public MixingRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");

      final NonNullList<Stage> stages = NonNullList.create();
      for(final JsonElement element : JSONUtils.getJsonArray(json, "stages", new JsonArray())) {
        stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(element.getAsString())));
      }

      final int tier = JSONUtils.getInt(json, "tier", 0);
      final int ticks = JSONUtils.getInt(json, "ticks");

      final NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
      if(ingredients.isEmpty()) {
        throw new JsonParseException("No ingredients for mixing recipe");
      }

      final FluidStack fluid = deserializeFluid(JSONUtils.getJsonObject(json, "fluid"));
      final ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
      return new MixingRecipe(id, group, stages, tier, ticks, result, ingredients, fluid);
    }

    private static FluidStack deserializeFluid(final JsonObject fluidJson) {
      final String id = JSONUtils.getString(fluidJson, "fluid");
      final Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(id));

      if(fluid == null) {
        throw new JsonSyntaxException("Unknown item '" + id + '\'');
      }

      final int amount = JSONUtils.getInt(fluidJson, "amount", 1000);
      return new FluidStack(fluid, amount);
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
    public MixingRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);

      final NonNullList<Stage> stages = NonNullList.create();

      final int stageCount = buffer.readVarInt();
      for(int i = 0; i < stageCount; i++) {
        stages.add(buffer.readRegistryIdSafe(Stage.class));
      }

      final int tier = buffer.readVarInt();
      final int ticks = buffer.readVarInt();

      final int ingredientCount = buffer.readVarInt();
      final NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
      for(int i = 0; i < ingredients.size(); ++i) {
        ingredients.set(i, Ingredient.read(buffer));
      }

      final FluidStack fluid = buffer.readFluidStack();
      final ItemStack result = buffer.readItemStack();

      return new MixingRecipe(id, group, stages, tier, ticks, result, ingredients, fluid);
    }

    @Override
    public void write(final PacketBuffer buffer, final MixingRecipe recipe) {
      buffer.writeString(recipe.group);

      buffer.writeVarInt(recipe.stages.size());
      for(final Stage stage : recipe.stages) {
        buffer.writeRegistryId(stage);
      }

      buffer.writeVarInt(recipe.tier);
      buffer.writeVarInt(recipe.ticks);

      buffer.writeVarInt(recipe.ingredients.size());
      for(final Ingredient ingredient : recipe.ingredients) {
        ingredient.write(buffer);
      }

      buffer.writeFluidStack(recipe.fluid);
      buffer.writeItemStack(recipe.result);
    }
  }
}
