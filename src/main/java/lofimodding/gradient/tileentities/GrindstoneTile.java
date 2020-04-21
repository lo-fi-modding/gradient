package lofimodding.gradient.tileentities;

import lofimodding.gradient.recipes.GrindingRecipe;
import lofimodding.gradient.tileentities.pieces.GrinderProcessor;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class GrindstoneTile extends ProcessorTile<GrindingRecipe, ManualEnergySource, GrinderProcessor> {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private static final int INPUT_SLOT = 0;
  private static final int OUTPUT_SLOT = 1;

  private final ItemStackHandler inv = new ItemStackHandler(2) {
    private final ItemStackHandler temp = new ItemStackHandler(this.getSlots());

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
      if(slot == INPUT_SLOT) {
        for(int i = 0; i < this.getSlots(); i++) {
          this.temp.setStackInSlot(i, this.getStackInSlot(i));
          this.temp.setStackInSlot(slot, stack);
        }

        return RecipeUtils.getRecipe(GrindingRecipe.TYPE, recipe -> recipe.matches(this.temp, INPUT_SLOT, INPUT_SLOT)).isPresent();
      }

      return GrindstoneTile.this.forceInsert;
    }

    @Override
    protected void onContentsChanged(final int slot) {
      if(slot == INPUT_SLOT) {
        final ItemStack stack = this.getStackInSlot(slot);

        if(!stack.isEmpty()) {
          if(!GrindstoneTile.this.hasRecipe()) {
            GrindstoneTile.this.updateRecipe();
          }
        } else {
          GrindstoneTile.this.clearRecipe();
        }
      }

      GrindstoneTile.this.sync();
    }
  };

  private final LazyOptional<IItemHandler> invLazy = LazyOptional.of(() -> this.inv);
  private final Set<Stage> stages = new HashSet<>();
  private boolean forceInsert;

  public GrindstoneTile() {
    super(new ManualEnergySource(), new GrinderProcessor());
  }

  public boolean hasInput() {
    return !this.inv.getStackInSlot(INPUT_SLOT).isEmpty();
  }

  public boolean hasOutput() {
    return !this.inv.getStackInSlot(OUTPUT_SLOT).isEmpty();
  }

  public ItemStack getInput() {
    return this.inv.getStackInSlot(INPUT_SLOT);
  }

  public ItemStack getOutput() {
    return this.inv.getStackInSlot(OUTPUT_SLOT);
  }

  public ItemStack takeInput() {
    return this.inv.extractItem(INPUT_SLOT, this.inv.getSlotLimit(INPUT_SLOT), false);
  }

  public ItemStack takeOutput() {
    return this.inv.extractItem(OUTPUT_SLOT, this.inv.getSlotLimit(OUTPUT_SLOT), false);
  }

  public ItemStack insertItem(final ItemStack stack, final PlayerEntity player) {
    if(!this.hasInput()) {
      this.stages.clear();
      this.stages.addAll(Progress.get(player).getStages());
      return this.inv.insertItem(INPUT_SLOT, stack, false);
    }

    return this.inv.insertItem(INPUT_SLOT, stack, false);
  }

  public void crank(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    this.getEnergy().crank(state, world, pos, player, hand, hit);
  }

  private void updateRecipe() {
    RecipeUtils.getRecipe(GrindingRecipe.TYPE, r -> r.matches(this.inv, this.stages, 0, 0)).ifPresent(this::setRecipe);
  }

  @Override
  protected void onProcessorTick() {
    ((ServerWorld)this.world).spawnParticle(ParticleTypes.SMOKE, this.pos.getX() + 0.5d, this.pos.getY() + 0.5d, this.pos.getZ() + 0.5d, 10, 0.1d, 0.1d, 0.1d, 0.01d);
  }

  @Override
  protected void onAnimationTick(final int ticks) {

  }

  @Override
  protected void onFinished(final GrindingRecipe recipe) {
    this.inv.extractItem(INPUT_SLOT, 1, false);
    this.forceInsert = true;
    this.inv.insertItem(OUTPUT_SLOT, recipe.getRecipeOutput().copy(), false);
    this.forceInsert = false;
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("inventory", this.inv.serializeNBT());

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
    this.inv.deserializeNBT(inv);

    final ListNBT stagesList = compound.getList("stages", Constants.NBT.TAG_STRING);
    this.stages.clear();
    for(int i = 0; i < stagesList.size(); i++) {
      this.stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(stagesList.getString(i))));
    }

    this.updateRecipe();

    super.read(compound);
  }

  private void sync() {
    if(!this.world.isRemote) {
      final BlockState state = this.world.getBlockState(this.getPos());
      this.world.notifyBlockUpdate(this.getPos(), state, state, 3);
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
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket packet) {
    this.read(packet.getNbtCompound());
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
