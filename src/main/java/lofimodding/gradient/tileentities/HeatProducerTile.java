package lofimodding.gradient.tileentities;

import net.minecraft.tileentity.TileEntityType;

public abstract class HeatProducerTile extends HeatSinkerTile {
  protected HeatProducerTile(final TileEntityType<? extends HeatSinkerTile> type) {
    super(type);
  }

  @Override
  protected void tickAfterCooldown() {
    this.heatUp();
  }

  private void heatUp() {
    this.addHeat(this.calculateHeatGain() / 20.0f);
  }

  protected abstract float calculateHeatGain();
}
