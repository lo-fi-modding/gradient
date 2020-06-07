package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface IEnergySource<Recipe extends IGradientRecipe, Energy extends IEnergySource<Recipe, Energy, Tile>, Tile extends ProcessorTile<Recipe, Energy, Tile>> {
  default void onAddToWorld(final Tile tile) {

  }

  default void onRemoveFromWorld(final Tile tile) {

  }

  default ActionResultType onInteract(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    return ActionResultType.PASS;
  }

  boolean consumeEnergy();
  CompoundNBT write(final CompoundNBT compound);
  void read(final CompoundNBT compound);

  default <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
    return LazyOptional.empty();
  }
}
