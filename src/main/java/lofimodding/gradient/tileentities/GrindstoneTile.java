package lofimodding.gradient.tileentities;

import lofimodding.gradient.tileentities.pieces.GrinderProcessor;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;

public class GrindstoneTile extends ManualProcessorTile {
  public GrindstoneTile() {
    super(new GrinderProcessor());
  }

  @Override
  protected void onProcessorTick() {
    ((ServerWorld)this.world).spawnParticle(ParticleTypes.SMOKE, this.pos.getX() + 0.5d, this.pos.getY() + 0.5d, this.pos.getZ() + 0.5d, 10, 0.1d, 0.1d, 0.1d, 0.01d);
  }
}
