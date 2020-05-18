package lofimodding.gradient.tileentities;

import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.tileentities.pieces.IProcessor;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class ProcessorTile<Recipe extends IRecipe<?>, Source extends IEnergySource, Processor extends IProcessor<Recipe>> extends TileEntity implements ITickableTileEntity {
  private final Source energy;
  private final Processor processor;
  private Recipe recipe;

  public ProcessorTile(final TileEntityType<?> type, final Source energy, final Processor processor) {
    super(type);
    this.energy = energy;
    this.processor = processor;
  }

  @Override
  public void tick() {
    if(this.energy.consumeEnergy()) {
      if(this.processor.tick()) {
        this.markDirty();

        if(!this.world.isRemote) {
          this.onProcessorTick();

          if(this.processor.isFinished()) {
            this.onFinished(this.recipe);
            this.processor.restart();
            this.sync();
          }
        } else {
          this.onAnimationTick(this.processor.getTicks());
        }
      }
    }
  }

  protected Source getEnergy() {
    return this.energy;
  }

  protected Processor getProcessor() {
    return this.processor;
  }

  protected void setRecipe(final Recipe recipe) {
    this.processor.setRecipe(recipe);
    this.recipe = recipe;
  }

  protected Recipe getRecipe() {
    return this.recipe;
  }

  protected boolean hasRecipe() {
    return this.recipe != null;
  }

  protected void clearRecipe() {
    this.processor.setRecipe(null);
    this.recipe = null;
    this.resetAnimation();
  }

  protected abstract void onProcessorTick();
  protected abstract void onFinished(final Recipe recipe);
  protected abstract void onAnimationTick(final int ticks);
  protected abstract void resetAnimation();

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("energy", this.energy.write(new CompoundNBT()));
    compound.put("processor", this.processor.write(new CompoundNBT()));
    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.energy.read(compound.getCompound("energy"));
    this.processor.read(compound.getCompound("processor"));
    super.read(compound);
  }

  protected void sync() {
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

    if(this.hasRecipe()) {
      this.onAnimationTick(this.processor.getTicks());
    } else {
      this.resetAnimation();
    }
  }
}
