package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.MechanicalMixingBasinBlock;
import lofimodding.gradient.recipes.MixingRecipe;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
import lofimodding.gradient.tileentities.pieces.ManualInteractor;
import lofimodding.gradient.tileentities.pieces.Processor;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class MechanicalMixingBasinTile extends ProcessorTile<MixingRecipe, KineticEnergySource<MixingRecipe, MechanicalMixingBasinTile>, MechanicalMixingBasinTile> {
  public static final int INPUT_SIZE = 5;

  private float animation;

  public MechanicalMixingBasinTile() {
    super(
      GradientTileEntities.MECHANICAL_MIXING_BASIN.get(),
      new KineticEnergySource<>(1.0f, 1.0f, 1.0f),
      builder -> builder.addProcessor(
        MixingRecipe.TYPE,
        processor -> processor
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
  protected void onFluidsChanged(final Processor.ProcessorFluidTank tank, final FluidStack stack) {
    super.onFluidsChanged(tank, stack);
    this.world.setBlockState(this.pos, this.getBlockState().with(MechanicalMixingBasinBlock.HAS_WATER, !stack.isEmpty()));
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
  }

  @Override
  protected void resetAnimation(final Processor processor) {

  }
}
