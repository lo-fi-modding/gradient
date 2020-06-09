package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.tileentities.ProcessorTile;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class Processor<Recipe extends IGradientRecipe> {
  private final IRecipeType<Recipe> recipeType;

  private final List<ItemSlot> itemSlots;
  private final List<ItemSlot> itemInputSlots;
  private final List<ItemSlot> itemOutputSlots;
  private final ProcessorItemHandler<Recipe> inv;

  private final List<FluidTank> fluidSlots;
  private final ProcessorTile.MultiTankWrapper fluids;

  private final Set<Stage> stages = new HashSet<>();

  private boolean tanksLocked = true;

  @Nullable
  private Recipe recipe;
  private int ticks;
  private int maxTicks;

  public Processor(final ProcessorItemHandler.Callback onItemChange, final ProcessorFluidTank.Callback onFluidChange, final IRecipeType<Recipe> recipeType, final Consumer<Builder<Recipe>> builder) {
    this.recipeType = recipeType;

    final Builder<Recipe> b = new Builder<>(this, onItemChange, onFluidChange);
    builder.accept(b);

    this.itemSlots = b.itemSlots;
    this.itemInputSlots = b.itemInputSlots;
    this.itemOutputSlots = b.itemOutputSlots;
    this.inv = new ProcessorItemHandler<>(this, this.itemSlots.size());

    this.fluidSlots = b.fluidSlots;
    this.fluids = new ProcessorTile.MultiTankWrapper(this.fluidSlots);
  }

  public void lockFluid() {
    this.tanksLocked = true;
  }

  public void unlockFluid() {
    this.tanksLocked = false;
  }

  public IItemHandlerModifiable getInv() {
    return this.inv;
  }

  public List<FluidTank> getFluids() {
    return this.fluidSlots;
  }

  public int inputSlots() {
    return this.itemInputSlots.size();
  }

  public int outputSlots() {
    return this.itemOutputSlots.size();
  }

  public boolean hasInput(final int slot) {
    return !this.getInput(slot).isEmpty();
  }

  public boolean hasOutput(final int slot) {
    return !this.getOutput(slot).isEmpty();
  }

  public ItemStack getInput(final int slot) {
    return this.itemInputSlots.get(slot).get(this.inv);
  }

  public ItemStack getOutput(final int slot) {
    return this.itemOutputSlots.get(slot).get(this.inv);
  }

  public ItemStack takeInput(final int slot, final PlayerEntity player) {
    this.stages.clear();
    this.stages.addAll(Progress.get(player).getStages());

    final ItemSlot s = this.itemInputSlots.get(slot);
    return s.extract(this.inv, s.limit, false);
  }

  public ItemStack takeOutput(final int slot) {
    final ItemSlot s = this.itemOutputSlots.get(slot);
    return s.extract(this.inv, s.limit, false);
  }

  private int findOpenSlot() {
    for(int slot = 0; slot < this.inputSlots(); slot++) {
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
    this.itemInputSlots.get(slot).set(this.inv, stack.split(this.itemInputSlots.get(slot).limit));

    return stack;
  }

  public void lockSlotsToCurrentContents() {
    for(final ItemSlot slot : this.itemInputSlots) {
      slot.setLock(slot.get(this.inv));
    }

    for(final FluidTank tank : this.fluidSlots) {
      ((ProcessorFluidTank<?>)tank).setLock(tank.getFluid());
    }
  }

  public void unlockSlots() {
    for(final ItemSlot slot : this.itemInputSlots) {
      slot.clearLock();
    }

    for(final FluidTank tank : this.fluidSlots) {
      ((ProcessorFluidTank<?>)tank).clearLock();
    }
  }

  public boolean tick(final boolean isClient) {
    if(this.hasRecipe()) {
      this.ticks++;

      if(!isClient && this.isFinished()) {
        this.finishRecipe();
        this.ticks = 0;
      }

      return true;
    }

    return false;
  }

  public int getTicks() {
    return this.ticks;
  }

  private boolean isFinished() {
    return this.ticks >= this.maxTicks;
  }

  private void finishRecipe() {
    final Recipe recipe = this.recipe;

    for(final ItemSlot slot : this.itemInputSlots) {
      slot.extract(this.inv, 1, false); //TODO: don't hardcode to extract one item
    }

    this.inv.disableValidation();

    for(int slot = 0; slot < this.itemOutputSlots.size(); slot++) {
      this.itemOutputSlots.get(slot).insert(this.inv, recipe.getItemOutput(slot), false);
    }

    this.inv.enableValidation();

    this.tanksLocked = false;
    for(int i = 0; i < recipe.getFluidInputCount(); i++) {
      this.fluids.drain(recipe.getFluidInput(i), IFluidHandler.FluidAction.EXECUTE);
    }
    for(int i = 0; i < recipe.getFluidOutputCount(); i++) {
      this.fluids.fill(recipe.getFluidOutput(i), IFluidHandler.FluidAction.EXECUTE);
    }
    this.tanksLocked = true;
  }

  public boolean hasRecipe() {
    return this.recipe != null;
  }

  private void updateRecipe() {
    final Recipe recipe = RecipeUtils.getRecipe(this.recipeType, this::recipeMatches).orElse(null);

    if(recipe != this.recipe) {
      this.recipe = recipe;
      this.ticks = 0;

      if(this.hasRecipe()) {
        this.maxTicks = this.recipe.getTicks() * 3;
      } else {
        this.maxTicks = Integer.MAX_VALUE;
      }
    }
  }

  private boolean recipeMatches(final IGradientRecipe recipe) {
    final NonNullList<ItemStack> items = NonNullList.create();

    for(final ItemSlot slot : this.itemInputSlots) {
      items.add(slot.get(this.inv));
    }

    this.tanksLocked = false;
    final boolean matches =
      recipe.matchesStages(this.stages) &&
      recipe.matchesItems(items) &&
      recipe.matchesFluids(this.fluids);
    this.tanksLocked = true;

    return matches;
  }

  public CompoundNBT write(final CompoundNBT compound) {
    if(this.inv.getSlots() != 0) {
      compound.put("Inventory", this.inv.serializeNBT());

      final ListNBT locks = new ListNBT();
      for(final ItemSlot slot : this.itemInputSlots) {
        final CompoundNBT lock = new CompoundNBT();
        slot.lockItem.write(lock);
        lock.putBoolean("Locked", slot.locked);
        locks.add(lock);
      }

      compound.put("InventoryLocks", locks);
    }

    if(!this.fluidSlots.isEmpty()) {
      final ListNBT fluids = new ListNBT();

      for(final FluidTank tank : this.fluidSlots) {
        final CompoundNBT tankNbt = tank.writeToNBT(new CompoundNBT());
        tankNbt.put("LockFluid", ((ProcessorFluidTank<?>)tank).lockFluid.writeToNBT(new CompoundNBT()));
        tankNbt.putBoolean("Locked", ((ProcessorFluidTank<?>)tank).locked);
        fluids.add(tankNbt);
      }

      compound.put("Fluids", fluids);
    }

    final ListNBT stagesList = new ListNBT();
    for(final Stage stage : this.stages) {
      stagesList.add(StringNBT.valueOf(stage.getRegistryName().toString()));
    }

    compound.put("Stages", stagesList);
    compound.putInt("Ticks", this.ticks);
    return compound;
  }

  public void read(final CompoundNBT compound) {
    if(compound.contains("Inventory")) {
      final CompoundNBT inv = compound.getCompound("Inventory");
      inv.remove("Size");
      this.inv.deserializeNBT(inv);
    }

    if(compound.contains("InventoryLocks")) {
      final ListNBT locks = compound.getList("InventoryLocks", Constants.NBT.TAG_COMPOUND);
      for(int i = 0; i < Math.min(this.inv.getSlots(), locks.size()); i++) {
        final CompoundNBT lock = locks.getCompound(i);
        final ItemSlot slot = this.itemInputSlots.get(i);
        slot.lockItem = ItemStack.read(lock);
        slot.locked = lock.getBoolean("Locked");
      }
    }

    if(compound.contains("Fluids")) {
      final ListNBT fluids = compound.getList("Fluids", Constants.NBT.TAG_COMPOUND);
      for(int i = 0; i < Math.min(fluids.size(), this.fluidSlots.size()); i++) {
        final CompoundNBT tankNbt = fluids.getCompound(i);
        final FluidTank tank = this.fluidSlots.get(i);

        tank.readFromNBT(tankNbt);
        ((ProcessorFluidTank<?>)tank).lockFluid = FluidStack.loadFluidStackFromNBT(tankNbt.getCompound("LockFluid"));
        ((ProcessorFluidTank<?>)tank).locked = tankNbt.getBoolean("Locked");
      }
    }

    final ListNBT stagesList = compound.getList("Stages", Constants.NBT.TAG_STRING);
    this.stages.clear();
    for(int i = 0; i < stagesList.size(); i++) {
      this.stages.add(Stage.REGISTRY.get().getValue(new ResourceLocation(stagesList.getString(i))));
    }

    this.ticks = compound.getInt("Ticks");

    this.updateRecipe();
  }

  public static class ProcessorItemHandler<Recipe extends IGradientRecipe> extends ItemStackHandler {
    private final Processor<Recipe> processor;

    private boolean validate = true;

    public ProcessorItemHandler(final Processor<Recipe> processor, final int size) {
      super(size);
      this.processor = processor;
    }

    protected void enableValidation() {
      this.validate = true;
    }

    protected void disableValidation() {
      this.validate = false;
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return !this.validate || super.isItemValid(slot, stack) && this.processor.itemSlots.get(slot).insertValidator.test(this, stack);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      if(this.validate && !this.processor.itemSlots.get(slot).extractValidator.test(this, this.getStackInSlot(slot))) {
        return ItemStack.EMPTY;
      }

      return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(final int slot) {
      return this.processor.itemSlots.get(slot).limit;
    }

    @Override
    protected void onContentsChanged(final int slot) {
      super.onContentsChanged(slot);
      this.processor.itemSlots.get(slot).onChanged.accept(this, this.getStackInSlot(slot));
    }

    @FunctionalInterface
    public interface Validator extends BiPredicate<ProcessorItemHandler<?>, ItemStack> {
      Validator ALWAYS = (inv, stack) -> true;
      Validator NEVER = (inv, stack) -> false;

      @Override
      default Validator and(final BiPredicate<? super ProcessorItemHandler<?>, ? super ItemStack> other) {
        return (inv, stack) -> this.test(inv, stack) && other.test(inv, stack);
      }
    }

    @FunctionalInterface
    public interface Callback extends BiConsumer<ProcessorItemHandler<?>, ItemStack> {
      Callback NOOP = (inv, stack) -> { };
      Callback UPDATE_RECIPE = (inv, stack) -> inv.processor.updateRecipe();

      @Override
      default Callback andThen(final BiConsumer<? super ProcessorItemHandler<?>, ? super ItemStack> after) {
        return (l, r) -> { this.accept(l, r); after.accept(l, r); };
      }
    }
  }

  public static class ItemSlot {
    private final int index;
    private final int limit;
    private final ProcessorItemHandler.Validator insertValidator;
    private final ProcessorItemHandler.Validator extractValidator;
    private final ProcessorItemHandler.Callback onChanged;

    private ItemStack lockItem = ItemStack.EMPTY;
    private boolean locked;

    public ItemSlot(final int index, final int limit, final ProcessorItemHandler.Validator insertValidator, final ProcessorItemHandler.Validator extractValidator, final ProcessorItemHandler.Callback onChanged) {
      this.index = index;
      this.limit = limit;
      this.insertValidator = insertValidator.and((inv, stack) -> !this.locked || this.lockItem.isItemEqual(stack));
      this.extractValidator = extractValidator;
      this.onChanged = onChanged;
    }

    public ItemStack get(final ItemStackHandler inv) {
      return inv.getStackInSlot(this.index);
    }

    public void set(final ProcessorItemHandler<?> inv, final ItemStack stack) {
      inv.setStackInSlot(this.index, stack);
    }

    public ItemStack insert(final ProcessorItemHandler<?> inv, final ItemStack stack, final boolean simulate) {
      return inv.insertItem(this.index, stack, simulate);
    }

    public ItemStack extract(final ProcessorItemHandler<?> inv, final int amount, final boolean simulate) {
      inv.disableValidation();
      final ItemStack stack = inv.extractItem(this.index, amount, simulate);
      inv.enableValidation();
      return stack;
    }

    public ItemStack getLockItem() {
      return this.lockItem;
    }

    public boolean isLocked() {
      return this.locked;
    }

    public void setLock(final ItemStack lock) {
      this.lockItem = lock.copy();
      this.locked = true;
    }

    public void clearLock() {
      this.setLock(ItemStack.EMPTY);
      this.locked = false;
    }
  }

  public static class ProcessorFluidTank<Recipe extends IGradientRecipe> extends FluidTank {
    private final Processor<Recipe> processor;
    private final Validator extractValidator;
    private final Callback onChanged;

    private FluidStack lockFluid = FluidStack.EMPTY;
    private boolean locked;

    public ProcessorFluidTank(final Processor<Recipe> processor, final int capacity, final Validator insertValidator, final Validator extractValidator, final Callback onChanged) {
      super(capacity);
      this.processor = processor;
      this.setValidator(stack -> (!this.locked || this.lockFluid.isFluidEqual(stack)) && !this.processor.tanksLocked || insertValidator.test(this, stack));
      this.extractValidator = (tank, stack) -> !this.processor.tanksLocked || extractValidator.test(this, stack);
      this.onChanged = onChanged;
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
      if(!this.extractValidator.test(this, this.fluid)) {
        return FluidStack.EMPTY;
      }

      return super.drain(maxDrain, action);
    }

    @Override
    protected void onContentsChanged() {
      super.onContentsChanged();
      this.onChanged.accept(this, this.fluid);
    }

    public void setLock(final FluidStack lock) {
      this.lockFluid = lock.copy();
      this.locked = true;
    }

    public void clearLock() {
      this.setLock(FluidStack.EMPTY);
      this.locked = false;
    }

    @FunctionalInterface
    public interface Validator extends BiPredicate<ProcessorFluidTank<?>, FluidStack> {
      Validator ALWAYS = (tank, stack) -> true;
      Validator NEVER = (tank, stack) -> false;

      static Validator forFluids(final Fluid... fluids) {
        return (tank, stack) -> Arrays.stream(fluids).anyMatch(fluid -> stack.getFluid() == fluid);
      }
    }

    @FunctionalInterface
    public interface Callback extends BiConsumer<ProcessorFluidTank<?>, FluidStack> {
      Callback NOOP = (tank, stack) -> { };
      Callback UPDATE_RECIPE = (tank, stack) -> tank.processor.updateRecipe();

      @Override
      default Callback andThen(final BiConsumer<? super ProcessorFluidTank<?>, ? super FluidStack> after) {
        return (l, r) -> { this.accept(l, r); after.accept(l, r); };
      }
    }
  }

  public static class Builder<Recipe extends IGradientRecipe> {
    private final Processor<Recipe> processor;
    private final ProcessorItemHandler.Callback onItemChanged;
    private final ProcessorFluidTank.Callback onFluidChanged;

    private final List<ItemSlot> itemSlots = new ArrayList<>();
    private final List<ItemSlot> itemInputSlots = new ArrayList<>();
    private final List<ItemSlot> itemOutputSlots = new ArrayList<>();
    private int itemSlotIndex;

    private final List<FluidTank> fluidSlots = new ArrayList<>();

    public Builder(final Processor<Recipe> processor, final ProcessorItemHandler.Callback onItemChanged, final ProcessorFluidTank.Callback onFluidChanged) {
      this.processor = processor;
      this.onItemChanged = onItemChanged;
      this.onFluidChanged = onFluidChanged;
    }

    public Builder<Recipe> addInputItem() {
      return this.addInputItem(64);
    }

    public Builder<Recipe> addInputItem(final int limit) {
      return this.addInputItem(limit, ProcessorItemHandler.Validator.ALWAYS, ProcessorItemHandler.Validator.NEVER, ProcessorItemHandler.Callback.UPDATE_RECIPE);
    }

    public Builder<Recipe> addInputItem(final int limit, final ProcessorItemHandler.Validator insertValidator, final ProcessorItemHandler.Validator extractValidator, final ProcessorItemHandler.Callback onChanged) {
      final ItemSlot slot = new ItemSlot(this.itemSlotIndex++, limit, insertValidator, extractValidator, onChanged.andThen(this.onItemChanged));
      this.itemSlots.add(slot);
      this.itemInputSlots.add(slot);
      return this;
    }

    public Builder<Recipe> addOutputItem() {
      return this.addOutputItem(64, ProcessorItemHandler.Validator.NEVER, ProcessorItemHandler.Validator.ALWAYS, ProcessorItemHandler.Callback.NOOP);
    }

    public Builder<Recipe> addOutputItem(final int limit, final ProcessorItemHandler.Validator insertValidator, final ProcessorItemHandler.Validator extractValidator, final ProcessorItemHandler.Callback onChanged) {
      final ItemSlot slot = new ItemSlot(this.itemSlotIndex++, limit, insertValidator, extractValidator, onChanged.andThen(this.onItemChanged));
      this.itemSlots.add(slot);
      this.itemOutputSlots.add(slot);
      return this;
    }

    public Builder<Recipe> addInputFluid(final int capacity) {
      return this.addInputFluid(capacity, ProcessorFluidTank.Validator.ALWAYS);
    }

    public Builder<Recipe> addInputFluid(final int capacity, final ProcessorFluidTank.Validator insertValidator) {
      return this.addInputFluid(capacity, insertValidator, ProcessorFluidTank.Validator.NEVER, ProcessorFluidTank.Callback.UPDATE_RECIPE);
    }

    public Builder<Recipe> addInputFluid(final int capacity, final ProcessorFluidTank.Validator insertValidator, final ProcessorFluidTank.Validator extractValidator, final ProcessorFluidTank.Callback onChanged) {
      this.fluidSlots.add(new ProcessorFluidTank<>(this.processor, capacity, insertValidator, extractValidator, onChanged.andThen(this.onFluidChanged)));
      return this;
    }

    public Builder<Recipe> addOutputFluid(final int capacity) {
      return this.addOutputFluid(capacity, ProcessorFluidTank.Validator.NEVER, ProcessorFluidTank.Validator.ALWAYS, ProcessorFluidTank.Callback.NOOP);
    }

    public Builder<Recipe> addOutputFluid(final int capacity, final ProcessorFluidTank.Validator insertValidator, final ProcessorFluidTank.Validator extractValidator, final ProcessorFluidTank.Callback onChanged) {
      this.fluidSlots.add(new ProcessorFluidTank<>(this.processor, capacity, insertValidator, extractValidator, onChanged.andThen(this.onFluidChanged)));
      return this;
    }
  }
}
