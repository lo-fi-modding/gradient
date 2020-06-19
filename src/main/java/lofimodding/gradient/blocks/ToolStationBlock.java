package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.ToolStationTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ToolStationBlock extends Block {
  public ToolStationBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new ToolStationTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    if(!player.isSneaking()) {
      final ToolStationTile te = WorldUtils.getTileEntity(world, pos, ToolStationTile.class);

      if(te == null) {
        return ActionResultType.SUCCESS;
      }

      NetworkHooks.openGui((ServerPlayerEntity)player, te, pos);
    }

    return ActionResultType.SUCCESS;
  }
}
