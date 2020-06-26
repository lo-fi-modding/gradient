package lofimodding.gradient.tileentities;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.capabilities.Tool;
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
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//TODO stages
//TODO weird shift-click stacking

public class ToolStationTile extends TileEntity implements INamedContainerProvider {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  @CapabilityInject(Tool.class)
  private static Capability<Tool> TOOL_CAPABILITY;

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

      if(!ToolStationTile.this.pauseRecipeLookup) {
        ToolStationTile.this.updateRecipe();
      }
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
      final ItemStack recipeOutput = ToolStationTile.this.getOutput(slot);

      if(recipeOutput.isEmpty()) {
        return ItemStack.EMPTY;
      }

      // Some recipes output more than one item, but we still only want to do one craft cycle per craft
      // i.e. doors output 3. We don't want to consume the planks 3 times.
      final int outputCount = recipeOutput.getCount();
      final int scaledAmount = Math.max(1, amount / outputCount);
      final int newAmount = ToolStationTile.this.getAmountCraftable(scaledAmount);

      if(!simulate && !ToolStationTile.this.world.isRemote) { //TODO why does this not work when only run on client? Don't item handlers sync autmoatically?
        ToolStationTile.this.consumeIngredients(newAmount, this.player);
      }

      final ItemStack output = super.extractItem(slot, newAmount * outputCount, simulate).copy();
      output.setCount(newAmount * outputCount);
      return output;
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);

      // If any output slots still have something in them, don't try to set the outputs again
      for(int i = 0; i < ToolStationTile.this.outputInv.getSlots(); i++) {
        if(!ToolStationTile.this.outputInv.getStackInSlot(i).isEmpty()) {
          return;
        }
      }

      ToolStationTile.this.updateOutput();
    }
  };

  private final ItemStackHandler toolsInv = new ItemHandler(3) {
    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return stack.getCapability(TOOL_CAPABILITY).isPresent();
    }

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

  private final IItemHandlerModifiable mergedInventory = new CombinedInvWrapper(this.recipeInv, this.outputInv, this.toolsInv, this.storageInv);
  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.mergedInventory);

  @Nullable
  private IRecipe<CraftingInventory> recipe;
  private int craftingSize = 3;
  private boolean pauseRecipeLookup;

  public ToolStationTile() {
    super(GradientTileEntities.TOOL_STATION.get());
  }

  public int getCraftingSize() {
    return this.craftingSize;
  }

  public IItemHandlerModifiable getRecipeInv() {
    return this.recipeInv;
  }

  public IItemHandlerModifiable getToolsInv() {
    return this.toolsInv;
  }

  public IItemHandlerModifiable getStorageInv() {
    return this.storageInv;
  }

  public IItemHandlerModifiable getOutputInv() {
    return this.outputInv;
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

    if(this.canFit() && this.hasRequiredTools()) {
      return this.hasRequiredIngredients(amount);
    }

    return 0;
  }

  public boolean canFit() {
    if(this.recipe == null) {
      return false;
    }

    if(this.recipe instanceof IToolStationRecipe) {
      if(((IToolStationRecipe)this.recipe).getOutputs().size() > this.outputInv.getSlots()) {
        return false;
      }
    }

    return this.recipe.canFit(this.getCraftingSize(), this.getCraftingSize());
  }

  public boolean hasRequiredTools() {
    if(this.recipe == null) {
      return false;
    }

    if(this.recipe.getType() == IRecipeType.CRAFTING) {
      return true;
    }

    if(this.recipe.getType() == IToolStationRecipe.TYPE) {
      final IToolStationRecipe recipe = (IToolStationRecipe)this.recipe;

      outer:
      for(final ToolType toolType : recipe.getTools()) {
        for(int slot = 0; slot < this.toolsInv.getSlots(); slot++) {
          final ItemStack toolStack = this.toolsInv.getStackInSlot(slot);

          if(toolStack.getCapability(TOOL_CAPABILITY).map(tool -> tool.hasToolType(toolStack, toolType)).orElse(Boolean.FALSE)) {
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

    final IItemHandlerModifiable temp = new ItemStackHandler(this.storageInv.getSlots());
    for(int slot = 0; slot < temp.getSlots(); slot++) {
      temp.setStackInSlot(slot, this.storageInv.getStackInSlot(slot).copy());
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

  private void consumeIngredients(final int amount, final PlayerEntity player) {
    for(int i = 0; i < amount; i++) {
      for(final Ingredient ingredient : this.recipe.getIngredients()) {
        for(int slot = 0; slot < this.storageInv.getSlots(); slot++) {
          final ItemStack stack = this.storageInv.getStackInSlot(slot);

          if(ingredient.test(stack)) {
            final ItemStack newStack = stack.copy();
            newStack.shrink(1);
            this.storageInv.setStackInSlot(slot, newStack);
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

    for(int slot = 0; slot < this.outputInv.getSlots(); slot++) {
      this.outputInv.setStackInSlot(slot, this.getOutput(slot).copy());
    }

    this.reentryProtection = false;
  }

  private ItemStack getOutput(final int slot) {
    if(this.recipe == null) {
      return ItemStack.EMPTY;
    }

    if(this.recipe.getType() == IToolStationRecipe.TYPE) {
      if(slot >= ((IToolStationRecipe)this.recipe).getOutputs().size()) {
        return ItemStack.EMPTY;
      }

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
    for(int i = 0; i < this.recipeInv.getSlots(); i++) {
      crafting.setInventorySlotContents(i, this.recipeInv.getStackInSlot(i));
    }

    return this.recipe.getCraftingResult(crafting);
  }

  @Nullable
  private IRecipe<CraftingInventory> findRecipe() {
    final IRecipe<CraftingInventory> shapelessToolStation = RecipeUtils.getRecipe(IToolStationRecipe.TYPE, recipe -> recipe.recipeMatches(this.recipeInv)).orElse(null);

    if(shapelessToolStation != null) {
      return shapelessToolStation;
    }

    //TODO refactor this crafting stuff

    final PlayerEntity player;
    if(this.world.isRemote) {
      player = Minecraft.getInstance().player;
    } else {
      player = FakePlayerFactory.getMinecraft((ServerWorld)this.world);
    }

    final CraftingInventory crafting = new CraftingInventory(new WorkbenchContainer(0, player.inventory), this.getCraftingSize(), this.getCraftingSize());
    for(int slot = 0; slot < this.recipeInv.getSlots(); slot++) {
      crafting.setInventorySlotContents(slot, this.recipeInv.getStackInSlot(slot));
    }

    return RecipeUtils.getRecipe(IRecipeType.CRAFTING, recipe -> recipe.matches(crafting, this.world)).orElse(null);
  }

  public void updateNeighbours() {
    final NonNullList<BlockPos> tiles = WorldUtils.getBlockCluster(this.pos, pos -> this.world.getBlockState(pos).getBlock() == GradientBlocks.TOOL_STATION.get());
    this.resize(Math.min(3, tiles.size()) - 1);

    this.markDirty();
    WorldUtils.notifyUpdate(this.world, this.pos);
  }

  private void resize(final int size) {
    final int oldCraftingWidth = this.getCraftingSize();

    this.pauseRecipeLookup = true;
    this.craftingSize = size + 3;
    this.resizeHandler(this.recipeInv, this.getCraftingSize() * this.getCraftingSize(), oldCraftingWidth, this.getCraftingSize(), false);
    this.resizeHandler(this.outputInv, 1 + size, 1, 1, false);
    this.resizeHandler(this.toolsInv, 3 + size, 100, 100, true);
    this.resizeHandler(this.storageInv, 9 * (size + 1), 9, 9, true);
    this.pauseRecipeLookup = false;

    this.updateRecipe();
  }

  private void resizeHandler(final ItemStackHandler handler, final int newSize, final int oldWidth, final int newWidth, final boolean dropExtra) {
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
        if(dropExtra) {
          InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), stack);
        }
      }
    }

    handler.setSize(newSize);

    for(int slot = 0; slot < newSize; slot++) {
      handler.setStackInSlot(slot, stacks.get(slot));
    }
  }

  public void addToInv(final IItemHandlerModifiable tools, final IItemHandlerModifiable storage) {
    for(int slot = 0; slot < tools.getSlots(); slot++) {
      final ItemStack remaining = ItemHandlerHelper.insertItem(this.toolsInv, tools.getStackInSlot(slot), false);
      tools.setStackInSlot(slot, remaining);
    }

    for(int slot = 0; slot < storage.getSlots(); slot++) {
      final ItemStack remaining = ItemHandlerHelper.insertItem(this.storageInv, storage.getStackInSlot(slot), false);
      storage.setStackInSlot(slot, remaining);
    }
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.putInt("CraftingSize", this.craftingSize);
    compound.put("RecipeInv", this.recipeInv.serializeNBT());
    compound.put("OutputInv", this.outputInv.serializeNBT());
    compound.put("ToolsInv", this.toolsInv.serializeNBT());
    compound.put("StorageInv", this.storageInv.serializeNBT());

    if(this.hasRecipe()) {
      compound.putString("RecipeId", this.recipe.getId().toString());
    }

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.craftingSize = compound.getInt("CraftingSize");
    this.recipeInv.deserializeNBT(compound.getCompound("RecipeInv"));
    this.outputInv.deserializeNBT(compound.getCompound("OutputInv"));
    this.toolsInv.deserializeNBT(compound.getCompound("ToolsInv"));
    this.storageInv.deserializeNBT(compound.getCompound("StorageInv"));

    if(compound.contains("RecipeId")) {
      final ResourceLocation recipeId = new ResourceLocation(compound.getString("RecipeId"));

      this.recipe = null;
      Gradient.getRecipeManager().getRecipe(recipeId).ifPresent(recipe -> {
        if(recipe instanceof ICraftingRecipe) {
          this.recipe = (ICraftingRecipe)recipe;
        } else if(recipe instanceof IToolStationRecipe) {
          this.recipe = (IToolStationRecipe)recipe;
        }
      });
    }

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
    if(capability == ITEM_HANDLER_CAPABILITY) {
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

  public class ItemHandler extends ItemStackHandler {
    @Nullable
    protected PlayerEntity player;

    private ItemHandler(final int size) {
      super(size);
    }

    public void setPlayer(@Nullable final PlayerEntity player) {
      this.player = player;
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);
      ToolStationTile.this.markDirty();
    }
  }
}
