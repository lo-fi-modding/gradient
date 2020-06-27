package lofimodding.gradient.utils;

import com.google.gson.JsonObject;
import lofimodding.gradient.Gradient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Predicate;

public final class RecipeUtils {
  private RecipeUtils() {
  }

  public static <I extends IInventory, T extends IRecipe<I>> Optional<T> getRecipe(final IRecipeType<T> type, final Predicate<T> matcher) {
    return Gradient.getRecipeManager().getRecipes(type).values().stream().flatMap(r -> Util.streamOptional(matcher.test((T)r) ? Optional.of((T)r) : Optional.empty())).findFirst();
  }

  public static FluidStack readFluidStackFromJson(final JsonObject json) {
    final Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(JSONUtils.getString(json, "Fluid")));
    final int amount = JSONUtils.getInt(json, "Amount");

    final CompoundNBT tag;
    if(json.has("NBT")) {
      tag = JsonUtils.readNBT(json, "NBT");
    } else {
      tag = null;
    }

    if(fluid == Fluids.EMPTY) {
      return FluidStack.EMPTY;
    }

    return new FluidStack(fluid, amount, tag);
  }

  public static JsonObject writeFluidStackToJson(final JsonObject json, final FluidStack stack) {
    json.addProperty("Fluid", stack.getFluid().getRegistryName().toString());
    json.addProperty("Amount", stack.getAmount());

    if(stack.hasTag()) {
      json.addProperty("NBT", stack.getTag().toString());
    }

    return json;
  }
}
