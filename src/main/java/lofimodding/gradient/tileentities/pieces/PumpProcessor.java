package lofimodding.gradient.tileentities.pieces;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PumpProcessor extends Processor {
  private final Map<Direction, LazyOptional<IFluidHandler>> outputs = new EnumMap<>(Direction.class);
  private BlockPos fluidRoot = BlockPos.ZERO;
  private FluidStack fluid = FluidStack.EMPTY;

  private int ticks;
  private static final int MAX_TICKS = 60;

  public PumpProcessor(final ProcessorTile<?> tile, final ProcessorItemHandler.Callback onItemChange, final ProcessorFluidTank.Callback onFluidChange, final Consumer<Builder> builder) {
    super(tile, onItemChange, onFluidChange, builder);
  }

  @Override
  public void onAddToWorld() {
    super.onAddToWorld();

    final World world = this.tile.getWorld();
    final BlockPos pos = this.tile.getPos();

    this.outputs.clear();

    for(final Direction direction : Direction.values()) {
      if(direction == Direction.DOWN) {
        continue;
      }

      this.outputs.put(direction, FluidUtil.getFluidHandler(world, pos.offset(direction), direction.getOpposite()));
    }

    this.updateFluid(world, pos);
  }

  @Override
  public void onRemoveFromWorld() {
    super.onRemoveFromWorld();
    this.outputs.clear();
  }

  @Override
  public void onNeighbourChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbor, final boolean isMoving) {
    super.onNeighbourChanged(state, world, pos, block, neighbor, isMoving);

    final Direction direction = WorldUtils.getFacingTowards(pos, neighbor);

    if(direction != Direction.DOWN) {
      this.outputs.put(direction, FluidUtil.getFluidHandler(world, neighbor, direction.getOpposite()));
    } else {
      this.updateFluid(world, pos);
    }
  }

  @Override
  public boolean tick(final boolean isClient) {
    if(this.hasWork()) {
      this.ticks++;

      if(!isClient && this.isFinished()) {
        this.pump(this.tile.getWorld());
        this.ticks = 0;
      }

      return true;
    }

    return false;
  }

  @Override
  public int getTicks() {
    return this.ticks;
  }

  private boolean isFinished() {
    return this.ticks >= MAX_TICKS;
  }

  @Override
  public boolean hasWork() {
    if(this.fluid.isEmpty()) {
      return false;
    }

    return this.getReceiversForFluid(this.fluid).findAny().isPresent();
  }

  @Override
  protected void onInputsChanged() {

  }

  private Stream<IFluidHandler> getReceiversForFluid(final FluidStack fluid) {
    return this.outputs.values().stream()
      .filter(LazyOptional::isPresent)
      .map(opt -> opt.orElse(null))
      .filter(handler -> handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) != 0);
  }

  private void updateFluid(final World world, final BlockPos pos) {
    this.fluidRoot = pos.down();
    final Fluid fluid = world.getBlockState(this.fluidRoot).getFluidState().getFluid();

    if(fluid != Fluids.EMPTY) {
      this.fluid = new FluidStack(fluid, 1);
    } else {
      this.fluid = FluidStack.EMPTY;
    }
  }

  private final Object2IntMap<IFluidHandler> pumpHandlers = new Object2IntOpenHashMap<>();

  private void pump(final World world) {
    final IFluidState fluidState = world.getFluidState(this.fluidRoot);

    if(fluidState.isEmpty() || !fluidState.isSource()) {
      return;
    }

    final FluidStack fluid = new FluidStack(fluidState.getFluid(), FluidAttributes.BUCKET_VOLUME);

    for(final LazyOptional<IFluidHandler> opt : this.outputs.values()) {
      opt.ifPresent(handler -> {
        final int amount = handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);

        if(amount != 0) {
          this.pumpHandlers.put(handler, amount);
        }
      });
    }

    if(!this.pumpHandlers.isEmpty()) {
      int total = 0;

      for(final int amount : this.pumpHandlers.values()) {
        total += amount;
      }

      // Make sure that at least 1 bucket is required between all handlers
      if(total < FluidAttributes.BUCKET_VOLUME) {
        return;
      }

      // Clear out the fluid
      final BlockState state = world.getBlockState(this.fluidRoot);

      if(!(state.getBlock() instanceof IBucketPickupHandler)) {
        return;
      }

      final Fluid fluid1 = ((IBucketPickupHandler)state.getBlock()).pickupFluid(world, this.fluidRoot, state);

      int remaining = total;

      while(remaining > 0) {
        final int oldRemaining = remaining;
        final int amountToFill = remaining / this.pumpHandlers.size();

        final Iterator<Object2IntMap.Entry<IFluidHandler>> it = this.pumpHandlers.object2IntEntrySet().iterator();
        while(it.hasNext()) {
          final Object2IntMap.Entry<IFluidHandler> entry = it.next();
          final IFluidHandler handler = entry.getKey();
          final int filled = handler.fill(new FluidStack(fluid1, amountToFill), IFluidHandler.FluidAction.EXECUTE);

          if(filled == 0) {
            it.remove();
            continue;
          }

          remaining -= filled;
        }

        // We couldn't fill anything more so bail so we don't get into an infinite loop
        if(oldRemaining == remaining) {
          break;
        }
      }
    }

    this.pumpHandlers.clear();
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    final CompoundNBT nbt = super.write(compound);
    nbt.putInt("Ticks", this.ticks);
    return nbt;
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.ticks = compound.getInt("Ticks");
    super.read(compound);
  }
}
