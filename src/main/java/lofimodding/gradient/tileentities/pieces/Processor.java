package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.recipes.IGradientRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
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
  private final LazyOptional<ItemStackHandler> lazyInv;

  private final Set<Stage> stages = new HashSet<>();

  @Nullable
  private Recipe recipe;
  private int ticks;
  private int maxTicks;

  public Processor(final IRecipeType<Recipe> recipeType, final Consumer<Builder> builder) {
    this.recipeType = recipeType;

    final Builder b = new Builder();
    builder.accept(b);

    this.itemSlots = b.itemSlots;
    this.itemInputSlots = b.itemInputSlots;
    this.itemOutputSlots = b.itemOutputSlots;

    this.inv = new ProcessorItemHandler<>(this, this.itemSlots.size());
    this.lazyInv = LazyOptional.of(() -> this.inv);
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
    this.itemInputSlots.get(slot).set(this.inv, stack.split(1));

    return stack;
  }

  public boolean tick() {
    if(this.hasRecipe()) {
      this.ticks++;

      if(this.isFinished()) {
        this.outputItem();
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

  private void outputItem() {
    final Recipe recipe = this.recipe;

    for(final ItemSlot slot : this.itemInputSlots) {
      slot.extract(this.inv, 1, false);
    }

    this.inv.disableValidation();

    for(int slot = 0; slot < this.itemOutputSlots.size(); slot++) {
      this.itemOutputSlots.get(slot).insert(this.inv, recipe.getOutput(slot), false);
    }

    this.inv.enableValidation();
  }

  public boolean hasRecipe() {
    return this.recipe != null;
  }

  private void updateRecipe() {
    this.recipe = RecipeUtils.getRecipe(this.recipeType, this::recipeMatches).orElse(null);
    this.ticks = 0;

    if(this.hasRecipe()) {
      this.maxTicks = this.recipe.getTicks() * 3;
    } else {
      this.maxTicks = Integer.MAX_VALUE;
    }
  }

  private boolean recipeMatches(final IGradientRecipe recipe) {
    final NonNullList<ItemStack> inputs = NonNullList.create();

    for(final ItemSlot slot : this.itemInputSlots) {
      inputs.add(slot.get(this.inv));
    }

    return
      recipe.matchesStages(this.stages) &&
      recipe.matchesItems(inputs);
  }

  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("Inventory", this.inv.serializeNBT());

    final ListNBT stagesList = new ListNBT();
    for(final Stage stage : this.stages) {
      stagesList.add(StringNBT.valueOf(stage.getRegistryName().toString()));
    }

    compound.put("Stages", stagesList);
    compound.putInt("Ticks", this.ticks);
    return compound;
  }

  public void read(final CompoundNBT compound) {
    final CompoundNBT inv = compound.getCompound("Inventory");
    inv.remove("Size");
    this.inv.deserializeNBT(inv);

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

    private boolean validate;

    public ProcessorItemHandler(final Processor<Recipe> processor, final int size) {
      super(size);
      this.processor = processor;
    }

    public void enableValidation() {
      this.validate = true;
    }

    public void disableValidation() {
      this.validate = false;
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
      return !this.validate || super.isItemValid(slot, stack) && this.processor.itemSlots.get(slot).validator.test(this, stack);
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
  }

  public static class ItemSlot {
    private final int index;
    private final int limit;
    private final Validator validator;
    private final Callback onChanged;

    public ItemSlot(final int index, final int limit, final Validator validator, final Callback onChanged) {
      this.index = index;
      this.limit = limit;
      this.validator = validator;
      this.onChanged = onChanged;
    }

    public ItemStack get(final ItemStackHandler inv) {
      return inv.getStackInSlot(this.index);
    }

    public void set(final ItemStackHandler inv, final ItemStack stack) {
      inv.setStackInSlot(this.index, stack);
    }

    public ItemStack insert(final ItemStackHandler inv, final ItemStack stack, final boolean simulate) {
      return inv.insertItem(this.index, stack, simulate);
    }

    public ItemStack extract(final ItemStackHandler inv, final int amount, final boolean simulate) {
      return inv.extractItem(this.index, amount, simulate);
    }
  }

  @FunctionalInterface
  public interface Validator extends BiPredicate<ProcessorItemHandler<?>, ItemStack> {
    Validator ALWAYS = (inv, stack) -> true;
    Validator NEVER = (inv, stack) -> false;
  }

  @FunctionalInterface
  public interface Callback extends BiConsumer<ProcessorItemHandler<?>, ItemStack> {
    Callback NOOP = (inv, stack) -> { };
    Callback UPDATE_RECIPE = (inv, stack) -> inv.processor.updateRecipe();
  }

  public static class Builder {
    private final List<ItemSlot> itemSlots = new ArrayList<>();
    private final List<ItemSlot> itemInputSlots = new ArrayList<>();
    private final List<ItemSlot> itemOutputSlots = new ArrayList<>();
    private int slotIndex;

    public Builder addInputItem() {
      this.addInputItem(64, Validator.ALWAYS, Callback.UPDATE_RECIPE);
      return this;
    }

    public Builder addInputItem(final int limit, final Validator validator, final Callback onChanged) {
      final ItemSlot slot = new ItemSlot(this.slotIndex++, limit, validator, onChanged);
      this.itemSlots.add(slot);
      this.itemInputSlots.add(slot);
      return this;
    }

    public Builder addOutputItem() {
      this.addOutputItem(64, Validator.NEVER, Callback.NOOP);
      return this;
    }

    public Builder addOutputItem(final int limit, final Validator validator, final Callback onChanged) {
      final ItemSlot slot = new ItemSlot(this.slotIndex++, limit, validator, onChanged);
      this.itemSlots.add(slot);
      this.itemOutputSlots.add(slot);
      return this;
    }
  }
}
