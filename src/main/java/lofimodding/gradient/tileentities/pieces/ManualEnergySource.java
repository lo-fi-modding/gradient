package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ManualEnergySource<Recipe extends IGradientRecipe, Tile extends ProcessorTile<Recipe, ManualEnergySource<Recipe, Tile>, Tile>> implements IEnergySource<Recipe, ManualEnergySource<Recipe, Tile>, Tile> {
  private final int energyAddedPerCrank;
  private final int energyConsumedPerTick;

  private Runnable onCrank;
  private int energy;

  public ManualEnergySource(final int energyAddedPerCrank, final int energyConsumedPerTick) {
    this.energyAddedPerCrank = energyAddedPerCrank;
    this.energyConsumedPerTick = energyConsumedPerTick;
  }

  public void setOnCrankCallback(final Runnable onCrank) {
    this.onCrank = onCrank;
  }

  @Override
  public ActionResultType onInteract(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(this.energy == 0) {
      this.energy = this.energyAddedPerCrank;
      this.onCrank.run();
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }

  @Override
  public boolean consumeEnergy() {
    if(this.energy > 0) {
      this.energy -= this.energyConsumedPerTick;
      return true;
    }

    return false;
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putInt("Energy", this.energy);
    return compound;
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.energy = compound.getInt("Energy");
  }
}
