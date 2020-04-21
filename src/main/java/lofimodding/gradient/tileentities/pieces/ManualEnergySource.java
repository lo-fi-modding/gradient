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
  private boolean hasEnergy;

  public ActionResultType crank(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    this.hasEnergy = true;
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean consumeEnergy() {
    if(this.hasEnergy) {
      this.hasEnergy = false;
      return true;
    }

    return false;
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putBoolean("has_energy", this.hasEnergy);
    return compound;
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.hasEnergy = compound.getBoolean("has_energy");
  }
}
