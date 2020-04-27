package lofimodding.gradient.tileentities;

import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class HeatSinkerTile extends TileEntity implements ITickableTileEntity {
  private final Map<BlockPos, HeatSinkerTile> sinks = new HashMap<>();

  private boolean firstTick = true;
  private long lastTick;

  private float heat;

  protected HeatSinkerTile(final TileEntityType<? extends HeatSinkerTile> type) {
    super(type);
  }

  public boolean hasHeat() {
    return this.heat != 0;
  }

  public float getHeat() {
    return this.heat;
  }

  protected void setHeat(final float heat) {
    this.heat = heat;
  }

  protected void addHeat(final float heat) {
    this.heat += heat;
  }

  protected void removeHeat(final float heat) {
    this.heat -= heat;

    if(this.heat < 0.1f) {
      this.heat = 0;
    }
  }

  public void updateSink(final BlockPos pos) {
    final HeatSinkerTile te = WorldUtils.getTileEntity(this.world, pos, HeatSinkerTile.class);

    if(te != null) {
      this.sinks.put(pos, te);
    }
  }

  private void findSurroundingSinks() {
    this.updateSink(this.pos.north());
    this.updateSink(this.pos.south());
    this.updateSink(this.pos.east());
    this.updateSink(this.pos.west());
    this.updateSink(this.pos.up());
    this.updateSink(this.pos.down());
  }

  @Override
  public void tick() {
    if(!this.hasHeat()) {
      return;
    }

    final long now = System.nanoTime();

    if(this.lastTick == 0) {
      this.lastTick = now - 50000000;
    }

    final float tickScale = (now - this.lastTick) / 50000000.0f;
    this.lastTick = now;

    this.tickBeforeCooldown(tickScale);
    this.coolDown(tickScale);
    this.tickAfterCooldown(tickScale);

    if(this.firstTick) {
      this.findSurroundingSinks();
      this.firstTick = false;
    }

    this.transferHeat();
  }

  private void transferHeat() {
    if(this.sinks.isEmpty()) {
      return;
    }

    final Iterator<Map.Entry<BlockPos, HeatSinkerTile>> iterator = this.sinks.entrySet().iterator();

    while(iterator.hasNext()) {
      final Map.Entry<BlockPos, HeatSinkerTile> entry = iterator.next();

      final HeatSinkerTile worldEntity = WorldUtils.getTileEntity(this.world, entry.getKey(), HeatSinkerTile.class);

      if(worldEntity == null) {
        iterator.remove();
        return;
      }

      if(worldEntity != entry.getValue()) {
        entry.setValue(worldEntity);
      }

      final HeatSinkerTile sink = entry.getValue();

      if(sink.getHeat() < this.getHeat()) {
        final float mod = Math.max(1.0f, 5.0f - sink.getHeat() / this.getHeat() * 4);
        final float heat = this.calculateHeatLoss(this.world.getBlockState(this.pos)) * sink.heatTransferEfficiency() * mod / 20.0f;
        this.removeHeat(heat);
        sink.addHeat(heat);
      }
    }
  }

  protected abstract void tickBeforeCooldown(final float tickScale);
  protected abstract void tickAfterCooldown(final float tickScale);

  private void coolDown(final float tickScale) {
    this.removeHeat(this.calculateHeatLoss(this.world.getBlockState(this.pos)) / 20.0f * tickScale);
  }

  protected abstract float calculateHeatLoss(BlockState state);

  /**
   * @return  The percentage of heat that is maintained when this sink absorbs heat. 0.0 = 0%, 1.0 = 100%
   */
  protected abstract float heatTransferEfficiency();

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putFloat("heat", this.getHeat());

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.setHeat(compound.getFloat("heat"));

    super.read(compound);
  }

  protected void sync() {
    if(!this.getWorld().isRemote) {
      final BlockState state = this.getWorld().getBlockState(this.getPos());
      this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
      this.markDirty();
    }
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
}
