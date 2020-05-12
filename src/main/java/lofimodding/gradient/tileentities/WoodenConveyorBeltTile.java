package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class WoodenConveyorBeltTile extends TileEntity {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final Map<WoodenConveyorBeltDriverTile, Direction> drivers = new HashMap<>();

  private final IItemHandler inv = new DummyItemHandler();
  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inv);

  public WoodenConveyorBeltTile() {
    super(GradientTileEntities.WOODEN_CONVEYOR_BELT.get());
  }

  public void addDriver(final WoodenConveyorBeltDriverTile driver, final Direction side) {
    this.drivers.put(driver, side);
  }

  public void removeDriver(final WoodenConveyorBeltDriverTile driver) {
    this.drivers.remove(driver);
  }

  public Map<WoodenConveyorBeltDriverTile, Direction> getDrivers() {
    return this.drivers;
  }

  public void onRemove() {
    for(final Map.Entry<WoodenConveyorBeltDriverTile, Direction> entry : this.drivers.entrySet()) {
      final WoodenConveyorBeltDriverTile driver = entry.getKey();
      final Direction driverSide = entry.getValue();

      driver.removeBelt(driverSide);
      driver.addBelt(driverSide);
    }
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == ITEM_HANDLER_CAPABILITY) {
      return this.lazyInv.cast();
    }

    return super.getCapability(capability, facing);
  }

  private class DummyItemHandler implements IItemHandler {
    @Override
    public int getSlots() {
      return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(final int slot) {
      return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate) {
      final World world = WoodenConveyorBeltTile.this.world;

      if(!world.isRemote && !simulate) {
        final BlockPos pos = WoodenConveyorBeltTile.this.pos;
        final ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5f, stack.copy());
        entity.setMotion(0.0d, 0.0d, 0.0d);
        world.addEntity(entity);
      }

      return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(final int slot) {
      return 64;
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return true;
    }
  }
}
