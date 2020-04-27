package lofimodding.gradient.blocks;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientSounds;
import lofimodding.gradient.tileentities.FirepitTile;
import lofimodding.gradient.utils.WorldUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public class FirepitBlock extends HeatSinkerBlock {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty HAS_FURNACE = BooleanProperty.create("has_furnace");

  private static final VoxelShape SHAPE = makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 4.0d, 16.0d);

  public FirepitBlock() {
    super(Properties.create(Material.WOOD, MaterialColor.RED).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HAS_FURNACE, Boolean.FALSE));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context) {
    if(state.get(HAS_FURNACE)) {
      return VoxelShapes.fullCube();
    }

    return SHAPE;
  }

  @Override
  public int getLightValue(final BlockState state, final IBlockReader world, final BlockPos pos) {
    final FirepitTile te = WorldUtils.getTileEntity(world, pos, FirepitTile.class);

    if(te != null) {
      return te.getLightLevel(state);
    }

    return super.getLightValue(state, world, pos);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult trace) {
    if(!world.isRemote) {
      final FirepitTile firepit = WorldUtils.getTileEntity(world, pos, FirepitTile.class);

      if(firepit == null) {
        return ActionResultType.PASS;
      }

      final ItemStack held = player.getHeldItem(hand);

      if(!player.isShiftKeyDown()) {
        if(held.getItem() == GradientItems.FIRE_STARTER.get()) {
          if(!firepit.isBurning()) {
            if(!player.isCreative()) {
              held.damageItem(1, player, p -> p.sendBreakAnimation(hand));
            }

            world.playSound(null, pos, GradientSounds.FIRE_STARTER.get(), SoundCategory.NEUTRAL, 1.0f, world.rand.nextFloat() * 0.1f + 0.9f);
            firepit.light();
            return ActionResultType.SUCCESS;
          }
        }

        //TODO
//        if(Block.getBlockFromItem(held.getItem()) instanceof BlockTorchUnlit) {
//          if(firepit.isBurning()) {
//            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.15f, world.rand.nextFloat() * 0.1f + 0.9f);
//            player.setHeldItem(hand, new ItemStack(((BlockTorchUnlit)((BlockItem)held.getItem()).getBlock()).lit, held.getCount()));
//            return ActionResultType.SUCCESS;
//          }
//        }

        if(Block.getBlockFromItem(held.getItem()) == GradientBlocks.CLAY_FURNACE.get()) {
          if(!state.get(HAS_FURNACE)) {
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.NEUTRAL, 1.0f, world.rand.nextFloat() * 0.1f + 0.9f);
            world.setBlockState(pos, state.with(HAS_FURNACE, true));

            firepit.validate();
            world.setTileEntity(pos, firepit);
            firepit.attachFurnace();

            if(!player.isCreative()) {
              held.shrink(1);
            }

            return ActionResultType.SUCCESS;
          }
        }
      }

      // Remove input
      if(player.isShiftKeyDown()) {
        if(firepit.hasInput()) {
          final ItemStack input = firepit.takeInput();
          ItemHandlerHelper.giveItemToPlayer(player, input);
          return ActionResultType.SUCCESS;
        }

        if(!firepit.isBurning()) {
          for(int slot = 0; slot < FirepitTile.FUEL_SLOTS_COUNT; slot++) {
            if(firepit.hasFuel(slot)) {
              final ItemStack fuel = firepit.takeFuel(slot);
              ItemHandlerHelper.giveItemToPlayer(player, fuel);
              return ActionResultType.SUCCESS;
            }
          }
        }

        return ActionResultType.SUCCESS;
      }

      // Take stuff out
      if(firepit.hasOutput()) {
        final ItemStack output = firepit.takeOutput();
        ItemHandlerHelper.giveItemToPlayer(player, output);
        return ActionResultType.SUCCESS;
      }

      // Put stuff in
      if(!held.isEmpty()) {
        final ItemStack remaining = firepit.insertItem(held.copy(), player);

        if(remaining.getCount() != held.getCount()) {
          world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
        }

        if(!player.isCreative()) {
          player.setHeldItem(hand, remaining);
        }

        return ActionResultType.SUCCESS;
      }
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    if(newState.getBlock() != this) {
      WorldUtils.dropInventory(world, pos, FirepitTile.FIRST_INPUT_SLOT);
    }

    super.onReplaced(state, world, pos, newState, isMoving);
  }

  private static final BlockPos.Mutable blockPlacedPos = new BlockPos.Mutable();

  @SubscribeEvent
  public static void blockPlacedHandler(final BlockEvent.EntityPlaceEvent event) {
    if(!(event.getEntity() instanceof PlayerEntity)) {
      return;
    }

    final IWorld world = event.getWorld();
    final BlockPos pos = event.getPos();

    final Set<Stage> stages = Progress.get((LivingEntity)event.getEntity()).getStages();

    blockPlacedPos.setPos(pos);
    updateFirePit(world, blockPlacedPos.move(Direction.NORTH), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.EAST), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.SOUTH), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.SOUTH), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.WEST), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.WEST), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.NORTH), pos, stages);
    updateFirePit(world, blockPlacedPos.move(Direction.NORTH), pos, stages);
  }

  private static void updateFirePit(final IWorld world, final BlockPos firePitPos, final BlockPos placedPos, final Set<Stage> stages) {
    final FirepitTile te = WorldUtils.getTileEntity(world, firePitPos, FirepitTile.class);

    if(te != null) {
      te.updateHardenable(placedPos, stages);
    }
  }

  @Override
  public void onBlockPlacedBy(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack) {
    final FirepitTile te = WorldUtils.getTileEntity(world, pos, FirepitTile.class);

    if(te != null) {
      te.updateSurroundingHardenables(Progress.get(placer).getStages());
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new FirepitTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, HAS_FURNACE);
  }
}
