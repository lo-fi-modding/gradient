package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.recipes.MixingRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MixingBasinTile extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  @CapabilityInject(IFluidHandler.class)
  private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY;

  private final FluidTank tank = new FluidTank(1000, fluid -> fluid.getFluid() == Fluids.WATER) {
    //TODO: for some reason, onContentsChanged is called when draining even on simulate mode
    private boolean simulating;

    @Override
    public int fill(final FluidStack resource, final FluidAction action) {
      this.simulating = action.simulate();
      return super.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
      this.simulating = action.simulate();
      return super.drain(maxDrain, action);
    }

    @Override
    protected void onContentsChanged() {
      super.onContentsChanged();

      if(!this.simulating) {
        MixingBasinTile.this.world.setBlockState(MixingBasinTile.this.pos, MixingBasinTile.this.getBlockState().with(MixingBasinBlock.HAS_WATER, !this.fluid.isEmpty()));
        MixingBasinTile.this.updateRecipe();
        MixingBasinTile.this.sync();
      }
    }
  };

  public static final int INPUT_SIZE = 5;
  private static final int OUTPUT_SLOT = INPUT_SIZE;

  private final ItemStackHandler inventory = new ItemStackHandler(INPUT_SIZE + 1) {
    @Override
    public int getSlotLimit(final int slot) {
      if(slot < INPUT_SIZE) {
        return 1;
      }

      return super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      if(!MixingBasinTile.this.force) {
        if(slot == OUTPUT_SLOT) {
          return false;
        }
      }

      return super.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      if(slot < INPUT_SIZE) {
        final ItemStack stack = this.getStackInSlot(slot);

        if(!stack.isEmpty()) {
          if(MixingBasinTile.this.recipe == null) {
            MixingBasinTile.this.passes = 0;
            MixingBasinTile.this.ticks = 0;
            MixingBasinTile.this.updateRecipe();

            if(MixingBasinTile.this.recipe != null) {
              MixingBasinTile.this.ticks = MixingBasinTile.this.recipe.getTicks();
            }
          }
        } else {
          MixingBasinTile.this.recipe = null;
          MixingBasinTile.this.passes = 0;
          MixingBasinTile.this.ticks = 0;
        }
      }

      MixingBasinTile.this.sync();
    }
  };

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inventory);
  private final LazyOptional<IFluidHandler> lazyTank = LazyOptional.of(() -> this.tank);

  @Nullable
  private MixingRecipe recipe;
  private final Set<Stage> stages = new HashSet<>();
  private int passes;
  private int ticks;
  private boolean force;

  public MixingBasinTile() {
    super(GradientTileEntities.MIXING_BASIN.get());
  }

  public boolean hasFluid() {
    return !this.tank.getFluid().isEmpty();
  }

  public boolean hasInput(final int slot) {
    return !this.getInput(slot).isEmpty();
  }

  public boolean hasOutput() {
    return !this.getOutput().isEmpty();
  }

  @Nullable
  public FluidStack getFluid() {
    return this.tank.getFluid();
  }

  public ItemStack getInput(final int slot) {
    return this.inventory.getStackInSlot(slot);
  }

  public ItemStack getOutput() {
    return this.inventory.getStackInSlot(OUTPUT_SLOT);
  }

  public ItemStack takeInput(final int slot, final PlayerEntity player) {
    this.stages.clear();
    this.stages.addAll(Progress.get(player).getStages());
    return this.inventory.extractItem(slot, this.inventory.getSlotLimit(slot), false);
  }

  public ItemStack takeOutput() {
    return this.inventory.extractItem(OUTPUT_SLOT, this.inventory.getSlotLimit(OUTPUT_SLOT), false);
  }

  private int findOpenSlot() {
    for(int slot = 0; slot < INPUT_SIZE; slot++) {
      if(!this.hasInput(slot)) {
        return slot;
      }
    }

    return -1;
  }

  public ItemStack insertItem(final ItemStack stack, final PlayerEntity player) {
    final int slot = this.findOpenSlot();

    // No space
    if(slot == -1) {
      return stack;
    }

    this.stages.clear();
    this.stages.addAll(Progress.get(player).getStages());
    this.inventory.setStackInSlot(slot, stack.split(1));

    return stack;
  }

  @Override
  public void tick() {
    if(this.world.isRemote) {
      return;
    }

    if(this.recipe == null) {
      return;
    }

    if(this.ticks < this.recipe.getTicks()) {
      final Random rand = this.getWorld().rand;

      if(rand.nextInt(2) == 0) {
        final double radius = rand.nextDouble() * 0.2d;
        final double angle = rand.nextDouble() * Math.PI * 2;

        final double x = this.pos.getX() + 0.5d + radius * Math.cos(angle);
        final double z = this.pos.getZ() + 0.5d + radius * Math.sin(angle);

        ((ServerWorld)this.world).spawnParticle(ParticleTypes.BUBBLE, x, this.pos.getY() + 0.4d, z, 1, 0.0d, 0.0d, 0.0d, 0.0d);
      }

      this.ticks++;
      this.markDirty();
    }

    if(this.ticks >= this.recipe.getTicks() && this.passes >= this.recipe.getPasses()) {
      final ItemStack output = this.recipe.getRecipeOutput().copy();

      this.passes = 0;
      this.tank.drain(this.recipe.getFluid(), IFluidHandler.FluidAction.EXECUTE);

      for(int slot = 0; slot < INPUT_SIZE; slot++) {
        this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
      }

      this.force = true;
      this.inventory.setStackInSlot(OUTPUT_SLOT, output);
      this.force = false;
    }
  }

  public void mix() {
    if(this.recipe == null) {
      return;
    }

    if(this.ticks >= this.recipe.getTicks()) {
      this.getWorld().playSound(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.BLOCKS, 0.8f + this.getWorld().rand.nextFloat(), this.getWorld().rand.nextFloat() * 0.7f + 0.3f, false);

      this.ticks = 0;
      this.passes++;

      this.markDirty();
    }
  }

  private void updateRecipe() {
    this.recipe = RecipeUtils.getRecipe(MixingRecipe.TYPE, recipe -> recipe.matches(this.inventory, this.stages, 0, INPUT_SIZE - 1, this.tank)).orElse(null);
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("inventory", this.inventory.serializeNBT());
    compound.put("tank", this.tank.writeToNBT(new CompoundNBT()));
    compound.putInt("passes", this.passes);
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
    this.tank.readFromNBT(compound.getCompound("tank"));
    this.passes = compound.getInt("passes");
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

    if(capability == FLUID_HANDLER_CAPABILITY) {
      return this.lazyTank.cast();
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
    final BlockState oldState = this.world.getBlockState(this.pos);
    this.read(pkt.getNbtCompound());
    this.world.notifyBlockUpdate(this.pos, oldState, this.world.getBlockState(this.pos), 2);
  }
}
