package lofimodding.gradient.tileentities;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientFluids;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.FirepitBlock;
import lofimodding.gradient.recipes.CookingRecipe;
import lofimodding.gradient.recipes.FuelRecipe;
import lofimodding.gradient.recipes.HardeningRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import lofimodding.progression.capabilities.Progress;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// With a single furnace, a crucible will heat up to 1038 degrees
// Two furnaces, 1152
// Two layers of furnaces, 1693
// Three layers of furnaces, 1725

public class FirepitTile extends HeatProducerTile {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  @CapabilityInject(IFluidHandler.class)
  private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY;

  public static final int FUEL_SLOTS_COUNT = 3;
  public static final int INPUT_SLOTS_COUNT = 1;
  public static final int OUTPUT_SLOTS_COUNT = 1;
  public static final int TOTAL_SLOTS_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

  public static final int FIRST_FUEL_SLOT = 0;
  public static final int FIRST_INPUT_SLOT = FIRST_FUEL_SLOT + FUEL_SLOTS_COUNT;
  public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;

  private static final int FLUID_CAPACITY = 100;

  private final Fluid air = GradientFluids.AIR.get();

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
      if(FirepitTile.this.force) {
        return true;
      }

      // Fuel
      if(slot < FUEL_SLOTS_COUNT) {
        return
          !FirepitTile.this.hasFuel(slot) &&
          RecipeUtils.getRecipe(FuelRecipe.TYPE, r -> r.matches(stack)).isPresent();
      }

      // Input
      if(slot < FIRST_OUTPUT_SLOT) {
        for(int i = 0; i < INPUT_SLOTS_COUNT; i++) {
          this.inputTemp.setStackInSlot(i, this.getStackInSlot(i + FIRST_INPUT_SLOT));
        }

        this.inputTemp.setStackInSlot(slot - FIRST_INPUT_SLOT, stack);

        return
          !FirepitTile.this.hasInput() &&
          !FirepitTile.this.hasFurnace(FirepitTile.this.world.getBlockState(FirepitTile.this.pos)) &&
          RecipeUtils.getRecipe(CookingRecipe.TYPE, recipe -> recipe.matches(this.inputTemp, 0, INPUT_SLOTS_COUNT - 1)).isPresent();
      }

      // Output
      return false;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
      if(!FirepitTile.this.force) {
        if(slot < FUEL_SLOTS_COUNT && FirepitTile.this.isBurning(slot)) {
          return ItemStack.EMPTY;
        }

        if(slot < FIRST_OUTPUT_SLOT && FirepitTile.this.isCooking()) {
          return ItemStack.EMPTY;
        }
      }

