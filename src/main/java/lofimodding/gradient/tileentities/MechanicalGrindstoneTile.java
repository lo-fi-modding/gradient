package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
import lofimodding.gradient.tileentities.pieces.Processor;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Consumer;

public class MechanicalGrindstoneTile extends ProcessorTile<GrindingRecipe, KineticEnergySource<GrindingRecipe, MechanicalGrindstoneTile>, MechanicalGrindstoneTile> {
  protected MechanicalGrindstoneTile(final TileEntityType<MechanicalGrindstoneTile> type, final KineticEnergySource<GrindingRecipe, MechanicalGrindstoneTile> kineticEnergySource, final Consumer<Builder<GrindingRecipe>> builder) {
    super(type, kineticEnergySource, builder);
  }

  @Override
  protected void onProcessorTick(final Processor<GrindingRecipe> processor) {

  }

  @Override
  protected void onAnimationTick(final Processor<GrindingRecipe> processor) {

  }

  @Override
  protected void resetAnimation(final Processor<GrindingRecipe> processor) {

  }
}
