package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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

  private BlockPos primary;

  public ToolStationSecondaryTile() {
    super(GradientTileEntities.TOOL_STATION_SECONDARY.get());
  }

  public BlockPos getPrimary() {
    return this.primary;
  }

  public void setPrimary(final BlockPos primary) {
    this.primary = primary;
    this.markDirty();
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("Primary", NBTUtil.writeBlockPos(this.primary));
    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.primary = NBTUtil.readBlockPos(compound.getCompound("Primary"));
    super.read(compound);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    this.read(pkt.getNbtCompound());
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    final ToolStationTile primary = WorldUtils.getTileEntity(this.world, this.primary, ToolStationTile.class);

    if(primary != null) {
      return primary.getCapability(capability, facing);
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public ITextComponent getDisplayName() {
    return GradientBlocks.TOOL_STATION.get().getNameTextComponent();
  }

  @Override
  public Container createMenu(final int id, final PlayerInventory playerInv, final PlayerEntity player) {
    final ToolStationTile primary = WorldUtils.getTileEntity(this.world, this.primary, ToolStationTile.class);

    if(primary != null) {
      return new ToolStationContainer(id, playerInv, primary);
    }

    return null;
  }
}
