package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.recipes.CookingRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class ClayOvenTile extends HeatSinkerTile {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  public static final int FIRST_INPUT_SLOT = 0;
  public static final int INPUT_SLOTS_COUNT = 1;

  public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;
  public static final int OUTPUT_SLOTS_COUNT = 1;

  public static final int TOTAL_SLOTS_COUNT = INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

  private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS_COUNT) {
    private final ItemStackHandler inputTemp = new ItemStackHandler(INPUT_SLOTS_COUNT);

    @Override
    public int getSlotLimit(final int slot) {
      if(slot >= FIRST_OUTPUT_SLOT) {
        return super.getSlotLimit(slot);
      }

      return 1;
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      if(ClayOvenTile.this.force) {
        return true;
      }

      if(slot < FIRST_OUTPUT_SLOT) {
        for(int i = 0; i < INPUT_SLOTS_COUNT; i++) {
          this.inputTemp.setStackInSlot(i, this.getStackInSlot(i + FIRST_INPUT_SLOT));
        }

        this.inputTemp.setStackInSlot(slot - FIRST_INPUT_SLOT, stack);

        return
          !ClayOvenTile.this.hasInput() &&
          RecipeUtils.getRecipe(CookingRecipe.TYPE, recipe -> recipe.matches(this.inputTemp, 0, INPUT_SLOTS_COUNT - 1)).isPresent();
      }

      return false;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      if(!ClayOvenTile.this.force) {
        if(slot < FIRST_OUTPUT_SLOT && ClayOvenTile.this.isCooking()) {
          return ItemStack.EMPTY;
        }
      }

      return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      final ItemStack stack = this.getStackInSlot(slot);

      if(slot < FIRST_OUTPUT_SLOT) {
        if(!stack.isEmpty()) {
          ClayOvenTile.this.updateRecipe();
        } else {
          ClayOvenTile.this.recipe = null;
        }

        ClayOvenTile.this.ticks = 0;
      }

      ClayOvenTile.this.sync();
    }
  };

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inventory);

  @Nullable
  private CookingRecipe recipe;
  private final NonNullList<Stage> stages = NonNullList.create();
  private int ticks;
  private boolean force;

  public ClayOvenTile() {
    super(GradientTileEntities.CLAY_OVEN.get());
  }

  public boolean isCooking() {
    return this.recipe != null;
  }

  public float getCookingPercent() {
    if(!this.isCooking()) {
      return 0.0f;
    }

    return this.ticks / (this.recipe.ticks * this.getHeatScale());
  }

  public boolean hasInput() {
    return !this.getInput().isEmpty();
  }

  public ItemStack getInput() {
    return this.inventory.getStackInSlot(FIRST_INPUT_SLOT);
  }


  public ItemStack takeInput() {
    return this.inventory.extractItem(FIRST_INPUT_SLOT, this.inventory.getSlotLimit(FIRST_INPUT_SLOT), false);
  }

  public boolean hasOutput() {
    return !this.getOutput().isEmpty();
  }

  public ItemStack getOutput() {
    return this.inventory.getStackInSlot(FIRST_OUTPUT_SLOT);
  }

  public ItemStack takeOutput() {
    return this.inventory.extractItem(FIRST_OUTPUT_SLOT, this.inventory.getSlotLimit(FIRST_OUTPUT_SLOT), false);
  }

  public ItemStack insertItem(final ItemStack stack, final PlayerEntity player) {
    if(!this.hasInput()) {
      this.stages.clear();
      this.stages.addAll(Progress.get(player).getStages());
      this.inventory.setStackInSlot(FIRST_INPUT_SLOT, stack.split(1));

      return stack;
    }

    return stack;
  }

  private void updateRecipe() {
    this.recipe = RecipeUtils.getRecipe(CookingRecipe.TYPE, recipe -> recipe.matches(this.inventory, this.stages, FIRST_INPUT_SLOT, FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT - 1)).orElse(null);
  }

  @Override
  protected void tickBeforeCooldown() {

  }

  @Override
  protected void tickAfterCooldown() {
    this.cook();

    if(this.getWorld().isRemote) {
      this.generateParticles();
    }
  }

  @Override
  protected float calculateHeatLoss(final BlockState state) {
    return (float)Math.max(0.5d, Math.pow(this.getHeat() / 800, 2));
  }

  @Override
  protected float heatTransferEfficiency() {
    return 0.6f;
  }

  private float getHeatScale() {
    if(this.recipe == null) {
      return 1.0f;
    }

    return 1.0f - ((this.getHeat() - this.recipe.temperature) / 2000.0f + 0.1f);
  }

  private void cook() {
    if(this.recipe == null) {
      return;
    }

    final float heatScale = this.getHeatScale();

    if(this.ticks < this.recipe.ticks * heatScale) {
      if(this.getHeat() >= this.recipe.temperature) {
        this.ticks++;
        this.markDirty();
      }
    }

    if(this.ticks >= this.recipe.ticks * heatScale) {
      final ItemStack output = this.recipe.getRecipeOutput().copy();
      this.force = true;
      this.inventory.extractItem(FIRST_INPUT_SLOT, 1, false);
      this.inventory.insertItem(FIRST_OUTPUT_SLOT, output, false);
      this.force = false;
    }
  }

  private void generateParticles() {
    if(this.hasHeat()) {
      final Random rand = this.getWorld().rand;

      if(rand.nextInt(10) == 0) {
        if(this.recipe != null && this.getHeat() >= this.recipe.temperature) {
          final double radius = rand.nextDouble() * 0.15d;
          final double angle  = rand.nextDouble() * Math.PI * 2;

          final double x = this.pos.getX() + 0.5d + radius * Math.cos(angle);
          final double z = this.pos.getZ() + 0.5d + radius * Math.sin(angle);

          this.getWorld().addParticle(ParticleTypes.SMOKE, x, this.pos.getY() + 0.1d, z, 0.0d, 0.0d, 0.0d);
        }
      }
    }
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("inventory", this.inventory.serializeNBT());

    final ListNBT stagesNbt = new ListNBT();
    for(final Stage stage : this.stages) {
      stagesNbt.add(StringNBT.valueOf(stage.getRegistryName().toString()));
    }

    compound.put("stages", stagesNbt);
    compound.putInt("ticks", this.ticks);

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    final CompoundNBT inv = compound.getCompound("inventory");
    inv.remove("Size");
    this.inventory.deserializeNBT(inv);

    final ListNBT stagesNbt = compound.getList("stages", Constants.NBT.TAG_STRING);
    this.stages.clear();
    for(int i = 0; i < stagesNbt.size(); i++) {
      final Stage stage = Stage.REGISTRY.get().getValue(new ResourceLocation(stagesNbt.getString(i)));

      if(stage != null) {
        this.stages.add(stage);
      }
    }

    this.ticks = compound.getInt("ticks");

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
}
