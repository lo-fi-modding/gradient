package lofimodding.gradient.blocks;

import lofimodding.gradient.network.UpdateHeatNeighboursPacket;
import lofimodding.gradient.tileentities.HeatSinkerTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class HeatSinkerBlock extends Block {
  protected HeatSinkerBlock(final Properties properties) {
    super(properties);
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public abstract TileEntity createTileEntity(final BlockState state, final IBlockReader world);

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbor, final boolean isMoving) {
    super.neighborChanged(state, world, pos, block, neighbor, isMoving);

    final HeatSinkerTile te = WorldUtils.getTileEntity(world, pos, HeatSinkerTile.class);

    if(te != null) {
      te.updateSink(neighbor);
      UpdateHeatNeighboursPacket.send(world.dimension.getType(), pos, neighbor);
    }
  }
}
