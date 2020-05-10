package lofimodding.gradient.utils;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public final class WorldUtils {
  private WorldUtils() { }

  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  @Nullable
  public static <T> T getTileEntity(final IBlockReader world, final BlockPos pos, final Class<T> cls) {
    final TileEntity te = world.getTileEntity(pos);
    return cls.isInstance(te) ? cls.cast(te) : null;
  }

  /**
   * Gets the facing of <tt>origin</tt> that points towards <tt>other</tt>
   */
  public static Direction getFacingTowards(final BlockPos origin, final BlockPos other) {
    return Direction.getFacingFromVector(other.getX() - origin.getX(), other.getY() - origin.getY(), other.getZ() - origin.getZ());
  }

  @Nullable
  public static Direction areBlocksAdjacent(final BlockPos a, final BlockPos b) {
    if(a.getX() == b.getX() && a.getY() == b.getY()) {
      if(a.getZ() == b.getZ() + 1 || a.getZ() == b.getZ() - 1) {
        return getFacingTowards(a, b);
      }
    }

    if(a.getX() == b.getX() && a.getZ() == b.getZ()) {
      if(a.getY() == b.getY() + 1 || a.getY() == b.getY() - 1) {
        return getFacingTowards(a, b);
      }
    }

    if(a.getY() == b.getY() && a.getZ() == b.getZ()) {
      if(a.getX() == b.getX() + 1 || a.getX() == b.getX() - 1) {
        return getFacingTowards(a, b);
      }
    }

    return null;
  }

  private static final int NUM_FACING_BITS = 3;
  private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
  private static final int NUM_Z_BITS = NUM_X_BITS;
  private static final int NUM_Y_BITS = Long.SIZE - NUM_X_BITS - NUM_Z_BITS - NUM_FACING_BITS;
  private static final int X_SHIFT = NUM_Z_BITS;
  private static final int Y_SHIFT = X_SHIFT + NUM_X_BITS;
  private static final int FACING_SHIFT = Long.SIZE - NUM_FACING_BITS;
  private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
  private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
  private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

  public static long serializeBlockPosAndFacing(final BlockPos pos, final Direction facing) {
    return (pos.getX() & X_MASK) << X_SHIFT | (pos.getY() & Y_MASK) << Y_SHIFT | pos.getZ() & Z_MASK | (long)facing.getIndex() << FACING_SHIFT;
  }

  public static BlockPos getBlockPosFromSerialized(final long serialized) {
    final int x = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
    final int y = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >>> 64 - NUM_Y_BITS);
    final int z = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
    return new BlockPos(x, y, z);
  }

  public static Direction getFacingFromSerialized(final long serialized) {
    return Direction.byIndex((int)(serialized >>> FACING_SHIFT));
  }

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

  public static BlockState copyStateProperties(final BlockState copyFrom, final BlockState copyTo) {
    BlockState newState = copyTo;

    for(final Map.Entry<IProperty<?>, Comparable<?>> entry : copyFrom.getValues().entrySet()) {
      final IProperty<?> property = entry.getKey();

      if(newState.has(property)) {
        newState = setValueHelper(newState, property, getName(property, entry.getValue()));
      }
    }

    return newState;
  }

  // Avoids generics issues
  private static <S extends IStateHolder<S>, T extends Comparable<T>> S setValueHelper(final S state, final IProperty<T> property, final String value) {
    final Optional<T> optional = property.parseValue(value);
    return optional.map(t -> state.with(property, t)).orElse(state);
  }

  // Avoids generics issues
  private static <T extends Comparable<T>> String getName(final IProperty<T> property, final Comparable<?> value) {
    return property.getName((T)value);
  }
}
