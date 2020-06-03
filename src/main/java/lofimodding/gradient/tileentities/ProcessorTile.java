package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.tileentities.pieces.IInteractor;
import lofimodding.gradient.tileentities.pieces.NoopInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class ProcessorTile<Recipe extends IGradientRecipe, Energy extends IEnergySource> extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final Energy energy;
  private final List<ProcessorInteractor<Recipe>> processors;
  private final IItemHandler inv;
  private final LazyOptional<IItemHandler> lazyInv;

  protected ProcessorTile(final TileEntityType<?> type, final Energy energy, final Consumer<Builder<Recipe>> builder) {
    super(type);
    this.energy = energy;

    final Builder<Recipe> b = new Builder<>(this::onInventoryChanged);
    builder.accept(b);

    this.processors = Collections.unmodifiableList(b.processors);

    this.inv = new CombinedInvWrapper(this.processors.stream().map(pi -> pi.processor.getInv()).toArray(IItemHandlerModifiable[]::new));
    this.lazyInv = LazyOptional.of(() -> this.inv);
  }

  protected Energy getEnergy() {
    return this.energy;
  }

  @Override
  public void tick() {
    if(!this.hasWork()) {
      return;
    }

    if(this.energy.consumeEnergy()) {
      for(final ProcessorInteractor<Recipe> pi : this.processors) {
        if(pi.processor.hasRecipe() && pi.processor.tick(this.world.isRemote)) {
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

  protected void onInventoryChanged(final Processor.ProcessorItemHandler<?> inv, final ItemStack stack) {

  }

  public ActionResultType onInteract(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    for(final ProcessorInteractor<Recipe> pi : this.processors) {
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
    for(final ProcessorInteractor<Recipe> pi : this.processors) {
      if(pi.processor.hasRecipe()) {
        return true;
      }
    }

    return false;
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

  protected abstract void onProcessorTick(final Processor<Recipe> processor);
  protected abstract void onAnimationTick(final Processor<Recipe> processor);
  protected abstract void resetAnimation(final Processor<Recipe> processor);

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("Energy", this.energy.write(new CompoundNBT()));

    final ListNBT processorsNbt = new ListNBT();
    for(final ProcessorInteractor<Recipe> processor : this.processors) {
      processorsNbt.add(processor.processor.write(new CompoundNBT()));
    }

    compound.put("Processors", processorsNbt);

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.energy.read(compound.getCompound("Energy"));

    final ListNBT processorsNbt = compound.getList("Processors", Constants.NBT.TAG_COMPOUND);

    for(int i = 0; i < Math.min(processorsNbt.size(), this.processors.size()); i++) {
      this.processors.get(i).processor.read(processorsNbt.getCompound(i));
    }

    super.read(compound);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
    if(cap == ITEM_HANDLER_CAPABILITY && this.inv.getSlots() > 0) {
      return this.lazyInv.cast();
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

    for(final ProcessorInteractor<Recipe> ri : this.processors) {
      if(ri.processor.hasRecipe()) {
        this.onAnimationTick(ri.processor);
      } else {
        this.resetAnimation(ri.processor);
      }
    }
  }

  private static final class ProcessorInteractor<Recipe extends IGradientRecipe> {
    private final Processor<Recipe> processor;
    private final IInteractor<Recipe> interactor;

    private ProcessorInteractor(final Processor<Recipe> processor, final IInteractor<Recipe> interactor) {
      this.processor = processor;
      this.interactor = interactor;
    }
  }

  public static class Builder<Recipe extends IGradientRecipe> {
    private final Processor.Callback onChanged;
    private final List<ProcessorInteractor<Recipe>> processors = new ArrayList<>();

    public Builder(final Processor.Callback onChanged) {
      this.onChanged = onChanged;
    }

    public Builder<Recipe> addProcessor(final IRecipeType<Recipe> recipeType, final Consumer<Processor.Builder> builder) {
      return this.addProcessor(recipeType, builder, new NoopInteractor<>());
    }

    public Builder<Recipe> addProcessor(final IRecipeType<Recipe> recipeType, final Consumer<Processor.Builder> builder, final IInteractor<Recipe> interactor) {
      this.processors.add(new ProcessorInteractor<>(new Processor<>(this.onChanged, recipeType, builder), interactor));
      return this;
    }
  }
}
