package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
import lofimodding.gradient.tileentities.pieces.ManualInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;

public class MechanicalGrindstoneTile extends ProcessorTile<GrindingRecipe, KineticEnergySource<GrindingRecipe, MechanicalGrindstoneTile>, MechanicalGrindstoneTile> {
  private float animation;

  public MechanicalGrindstoneTile() {
    super(
      GradientTileEntities.MECHANICAL_GRINDSTONE.get(),
      new KineticEnergySource<>(1.0f, 1.0f, 1.0f),
      builder -> builder.addProcessor(
        GrindingRecipe.TYPE,
        processor -> processor
          .tier(ProcessorTier.BASIC)
          .addInputItem()
          .addOutputItem(),
        new ManualInteractor()
      )
    );
  }

  public float getAnimation() {
    return this.animation;
  }

  @Override
  protected void onInventoryChanged(final Processor.ProcessorItemHandler inv, final ItemStack stack) {
    super.onInventoryChanged(inv, stack);
    this.syncToSurrounding();
  }

  @Override
  protected void onProcessorTick(final Processor processor) {
    ((ServerWorld)this.world).spawnParticle(ParticleTypes.SMOKE, this.pos.getX() + 0.5d, this.pos.getY() + 0.5d, this.pos.getZ() + 0.5d, 1, 0.1d, 0.1d, 0.1d, 0.01d);
  }

  @Override
  protected void onAnimationTick(final Processor processor) {
    this.animation = Math.abs((processor.getTicks() + 10) % 20 - 10.0f) / 10.0f;
  }

  @Override
  protected void resetAnimation(final Processor processor) {

  }
}
