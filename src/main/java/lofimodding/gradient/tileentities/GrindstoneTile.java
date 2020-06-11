package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientSounds;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
import lofimodding.gradient.tileentities.pieces.ManualInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.server.ServerWorld;

public class GrindstoneTile extends ProcessorTile<ManualEnergySource> {
  private float animation;

  public GrindstoneTile() {
    super(
      GradientTileEntities.GRINDSTONE.get(),
      new ManualEnergySource(20, 1),

      builder -> builder.addProcessor(
        GrindingRecipe.TYPE,
        processor -> processor
          .tier(ProcessorTier.BASIC)
          .addInputItem()
          .addOutputItem(),
        new ManualInteractor()
      )
    );

    this.getEnergy().setOnCrankCallback(this::onCrank);
  }

  public float getAnimation() {
    return this.animation;
  }

  private void onCrank() {
    this.world.playSound(null, this.pos, GradientSounds.GRINDSTONE.get(), SoundCategory.NEUTRAL, 1.0f, this.world.rand.nextFloat() * 0.1f + 0.9f);
    this.syncToSurrounding();
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
