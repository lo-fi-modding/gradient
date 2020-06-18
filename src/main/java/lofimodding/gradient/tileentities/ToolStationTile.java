package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.containers.ToolStationContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiPredicate;

public class ToolStationTile extends TileEntity implements INamedContainerProvider {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final ItemStackHandler recipe = new ItemHandler(9, (slot, stack) -> false);
  private final ItemStackHandler output = new ItemHandler(1, (slot, stack) -> false);
  private final ItemStackHandler tools = new ItemHandler(3, (slot, stack) -> true);
  private final ItemStackHandler storage = new ItemHandler(6, (slot, stack) -> true);

  private final IItemHandlerModifiable mergedRecipe = new CombinedInvWrapper(this.recipe);
  private final IItemHandlerModifiable mergedOutput = new CombinedInvWrapper(this.output);
  private final IItemHandlerModifiable mergedTools = new CombinedInvWrapper(this.tools);
  private final IItemHandlerModifiable mergedStorage = new CombinedInvWrapper(this.storage);
  private final IItemHandlerModifiable mergedInventory = new CombinedInvWrapper(this.mergedRecipe, this.mergedOutput, this.mergedTools, this.mergedStorage);

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.mergedInventory);

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

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == ITEM_HANDLER_CAPABILITY) {
      return this.lazyInv.cast();
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("container.gradient.tool_station");
  }

  @Override
  public Container createMenu(final int id, final PlayerInventory playerInv, final PlayerEntity player) {
    return new ToolStationContainer(id, playerInv, this);
  }

  private class ItemHandler extends ItemStackHandler {
    private final BiPredicate<Integer, ItemStack> validator;

    private ItemHandler(final int size, final BiPredicate<Integer, ItemStack> validator) {
      super(size);
      this.validator = validator;
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return this.validator.test(slot, stack);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);
      ToolStationTile.this.markDirty();
    }
  }
}
