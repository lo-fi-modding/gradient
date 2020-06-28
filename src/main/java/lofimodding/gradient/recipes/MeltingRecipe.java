package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Set;

public class MeltingRecipe implements IRecipe<IInventory> {
  public static final IRecipeType<MeltingRecipe> TYPE = IRecipeType.register("melting");

  private final ResourceLocation id;
  private final String group;
  private final NonNullList<Stage> stages;
  private final int ticks;
  private final float temperature;
  private final Ingredient ingredient;
  private final NonNullList<Ingredient> ingredients;
  private final FluidStack output;

  public MeltingRecipe(final ResourceLocation id, final String group, final NonNullList<Stage> stages, final int ticks, final float temperature, final Ingredient ingredient, final FluidStack output) {
    this.id = id;
    this.group = group;
    this.stages = stages;
    this.ticks = ticks;
    this.temperature = temperature;
    this.ingredient = ingredient;
    this.ingredients = NonNullList.withSize(1, ingredient);
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
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return this.ingredients;
  }

  public FluidStack getFluidOutput() {
    return this.output;
  }

  @Override
  @Deprecated
  public boolean matches(final IInventory inv, final World world) {
    return false;
  }

  public boolean matches(final ItemStack input, final Set<Stage> stages) {
    for(final Stage stage : this.stages) {
      if(!stages.contains(stage)) {
        return false;
      }
    }

    return this.matches(input);
  }

  public boolean matches(final ItemStack input) {
    return this.ingredient.test(input);
  }

  @Override
  public ItemStack getCraftingResult(final IInventory inv) {
    return ItemStack.EMPTY;
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

      final Ingredient ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
      final FluidStack fluid = RecipeUtils.readFluidStackFromJson(JSONUtils.getJsonObject(json, "fluid"));
      return new MeltingRecipe(id, group, stages, ticks, temperature, ingredient, fluid);
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
      final Ingredient ingredient = Ingredient.read(buffer);
      final FluidStack fluid = FluidStack.readFromPacket(buffer);

      return new MeltingRecipe(id, group, stages, ticks, temperature, ingredient, fluid);
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
      recipe.ingredient.write(buffer);
      recipe.output.writeToPacket(buffer);
    }
  }
}
