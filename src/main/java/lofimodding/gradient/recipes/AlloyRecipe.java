package lofimodding.gradient.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.fluids.GradientFluid;
import lofimodding.gradient.fluids.GradientFluidStack;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AlloyRecipe implements IRecipe<IInventory> {
  public static final IRecipeType<AlloyRecipe> TYPE = IRecipeType.register("alloy");

  private final ResourceLocation id;
  private final String group;
  private final GradientFluidStack output;
  private final NonNullList<GradientFluidStack> inputs;
  private final Map<GradientFluid, GradientFluidStack> inputMap;

  public AlloyRecipe(final ResourceLocation id, final String group, final GradientFluidStack output, final NonNullList<GradientFluidStack> inputs) {
    this.id = id;
    this.group = group;
    this.output = output;
    this.inputs = inputs;
    this.inputMap = this.unifyFluids(inputs);
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
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.create();
  }

  public Collection<GradientFluidStack> getFluidInputs() {
    return this.inputMap.values();
  }

  public GradientFluidStack getFluidOutput() {
    return this.output;
  }

  @Override
  @Deprecated
  public boolean matches(final IInventory inv, final World world) {
    return false;
  }

  public boolean matches(final NonNullList<GradientFluidStack> inputs) {
    final Map<GradientFluid, GradientFluidStack> inputMap = this.unifyFluids(inputs);

    // Make sure we have all required fluids
    for(final GradientFluidStack required : this.inputs) {
      if(!inputMap.containsKey(required.getFluid()) || inputMap.get(required.getFluid()).getAmount() < required.getAmount()) {
        return false;
      }
    }

    // Make sure there are no extra fluids
    for(final GradientFluidStack required : inputs) {
      if(!this.inputMap.containsKey(required.getFluid())) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack getCraftingResult(final IInventory inv) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canFit(final int width, final int height) {
    return false;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(GradientBlocks.CLAY_METAL_MIXER.get());
  }

  private Map<GradientFluid, GradientFluidStack> unifyFluids(final NonNullList<GradientFluidStack> inputs) {
    final Map<GradientFluid, GradientFluidStack> outputs = new HashMap<>();

    for(final GradientFluidStack fluidStack : inputs) {
      outputs.computeIfAbsent(fluidStack.getFluid(), fluid -> new GradientFluidStack(fluid, 0)).mix(fluidStack);
    }

    return outputs;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GradientRecipeSerializers.ALLOY.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return TYPE;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlloyRecipe> {
    @Override
    public AlloyRecipe read(final ResourceLocation id, final JsonObject json) {
      final String group = JSONUtils.getString(json, "group", "");
      final GradientFluidStack output = GradientFluidStack.read(JSONUtils.getJsonObject(json, "output"));

      final NonNullList<GradientFluidStack> inputs = NonNullList.create();
      for(final JsonElement element : JSONUtils.getJsonArray(json, "inputs", new JsonArray())) {
        inputs.add(GradientFluidStack.read(element.getAsJsonObject()));
      }

      return new AlloyRecipe(id, group, output, inputs);
    }

    @Override
    public AlloyRecipe read(final ResourceLocation id, final PacketBuffer buffer) {
      final String group = buffer.readString(32767);
      final GradientFluidStack output = GradientFluidStack.read(buffer);

      final NonNullList<GradientFluidStack> inputs = NonNullList.create();

      final int inputCount = buffer.readVarInt();
      for(int i = 0; i < inputCount; i++) {
        inputs.add(GradientFluidStack.read(buffer));
      }


      return new AlloyRecipe(id, group, output, inputs);
    }

    @Override
    public void write(final PacketBuffer buffer, final AlloyRecipe recipe) {
      buffer.writeString(recipe.group);
      recipe.output.write(buffer);

      buffer.writeVarInt(recipe.inputs.size());
      for(final GradientFluidStack input : recipe.inputs) {
        input.write(buffer);
      }
    }
  }
}
