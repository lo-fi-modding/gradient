package lofimodding.gradient.utils;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

public final class WorldUtils {
  private WorldUtils() { }

  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  public static void dropInventory(final World world, final BlockPos pos, final int firstSlot) {
    final TileEntity te = world.getTileEntity(pos);

    if(te != null) {
      te.getCapability(ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
        for(int i = firstSlot; i < inv.getSlots(); i++) {
          final ItemStack stack = inv.getStackInSlot(i);

          if(!stack.isEmpty()) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
          }
        }
      });
    }
  }

  public static void dropInventory(final World world, final BlockPos pos) {
    dropInventory(world, pos, 0);
  }
}
