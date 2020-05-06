package lofimodding.gradient.fluids;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lofimodding.gradient.utils.MathHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;

public class GradientFluidStack {
  public static final GradientFluidStack EMPTY = new GradientFluidStack(GradientFluids.EMPTY.get(), 0.0f, Float.NaN) {
    @Override
    public void setAmount(final float amount) {
      throw new IllegalStateException("Can't modify EMPTY");
    }

    @Override
    public void setTemperature(final float temperature) {
      throw new IllegalStateException("Can't modify EMPTY");
    }
  };

  private boolean isEmpty;
  private final IRegistryDelegate<GradientFluid> fluidDelegate;
  private float amount;
  private float temperature;

  public static GradientFluidStack read(final CompoundNBT nbt) {
    final String id = nbt.getString("fluid");
    final GradientFluid fluid = GradientFluid.REGISTRY.get().getValue(new ResourceLocation(id));
    final float amount = nbt.getFloat("amount");
    final float temperature = nbt.getFloat("temperature");
    return new GradientFluidStack(fluid != null ? fluid : GradientFluids.EMPTY.get(), amount, temperature);
  }

  public static GradientFluidStack read(final JsonObject json) {
    final String id = JSONUtils.getString(json, "fluid");
    final GradientFluid fluid = GradientFluid.REGISTRY.get().getValue(new ResourceLocation(id));

    if(fluid == null) {
      throw new JsonSyntaxException("Unknown fluid '" + id + '\'');
    }

    final float amount = JSONUtils.getFloat(json, "amount", 1000);
    final float temperature = JSONUtils.getFloat(json, "temperature", Float.NaN);
    return new GradientFluidStack(fluid, amount, temperature);
  }

  public static GradientFluidStack read(final PacketBuffer buffer) {
    final String id = buffer.readString(100);
    final GradientFluid fluid = GradientFluid.REGISTRY.get().getValue(new ResourceLocation(id));
    final float amount = buffer.readFloat();
    final float temperature = buffer.readFloat();
    return new GradientFluidStack(fluid != null ? fluid : GradientFluids.EMPTY.get(), amount, temperature);
  }

  public GradientFluidStack(final GradientFluid fluid, final float amount, final float temperature) {
    this.fluidDelegate = fluid.delegate;
    this.amount = amount;
    this.temperature = temperature;
    this.updateEmpty();
  }

  public GradientFluidStack(final GradientFluid fluid, final float amount) {
    this(fluid, amount, Float.NaN);
  }

  public ITextComponent getName() {
    return this.getFluid().getName(this);
  }

  public ResourceLocation getStill() {
    return this.getFluid().getStillTexture(this);
  }

  public int getColour() {
    return this.getFluid().getColour(this);
  }

  public GradientFluid getFluid() {
    return this.fluidDelegate.get();
  }

  public final GradientFluid getRawFluid() {
    return this.fluidDelegate.get();
  }

  public float getAmount() {
    return this.isEmpty ? 0.0f : this.amount;
  }

  public void setAmount(final float amount) {
    this.amount = amount;
    this.updateEmpty();
  }

  /**
   * Mix two fluids together, averaging their temperatures
   *
   * @param other The fluid to mix into this one
   */
  public void mix(final GradientFluidStack other) {
    if(!this.isFluidEqual(other)) {
      return;
    }

    this.grow(other.amount, other.temperature);
  }

  /**
   * Add more fluid, averaging their temperatures
   *
   * @param amount The amount of fluid being added
   * @param temperature The temperature of the fluid being added
   */
  public void grow(final float amount, final float temperature) {
    final float newAmount = this.getAmount() + amount;
    final float newTemperature;

    if(Float.isNaN(this.temperature)) {
      newTemperature = temperature;
    } else if(Float.isNaN(temperature)) {
      newTemperature = this.temperature;
    } else {
      newTemperature = (this.getTemperature() * this.getAmount() + temperature * amount) / newAmount;
    }

    this.setAmount(newAmount);
    this.setTemperature(newTemperature);
  }

  public void grow(final float amount) {
    this.setAmount(this.amount + amount);
  }

  public void shrink(final float amount) {
    this.setAmount(this.amount - amount);
  }

  public float getTemperature() {
    return this.isEmpty ? Float.NaN : this.temperature;
  }

  public void setTemperature(final float temperature) {
    this.temperature = temperature;
    this.updateEmpty();
  }

  public void heat(final float amount) {
    this.setTemperature(this.amount + amount);
  }

  public void cool(final float amount) {
    this.setTemperature(this.amount - amount);
  }

  public boolean isEmpty() {
    return this.isEmpty;
  }

  protected void updateEmpty() {
    this.isEmpty = this.getRawFluid() == GradientFluids.EMPTY.get() || this.amount <= 0;
  }

  public GradientFluidStack copy() {
    return new GradientFluidStack(this.getFluid(), this.amount, this.temperature);
  }

  public boolean isFluidEqual(@Nonnull final GradientFluidStack other) {
    return this.getFluid() == other.getFluid();
  }

  public boolean isFluidStackIdentical(final GradientFluidStack other) {
    return this.isFluidEqual(other) && MathHelper.flEq(this.amount, other.amount);
  }

  public boolean containsFluid(final GradientFluidStack other) {
    return this.isFluidEqual(other) && this.amount >= other.amount;
  }

  @Override
  public final int hashCode() {
    int code = 1;
    code = 31 * code + this.getFluid().hashCode();
    code = 31 * code + Float.hashCode(this.amount);
    return code;
  }

  @Override
  public final boolean equals(final Object o) {
    if(!(o instanceof GradientFluidStack)) {
      return false;
    }

    return this.isFluidEqual((GradientFluidStack)o);
  }

  public CompoundNBT write(final CompoundNBT nbt) {
    nbt.putString("fluid", this.getFluid().getRegistryName().toString());
    nbt.putFloat("amount", this.amount);
    nbt.putFloat("temperature", this.temperature);
    return nbt;
  }

  public void write(final PacketBuffer buffer) {
    buffer.writeString(this.getFluid().getRegistryName().toString(), 100);
    buffer.writeFloat(this.amount);
    buffer.writeFloat(this.temperature);
  }

  public JsonObject write(final JsonObject json) {
    json.addProperty("fluid", this.getFluid().getRegistryName().toString());
    json.addProperty("amount", this.amount);
    json.addProperty("temperature", this.temperature);
    return json;
  }
}
