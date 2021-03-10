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
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Comparator;

public class ToolStationBlock extends Block {
  public static final BooleanProperty PRIMARY = BooleanProperty.create("primary");
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  private static final VoxelShape BASE_SHAPE = makeCuboidShape(1.0d, 0.0d, 1.0d, 15.0d, 11.0d, 15.0d);
  private static final VoxelShape TOP_SHAPE = makeCuboidShape(0.0d, 11.0d, 0.0d, 16.0d, 16.0d, 16.0d);
  private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(BASE_SHAPE, TOP_SHAPE, IBooleanFunction.OR);

  public ToolStationBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
    this.setDefaultState(this.stateContainer.getBaseState().with(PRIMARY, Boolean.TRUE).with(FACING, Direction.NORTH));
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(PRIMARY, FACING);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
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
              return new TranslationTextComponent(ToolStationBlock.this.getTranslationKey());
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
        return super.getStateForPlacement(context).with(PRIMARY, Boolean.FALSE).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
      }
    }

    return super.getStateForPlacement(context).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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

        final ToolStationTile tile = WorldUtils.getTileEntity(world, primary.get(0), ToolStationTile.class);

        for(int i = 1; i < primary.size(); i++) {
          if(tile != null) {
            final ToolStationTile oldPrimary = WorldUtils.getTileEntity(world, primary.get(i), ToolStationTile.class);
            final IItemHandlerModifiable tools = oldPrimary.getToolsInv();
            final IItemHandlerModifiable storage = oldPrimary.getStorageInv();
            tile.addToInv(tools, storage);
          }

          world.setBlockState(primary.get(i), world.getBlockState(primary.get(i)).with(PRIMARY, Boolean.FALSE));
        }

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

    // Don't do all this stuff if adding a block is what's triggering the removal (replacement)
    if(this.onBlockAddedReentryProtection) {
      super.onReplaced(state, world, pos, newState, isMoving);
      return;
    }

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
              world.setBlockState(blob.get(i), existing.with(PRIMARY, Boolean.FALSE));
            }
          }

          blob.sort(Comparator.comparingLong(BlockPos::toLong));
          world.setBlockState(blob.get(0), world.getBlockState(blob.get(0)).with(PRIMARY, Boolean.TRUE));

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
            world.setBlockState(blocks.get(0), world.getBlockState(blocks.get(0)).with(PRIMARY, Boolean.TRUE));
          }
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState rotate(final BlockState state, final Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(final BlockState state, final Mirror mirror) {
    return state.rotate(mirror.toRotation(state.get(FACING)));
  }
}
