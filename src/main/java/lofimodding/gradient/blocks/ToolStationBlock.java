package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.ToolStationSecondaryTile;
import lofimodding.gradient.tileentities.ToolStationTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Comparator;

public class ToolStationBlock extends Block {
  public static final BooleanProperty PRIMARY = BooleanProperty.create("primary");

  public ToolStationBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
    this.setDefaultState(this.stateContainer.getBaseState().with(PRIMARY, Boolean.TRUE));
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    builder.add(PRIMARY);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    if(!state.get(PRIMARY)) {
      return new ToolStationSecondaryTile();
    }

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
      final TileEntity te = world.getTileEntity(pos);

      if(te instanceof ToolStationTile) {
        NetworkHooks.openGui((ServerPlayerEntity)player, (ToolStationTile)te, pos);
      }

      if(te instanceof ToolStationSecondaryTile) {
        NetworkHooks.openGui((ServerPlayerEntity)player, (ToolStationSecondaryTile)te, ((ToolStationSecondaryTile)te).getPrimary());
      }
    }

    return ActionResultType.SUCCESS;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    final World world = context.getWorld();
    final BlockPos pos = context.getPos();

    for(final Direction direction : Direction.Plane.HORIZONTAL) {
      final BlockState state = world.getBlockState(pos.offset(direction));

      if(state.getBlock() == this) {
        return super.getStateForPlacement(context).with(PRIMARY, Boolean.FALSE);
      }
    }

    return super.getStateForPlacement(context);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    super.onBlockAdded(state, world, pos, oldState, isMoving);

    //TODO tile entities not getting swapped with blockstate

    if(!state.get(PRIMARY)) {
      for(final Direction direction : Direction.Plane.HORIZONTAL) {
        final ToolStationTile primary = WorldUtils.getTileEntity(world, pos.offset(direction), ToolStationTile.class);

        if(primary != null) {
          WorldUtils.getTileEntity(world, pos, ToolStationSecondaryTile.class).setPrimary(primary.getPos());
          primary.updateNeighbours();
          continue;
        }

        final ToolStationSecondaryTile secondary = WorldUtils.getTileEntity(world, pos.offset(direction), ToolStationSecondaryTile.class);

        if(secondary != null) {
          WorldUtils.getTileEntity(world, pos, ToolStationSecondaryTile.class).setPrimary(secondary.getPrimary());
          WorldUtils.getTileEntity(world, secondary.getPrimary(), ToolStationTile.class).updateNeighbours();
        }
      }
    }

    //TODO: handle placing one in between two others
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);

    if(state.get(PRIMARY)) {
      for(final Direction direction : Direction.Plane.HORIZONTAL) {
        final NonNullList<BlockPos> blob = WorldUtils.getBlockCluster(pos.offset(direction), p -> world.getBlockState(p).getBlock() == this);

        if(!blob.isEmpty()) {
          for(int i = 1; i < blob.size(); i++) {
            final BlockState existing = world.getBlockState(blob.get(i));
            if(existing.get(PRIMARY)) {
              world.removeBlock(blob.get(i), false);
              world.setBlockState(blob.get(i), this.getDefaultState().with(PRIMARY, Boolean.FALSE));
              WorldUtils.getTileEntity(world, blob.get(i), ToolStationSecondaryTile.class).setPrimary(blob.get(0));
            }
          }

          blob.sort(Comparator.comparingLong(BlockPos::toLong));
          world.removeBlock(blob.get(0), false);
          world.setBlockState(blob.get(0), this.getDefaultState().with(PRIMARY, Boolean.TRUE));
        }
      }
    } else {
      final ToolStationSecondaryTile secondary = WorldUtils.getTileEntity(world, pos, ToolStationSecondaryTile.class);

      if(secondary != null) {
        final ToolStationTile primary = WorldUtils.getTileEntity(world, secondary.getPrimary(), ToolStationTile.class);

        if(primary != null) {
          primary.updateNeighbours();
        }
      }
    }

    for(final Direction direction : Direction.Plane.HORIZONTAL) {
      final ToolStationTile tile = WorldUtils.getTileEntity(world, pos.offset(direction), ToolStationTile.class);

      if(tile != null) {
        tile.updateNeighbours();
      }
    }

    //TODO drop items
  }
}
