package lofimodding.gradient.blocks;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class ProcessorBlock<Recipe extends IGradientRecipe, Energy extends IEnergySource, Tile extends ProcessorTile<Recipe, Energy>> extends Block {
  private final Class<Tile> cls;

  protected ProcessorBlock(final Class<Tile> cls, final Properties properties) {
    super(properties);
    this.cls = cls;
  }

  @Override
  public abstract Tile createTileEntity(final BlockState state, final IBlockReader world);

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    final Tile tile = WorldUtils.getTileEntity(world, pos, this.cls);

    if(tile == null) {
      return ActionResultType.SUCCESS;
    }

    final ActionResultType result = tile.onInteract(state, world, pos, player, hand, hit);

    if(result != ActionResultType.PASS) {
      return result;
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    WorldUtils.dropInventory(world, pos);
    super.onReplaced(state, world, pos, newState, isMoving);
  }
}
