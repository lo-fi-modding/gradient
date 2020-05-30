package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.containers.WoodenHopperContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WoodenHopperTile extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
  public static final int SLOTS = 3;

  private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
  private int transferCooldown = -1;
  private long tickedGameTime;

  public WoodenHopperTile() {
    super(GradientTileEntities.WOODEN_HOPPER.get());
  }

  @Override
  public void read(final CompoundNBT compound) {
    super.read(compound);
    this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
    if(!this.checkLootAndRead(compound)) {
      ItemStackHelper.loadAllItems(compound, this.inventory);
    }

    this.transferCooldown = compound.getInt("TransferCooldown");
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    super.write(compound);
    if(!this.checkLootAndWrite(compound)) {
      ItemStackHelper.saveAllItems(compound, this.inventory);
    }

    compound.putInt("TransferCooldown", this.transferCooldown);
    return compound;
  }

  /**
   * Returns the number of slots in the inventory.
   */
  @Override
  public int getSizeInventory() {
    return this.inventory.size();
  }

  /**
   * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
   */
  @Override
  public ItemStack decrStackSize(final int index, final int count) {
    this.fillWithLoot(null);
    return ItemStackHelper.getAndSplit(this.getItems(), index, count);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  @Override
  public void setInventorySlotContents(final int index, final ItemStack stack) {
    this.fillWithLoot(null);
    this.getItems().set(index, stack);
    if(stack.getCount() > this.getInventoryStackLimit()) {
      stack.setCount(this.getInventoryStackLimit());
    }

  }

  @Override
  protected ITextComponent getDefaultName() {
    return new TranslationTextComponent("container.hopper");
  }

  @Override
  public void tick() {
    if(this.world != null && !this.world.isRemote) {
      --this.transferCooldown;
      this.tickedGameTime = this.world.getGameTime();
      if(!this.isOnTransferCooldown()) {
        this.setTransferCooldown(0);
        this.updateHopper(() -> pullItems(this));
      }

    }
  }

  private boolean updateHopper(final Supplier<Boolean> p_200109_1_) {
    if(this.world != null && !this.world.isRemote) {
      if(!this.isOnTransferCooldown() && this.getBlockState().get(HopperBlock.ENABLED)) {
        boolean flag = false;
        if(!this.isEmpty()) {
          flag = this.transferItemsOut();
        }

        if(!this.isFull()) {
          flag |= p_200109_1_.get();
        }

        if(flag) {
          this.setTransferCooldown(8);
          this.markDirty();
          return true;
        }
      }

      return false;
    }
    return false;
  }

  private boolean isFull() {
    for(final ItemStack itemstack : this.inventory) {
      if(itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
        return false;
      }
    }

    return true;
  }

  private boolean transferItemsOut() {
    if(insertHook(this)) {
      return true;
    }
    final IInventory iinventory = this.getInventoryForHopperTransfer();
    if(iinventory == null) {
      return false;
    }
    final Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
    if(this.isInventoryFull(iinventory, direction)) {
      return false;
    }
    for(int i = 0; i < this.getSizeInventory(); ++i) {
      if(!this.getStackInSlot(i).isEmpty()) {
        final ItemStack itemstack = this.getStackInSlot(i).copy();
        final ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, this.decrStackSize(i, 1), direction);
        if(itemstack1.isEmpty()) {
          iinventory.markDirty();
          return true;
        }

        this.setInventorySlotContents(i, itemstack);
      }
    }

    return false;
  }

  private static IntStream func_213972_a(final IInventory p_213972_0_, final Direction p_213972_1_) {
    return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory)p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
  }

  /**
   * Returns false if the inventory has any room to place items in
   */
  private boolean isInventoryFull(final IInventory inventoryIn, final Direction side) {
    return func_213972_a(inventoryIn, side).allMatch(p_213970_1_ -> {
      final ItemStack itemstack = inventoryIn.getStackInSlot(p_213970_1_);
      return itemstack.getCount() >= itemstack.getMaxStackSize();
    });
  }

  /**
   * Returns false if the specified IInventory contains any items
   */
  private static boolean isInventoryEmpty(final IInventory inventoryIn, final Direction side) {
    return func_213972_a(inventoryIn, side).allMatch(p_213973_1_ -> inventoryIn.getStackInSlot(p_213973_1_).isEmpty());
  }

  /**
   * Pull dropped {@link ItemEntity EntityItem}s from the world above the hopper and items
   * from any inventory attached to this hopper into the hopper's inventory.
   *
   * @param hopper the hopper in question
   *
   * @return whether any items were successfully added to the hopper
   */
  public static boolean pullItems(final IHopper hopper) {
    final Boolean ret = VanillaInventoryCodeHooks.extractHook(hopper);
    if(ret != null) {
      return ret;
    }
    final IInventory iinventory = getSourceInventory(hopper);
    if(iinventory != null) {
      final Direction direction = Direction.DOWN;
      return !isInventoryEmpty(iinventory, direction) && func_213972_a(iinventory, direction).anyMatch(p_213971_3_ -> pullItemFromSlot(hopper, iinventory, p_213971_3_, direction));
    }
    for(final ItemEntity itementity : getCaptureItems(hopper)) {
      if(captureItem(hopper, itementity)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if the
   * entire stack was moved
   */
  private static boolean pullItemFromSlot(final IHopper hopper, final IInventory inventoryIn, final int index, final Direction direction) {
    final ItemStack itemstack = inventoryIn.getStackInSlot(index);
    if(!itemstack.isEmpty() && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
      final ItemStack itemstack1 = itemstack.copy();
      final ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), null);
      if(itemstack2.isEmpty()) {
        inventoryIn.markDirty();
        return true;
      }

      inventoryIn.setInventorySlotContents(index, itemstack1);
    }

    return false;
  }

  public static boolean captureItem(final IInventory p_200114_0_, final ItemEntity p_200114_1_) {
    boolean flag = false;
    final ItemStack itemstack = p_200114_1_.getItem().copy();
    final ItemStack itemstack1 = putStackInInventoryAllSlots(null, p_200114_0_, itemstack, null);
    if(itemstack1.isEmpty()) {
      flag = true;
      p_200114_1_.remove();
    } else {
      p_200114_1_.setItem(itemstack1);
    }

    return flag;
  }

  /**
   * Attempts to place the passed stack in the inventory, using as many slots as required. Returns leftover items
   */
  public static ItemStack putStackInInventoryAllSlots(@Nullable final IInventory source, final IInventory destination, ItemStack stack, @Nullable final Direction direction) {
    if(destination instanceof ISidedInventory && direction != null) {
      final ISidedInventory isidedinventory = (ISidedInventory)destination;
      final int[] aint = isidedinventory.getSlotsForFace(direction);

      for(int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
        stack = insertStack(source, destination, stack, aint[k], direction);
      }
    } else {
      final int i = destination.getSizeInventory();

      for(int j = 0; j < i && !stack.isEmpty(); ++j) {
        stack = insertStack(source, destination, stack, j, direction);
      }
    }

    return stack;
  }

  /**
   * Can this hopper insert the specified item from the specified slot on the specified side?
   */
  private static boolean canInsertItemInSlot(final IInventory inventoryIn, final ItemStack stack, final int index, @Nullable final Direction side) {
    if(!inventoryIn.isItemValidForSlot(index, stack)) {
      return false;
    }
    return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
  }

  /**
   * Can this hopper extract the specified item from the specified slot on the specified side?
   */
  private static boolean canExtractItemFromSlot(final IInventory inventoryIn, final ItemStack stack, final int index, final Direction side) {
    return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
  }

  /**
   * Insert the specified stack to the specified inventory and return any leftover items
   */
  private static ItemStack insertStack(@Nullable final IInventory source, final IInventory destination, ItemStack stack, final int index, @Nullable final Direction direction) {
    final ItemStack itemstack = destination.getStackInSlot(index);
    if(canInsertItemInSlot(destination, stack, index, direction)) {
      boolean flag = false;
      final boolean flag1 = destination.isEmpty();
      if(itemstack.isEmpty()) {
        destination.setInventorySlotContents(index, stack);
        stack = ItemStack.EMPTY;
        flag = true;
      } else if(canCombine(itemstack, stack)) {
        final int i = stack.getMaxStackSize() - itemstack.getCount();
        final int j = Math.min(stack.getCount(), i);
        stack.shrink(j);
        itemstack.grow(j);
        flag = j > 0;
      }

      if(flag) {
        if(flag1 && destination instanceof WoodenHopperTile) {
          final WoodenHopperTile hoppertileentity1 = (WoodenHopperTile)destination;
          if(!hoppertileentity1.mayTransfer()) {
            int k = 0;
            if(source instanceof WoodenHopperTile) {
              final WoodenHopperTile hoppertileentity = (WoodenHopperTile)source;
              if(hoppertileentity1.tickedGameTime >= hoppertileentity.tickedGameTime) {
                k = 1;
              }
            }

            hoppertileentity1.setTransferCooldown(8 - k);
          }
        }

        destination.markDirty();
      }
    }

    return stack;
  }

  /**
   * Returns the IInventory that this hopper is pointing into
   */
  @Nullable
  private IInventory getInventoryForHopperTransfer() {
    final Direction direction = this.getBlockState().get(HopperBlock.FACING);
    return getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
  }

  /**
   * Gets the inventory that the provided hopper will transfer items from.
   */
  @Nullable
  public static IInventory getSourceInventory(final IHopper hopper) {
    return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
  }

  public static List<ItemEntity> getCaptureItems(final IHopper hopper) {
    return hopper.getCollectionArea().toBoundingBoxList().stream().flatMap(p_200110_1_ -> hopper.getWorld().getEntitiesWithinAABB(ItemEntity.class, p_200110_1_.offset(hopper.getXPos() - 0.5D, hopper.getYPos() - 0.5D, hopper.getZPos() - 0.5D), EntityPredicates.IS_ALIVE).stream()).collect(Collectors.toList());
  }

  @Nullable
  public static IInventory getInventoryAtPosition(final World world, final BlockPos pos) {
    return getInventoryAtPosition(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
  }

  /**
   * Returns the IInventory (if applicable) of the TileEntity at the specified position
   */
  @Nullable
  public static IInventory getInventoryAtPosition(final World world, final double x, final double y, final double z) {
    IInventory iinventory = null;
    final BlockPos blockpos = new BlockPos(x, y, z);
    final BlockState blockstate = world.getBlockState(blockpos);
    final Block block = blockstate.getBlock();
    if(block instanceof ISidedInventoryProvider) {
      iinventory = ((ISidedInventoryProvider)block).createInventory(blockstate, world, blockpos);
    } else if(blockstate.hasTileEntity()) {
      final TileEntity tileentity = world.getTileEntity(blockpos);
      if(tileentity instanceof IInventory) {
        iinventory = (IInventory)tileentity;
        if(iinventory instanceof ChestTileEntity && block instanceof ChestBlock) {
          iinventory = ChestBlock.func_226916_a_((ChestBlock)block, blockstate, world, blockpos, true);
        }
      }
    }

    if(iinventory == null) {
      final List<Entity> list = world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.HAS_INVENTORY);
      if(!list.isEmpty()) {
        iinventory = (IInventory)list.get(world.rand.nextInt(list.size()));
      }
    }

    return iinventory;
  }

  private static boolean canCombine(final ItemStack stack1, final ItemStack stack2) {
    if(stack1.getItem() != stack2.getItem()) {
      return false;
    }
    if(stack1.getDamage() != stack2.getDamage()) {
      return false;
    }
    if(stack1.getCount() > stack1.getMaxStackSize()) {
      return false;
    }
    return ItemStack.areItemStackTagsEqual(stack1, stack2);
  }

  /**
   * Gets the world X position for this hopper entity.
   */
  @Override
  public double getXPos() {
    return this.pos.getX() + 0.5D;
  }

  /**
   * Gets the world Y position for this hopper entity.
   */
  @Override
  public double getYPos() {
    return this.pos.getY() + 0.5D;
  }

  /**
   * Gets the world Z position for this hopper entity.
   */
  @Override
  public double getZPos() {
    return this.pos.getZ() + 0.5D;
  }

  public void setTransferCooldown(final int ticks) {
    this.transferCooldown = ticks;
  }

  private boolean isOnTransferCooldown() {
    return this.transferCooldown > 0;
  }

  public boolean mayTransfer() {
    return this.transferCooldown > 8;
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.inventory;
  }

  @Override
  protected void setItems(final NonNullList<ItemStack> items) {
    this.inventory = items;
  }

  public void onEntityCollision(final Entity entity) {
    if(entity instanceof ItemEntity) {
      final BlockPos blockpos = this.getPos();
      if(VoxelShapes.compare(VoxelShapes.create(entity.getBoundingBox().offset(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())), this.getCollectionArea(), IBooleanFunction.AND)) {
        this.updateHopper(() -> captureItem(this, (ItemEntity)entity));
      }
    }
  }

  @Override
  protected Container createMenu(final int id, final PlayerInventory player) {
    return new WoodenHopperContainer(id, player, this);
  }

  @Override
  protected IItemHandler createUnSidedHandler() {
    return new VanillaHopperItemHandler(this);
  }

  public long getLastUpdateTime() {
    return this.tickedGameTime;
  }

  public static boolean insertHook(final WoodenHopperTile hopper) {
    final Direction hopperFacing = hopper.getBlockState().get(HopperBlock.FACING);
    return getItemHandler(hopper, hopperFacing).map(destinationResult -> {
      final IItemHandler itemHandler = destinationResult.getKey();
      final Object destination = destinationResult.getValue();
      if(isFull(itemHandler)) {
        return false;
      }

      for(int i = 0; i < hopper.getSizeInventory(); ++i) {
        if(!hopper.getStackInSlot(i).isEmpty()) {
          final ItemStack originalSlotContents = hopper.getStackInSlot(i).copy();
          final ItemStack insertStack = hopper.decrStackSize(i, 1);
          final ItemStack remainder = putStackInInventoryAllSlots(hopper, destination, itemHandler, insertStack);

          if(remainder.isEmpty()) {
            return true;
          }

          hopper.setInventorySlotContents(i, originalSlotContents);
        }
      }

      return false;
    }).orElse(false);
  }

  private static LazyOptional<Pair<IItemHandler, Object>> getItemHandler(final IHopper hopper, final Direction hopperFacing) {
    final double x = hopper.getXPos() + hopperFacing.getXOffset();
    final double y = hopper.getYPos() + hopperFacing.getYOffset();
    final double z = hopper.getZPos() + hopperFacing.getZOffset();
    return VanillaInventoryCodeHooks.getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
  }

  private static boolean isFull(final IItemHandler itemHandler) {
    for(int slot = 0; slot < itemHandler.getSlots(); slot++) {
      final ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
      if(stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isEmpty(final IItemHandler itemHandler) {
    for(int slot = 0; slot < itemHandler.getSlots(); slot++) {
      final ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
      if(stackInSlot.getCount() > 0) {
        return false;
      }
    }
    return true;
  }

  private static ItemStack putStackInInventoryAllSlots(final TileEntity source, final Object destination, final IItemHandler destInventory, ItemStack stack) {
    for(int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++) {
      stack = insertStack(source, destination, destInventory, stack, slot);
    }
    return stack;
  }

  private static ItemStack insertStack(final TileEntity source, final Object destination, final IItemHandler destInventory, ItemStack stack, final int slot) {
    final ItemStack itemstack = destInventory.getStackInSlot(slot);

    if(destInventory.insertItem(slot, stack, true).isEmpty()) {
      boolean insertedItem = false;
      final boolean inventoryWasEmpty = isEmpty(destInventory);

      if(itemstack.isEmpty()) {
        destInventory.insertItem(slot, stack, false);
        stack = ItemStack.EMPTY;
        insertedItem = true;
      } else if(ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
        final int originalSize = stack.getCount();
        stack = destInventory.insertItem(slot, stack, false);
        insertedItem = originalSize < stack.getCount();
      }

      if(insertedItem) {
        if(inventoryWasEmpty && destination instanceof WoodenHopperTile) {
          final WoodenHopperTile destinationHopper = (WoodenHopperTile)destination;

          if(!destinationHopper.mayTransfer()) {
            int k = 0;
            if(source instanceof WoodenHopperTile) {
              if(destinationHopper.getLastUpdateTime() >= ((WoodenHopperTile)source).getLastUpdateTime()) {
                k = 1;
              }
            }
            destinationHopper.setTransferCooldown(8 - k);
          }
        }
      }
    }

    return stack;
  }

  public static class VanillaHopperItemHandler extends InvWrapper {
    private final WoodenHopperTile hopper;

    public VanillaHopperItemHandler(final WoodenHopperTile hopper) {
      super(hopper);
      this.hopper = hopper;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(final int slot, @Nonnull ItemStack stack, final boolean simulate) {
      if(simulate) {
        return super.insertItem(slot, stack, simulate);
      }

      final boolean wasEmpty = this.getInv().isEmpty();

      final int originalStackSize = stack.getCount();
      stack = super.insertItem(slot, stack, simulate);

      if(wasEmpty && originalStackSize > stack.getCount()) {
        if(!this.hopper.mayTransfer()) {
          // This cooldown is always set to 8 in vanilla with one exception:
          // Hopper -> Hopper transfer sets this cooldown to 7 when this hopper
          // has not been updated as recently as the one pushing items into it.
          // This vanilla behavior is preserved by VanillaInventoryCodeHooks#insertStack,
          // the cooldown is set properly by the hopper that is pushing items into this one.
          this.hopper.setTransferCooldown(8);
        }
      }

      return stack;
    }
  }
}
