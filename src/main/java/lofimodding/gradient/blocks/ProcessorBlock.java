package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.tileentities.pieces.IEnergySource;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ProcessorBlock<Energy extends IEnergySource<Energy>, Tile extends ProcessorTile<Energy>> extends Block {
  private final Class<Tile> cls;
  private final ProcessorTier tier;

  protected ProcessorBlock(final Class<Tile> cls, final ProcessorTier tier, final Properties properties) {
    super(properties);
    this.cls = cls;
    this.tier = tier;
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent("gradient.processor.tooltip.1", this.tier.getLocalizedName(), this.tier.getTier()));
    tooltip.add(new TranslationTextComponent("gradient.processor.tooltip.2"));
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
    if(state.getBlock() != newState.getBlock()) {
      WorldUtils.dropInventory(world, pos);
      super.onReplaced(state, world, pos, newState, isMoving);
    }
  }
  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbor, final boolean isMoving) {
    super.neighborChanged(state, world, pos, block, neighbor, isMoving);

    final ProcessorTile<?> te = WorldUtils.getTileEntity(world, pos, ProcessorTile.class);

    if(te != null) {
      te.neighborChanged(state, world, pos, block, neighbor, isMoving);
    }
  }
}
