package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientMaterials;
import lofimodding.gradient.tileentities.ClayMetalMixerTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ClayMetalMixerBlock extends HeatSinkerBlock {
  @CapabilityInject(IFluidHandler.class)
  private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY;

  private static final VoxelShape SHAPE = makeCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d);

  public static final Map<Direction, IProperty<Boolean>> CONNECTED = new EnumMap<>(Direction.class);

  static {
    CONNECTED.put(Direction.NORTH, BooleanProperty.create("connected_north"));
    CONNECTED.put(Direction.SOUTH, BooleanProperty.create("connected_south"));
    CONNECTED.put(Direction.WEST, BooleanProperty.create("connected_west"));
    CONNECTED.put(Direction.EAST, BooleanProperty.create("connected_east"));
  }

  public ClayMetalMixerBlock() {
    super(Properties.create(GradientMaterials.CLAY_MACHINE).hardnessAndResistance(1.0f, 5.0f).notSolid());
    this.setDefaultState(this.stateContainer.getBaseState().with(CONNECTED.get(Direction.NORTH), Boolean.FALSE).with(CONNECTED.get(Direction.SOUTH), Boolean.FALSE).with(CONNECTED.get(Direction.WEST), Boolean.FALSE).with(CONNECTED.get(Direction.EAST), Boolean.FALSE));
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new ClayMetalMixerTile();
  }

  @Override
  protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(CONNECTED.get(Direction.NORTH), CONNECTED.get(Direction.SOUTH), CONNECTED.get(Direction.WEST), CONNECTED.get(Direction.EAST));
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

  @Nullable
  @Override
  public BlockState getStateForPlacement(final BlockItemUseContext context) {
    final World world = context.getWorld();
    final BlockPos pos = context.getPos();

    return super.getStateForPlacement(context)
      .with(CONNECTED.get(Direction.NORTH), this.getFluidHandler(world, pos.offset(Direction.NORTH)) != null)
      .with(CONNECTED.get(Direction.SOUTH), this.getFluidHandler(world, pos.offset(Direction.SOUTH)) != null)
      .with(CONNECTED.get(Direction.WEST),  this.getFluidHandler(world, pos.offset(Direction.WEST))  != null)
      .with(CONNECTED.get(Direction.EAST),  this.getFluidHandler(world, pos.offset(Direction.EAST))  != null);
  }

  @Override
  public void onNeighborChange(final BlockState state, final IWorldReader world, final BlockPos pos, final BlockPos neighbor) {
    super.onNeighborChange(state, world, pos, neighbor);

    if(world instanceof World && ((World)world).isRemote) {
      return;
    }

    final ClayMetalMixerTile mixer = WorldUtils.getTileEntity(world, pos, ClayMetalMixerTile.class);

    if(mixer == null) {
      return;
    }

    final Direction side = WorldUtils.getFacingTowards(pos, neighbor);

    if(side.getAxis().isHorizontal()) {
      mixer.inputUpdated(side);
    } else if(side == Direction.DOWN) {
      mixer.outputUpdated();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbor, final boolean isMoving) {
    super.neighborChanged(state, world, pos, block, neighbor, isMoving);

    final ClayMetalMixerTile mixer = WorldUtils.getTileEntity(world, pos, ClayMetalMixerTile.class);

    if(mixer == null) {
      return;
    }

    // Output
    if(neighbor.equals(pos.down())) {
      mixer.outputChanged(this.getFluidHandler(world, neighbor));
      return;
    }

    // Inputs
    final Direction side = WorldUtils.getFacingTowards(pos, neighbor);

    if(side == Direction.UP) {
      return;
    }

    mixer.inputChanged(side, this.getFluidHandler(world, neighbor));

    if(mixer.isConnected(side) != state.get(CONNECTED.get(side))) {
      world.setBlockState(pos, state.with(CONNECTED.get(side), mixer.isConnected(side)));
      mixer.validate();
      world.setTileEntity(pos, mixer);
    }
  }

  @Nullable
  private IFluidHandler getFluidHandler(final IBlockReader world, final BlockPos pos) {
    final TileEntity te = world.getTileEntity(pos);

    if(te == null) {
      return null;
    }

    return te.getCapability(FLUID_HANDLER_CAPABILITY, Direction.UP).orElse(null);
  }
}
