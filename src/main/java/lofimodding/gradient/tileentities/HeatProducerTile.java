package lofimodding.gradient.tileentities;

import net.minecraft.tileentity.TileEntityType;

public abstract class HeatProducerTile extends HeatSinkerTile {
  protected HeatProducerTile(final TileEntityType<? extends HeatSinkerTile> type) {
    super(type);
  }

  @Override
  protected void tickAfterCooldown(final float tickScale) {
    this.heatUp(tickScale);
  }

  private void heatUp(final float tickScale) {
    this.addHeat(this.calculateHeatGain() / 20.0f * tickScale);
  }

  protected abstract float calculateHeatGain();
}