      return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(final int slot) {
      final ItemStack stack = this.getStackInSlot(slot);

      if(slot < FUEL_SLOTS_COUNT) {
        if(!stack.isEmpty()) {
          RecipeUtils.getRecipe(FuelRecipe.TYPE, r -> r.matches(stack)).ifPresent(recipe -> {
            FirepitTile.this.fuels[slot] = new Fuel(recipe);
          });
        } else {
          FirepitTile.this.fuels[slot] = null;
        }
      } else if(slot < FIRST_OUTPUT_SLOT) {
        if(!stack.isEmpty()) {
          FirepitTile.this.updateRecipe();
        } else {
          FirepitTile.this.recipe = null;
        }

        FirepitTile.this.ticks = 0;
      }

      FirepitTile.this.sync();
    }
  };

  public final FluidTank tank = new FluidTank(FLUID_CAPACITY, stack -> stack.getFluid() == FirepitTile.this.air) {
    @Override
    protected void onContentsChanged() {
      if(!FirepitTile.this.world.isRemote) {
        FirepitTile.this.sync();
      }
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
      if(!FirepitTile.this.canDrain) {
        return FluidStack.EMPTY;
      }

      return super.drain(maxDrain, action);
    }
  };

  private final LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inventory);
  private final LazyOptional<IFluidHandler> lazyTank = LazyOptional.of(() -> this.tank);
  private boolean canDrain;

  @Nullable
  private CookingRecipe recipe;
  private final NonNullList<Stage> stages = NonNullList.create();
  private int ticks;

  private boolean force;
  private final Fuel[] fuels = new Fuel[FUEL_SLOTS_COUNT];

  private final Map<BlockPos, Hardening> hardenables = new HashMap<>();

  private int lastLight;

  public FirepitTile() {
    super(GradientTileEntities.FIREPIT.get());
  }

  public boolean hasFurnace(final BlockState state) {
    return state.getBlock() == GradientBlocks.FIREPIT.get() && state.get(FirepitBlock.HAS_FURNACE);
  }

  public boolean isBurning() {
    for(int i = 0; i < FUEL_SLOTS_COUNT; i++) {
      if(this.isBurning(i)) {
        return true;
      }
    }

    return false;
  }

  public boolean isBurning(final int slot) {
    return this.fuels[slot] != null && this.fuels[slot].isBurning;
  }

  public boolean isCooking() {
    return this.recipe != null && this.ticks != 0;
  }

  public float getCookingPercent() {
    if(!this.isCooking()) {
      return 0.0f;
    }

    return (float)this.ticks / this.recipe.ticks;
  }

  public Fuel getBurningFuel(final int slot) {
    return this.fuels[slot];
  }

  public boolean hasFuel(final int slot) {
    return !this.getFuel(slot).isEmpty();
  }

  public ItemStack getFuel(final int slot) {
    return this.inventory.getStackInSlot(FIRST_FUEL_SLOT + slot);
  }

  public ItemStack takeFuel(final int slot) {
    return this.inventory.extractItem(FIRST_FUEL_SLOT + slot, this.inventory.getSlotLimit(FIRST_FUEL_SLOT + slot), false);
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
    for(int slot = 0; slot < FIRST_OUTPUT_SLOT; slot++) {
      if(this.inventory.isItemValid(slot, stack)) {
        this.inventory.setStackInSlot(slot, stack.split(1));

        if(slot == FIRST_INPUT_SLOT) {
          this.stages.clear();
          this.stages.addAll(Progress.get(player).getStages());
        }

        return stack;
      }
    }

    return stack;
  }

  public int getLightLevel(final BlockState state) {
    if(this.getHeat() == 0) {
      return 0;
    }

    return Math.min(
      !this.hasFurnace(state) ?
        (int)(this.getHeat() /  800 * 11) + 4 :
        (int)(this.getHeat() / 1000 *  9) + 2,
      15
    );
  }

  public void light() {
    if(this.isBurning()) {
      return;
    }

    this.setHeat(Math.max(50, this.getHeat()));
    this.sync();
  }

  public void attachFurnace() {
    this.sync();
  }

  public void updateHardenable(final BlockPos pos, final Set<Stage> stages) {
    if(this.world.isRemote) {
      return;
    }

    final BlockState state = this.getWorld().getBlockState(pos);

    final HardeningRecipe recipe = RecipeUtils.getRecipe(HardeningRecipe.TYPE, r -> r.matches(state, stages)).orElse(null);

    if(recipe == null) {
      this.hardenables.remove(pos);
      return;
    }

    this.hardenables.put(pos, new Hardening(recipe, stages));
  }

  public void updateSurroundingHardenables(final Set<Stage> stages) {
    final BlockPos north = this.pos.north();
    final BlockPos south = this.pos.south();

    this.updateHardenable(north.east(), stages);
    this.updateHardenable(north, stages);
    this.updateHardenable(north.west(), stages);
    this.updateHardenable(this.pos.east(), stages);
    this.updateHardenable(this.pos.west(), stages);
    this.updateHardenable(south.east(), stages);
    this.updateHardenable(south, stages);
    this.updateHardenable(south.west(), stages);
  }

  @Override
  protected void tickBeforeCooldown(final float tickScale) {
    this.igniteFuel();
  }

  @Override
  protected void tickAfterCooldown(final float tickScale) {
    super.tickAfterCooldown(tickScale);

    this.cook();
    this.updateLight();

    if(this.getWorld().isRemote) {
      this.generateParticles();
      this.playSounds();
    } else {
      this.hardenHardenables();
    }
  }

  private void igniteFuel() {
    for(int i = 0; i < FUEL_SLOTS_COUNT; i++) {
      if(!this.isBurning(i) && this.fuels[i] != null) {
        if(this.canIgnite(this.fuels[i])) {
          this.fuels[i].ignite();
          this.sync();
        }
      }
    }
  }

  private void cook() {
    if(this.recipe == null) {
      return;
    }

    if(this.ticks < this.recipe.ticks) {
      if(this.getHeat() >= this.recipe.temperature) {
        this.ticks++;
        this.markDirty();
      }
    }

    if(this.ticks >= this.recipe.ticks) {
      final ItemStack output = this.recipe.getRecipeOutput().copy();
      this.force = true;
      this.inventory.extractItem(FIRST_INPUT_SLOT, 1, false);
      this.inventory.insertItem(FIRST_OUTPUT_SLOT, output, false);
      this.force = false;
    }
  }

  private void hardenHardenables() {
    if(this.hardenables.isEmpty()) {
      return;
    }

    final Iterator<Map.Entry<BlockPos, Hardening>> it = this.hardenables.entrySet().iterator();

    final Map<BlockPos, BlockState> toAdd = new HashMap<>();

    while(it.hasNext()) {
      final Map.Entry<BlockPos, Hardening> entry = it.next();
      final BlockPos pos = entry.getKey();
      final Hardening hardening = entry.getValue();
      final BlockState current = this.getWorld().getBlockState(pos);

      if(!hardening.recipe.matches(current, hardening.stages)) {
        it.remove();
        continue;
      }

      hardening.tick();

      if(hardening.isHardened()) {
        toAdd.put(pos, hardening.recipe.getCraftingResult(current));
        it.remove();
      }
    }

    this.markDirty();

    for(final Map.Entry<BlockPos, BlockState> entry : toAdd.entrySet()) {
      this.getWorld().setBlockState(entry.getKey(), entry.getValue());
    }
  }

  private void updateLight() {
    final int light = this.getLightLevel(this.getBlockState());

    if(this.lastLight != light) {
      this.world.getLightManager().checkBlock(this.pos);

      this.lastLight = light;
    }
  }

  private float getHeatRatio() {
    return (float)this.tank.getFluidAmount() / this.tank.getCapacity();
  }

  private void generateParticles() {
    if(this.hasHeat()) {
      final Random rand = this.getWorld().rand;

      final int amount = 1 + (int)(this.getHeatRatio() / 10.0f);

      for(int i = 0; i < amount; i++) {
        // Fire
        if(this.isBurning()) {
          if(this.getHeat() >= 200 || rand.nextInt(600) >= 400 - this.getHeat() * 2) {
            final double radius = rand.nextDouble() * 0.25d;
            final double angle = rand.nextDouble() * Math.PI * 2;

            final double x = this.pos.getX() + 0.5d + radius * Math.cos(angle);
            final double z = this.pos.getZ() + 0.5d + radius * Math.sin(angle);

            this.getWorld().addParticle(ParticleTypes.FLAME, x, this.pos.getY() + 0.1d, z, 0.0d, 0.0d, 0.0d);
          }

          if(this.tank.getFluidAmount() > 0) {
            //TODO uhh empty if statement??
          }
        }

        // Smoke
        if(this.getHeat() >= 200 || !this.isBurning()) {
          final double radius = rand.nextDouble() * 0.35d;
          final double angle = rand.nextDouble() * Math.PI * 2;

          final double x = this.pos.getX() + 0.5d + radius * Math.cos(angle);
          final double z = this.pos.getZ() + 0.5d + radius * Math.sin(angle);

          this.getWorld().addParticle(ParticleTypes.SMOKE, x, this.pos.getY() + 0.1d, z, 0.0d, 0.0d, 0.0d);
        }
      }
    }
  }

  private void playSounds() {
    if(this.getHeat() > 0) {
      if(this.getWorld().rand.nextInt(40 - (int)(this.getHeatRatio() / 8.0f)) == 0) {
        this.getWorld().playSound(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.8f + this.getWorld().rand.nextFloat(), this.getWorld().rand.nextFloat() * 0.7f + 0.3f, false);
      }
    }
  }

  @Override
  protected float calculateHeatGain() {
    final float airBonus = 1.0f + this.getHeatRatio() / 3.0f;
    this.canDrain = true;
    this.tank.drain(2, IFluidHandler.FluidAction.EXECUTE);
    this.canDrain = false;

    float temperatureChange = 0;
    for(int slot = 0; slot < FUEL_SLOTS_COUNT; slot++) {
      if(this.isBurning(slot)) {
        final Fuel fuel = this.fuels[slot];

        fuel.tick();

        if(fuel.isDepleted()) {
          this.setFuelSlot(slot, ItemStack.EMPTY);
        }

        if(fuel.recipe.burnTemp > this.getHeat()) {
          temperatureChange += fuel.recipe.heatPerSec * airBonus;
        }
      }
    }

    return temperatureChange;
  }

  @Override
  protected float calculateHeatLoss(final BlockState state) {
    return !this.hasFurnace(state) ?
      (float)Math.pow(this.getHeat() /  500 + 1, 2) / 1.5f :
      (float)Math.pow(this.getHeat() / 1600 + 1, 2);
  }

  @Override
  protected float heatTransferEfficiency() {
    return 0.6f;
  }

  private void setFuelSlot(final int slot, final ItemStack stack) {
    this.inventory.setStackInSlot(FIRST_FUEL_SLOT + slot, stack);
  }

  private boolean canIgnite(final Fuel fuel) {
    return this.getHeat() >= fuel.recipe.ignitionTemp;
  }

  private void updateRecipe() {
    this.recipe = RecipeUtils.getRecipe(CookingRecipe.TYPE, recipe -> recipe.matches(this.inventory, this.stages, FIRST_INPUT_SLOT, FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT - 1)).orElse(null);
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    compound.put("inventory", this.inventory.serializeNBT());
    this.tank.writeToNBT(compound);

    final ListNBT fuels = new ListNBT();

    for(int i = 0; i < FUEL_SLOTS_COUNT; i++) {
      if(this.isBurning(i)) {
        final Fuel fuel = this.getBurningFuel(i);

        final CompoundNBT tag = new CompoundNBT();
        tag.putInt("slot", i);
        tag.putString("recipe", fuel.recipe.getId().toString());
        tag.putInt("ticks", fuel.burnTicks);
        tag.putBoolean("burning", fuel.isBurning);
        fuels.add(tag);
      }
    }

    compound.put("fuel", fuels);

    final ListNBT stagesNbt = new ListNBT();
    for(final Stage stage : this.stages) {
      stagesNbt.add(StringNBT.valueOf(stage.getRegistryName().toString()));
    }

    compound.put("stages", stagesNbt);
    compound.putInt("ticks", this.ticks);

    final ListNBT hardenings = new ListNBT();
    for(final Map.Entry<BlockPos, Hardening> entry : this.hardenables.entrySet()) {
      final CompoundNBT hardening = new CompoundNBT();
      hardening.put("pos", NBTUtil.writeBlockPos(entry.getKey()));
      hardening.putString("recipe", entry.getValue().recipe.getId().toString());

      final ListNBT recipeStagesNbt = new ListNBT();
      for(final Stage stage : entry.getValue().stages) {
        recipeStagesNbt.add(StringNBT.valueOf(stage.getRegistryName().toString()));
      }

      hardening.put("stages", recipeStagesNbt);
      hardening.putInt("ticks", entry.getValue().hardenTicks);

      hardenings.add(hardening);
    }

    compound.put("hardening", hardenings);

    return super.write(compound);
  }

  @Override
  public void read(final CompoundNBT compound) {
    this.lastLight = -1;

    Arrays.fill(this.fuels, null);

    final CompoundNBT inv = compound.getCompound("inventory");
    inv.remove("Size");
    this.inventory.deserializeNBT(inv);

    this.tank.readFromNBT(compound);

    final ListNBT fuels = compound.getList("fuel", Constants.NBT.TAG_COMPOUND);

    for(int i = 0; i < fuels.size(); i++) {
      final CompoundNBT tag = fuels.getCompound(i);

      final int slot = tag.getInt("slot");

      if(slot < FUEL_SLOTS_COUNT) {
        Gradient.getRecipeManager().getRecipe(new ResourceLocation(tag.getString("recipe"))).ifPresent(recipe -> {
          final int ticks = tag.getInt("ticks");
          final boolean burning = tag.getBoolean("burning");

          final Fuel fuel = new Fuel((FuelRecipe)recipe);
          fuel.burnTicks = ticks;
          fuel.isBurning = burning;

          this.fuels[slot] = fuel;
        });
      }
    }

    final ListNBT stagesNbt = compound.getList("stages", Constants.NBT.TAG_STRING);
    this.stages.clear();
    for(int i = 0; i < stagesNbt.size(); i++) {
      final Stage stage = Stage.REGISTRY.get().getValue(new ResourceLocation(stagesNbt.getString(i)));

      if(stage != null) {
        this.stages.add(stage);
      }
    }

    this.ticks = compound.getInt("ticks");

    this.hardenables.clear();

    for(final INBT tag : compound.getList("hardening", Constants.NBT.TAG_COMPOUND)) {
      final CompoundNBT hardeningNbt = (CompoundNBT)tag;

      final BlockPos pos = NBTUtil.readBlockPos(hardeningNbt.getCompound("pos"));
      Gradient.getRecipeManager().getRecipe(new ResourceLocation(hardeningNbt.getString("recipe"))).ifPresent(recipe -> {
        final ListNBT recipeStagesNbt = compound.getList("stages", Constants.NBT.TAG_STRING);
        final Set<Stage> stages = new HashSet<>();
        for(int i = 0; i < recipeStagesNbt.size(); i++) {
          final Stage stage = Stage.REGISTRY.get().getValue(new ResourceLocation(recipeStagesNbt.getString(i)));

          if(stage != null) {
            stages.add(stage);
          }
        }

        final int ticks = hardeningNbt.getInt("ticks");

        final Hardening hardening = new Hardening((HardeningRecipe)recipe, stages);
        hardening.hardenTicks = ticks;

        this.hardenables.put(pos, hardening);
      });
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

  public static final class Fuel {
    public final FuelRecipe recipe;
    private final int burnTicksTotal;
    private int burnTicks;
    private boolean isBurning;

    private Fuel(final FuelRecipe recipe) {
      this.recipe = recipe;
      this.burnTicksTotal = this.recipe.duration * 20;
    }

    private void tick() {
      this.burnTicks++;
    }

    private void ignite() {
      this.isBurning = true;
    }

    public boolean isDepleted() {
      return this.burnTicks >= this.burnTicksTotal;
    }

    public float percentBurned() {
      return (float)this.burnTicks / this.burnTicksTotal;
    }
  }

  public static final class Hardening {
    public final HardeningRecipe recipe;
    public final Set<Stage> stages;
    private int hardenTicks;

    private Hardening(final HardeningRecipe recipe, final Set<Stage> stages) {
      this.recipe = recipe;
      this.stages = stages;
    }

    private void tick() {
      this.hardenTicks++;
    }

    public boolean isHardened() {
      return this.hardenTicks >= this.recipe.ticks;
    }
  }
}
