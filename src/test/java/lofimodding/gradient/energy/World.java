package lofimodding.gradient.energy;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class World implements IBlockReader {
  private final Map<BlockPos, TileEntity> tes = new HashMap<>();

  public TileEntity addTileEntity(final BlockPos pos, final TileEntity te) {
    te.setPos(pos);
    this.tes.put(pos, te);
    return te;
  }

  public void removeTileEntity(final BlockPos pos) {
    this.tes.remove(pos);
  }

  @Nullable
  @Override
  public TileEntity getTileEntity(final BlockPos pos) {
    return this.tes.get(pos);
  }

  @Override
  public BlockState getBlockState(final BlockPos pos) {
    return null;
  }

  @Override
  public IFluidState getFluidState(final BlockPos pos) {
    return null;
  }

  @Override
  public int getLightValue(final BlockPos pos) {
    return 0;
  }

  @Override
  public int getMaxLightLevel() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public BlockRayTraceResult rayTraceBlocks(final RayTraceContext context) {
    return null;
  }

  @Nullable
  @Override
  public BlockRayTraceResult rayTraceBlocks(final Vec3d p_217296_1_, final Vec3d p_217296_2_, final BlockPos p_217296_3_, final VoxelShape p_217296_4_, final BlockState p_217296_5_) {
    return null;
  }
}
