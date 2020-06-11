package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.recipes.MixingRecipe;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
import lofimodding.gradient.tileentities.pieces.ManualInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class MixingBasinTile extends ProcessorTile<ManualEnergySource> {
  public static final int INPUT_SIZE = 5;

  private float animation;
  private boolean isMixing;

  public MixingBasinTile() {
    super(
      GradientTileEntities.MIXING_BASIN.get(),
      new ManualEnergySource(20, 1),
      builder -> builder.addProcessor(
        MixingRecipe.TYPE,
        processor -> processor
          .tier(ProcessorTier.BASIC)
          .addInputItem(1)
          .addInputItem(1)
          .addInputItem(1)
          .addInputItem(1)
          .addInputItem(1)
          .addOutputItem()
          .addInputFluid(1000, Processor.ProcessorFluidTank.Validator.forFluids(Fluids.WATER)),
        new ManualInteractor()
      )
    );

    this.getEnergy().setOnCrankCallback(this::onCrank);
  }

  public float getAnimation() {
    return this.animation;
  }

  public boolean isMixing() {
    return this.isMixing;
  }

  private void onCrank() {
    this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.NEUTRAL, 0.8f, this.world.rand.nextFloat() * 0.7f + 0.3f);
    this.syncToSurrounding();
  }

  @Override
  protected void onInventoryChanged(final Processor.ProcessorItemHandler inv, final ItemStack stack) {
    super.onInventoryChanged(inv, stack);
    this.syncToSurrounding();
  }

  @Override
  protected void onFluidsChanged(final Processor.ProcessorFluidTank tank, final FluidStack stack) {
    super.onFluidsChanged(tank, stack);
    this.world.setBlockState(this.pos, this.getBlockState().with(MixingBasinBlock.HAS_WATER, !stack.isEmpty()));
    this.syncToSurrounding();
  }

  @Override
  protected void onProcessorTick(final Processor processor) {
    if(this.world.rand.nextBoolean()) {
      final double x = this.pos.getX() + 0.5d;
      final double z = this.pos.getZ() + 0.5d;

      ((ServerWorld)this.world).spawnParticle(ParticleTypes.SPLASH, x, this.pos.getY() + 0.4d, z, 1, 0.0d, 0.0d, 0.0d, 0.0001d);
    }
  }

  @Override
  protected void onAnimationTick(final Processor processor) {
    this.animation = (processor.getTicks() % 40) / 40.0f;
    this.isMixing = processor.getTicks() != 0;
  }

  @Override
  protected void resetAnimation(final Processor processor) {
    this.isMixing = false;
  }
}
