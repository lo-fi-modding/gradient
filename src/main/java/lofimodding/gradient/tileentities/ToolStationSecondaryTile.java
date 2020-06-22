package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.ToolStationBlock;
import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ToolStationSecondaryTile extends TileEntity implements INamedContainerProvider {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  public ToolStationSecondaryTile() {
    super(GradientTileEntities.TOOL_STATION_SECONDARY.get());
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    final BlockPos primary = WorldUtils.findControllerBlock(this.pos, pos -> this.world.getBlockState(pos).getBlock() == GradientBlocks.TOOL_STATION.get(), pos -> this.world.getBlockState(pos).get(ToolStationBlock.PRIMARY));

    if(primary != BlockPos.ZERO) {
      final ToolStationTile tile = WorldUtils.getTileEntity(this.world, primary, ToolStationTile.class);

      if(tile != null) {
        return tile.getCapability(capability, facing);
      }
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public ITextComponent getDisplayName() {
    return GradientBlocks.TOOL_STATION.get().getNameTextComponent();
  }

  @Override
  public Container createMenu(final int id, final PlayerInventory playerInv, final PlayerEntity player) {
    final BlockPos primary = WorldUtils.findControllerBlock(this.pos, pos -> this.world.getBlockState(pos).getBlock() == GradientBlocks.TOOL_STATION.get(), pos -> this.world.getBlockState(pos).get(ToolStationBlock.PRIMARY));

    if(primary != BlockPos.ZERO) {
      final ToolStationTile tile = WorldUtils.getTileEntity(this.world, primary, ToolStationTile.class);

      if(tile != null) {
        return new ToolStationContainer(id, playerInv, tile);
      }
    }

    return null;
  }
}
