package lofimodding.gradient.recipes;

import com.google.gson.JsonObject;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FuelRecipe implements IRecipe<IInventory> {
  public static final IRecipeType<FuelRecipe> TYPE = IRecipeType.register("fuel");

  private final ResourceLocation id;
  private final String group;
  public final int ticks;
  public final float ignitionTemp;
  public final float burnTemp;
  public final float heatPerSec;
  private final Ingredient ingredient;
  private final NonNullList<Ingredient> ingredients;

  public FuelRecipe(final ResourceLocation id, final String group, final int ticks, final float ignitionTemp, final float burnTemp, final float heatPerSec, final Ingredient ingredient) {
    this.id = id;
    this.group = group;
    this.ticks = ticks;
    this.ignitionTemp = ignitionTemp;
    this.burnTemp = burnTemp;
    this.heatPerSec = heatPerSec;
    this.ingredient = ingredient;
    this.ingredients = NonNullList.from(Ingredient.EMPTY, ingredient);
  }

  public boolean matches(final ItemStack stack) {
    return this.ingredient.test(stack);
  }

  @Override
  @Deprecated
  public boolean matches(final IInventory inv, final World world) {
    return false;
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
  public ItemStack getRecipeOutput() {
    return new ItemStack(GradientItems.FIREPIT.get());
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
    return new ItemStack(Items.COAL);
  }

  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.FUEL.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FuelRecipe> {
    @Override
    public FuelRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");
      final int ticks = JSONUtils.getInt(json, "ticks");
      final float ignitionTemp = JSONUtils.getFloat(json, "ignition_temp");
      final float burnTemp = JSONUtils.getFloat(json, "burn_temp");
      final float heatPerSecond = JSONUtils.getFloat(json, "heat_per_sec");
      final Ingredient ingredient = Ingredient.deserialize(json.get("ingredient"));
      return new FuelRecipe(id, group, ticks, ignitionTemp, burnTemp, heatPerSecond, ingredient);
    }

    @Override
    public FuelRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);
      final int ticks = buffer.readVarInt();
      final float ignitionTemp = buffer.readFloat();
      final float burnTemp = buffer.readFloat();
      final float heatPerSecond = buffer.readFloat();
      final Ingredient ingredient = Ingredient.read(buffer);
      return new FuelRecipe(id, group, ticks, ignitionTemp, burnTemp, heatPerSecond, ingredient);
    }

    @Override
    public void write(final PacketBuffer buffer, final FuelRecipe recipe) {
      buffer.writeString(recipe.group);
      buffer.writeVarInt(recipe.ticks);
      buffer.writeFloat(recipe.ignitionTemp);
      buffer.writeFloat(recipe.burnTemp);
      buffer.writeFloat(recipe.heatPerSec);
      recipe.ingredient.write(buffer);
    }
  }
}
