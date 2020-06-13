package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
import lofimodding.gradient.tileentities.pieces.Processor;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;

public class MechanicalPumpTile extends ProcessorTile<KineticEnergySource> {
  public MechanicalPumpTile() {
    super(
      GradientTileEntities.MECHANICAL_PUMP.get(),
      new KineticEnergySource(0.25f, 0.25f, 0.25f),
      builder -> builder.addPumpProcessor(
        pump -> pump
          .tier(ProcessorTier.BASIC)
      )
    );
  }

  @Override
  protected void onProcessorTick(final Processor processor) {

  }

  @Override
  protected void onAnimationTick(final Processor processor) {

  }

  @Override
  protected void resetAnimation(final Processor processor) {

  }
}
