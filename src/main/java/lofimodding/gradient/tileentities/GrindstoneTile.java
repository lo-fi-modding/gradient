package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
import lofimodding.gradient.tileentities.pieces.ManualItemInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;

public class GrindstoneTile extends ProcessorTile<GrindingRecipe, ManualEnergySource> {
  public GrindstoneTile() {
    super(
      GradientTileEntities.GRINDSTONE.get(),
      new ManualEnergySource(20, 1),

      builder -> builder.addProcessor(
        new Processor<>(GrindingRecipe.TYPE, processor -> processor.addInputItem().addOutputItem()),
        new ManualItemInteractor<>()
      )
    );
  }
}
