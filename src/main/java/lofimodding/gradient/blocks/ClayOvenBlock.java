package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientMaterials;
import lofimodding.gradient.tileentities.ClayOvenTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ClayOvenBlock extends HeatSinkerBlock {
  private static final VoxelShape SHAPE = makeCuboidShape(2.0d, 0.0d, 2.0d, 14.0d, 6.0d, 14.0d);

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public ClayOvenBlock() {
    super(Properties.create(GradientMaterials.CLAY_MACHINE).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public ClayOvenTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new ClayOvenTile();
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult trace) {
    if(!world.isRemote) {
      final ClayOvenTile oven = WorldUtils.getTileEntity(world, pos, ClayOvenTile.class);

      if(oven == null) {
        return ActionResultType.PASS;
      }

      // Remove input
      if(player.isSneaking()) {
        if(oven.hasInput()) {
          final ItemStack input = oven.takeInput();
          ItemHandlerHelper.giveItemToPlayer(player, input);
          return ActionResultType.SUCCESS;
        }

        return ActionResultType.SUCCESS;
      }

      // Take stuff out
      if(oven.hasOutput()) {
        final ItemStack output = oven.takeOutput();
        ItemHandlerHelper.giveItemToPlayer(player, output);
        return ActionResultType.SUCCESS;
      }

      final ItemStack held = player.getHeldItem(hand);

      // Put stuff in
      if(!held.isEmpty()) {
        final ItemStack remaining = oven.insertItem(held.copy(), player);

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
      WorldUtils.dropInventory(world, pos, ClayOvenTile.FIRST_INPUT_SLOT);
    }

    super.onReplaced(state, world, pos, newState, isMoving);
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
    super.fillStateContainer(builder);
    builder.add(FACING);
  }
}
