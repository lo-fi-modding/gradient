package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientFluids;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.GradientFluidTank;
import lofimodding.gradient.fluids.IGradientFluidHandler;
import lofimodding.gradient.fluids.MetalFluid;
import lofimodding.gradient.recipes.MeltingRecipe;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.utils.MathHelper;
import lofimodding.gradient.utils.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class ClayCrucibleTile extends HeatSinkerTile {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  @CapabilityInject(IGradientFluidHandler.class)
  private static Capability<IGradientFluidHandler> FLUID_HANDLER_CAPABILITY;

  public static final int FLUID_CAPACITY = 8;

  public static final int METAL_SLOTS_COUNT = 1;
  public static final int TOTAL_SLOTS_COUNT = METAL_SLOTS_COUNT;

  public static final int FIRST_METAL_SLOT = 0;

  private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS_COUNT) {
    @Override
    public int getSlotLimit(final int slot) {
      return 1;
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return super.isItemValid(slot, stack) && RecipeUtils.getRecipe(MeltingRecipe.class, r -> r.matches(stack)).isPresent();
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      if(ClayCrucibleTile.this.isMelting(slot)) {
        return ItemStack.EMPTY;
      }

      return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);

      final ItemStack stack = this.getStackInSlot(slot);

      if(stack.isEmpty()) {
        ClayCrucibleTile.this.melting[slot] = null;
      }

      ClayCrucibleTile.this.sync();
    }
  };

  public final GradientFluidTank tank = new GradientFluidTank(FLUID_CAPACITY, stack -> stack.getFluid() instanceof MetalFluid) {
    @Override
    protected void onContentsChanged() {
      super.onContentsChanged();
      ClayCrucibleTile.this.sync();
    }
  };

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inventory);
  private final LazyOptional<IGradientFluidHandler> lazyTank = LazyOptional.of(() -> this.tank);

  private final MeltingMetal[] melting = new MeltingMetal[METAL_SLOTS_COUNT];

  private int lastLight;

  public ClayCrucibleTile() {
    super(GradientTileEntities.CLAY_CRUCIBLE.get());
  }

  public boolean isMelting(final int slot) {
    return this.melting[slot] != null;
  }

  public MeltingMetal getMeltingMetal(final int slot) {
    return this.melting[slot];
  }

  @Nullable
  public GradientFluidStack getMoltenMetal() {
    return this.tank.getFluidStack();
  }

  //TODO
  public int getLightLevel() {
    if(this.getHeat() == 0) {
      return 0;
    }

    return Math.min((int)(this.getHeat() / 800 * 11) + 4, 15);
  }

  public void consumeMetal(final int amount) {
    this.tank.drain(amount, IGradientFluidHandler.FluidAction.EXECUTE);
  }

  @Override
  protected void tickBeforeCooldown(final float tickScale) {
    if(!this.world.isRemote) {
      this.meltMetal();
    }

    this.checkForMoltenMetal();
  }

  @Override
  protected void tickAfterCooldown(final float tickScale) {
    this.updateLight();
  }

  private void meltMetal() {
    boolean update = false;

    for(int slot = 0; slot < METAL_SLOTS_COUNT; slot++) {
      if(!this.isMelting(slot) && !this.getMetalSlot(slot).isEmpty()) {
        final int slot2 = slot;
        final MeltingRecipe meltable = RecipeUtils.getRecipe(MeltingRecipe.class, r -> r.matches(this.getMetalSlot(slot2)));

        if(this.canMelt(meltable)) {
          this.melting[slot] = new MeltingMetal(meltable, meltable.getFluidOutput());
          update = true;
        }
      }
    }

    if(update) {
      this.sync();
    }
  }

  private void checkForMoltenMetal() {
    for(int slot = 0; slot < METAL_SLOTS_COUNT; slot++) {
      if(this.isMelting(slot)) {
        final MeltingMetal melting = this.getMeltingMetal(slot);

        melting.tick();

        if(!this.world.isRemote) {
          if(melting.isMelted()) {
            final GradientFluidStack fluid = melting.meltable.getFluidOutput();

            if(this.hasRoom(fluid)) {
              this.setMetalSlot(slot, ItemStack.EMPTY);
              this.tank.fill(fluid.copy(), IGradientFluidHandler.FluidAction.EXECUTE);
            }
          }
        }
      }
    }
  }

  private boolean hasRoom(final GradientFluidStack fluid) {
    return MathHelper.flEq(this.tank.fill(fluid, IGradientFluidHandler.FluidAction.SIMULATE), fluid.getAmount());
  }

  private ItemStack getMetalSlot(final int slot) {
    return this.inventory.getStackInSlot(FIRST_METAL_SLOT + slot);
  }

  private void setMetalSlot(final int slot, final ItemStack stack) {
    this.inventory.setStackInSlot(FIRST_METAL_SLOT + slot, stack);
  }

  private boolean canMelt(final MeltingRecipe meltable) {
    return (this.tank.getFluidStack().isEmpty() || this.tank.getFluidStack().isFluidEqual(meltable.getFluidOutput())) && this.getHeat() >= meltable.getTemperature();
  }

  @Override
  protected float calculateHeatLoss(final BlockState state) {
    return (float)Math.max(0.5d, Math.pow(this.getHeat() / 800, 2));
  }

  @Override
  protected float heatTransferEfficiency() {
    return 0.6f;
  }

  private void updateLight() {
    if(this.lastLight != this.getLightLevel()) {
      this.world.getLightManager().checkBlock(this.pos);
      this.lastLight = this.getLightLevel();
    }
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("inventory", this.inventory.serializeNBT());
    this.tank.write(compound);

    final ListNBT meltings = new ListNBT();

    for(int i = 0; i < METAL_SLOTS_COUNT; i++) {
      if(this.isMelting(i)) {
        final MeltingMetal melting = this.getMeltingMetal(i);

        final CompoundNBT tag = new CompoundNBT();
        tag.putInt("slot", i);
        melting.write(tag);
        meltings.add(tag);
      }
    }

    compound.put("melting", meltings);

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.lastLight = this.getLightLevel();

    Arrays.fill(this.melting, null);

    final CompoundNBT inv = compound.getCompound("inventory");
    inv.remove("Size");
    this.inventory.deserializeNBT(inv);

    this.tank.read(compound);

    final ListNBT meltings = compound.getList("melting", Constants.NBT.TAG_COMPOUND);

    for(int i = 0; i < meltings.size(); i++) {
      final CompoundNBT tag = meltings.getCompound(i);

      final int slot = tag.getInt("slot");

      if(slot < METAL_SLOTS_COUNT) {
        final MeltingRecipe meltable = RecipeUtils.getRecipe(MeltingRecipe.class, r -> r.matches(this.getMetalSlot(slot)));
        this.melting[slot] = MeltingMetal.fromNbt(meltable, meltable.getFluidOutput(), tag);
      }
    }

    super.read(compound);
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    final BlockState oldState = this.world.getBlockState(this.pos);
    super.onDataPacket(net, pkt);
    this.world.notifyBlockUpdate(this.pos, oldState, this.world.getBlockState(this.pos), 2);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == ITEM_HANDLER_CAPABILITY) {
      return this.lazyInv.cast();
    }

    if(capability == FLUID_HANDLER_CAPABILITY) {
      return this.lazyTank.cast();
    }

    return super.getCapability(capability, facing);
  }

  public static final class MeltingMetal {
    public final MeltingRecipe meltable;
    public final Metal metal;
    private final int meltTicksTotal;
    private int meltTicks;

    public static MeltingMetal fromNbt(final MeltingRecipe meltable, final Metal metal, final CompoundNBT tag) {
      final MeltingMetal melting = new MeltingMetal(meltable, metal, tag.getInt("ticksTotal"));
      melting.meltTicks = tag.getInt("ticks");
      return melting;
    }

    public static MeltingMetal fromNbt(final MeltingRecipe meltable, final GradientFluidStack stack, final CompoundNBT tag) {
      return fromNbt(meltable, ((MetalFluid)stack.getFluid()).metal, tag);
    }

    private MeltingMetal(final MeltingRecipe meltable, final Metal metal) {
      this(meltable, metal, meltable.getTicks());
    }

    private MeltingMetal(final MeltingRecipe meltable, final GradientFluidStack stack) {
      this(meltable, ((MetalFluid)stack.getFluid()).metal);
    }

    private MeltingMetal(final MeltingRecipe meltable, final Metal metal, final int meltTicksTotal) {
      this.meltable = meltable;
      this.metal = metal;
      this.meltTicksTotal = meltTicksTotal;
    }

    public void tick() {
      this.meltTicks++;
    }

    public boolean isMelted() {
      return this.meltTicks >= this.meltTicksTotal;
    }

    public float meltPercent() {
      return Math.min((float)this.meltTicks / this.meltTicksTotal, 1.0f);
    }

    public CompoundNBT write(final CompoundNBT tag) {
      tag.putInt("ticksTotal", this.meltTicksTotal);
      tag.putInt("ticks", this.meltTicks);
      return tag;
    }
  }
}
