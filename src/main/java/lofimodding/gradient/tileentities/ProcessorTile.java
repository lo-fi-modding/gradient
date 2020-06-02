package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.tileentities.pieces.IInteractor;
import lofimodding.gradient.tileentities.pieces.NoopInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class ProcessorTile<Recipe extends IGradientRecipe, Energy extends IEnergySource> extends TileEntity implements ITickableTileEntity {
  private final Energy energy;
  private final List<ProcessorInteractor<Recipe>> processors;

  public ProcessorTile(final TileEntityType<?> type, final Energy energy, final Consumer<Builder<Recipe>> builder) {
    super(type);
    this.energy = energy;

    final Builder<Recipe> b = new Builder<>();
    builder.accept(b);

    this.processors = Collections.unmodifiableList(b.processors);
  }

  @Override
  public void tick() {
    for(final ProcessorInteractor<Recipe> pi : this.processors) {
      if(pi.processor.hasRecipe() && this.energy.consumeEnergy()) {
        if(pi.processor.tick()) {
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

  public ActionResultType onInteract(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    for(final ProcessorInteractor<Recipe> pi : this.processors) {
      final ActionResultType result = pi.interactor.onInteract(pi.processor, state, world, pos, player, hand, hit);

      if(result != ActionResultType.PASS) {
        return result;
      }
    }

    return this.energy.onInteract(state, world, pos, player, hand, hit);
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

  private static final class ProcessorInteractor<Recipe extends IGradientRecipe> {
    private final Processor<Recipe> processor;
    private final IInteractor<Recipe> interactor;

    private ProcessorInteractor(final Processor<Recipe> processor, final IInteractor<Recipe> interactor) {
      this.processor = processor;
      this.interactor = interactor;
    }
  }

  public static class Builder<Recipe extends IGradientRecipe> {
    private final List<ProcessorInteractor<Recipe>> processors = new ArrayList<>();

    public Builder<Recipe> addProcessor(final Processor<Recipe> processor) {
      return this.addProcessor(processor, new NoopInteractor<>());
    }

    public Builder<Recipe> addProcessor(final Processor<Recipe> processor, final IInteractor<Recipe> interactor) {
      this.processors.add(new ProcessorInteractor<>(processor, interactor));
      return this;
    }
  }
}
