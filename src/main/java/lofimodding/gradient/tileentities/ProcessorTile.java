package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.tileentities.pieces.IInteractor;
import lofimodding.gradient.tileentities.pieces.NoopInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import lofimodding.gradient.tileentities.pieces.PumpProcessor;
import lofimodding.gradient.tileentities.pieces.RecipeProcessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class ProcessorTile<Energy extends IEnergySource<Energy>> extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  @CapabilityInject(IFluidHandler.class)
  private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY;

  private final Energy energy;
  private final List<ProcessorInteractor> processors;
  private final IItemHandler inv;
  private final IFluidHandler fluids;
  private final LazyOptional<IItemHandler> lazyInv;
  private final LazyOptional<IFluidHandler> lazyFluids;

  private boolean slotsLocked;

  protected ProcessorTile(final TileEntityType<? extends ProcessorTile<Energy>> type, final Energy energy, final Consumer<Builder> builder) {
    super(type);
    this.energy = energy;

    final Builder b = new Builder(this::onInventoryChanged, this::onFluidsChanged);
    builder.accept(b);

    this.processors = Collections.unmodifiableList(b.processors);

    this.inv = new CombinedInvWrapper(this.processors.stream().map(pi -> pi.processor.getInv()).toArray(IItemHandlerModifiable[]::new));

    final List<FluidTank> tanks = new ArrayList<>();
    this.processors.stream().map(pi -> pi.processor.getFluids()).forEach(tanks::addAll);
    this.fluids = new MultiTankWrapper(tanks);

    this.lazyInv = LazyOptional.of(() -> this.inv);
    this.lazyFluids = LazyOptional.of(() -> this.fluids);
  }

  protected Energy getEnergy() {
    return this.energy;
  }

  @Override
  public void onLoad() {
    if(this.world.isRemote) {
      return;
    }

    this.energy.onAddToWorld(this);
  }

  @Override
  public void remove() {
    if(this.world.isRemote) {
      return;
    }

    this.energy.onRemoveFromWorld(this);
  }

  @Override
  public void tick() {
    if(!this.hasWork()) {
      return;
    }

    if(this.energy.consumeEnergy()) {
      for(final ProcessorInteractor pi : this.processors) {
        if(pi.processor.hasWork() && pi.processor.tick(this.world.isRemote)) {
          this.markDirty();

          if(!this.world.isRemote) {
            this.onProcessorTick(pi.processor);
          } else {
            this.onAnimationTick(pi.processor);
          }
        }
      }
    }
  }

  protected void onInventoryChanged(final Processor.ProcessorItemHandler inv, final ItemStack stack) {

  }

  protected void onFluidsChanged(final Processor.ProcessorFluidTank tank, final FluidStack stack) {

  }

  public boolean areSlotsLocked() {
    return this.slotsLocked;
  }

  public void lockSlotsToCurrentContents() {
    for(final ProcessorInteractor pi : this.processors) {
      pi.processor.lockSlotsToCurrentContents();
    }

    this.slotsLocked = true;
  }

  public void unlockSlots() {
    for(final ProcessorInteractor pi : this.processors) {
      pi.processor.unlockSlots();
    }

    this.slotsLocked = false;
  }

  public ActionResultType onInteract(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    for(final ProcessorInteractor pi : this.processors) {
      final ActionResultType result = pi.interactor.onInteract(pi.processor, state, world, pos, player, hand, hit);

      if(result != ActionResultType.PASS) {
        return result;
      }
    }

    if(!this.hasWork()) {
      return ActionResultType.PASS;
    }

    return this.energy.onInteract(state, world, pos, player, hand, hit);
  }

  public boolean hasWork() {
    for(final ProcessorInteractor pi : this.processors) {
      if(pi.processor.hasWork()) {
        return true;
      }
    }

    return false;
  }

  public int getProcessors() {
    return this.processors.size();
  }

  public Stream<ItemStack> getAllItemInputs() {
    return this.processors.stream()
      .map(pi -> pi.processor)
      .flatMap(Processor::getItemInputs);
  }

  public int getItemInputSlots() {
    return this.getItemInputSlots(0);
  }

  public int getItemInputSlots(final int processor) {
    return this.processors.get(processor).processor.inputSlots();
  }

  public boolean hasInput(final int slot) {
    return this.hasInput(0, slot);
  }

  public boolean hasInput(final int processor, final int slot) {
    return !this.getInput(processor, slot).isEmpty();
  }

  public ItemStack getInput(final int slot) {
    return this.getInput(0, slot);
  }

  public ItemStack getInput(final int processor, final int slot) {
    return this.processors.get(processor).processor.getInput(slot);
  }

  public boolean hasOutput(final int slot) {
    return this.hasOutput(0, slot);
  }

  public boolean hasOutput(final int processor, final int slot) {
    return !this.getOutput(processor, slot).isEmpty();
  }

  public ItemStack getOutput(final int slot) {
    return this.getOutput(0, slot);
  }

  public ItemStack getOutput(final int processor, final int slot) {
    return this.processors.get(processor).processor.getOutput(slot);
  }

  public int getFluidInputSlots() {
    return this.getFluidInputSlots(0);
  }

  public int getFluidInputSlots(final int processor) {
    return this.processors.get(processor).processor.fluidInputSlots();
  }

  public Stream<FluidStack> getAllFluidInputs() {
    return this.processors.stream()
      .map(pi -> pi.processor)
      .flatMap(Processor::getFluidInputs);
  }

  public boolean hasFluidInput(final int slot) {
    return this.hasFluidInput(0, slot);
  }

  public boolean hasFluidInput(final int processor, final int slot) {
    return !this.getFluidInput(processor, slot).isEmpty();
  }

  public FluidStack getFluidInput(final int slot) {
    return this.getFluidInput(0, slot);
  }

  public FluidStack getFluidInput(final int processor, final int slot) {
    return this.processors.get(processor).processor.getFluidInput(slot);
  }

  public boolean hasFluidOutput(final int slot) {
    return this.hasFluidOutput(0, slot);
  }

  public boolean hasFluidOutput(final int processor, final int slot) {
    return !this.getFluidOutput(processor, slot).isEmpty();
  }

  public FluidStack getFluidOutput(final int slot) {
    return this.getFluidOutput(0, slot);
  }

  public FluidStack getFluidOutput(final int processor, final int slot) {
    return this.processors.get(processor).processor.getFluidOutput(slot);
  }

  protected abstract void onProcessorTick(final Processor processor);
  protected abstract void onAnimationTick(final Processor processor);
  protected abstract void resetAnimation(final Processor processor);

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("Energy", this.energy.write(new CompoundNBT()));

    final ListNBT processorsNbt = new ListNBT();
    for(final ProcessorInteractor processor : this.processors) {
      processorsNbt.add(processor.processor.write(new CompoundNBT()));
    }

    compound.put("Processors", processorsNbt);
    compound.putBoolean("InputsLocked", this.slotsLocked);

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.energy.read(compound.getCompound("Energy"));

    final ListNBT processorsNbt = compound.getList("Processors", Constants.NBT.TAG_COMPOUND);

    for(int i = 0; i < Math.min(processorsNbt.size(), this.processors.size()); i++) {
      this.processors.get(i).processor.read(processorsNbt.getCompound(i));
    }

    this.slotsLocked = compound.getBoolean("InputsLocked");

    super.read(compound);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
    final LazyOptional<T> energyCap = this.energy.getCapability(cap, side);

    if(energyCap.isPresent()) {
      return energyCap;
    }

    if(cap == ITEM_HANDLER_CAPABILITY && this.inv.getSlots() > 0) {
      return this.lazyInv.cast();
    }

    if(cap == FLUID_HANDLER_CAPABILITY && this.fluids.getTanks() > 0) {
      return this.lazyFluids.cast();
    }

    return super.getCapability(cap, side);
  }

  protected void syncToSurrounding() {
    if(!this.world.isRemote) {
      final BlockState state = this.world.getBlockState(this.getPos());
      this.world.notifyBlockUpdate(this.getPos(), state, state, 3);
      this.markDirty();
    }
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket packet) {
    this.read(packet.getNbtCompound());

    for(final ProcessorInteractor ri : this.processors) {
      if(ri.processor.hasWork()) {
        this.onAnimationTick(ri.processor);
      } else {
        this.resetAnimation(ri.processor);
      }
    }
  }

  private static final class ProcessorInteractor {
    private final Processor processor;
    private final IInteractor interactor;

    private ProcessorInteractor(final Processor processor, final IInteractor interactor) {
      this.processor = processor;
      this.interactor = interactor;
    }
  }

  public static final class MultiTankWrapper implements IFluidHandler {
    private final List<FluidTank> tanks;

    public MultiTankWrapper(final List<FluidTank> tanks) {
      this.tanks = tanks;
    }

    @Override
    public int getTanks() {
      return this.tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(final int tank) {
      if(tank < 0 || tank >= this.tanks.size()) {
        return FluidStack.EMPTY;
      }

      return this.tanks.get(tank).getFluid().copy();
    }

    @Override
    public int getTankCapacity(final int tank) {
      if(tank < 0 || tank >= this.tanks.size()) {
        return 0;
      }

      return this.tanks.get(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(final int tank, @Nonnull final FluidStack stack) {
      if(tank < 0 || tank >= this.tanks.size()) {
        return false;
      }

      return this.tanks.get(tank).isFluidValid(stack);
    }

    @Override
    public int fill(final FluidStack resource, final IFluidHandler.FluidAction action) {
      final FluidStack remaining = resource.copy();
      int totalFilled = 0;

      for(final FluidTank tank : this.tanks) {
        final int filled = tank.fill(remaining, action);
        totalFilled += filled;
        remaining.shrink(filled);

        if(totalFilled >= resource.getAmount()) {
          break;
        }
      }

      return totalFilled;
    }

    @Nonnull
    @Override
    public FluidStack drain(final FluidStack resource, final IFluidHandler.FluidAction action) {
      final FluidStack remaining = resource.copy();
      int totalDrained = 0;

      for(final FluidTank tank : this.tanks) {
        final FluidStack drained = tank.drain(remaining, action);
        totalDrained += drained.getAmount();
        remaining.shrink(drained.getAmount());

        if(totalDrained >= resource.getAmount()) {
          break;
        }
      }

      if(totalDrained == 0) {
        return FluidStack.EMPTY;
      }

      return new FluidStack(resource.getFluid(), totalDrained);
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final IFluidHandler.FluidAction action) {
      FluidStack remaining = FluidStack.EMPTY;
      int totalDrained = 0;

      for(final FluidTank tank : this.tanks) {
        final FluidStack drained;
        if(remaining.isEmpty()) {
          drained = tank.drain(maxDrain, action);
          remaining = new FluidStack(drained.getFluid(), maxDrain);
        } else {
          drained = tank.drain(remaining, action);
        }

        if(!drained.isEmpty()) {
          totalDrained += drained.getAmount();
          remaining.shrink(drained.getAmount());

          if(totalDrained >= maxDrain) {
            break;
          }
        }
      }

      if(totalDrained == 0) {
        return FluidStack.EMPTY;
      }

      return new FluidStack(remaining.getRawFluid(), totalDrained);
    }
  }

  public static class Builder {
    private final Processor.ProcessorItemHandler.Callback onItemChanged;
    private final Processor.ProcessorFluidTank.Callback onFluidChanged;
    private final List<ProcessorInteractor> processors = new ArrayList<>();

    public Builder(final Processor.ProcessorItemHandler.Callback onItemChanged, final Processor.ProcessorFluidTank.Callback onFluidChanged) {
      this.onItemChanged = onItemChanged;
      this.onFluidChanged = onFluidChanged;
    }

    public <Recipe extends IGradientRecipe> Builder addRecipeProcessor(final IRecipeType<Recipe> recipeType, final Consumer<Processor.Builder> builder) {
      return this.addRecipeProcessor(recipeType, builder, new NoopInteractor());
    }

    public <Recipe extends IGradientRecipe> Builder addRecipeProcessor(final IRecipeType<Recipe> recipeType, final Consumer<Processor.Builder> builder, final IInteractor interactor) {
      this.processors.add(new ProcessorInteractor(new RecipeProcessor<>(this.onItemChanged, this.onFluidChanged, recipeType, builder), interactor));
      return this;
    }

    public Builder addPumpProcessor(final Consumer<Processor.Builder> builder) {
      return this.addPumpProcessor(builder, new NoopInteractor());
    }

    public Builder addPumpProcessor(final Consumer<Processor.Builder> builder, final IInteractor interactor) {
      this.processors.add(new ProcessorInteractor(new PumpProcessor(this.onItemChanged, this.onFluidChanged, builder), interactor));
      return this;
    }
  }
}
