package lofimodding.gradient.blocks;

import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.tileentities.ToolStationTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandlerModifiable;

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
    return new ToolStationTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return state.get(PRIMARY);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    if(!player.isSneaking() && state.getBlock() == this) {
      final ToolStationTile te = WorldUtils.getTileEntity(world, pos, ToolStationTile.class);

      if(te != null) {
        NetworkHooks.openGui((ServerPlayerEntity)player, te, pos);
      } else {
        final BlockPos primary = WorldUtils.findControllerBlock(pos, p -> world.getBlockState(p).getBlock() == this, p -> world.getBlockState(p).get(PRIMARY));

        if(primary != BlockPos.ZERO) {
          NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
              return ToolStationBlock.this.getNameTextComponent();
            }

            @Override
            public Container createMenu(final int id, final PlayerInventory inv, final PlayerEntity player) {
              return new ToolStationContainer(id, inv, WorldUtils.getTileEntity(world, primary, ToolStationTile.class));
            }
          }, primary);
        }
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

  private boolean onBlockAddedReentryProtection;

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
    super.onBlockAdded(state, world, pos, oldState, isMoving);

    if(this.onBlockAddedReentryProtection) {
      return;
    }

    this.onBlockAddedReentryProtection = true;

    if(!state.get(PRIMARY)) {
      final NonNullList<BlockPos> primary = WorldUtils.findControllerBlocks(pos, p -> world.getBlockState(p).getBlock() == this, p -> world.getBlockState(p).get(PRIMARY));

      if(!primary.isEmpty()) {
        primary.sort(Comparator.comparingLong(BlockPos::toLong));

        for(int i = 1; i < primary.size(); i++) {
          world.setBlockState(primary.get(i), this.getDefaultState().with(PRIMARY, Boolean.FALSE));
        }

        final ToolStationTile tile = WorldUtils.getTileEntity(world, primary.get(0), ToolStationTile.class);

        if(tile != null) {
          tile.updateNeighbours();
        }
      }
    }

    this.onBlockAddedReentryProtection = false;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    // super calls are weird because we may need to grab the inv before removing the TE

    if(state.get(PRIMARY)) {
      final ToolStationTile primary = WorldUtils.getTileEntity(world, pos, ToolStationTile.class);
      final IItemHandlerModifiable tools = primary.getToolsInv();
      final IItemHandlerModifiable storage = primary.getStorageInv();

      super.onReplaced(state, world, pos, newState, isMoving);

      for(final Direction direction : Direction.Plane.HORIZONTAL) {
        final NonNullList<BlockPos> blob = WorldUtils.getBlockCluster(pos.offset(direction), p -> world.getBlockState(p).getBlock() == this);

        if(!blob.isEmpty()) {
          for(int i = 1; i < blob.size(); i++) {
            final BlockState existing = world.getBlockState(blob.get(i));
            if(existing.get(PRIMARY)) {
              world.setBlockState(blob.get(i), this.getDefaultState().with(PRIMARY, Boolean.FALSE));
            }
          }

          blob.sort(Comparator.comparingLong(BlockPos::toLong));
          world.setBlockState(blob.get(0), this.getDefaultState().with(PRIMARY, Boolean.TRUE));

          WorldUtils.getTileEntity(world, blob.get(0), ToolStationTile.class).addToInv(tools, storage);
        }
      }

      for(int slot = 0; slot < tools.getSlots(); slot++) {
        final ItemStack stack = tools.getStackInSlot(slot);

        if(!stack.isEmpty()) {
          InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
      }

      for(int slot = 0; slot < storage.getSlots(); slot++) {
        final ItemStack stack = storage.getStackInSlot(slot);

        if(!stack.isEmpty()) {
          InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
      }
    } else {
      super.onReplaced(state, world, pos, newState, isMoving);

      for(final Direction direction : Direction.Plane.HORIZONTAL) {
        final BlockPos secondary = WorldUtils.findControllerBlock(pos.offset(direction), p -> world.getBlockState(p).getBlock() == this, p -> world.getBlockState(p).get(PRIMARY));

        if(secondary != BlockPos.ZERO) {
          final ToolStationTile tile = WorldUtils.getTileEntity(world, secondary, ToolStationTile.class);

          if(tile != null) {
            tile.updateNeighbours();
          }
        } else {
          final NonNullList<BlockPos> blocks = WorldUtils.getBlockCluster(pos.offset(direction), p -> world.getBlockState(p).getBlock() == this);

          if(!blocks.isEmpty()) {
            blocks.sort(Comparator.comparingLong(BlockPos::toLong));
            world.setBlockState(blocks.get(0), this.getDefaultState().with(PRIMARY, Boolean.TRUE));
          }
        }
      }
    }

    //TODO drop items
    //TODO stack overflow when placing 5 blocks in an L shape
  }
}
