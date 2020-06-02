package lofimodding.gradient.tileentities.pieces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface IEnergySource {
  default ActionResultType onInteract(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    return ActionResultType.PASS;
  }

  boolean consumeEnergy();
  CompoundNBT write(final CompoundNBT compound);
  void read(final CompoundNBT compound);
}
