package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.tileentities.pieces.IInteractor;
import lofimodding.gradient.tileentities.pieces.NoopInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ProcessorTile<Recipe extends IGradientRecipe, Energy extends IEnergySource> extends TileEntity implements ITickableTileEntity {
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
    for(final ProcessorInteractor<Recipe> processor : this.processors) {
      if(processor.processor.hasRecipe() && this.energy.consumeEnergy()) {
        if(processor.processor.tick()) {
          this.markDirty();
        }
      }
    }
  }

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
