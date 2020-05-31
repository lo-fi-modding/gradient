package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.CreativeGeneratorTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CreativeGeneratorBlock extends Block {
  public CreativeGeneratorBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);

    final CreativeGeneratorTile generator = WorldUtils.getTileEntity(world, pos, CreativeGeneratorTile.class);

    if(generator != null) {
      generator.remove();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult trace) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    if(!player.isSneaking()) {
      final CreativeGeneratorTile te = WorldUtils.getTileEntity(world, pos, CreativeGeneratorTile.class);

      if(te == null) {
        return ActionResultType.SUCCESS;
      }

      NetworkHooks.openGui((ServerPlayerEntity)player, te, pos);
    }

    return ActionResultType.SUCCESS;
  }

  @Override
  public CreativeGeneratorTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new CreativeGeneratorTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
}
