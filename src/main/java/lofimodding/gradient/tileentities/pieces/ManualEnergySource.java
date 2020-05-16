package lofimodding.gradient.tileentities.pieces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ManualEnergySource implements IEnergySource {
  private int energy;

  public ActionResultType crank(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(this.energy == 0) {
      this.energy = 20;
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.FAIL;
  }

  @Override
  public boolean consumeEnergy() {
    if(this.energy > 0) {
      this.energy--;
      return true;
    }

    return false;
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putInt("energy", this.energy);
    return compound;
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.energy = compound.getInt("energy");
  }
}
