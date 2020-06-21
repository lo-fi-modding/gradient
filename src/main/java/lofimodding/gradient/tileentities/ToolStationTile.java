package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.recipes.IToolStationRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ToolStationTile extends TileEntity implements INamedContainerProvider {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final ItemStackHandler recipeInv = new ItemHandler(9) {
    @Override
    public int getSlotLimit(final int slot) {
      return 1;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate) {
      return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      return ItemStack.EMPTY;
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);
      ToolStationTile.this.updateRecipe();
    }
  };

  private final ItemStackHandler outputInv = new ItemHandler(1) {
    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate) {
      return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      // Some recipes output more than one item, but we still only want to do one craft cycle per craft
      // i.e. doors output 3. We don't want to consume the planks 3 times.
      final int outputCount = ToolStationTile.this.getOutput(slot).getCount();
      final int scaledAmount = Math.max(1, amount / outputCount);
      final int newAmount = ToolStationTile.this.getAmountCraftable(scaledAmount);

      if(!simulate) {
        ToolStationTile.this.consumeIngredients(newAmount);
      }

      return super.extractItem(slot, newAmount * outputCount, simulate);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);

      // If any output slots still have something in them, don't try to set the outputs again
      for(int i = 0; i < ToolStationTile.this.mergedOutput.getSlots(); i++) {
        if(!ToolStationTile.this.mergedOutput.getStackInSlot(i).isEmpty()) {
          return;
        }
      }

      ToolStationTile.this.updateOutput();
    }
  };

  private final ItemStackHandler toolsInv = new ItemHandler(3) {
    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      return ItemStack.EMPTY;
    }
  };

  private final ItemStackHandler storageInv = new ItemHandler(9) {
    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      return ItemStack.EMPTY;
    }
  };

  private IItemHandlerModifiable mergedRecipe;
  private IItemHandlerModifiable mergedOutput;
  private IItemHandlerModifiable mergedTools;
  private IItemHandlerModifiable mergedStorage;
  private IItemHandlerModifiable mergedInventory;

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.mergedInventory);

  private IRecipe<CraftingInventory> recipe;
  private int craftingSize = 3;
  private int neighbourCount; //TODO actually resize

  public ToolStationTile() {
    super(GradientTileEntities.TOOL_STATION.get());
  }

  public int getCraftingSize() {
    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    return this.craftingSize;
  }

  public IItemHandler getMergedRecipeInv() {
    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    return this.mergedRecipe;
  }

  public IItemHandler getMergedToolsInv() {
    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    return this.mergedTools;
  }

  public IItemHandler getMergedStorageInv() {
    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    return this.mergedStorage;
  }

  public IItemHandler getMergedOutputInv() {
    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    return this.mergedOutput;
  }

  public boolean hasRecipe() {
    return this.recipe != null;
  }

  public IRecipe<CraftingInventory> getRecipe() {
    return this.recipe;
  }

  public int getAmountCraftable(final int amount) {
    if(this.recipe == null) {
      return 0;
    }

    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    if(this.canFit() && this.hasRequiredTools()) {
      return this.hasRequiredIngredients(amount);
    }

    return 0;
  }

  public boolean canFit() {
    if(this.recipe == null) {
      return false;
    }

    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    if(this.recipe instanceof IToolStationRecipe) {
      if(((IToolStationRecipe)this.recipe).getOutputs().size() > this.mergedOutput.getSlots()) {
        return false;
      }
    }

    return this.recipe.canFit(this.getCraftingSize(), this.getCraftingSize());
  }

  public boolean hasRequiredTools() {
    if(this.recipe == null) {
      return false;
    }

    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    if(this.recipe.getType() == IRecipeType.CRAFTING) {
      return true;
    }

    if(this.recipe.getType() == IToolStationRecipe.TYPE) {
      final IToolStationRecipe recipe = (IToolStationRecipe)this.recipe;

      outer:
      for(final Ingredient tool : recipe.getTools()) {
        for(int slot = 0; slot < this.mergedTools.getSlots(); slot++) {
          if(tool.test(this.mergedTools.getStackInSlot(slot))) {
            continue outer;
          }
        }

        return false;
      }
    }

    return true;
  }

  public int hasRequiredIngredients(final int amount) {
    if(this.recipe == null) {
      return 0;
    }

    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    final IItemHandlerModifiable temp = new ItemStackHandler(this.mergedStorage.getSlots());
    for(int slot = 0; slot < temp.getSlots(); slot++) {
      temp.setStackInSlot(slot, this.mergedStorage.getStackInSlot(slot).copy());
    }

    for(int i = 0; i < amount; i++) {
      outer:
      for(final Ingredient ingredient : this.recipe.getIngredients()) {
        for(int slot = 0; slot < temp.getSlots(); slot++) {
          final ItemStack stack = temp.getStackInSlot(slot);

          if(ingredient.test(stack)) {
            final ItemStack newStack = stack.copy();
            newStack.shrink(1);
            temp.setStackInSlot(slot, newStack);
            continue outer;
          }
        }

        return i;
      }
    }

    return amount;
  }

  private void consumeIngredients(final int amount) {
    for(int i = 0; i < amount; i++) {
      for(final Ingredient ingredient : this.recipe.getIngredients()) {
        for(int slot = 0; slot < this.mergedStorage.getSlots(); slot++) {
          final ItemStack stack = this.mergedStorage.getStackInSlot(slot);

          if(ingredient.test(stack)) {
            final ItemStack newStack = stack.copy();
            newStack.shrink(1);
            this.mergedStorage.setStackInSlot(slot, newStack);
            break;
          }
        }
      }
    }
  }

  private void updateRecipe() {
    this.recipe = this.findRecipe();
    this.updateOutput();
  }

  private boolean reentryProtection;

  private void updateOutput() {
    if(this.reentryProtection) {
      return;
    }

    this.reentryProtection = true;

    if(this.mergedInventory == null) {
      this.updateNeighbours();
    }

    for(int slot = 0; slot < this.mergedOutput.getSlots(); slot++) {
      this.mergedOutput.setStackInSlot(slot, this.getOutput(slot));
    }

    this.reentryProtection = false;
  }

  private ItemStack getOutput(final int slot) {
    if(this.recipe == null) {
      return ItemStack.EMPTY;
    }

    if(this.recipe.getType() == IToolStationRecipe.TYPE) {
      return ((IToolStationRecipe)this.recipe).getOutputs().get(slot);
    }

    // Regular recipes only have one output
    if(slot != 0) {
      return ItemStack.EMPTY;
    }

    //TODO refactor this crafting stuff

    final PlayerEntity player;
    if(this.world.isRemote) {
      player = Minecraft.getInstance().player;
    } else {
      player = FakePlayerFactory.getMinecraft((ServerWorld)this.world);
    }

    final CraftingInventory crafting = new CraftingInventory(new WorkbenchContainer(0, player.inventory), this.getCraftingSize(), this.getCraftingSize());
    for(int i = 0; i < this.mergedRecipe.getSlots(); i++) {
      crafting.setInventorySlotContents(i, this.mergedRecipe.getStackInSlot(i));
    }

    return this.recipe.getCraftingResult(crafting);
  }

  @Nullable
  private IRecipe<CraftingInventory> findRecipe() {
    final IRecipe<CraftingInventory> shapelessToolStation = RecipeUtils.getRecipe(IToolStationRecipe.TYPE, recipe -> recipe.recipeMatches(this.mergedRecipe)).orElse(null);

    if(shapelessToolStation != null) {
      return shapelessToolStation;
    }

    //TODO save what's in the item handlers
    //TODO refactor this crafting stuff

    final PlayerEntity player;
    if(this.world.isRemote) {
      player = Minecraft.getInstance().player;
    } else {
      player = FakePlayerFactory.getMinecraft((ServerWorld)this.world);
    }

    final CraftingInventory crafting = new CraftingInventory(new WorkbenchContainer(0, player.inventory), this.getCraftingSize(), this.getCraftingSize());
    for(int slot = 0; slot < this.mergedRecipe.getSlots(); slot++) {
      crafting.setInventorySlotContents(slot, this.mergedRecipe.getStackInSlot(slot));
    }

    return RecipeUtils.getRecipe(IRecipeType.CRAFTING, recipe -> recipe.matches(crafting, this.world)).orElse(null);
  }

  public void setMain(final ToolStationTile main) {
    if(main == this) {
      this.mergedRecipe = this.recipeInv;
      this.mergedOutput = this.outputInv;
      this.mergedTools = this.toolsInv;
      this.mergedStorage = this.storageInv;
      this.mergedInventory = new CombinedInvWrapper(this.mergedRecipe, this.mergedOutput, this.mergedTools, this.mergedStorage);
    } else {
      this.mergedRecipe = main.mergedRecipe;
      this.mergedOutput = main.mergedOutput;
      this.mergedTools = main.mergedTools;
      this.mergedStorage = main.mergedStorage;
      this.mergedInventory = main.mergedInventory;
    }
  }

  public void updateNeighbours() {
    final List<ToolStationTile> tiles = new ArrayList<>();
    this.addNeighbours(tiles, this);
    tiles.sort(Comparator.comparingLong(value -> value.pos.toLong()));

    final ToolStationTile first = tiles.get(0);

    for(final ToolStationTile tile : tiles) {
      tile.setMain(first);
    }

    this.resize(tiles.size() - 1);
  }

  private void addNeighbours(final List<ToolStationTile> tiles, final ToolStationTile centre) {
    tiles.add(centre);

    for(final Direction direction : Direction.Plane.HORIZONTAL) {
      final ToolStationTile tile = WorldUtils.getTileEntity(centre.world, centre.pos.offset(direction), ToolStationTile.class);

      if(tile != null && !tiles.contains(tile)) {
        this.addNeighbours(tiles, tile);
      }
    }
  }

  private void resize(final int size) {
    final int oldCraftingWidth = this.getCraftingSize();

    this.craftingSize = size + 3;
    this.neighbourCount = size;
    this.resizeHandler(this.recipeInv, this.getCraftingSize() * this.getCraftingSize(), oldCraftingWidth, this.getCraftingSize());
    this.resizeHandler(this.outputInv, 1 + this.neighbourCount, 1, 1);
    this.resizeHandler(this.toolsInv, 3 + this.neighbourCount, 100, 100);
    this.resizeHandler(this.storageInv, 9 * (this.neighbourCount + 1), 9, 9);
  }

  private void resizeHandler(final ItemStackHandler handler, final int newSize, final int oldWidth, final int newWidth) {
    final int oldSize = handler.getSlots();

    final NonNullList<ItemStack> stacks = NonNullList.withSize(newSize, ItemStack.EMPTY);

    for(int slot = 0; slot < oldSize; slot++) {
      final ItemStack stack = handler.getStackInSlot(slot);

      final int oldX = slot % oldWidth;
      final int oldY = slot / oldWidth;
      final int newSlot = oldY * newWidth + oldX;

      if(newSlot < stacks.size()) {
        stacks.set(newSlot, stack);
      } else {
        InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), stack);
      }
    }

    handler.setSize(newSize);

    for(int slot = 0; slot < newSize; slot++) {
      handler.setStackInSlot(slot, stacks.get(slot));
    }
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putInt("CraftingSize", this.craftingSize);
    compound.put("RecipeInv", this.recipeInv.serializeNBT());
    compound.put("OutputInv", this.outputInv.serializeNBT());
    compound.put("ToolsInv", this.toolsInv.serializeNBT());
    compound.put("StorageInv", this.storageInv.serializeNBT());
    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.craftingSize = compound.getInt("CraftingSize");
    this.recipeInv.deserializeNBT(compound.getCompound("RecipeInv"));
    this.outputInv.deserializeNBT(compound.getCompound("OutputInv"));
    this.toolsInv.deserializeNBT(compound.getCompound("ToolsInv"));
    this.storageInv.deserializeNBT(compound.getCompound("StorageInv"));

    if(this.mergedInventory == null) {
      this.setMain(this);
    }

    super.read(compound);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == ITEM_HANDLER_CAPABILITY) {
      if(this.mergedInventory == null) {
        this.updateNeighbours();
      }

      return this.lazyInv.cast();
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public ITextComponent getDisplayName() {
    return GradientBlocks.TOOL_STATION.get().getNameTextComponent();
  }

  @Override
  public Container createMenu(final int id, final PlayerInventory playerInv, final PlayerEntity player) {
    return new ToolStationContainer(id, playerInv, this);
  }

  private class ItemHandler extends ItemStackHandler {
    private ItemHandler(final int size) {
      super(size);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);
      ToolStationTile.this.markDirty();
    }
  }
}
