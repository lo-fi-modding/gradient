package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.recipes.IToolStationRecipe;
import lofimodding.gradient.recipes.ShapelessToolStationRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

  //TODO: I think this should probably all be changed to just have one master with an inventory
  private final IItemHandlerModifiable mergedRecipe = new CombinedInvWrapper(this.recipeInv);
  private final IItemHandlerModifiable mergedOutput = new CombinedInvWrapper(this.outputInv);
  private final IItemHandlerModifiable mergedTools = new CombinedInvWrapper(this.toolsInv);
  private final IItemHandlerModifiable mergedStorage = new CombinedInvWrapper(this.storageInv);
  private final IItemHandlerModifiable mergedInventory = new CombinedInvWrapper(this.mergedRecipe, this.mergedOutput, this.mergedTools, this.mergedStorage);

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.mergedInventory);

  private IRecipe<CraftingInventory> recipe;

  public ToolStationTile() {
    super(GradientTileEntities.TOOL_STATION.get());
  }

  public int getCraftingSize() {
    return 3; //TODO
  }

  public IItemHandler getMergedRecipeInv() {
    return this.mergedRecipe;
  }

  public IItemHandler getMergedToolsInv() {
    return this.mergedTools;
  }

  public IItemHandler getMergedStorageInv() {
    return this.mergedStorage;
  }

  public IItemHandler getMergedOutputInv() {
    return this.mergedOutput;
  }

  public IRecipe<?> getRecipe() {
    return this.recipe;
  }

  private void updateRecipe() {
    this.recipe = this.findRecipe();

    for(int slot = 0; slot < this.mergedOutput.getSlots(); slot++) {
      this.mergedOutput.setStackInSlot(slot, ItemStack.EMPTY);
    }

    if(this.recipe == null) {
      return;
    }

    if(this.recipe instanceof IToolStationRecipe) {
      for(int slot = 0; slot < Math.min(this.mergedOutput.getSlots(), ((IToolStationRecipe)this.recipe).getOutputs().size()); slot++) {
        this.mergedOutput.setStackInSlot(slot, ((IToolStationRecipe)this.recipe).getOutputs().get(slot));
      }
    } else {
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

      this.mergedOutput.setStackInSlot(0, this.recipe.getCraftingResult(crafting));
    }
  }

  @Nullable
  private IRecipe<CraftingInventory> findRecipe() {
    final IRecipe<CraftingInventory> shapelessToolStation = RecipeUtils.getRecipe(ShapelessToolStationRecipe.TYPE, recipe -> recipe.recipeMatches(this.mergedRecipe)).orElse(null);

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
