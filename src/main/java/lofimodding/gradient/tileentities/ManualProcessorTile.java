package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.tileentities.pieces.IProcessor;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class ManualProcessorTile<Recipe extends IRecipe<?>> extends TileEntity implements ITickableTileEntity {
  private final ManualEnergySource energySource = new ManualEnergySource();
  private final IProcessor<Recipe> processor;
  private Recipe recipe;

  public ManualProcessorTile(final IProcessor<Recipe> processor) {
    super(GradientTileEntities.GRINDSTONE.get());
    this.processor = processor;
  }

  @Override
  public void tick() {
    if(this.world.isRemote) {
      return;
    }

    if(this.energySource.consumeEnergy()) {
      if(this.processor.tick()) {
        this.onProcessorTick();
        this.markDirty();

        if(this.processor.isFinished()) {
          this.onFinished(this.recipe);
        }
      }
    }
  }

  protected void setRecipe(final Recipe recipe) {
    this.processor.setRecipe(recipe);
    this.recipe = recipe;
  }

  protected void clearRecipe() {
    this.processor.setRecipe(null);
    this.recipe = null;
  }

  protected abstract void onProcessorTick();
  protected abstract void onFinished(final Recipe recipe);
}
