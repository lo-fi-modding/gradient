package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.MixingBasinTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;

public class MixingBasinBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty HAS_WATER = BooleanProperty.create("has_water");

  private static final VoxelShape SHAPE = makeCuboidShape(2.0d, 0.0d, 2.0d, 14.0d, 8.0d, 14.0d);

  public MixingBasinBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HAS_WATER, Boolean.FALSE));
  }

  @Override
  public MixingBasinTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new MixingBasinTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(!world.isRemote) {
      final MixingBasinTile basin = WorldUtils.getTileEntity(world, pos, MixingBasinTile.class);

      if(basin == null) {
        return ActionResultType.SUCCESS;
      }

      // Water
      if(FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent()) {
        final FluidStack fluid = FluidUtil.getFluidContained(player.getHeldItem(hand)).orElse(FluidStack.EMPTY);

        // Make sure the fluid handler is either empty, or contains 1000 mB of water
        if(!fluid.isEmpty() && (fluid.getFluid() != Fluids.WATER || fluid.getAmount() < 1000)) {
          return ActionResultType.SUCCESS;
        }

        if(FluidUtil.interactWithFluidHandler(player, hand, world, pos, hit.getFace())) {
          if(fluid.isEmpty()) {
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.NEUTRAL, 0.15f, world.rand.nextFloat() * 0.1f + 0.9f);
          } else {
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 0.15f, world.rand.nextFloat() * 0.1f + 0.9f);
          }
        }

        return ActionResultType.SUCCESS;
      }

      // Remove input
      if(player.isSneaking()) {
        for(int slot = 0; slot < MixingBasinTile.INPUT_SIZE; slot++) {
          if(basin.hasInput(slot)) {
            final ItemStack stack = basin.takeInput(slot, player);
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            return ActionResultType.SUCCESS;
          }
        }

        return ActionResultType.SUCCESS;
      }

      // Take stuff out
      if(basin.hasOutput()) {
        final ItemStack stack = basin.takeOutput();
        ItemHandlerHelper.giveItemToPlayer(player, stack);
        return ActionResultType.SUCCESS;
      }

      final ItemStack held = player.getHeldItem(hand);

      // Put stuff in
      if(!held.isEmpty()) {
        final ItemStack remaining = basin.insertItem(held.copy(), player);

        if(remaining.getCount() != held.getCount()) {
          world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
        }

        if(!player.isCreative()) {
          player.setHeldItem(hand, remaining);
        }

        return ActionResultType.SUCCESS;
      }

      return basin.mix(state, world, pos, player, hand, hit);
    }

    return ActionResultType.SUCCESS;
  }

  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    if(state.getBlock() != newState.getBlock()) {
      WorldUtils.dropInventory(world, pos);
      super.onReplaced(state, world, pos, newState, isMoving);
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

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, HAS_WATER);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }
}
