package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.recipes.DryingRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class DryingRackTile extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final ItemStackHandler inventory = new ItemStackHandler(1) {
    @Override
    public int getSlotLimit(final int slot) {
      return 1;
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);

      final ItemStack stack = this.getStackInSlot(slot);

      if(stack.isEmpty()) {
        DryingRackTile.this.recipe = null;
      } else {
        DryingRackTile.this.updateRecipe();
      }

      DryingRackTile.this.ticks = 0;
      DryingRackTile.this.sync();
    }
  };

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inventory);

  @Nullable
  private DryingRecipe recipe;
  private final Set<Stage> stages = new HashSet<>();
  private int ticks;

  public DryingRackTile() {
    super(GradientTileEntities.DRYING_RACK.get());
  }

  public boolean hasItem() {
    return !this.getItem().isEmpty();
  }

  public ItemStack getItem() {
    return this.inventory.getStackInSlot(0);
  }

  public ItemStack takeItem() {
    return this.inventory.extractItem(0, this.inventory.getSlotLimit(0), false);
  }

  public ItemStack insertItem(final ItemStack stack, final PlayerEntity player) {
    if(!this.hasItem()) {
      this.stages.clear();
      this.stages.addAll(Progress.get(player).getStages());

      final ItemStack input = stack.split(1);
      this.inventory.setStackInSlot(0, input);

      return stack;
    }

    return stack;
  }

  @Override
  public void tick() {
    if(this.world.isRemote) {
      return;
    }

    this.dry();
  }

  private void dry() {
    if(this.recipe == null) {
      return;
    }

    if(this.ticks < this.recipe.getTicks()) {
      this.ticks++;
      this.markDirty();
    }

    if(this.ticks >= this.recipe.getTicks()) {
      this.inventory.setStackInSlot(0, this.recipe.getRecipeOutput().copy());
      this.sync();
    }
  }

  private void updateRecipe() {
    this.recipe = RecipeUtils.getRecipe(DryingRecipe.TYPE, recipe -> recipe.matches(this.inventory, this.stages, 0, 0)).orElse(null);
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("inventory", this.inventory.serializeNBT());
    compound.putInt("ticks", this.ticks);

    final ListNBT stagesList = new ListNBT();
    for(final Stage stage : this.stages) {
      stagesList.add(StringNBT.valueOf(stage.getRegistryName().toString()));
    }

    compound.put("stages", stagesList);

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    final CompoundNBT inv = compound.getCompound("inventory");
    inv.remove("Size");
    this.inventory.deserializeNBT(inv);
    this.ticks = compound.getInt("ticks");

    final ListNBT stagesList = compound.getList("stages", Constants.NBT.TAG_STRING);
    this.stages.clear();
    for(int i = 0; i < stagesList.size(); i++) {
      this.stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(stagesList.getString(i))));
    }

    this.updateRecipe();

    super.read(compound);
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == ITEM_HANDLER_CAPABILITY) {
      return this.lazyInv.cast();
    }

    return super.getCapability(capability, facing);
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
