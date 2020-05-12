package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.WoodenCrankTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WoodenCrankBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public WoodenCrankBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);

    final WoodenCrankTile crank = WorldUtils.getTileEntity(world, pos, WoodenCrankTile.class);

    if(crank != null) {
      crank.remove();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult trace) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    final WoodenCrankTile crank = WorldUtils.getTileEntity(world, pos, WoodenCrankTile.class);

    if(crank == null) {
      return ActionResultType.SUCCESS;
    }

    boolean leashed = false;
    for(final AbstractHorseEntity horse : world.getEntitiesWithinAABB(AbstractHorseEntity.class, new AxisAlignedBB(pos.getX() - 15.0d, pos.getY() - 15.0d, pos.getZ() - 15.0d, pos.getX() + 15.0d, pos.getY() + 15.0d, pos.getZ() + 15.0d))) {
      if(horse.getLeashed() && horse.getLeashHolder() == player) {
        crank.attachWorker(horse);
        leashed = true;
      }
    }

    if(leashed) {
      return ActionResultType.SUCCESS;
    }

    if(crank.hasWorker()) {
      crank.detachWorkers(player);
      return ActionResultType.SUCCESS;
    }

    crank.crank();
    return ActionResultType.SUCCESS;
  }

  @Override
  public WoodenCrankTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenCrankTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    return super.getStateForPlacement(context).with(FACING, context.getFace().getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
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
