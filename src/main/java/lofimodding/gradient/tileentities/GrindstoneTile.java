package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.pieces.GrinderProcessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrindstoneTile extends ManualProcessorTile<GrindingRecipe> {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final ItemStackHandler inv = new ItemStackHandler(2);
  private final LazyOptional<IItemHandler> invLazy = LazyOptional.of(() -> this.inv);

  public GrindstoneTile() {
    super(new GrinderProcessor());
  }

  public boolean hasInput() {
    return !this.inv.getStackInSlot(0).isEmpty();
  }

  public boolean hasOutput() {
    return !this.inv.getStackInSlot(1).isEmpty();
  }

  public ItemStack getInput() {
    return this.inv.getStackInSlot(0);
  }

  public ItemStack getOutput() {
    return this.inv.getStackInSlot(1);
  }

  public ItemStack takeInput() {
    this.clearRecipe();
    final ItemStack input = this.inv.extractItem(0, this.inv.getSlotLimit(0), false);
    this.sync();
    return input;
  }

  public ItemStack takeOutput() {
    final ItemStack output = this.inv.extractItem(1, this.inv.getSlotLimit(1), false);
    this.sync();
    return output;
  }

  public ItemStack insertItem(final ItemStack stack, final PlayerEntity player) {
    if(!this.hasInput()) {
      this.age = AgeUtils.getPlayerAge(player);

      final ItemStack remaining = this.inv.insertItem(0, stack, false);

      this.updateRecipe();
      this.world.getRecipeManager().getRecipes(GrindingRecipe.TYPE).values().stream().flatMap(r -> {
        return Util.streamOptional(GrindingRecipe.TYPE.matches(r, worldIn, inventoryIn));
      }).findFirst();

      this.sync();
      return remaining;
    }

    final ItemStack remaining = this.inv.insertItem(0, stack, false);
    this.sync();
    return remaining;
  }

  @Override
  protected void onProcessorTick() {
    ((ServerWorld)this.world).spawnParticle(ParticleTypes.SMOKE, this.pos.getX() + 0.5d, this.pos.getY() + 0.5d, this.pos.getZ() + 0.5d, 10, 0.1d, 0.1d, 0.1d, 0.01d);
  }

  @Override
  protected void onFinished(final GrindingRecipe recipe) {
    this.inv.extractItem(0, 1, false);
    this.inv.insertItem(1, recipe.getRecipeOutput().copy(), false);

    if(!this.hasInput()) {
      this.clearRecipe();
    }
  }

  private void sync() {
    if(!this.world.isRemote) {
      final BlockState state = this.world.getBlockState(this.getPos());
      this.world.notifyBlockUpdate(this.getPos(), state, state, 3);
      this.markDirty();
    }
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, @Nullable final Direction side) {
    if(cap == ITEM_HANDLER_CAPABILITY) {
      return this.invLazy.cast();
    }

    return super.getCapability(cap, side);
  }
}
