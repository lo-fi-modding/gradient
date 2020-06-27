package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientFluids;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.network.UpdateClayMetalMixerNeighboursPacket;
import lofimodding.gradient.recipes.AlloyRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClayMetalMixerTile extends HeatSinkerTile {
  @CapabilityInject(IFluidHandler.class)
  private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY;

  private final Map<Direction, IFluidHandler> inputs = new EnumMap<>(Direction.class);
  private final Map<Direction, FluidStack> flowing = new EnumMap<>(Direction.class);
  private final Map<Direction, FluidStack> lastFlowing = new EnumMap<>(Direction.class);
  private final Map<Fluid, List<Direction>> fluidSideMap = new HashMap<>();

  @Nullable
  private IFluidHandler output;

  @Nullable
  private AlloyRecipe recipe;
  private int recipeTicks;

  private final Random rand = new Random();

  public ClayMetalMixerTile() {
    super(GradientTileEntities.CLAY_METAL_MIXER.get());
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return super.getRenderBoundingBox().expand(0.0d, -1.0d, 0.0d);
  }

  public boolean isConnected(final Direction side) {
    return this.inputs.get(side) != null;
  }

  public FluidStack getFlowingFluid(final Direction side) {
    return this.flowing.getOrDefault(side, FluidStack.EMPTY);
  }

  public void inputUpdated(final Direction side) {
    if(this.inputs.containsKey(side)) {
      this.updateRecipe();
    }
  }

  public void outputUpdated() {

  }

  public void inputChanged(final Direction side, @Nullable final IFluidHandler fluidHandler) {
    this.inputs.put(side, fluidHandler);

    UpdateClayMetalMixerNeighboursPacket.send(this.world.getDimension().getType(), this.pos);
  }

  public void outputChanged(@Nullable final IFluidHandler fluidHandler) {
    this.output = fluidHandler;

    UpdateClayMetalMixerNeighboursPacket.send(this.world.getDimension().getType(), this.pos);
  }

  public void updateAllSides() {
    this.output = this.getFluidHandler(this.world, this.pos.down(), Direction.UP);

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      this.inputs.put(side, this.getFluidHandler(this.world, this.pos.offset(side), side.getOpposite()));
    }
  }

  private void updateRecipe() {
    this.fluidSideMap.clear();

    final NonNullList<FluidStack> fluids = NonNullList.create();

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      final IFluidHandler handler = this.inputs.get(side);

      if(handler != null) {
        final FluidStack fluidStack = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

        if(!fluidStack.isEmpty()) {
          fluids.add(fluidStack);
          this.fluidSideMap.computeIfAbsent(fluidStack.getFluid(), key -> new ArrayList<>()).add(side);
        }
      }
    }

    this.recipe = RecipeUtils.getRecipe(AlloyRecipe.TYPE, r -> r.matches(fluids)).orElse(null);

    if(this.recipe != null) {
      this.recipeTicks = this.getTicksForRecipe(this.recipe);
    }
  }

  private int getTicksForRecipe(final AlloyRecipe recipe) {
    return (int)(recipe.getFluidOutput().getAmount() * 1.5f);
  }

  @Override
  public void firstTick() {
    super.firstTick();
    this.updateAllSides();
    this.outputUpdated();

    if(!this.world.isRemote) {
      this.updateRecipe();
    }
  }

  @Override
  protected void tickBeforeCooldown() {

  }

  @Override
  protected void tickAfterCooldown() {
    if(this.world.isRemote) {
      return;
    }

    if(this.recipe != null && this.output != null) {
      if(this.recipeTicks == 0) {
        // Only mix if there's room
        if(this.output.fill(this.recipe.getFluidOutput(), IFluidHandler.FluidAction.SIMULATE) < this.recipe.getFluidOutput().getAmount()) {
          this.flowing.clear();
          return;
        }

        // Copy ref to recipe - it can be updated inside of the for loop by block updates
        final AlloyRecipe recipe = this.recipe;

        for(final FluidStack recipeFluid : recipe.getFluidInputs()) {
          int remaining = recipeFluid.getAmount();
          int failed = 0;

          final List<Direction> sides = this.fluidSideMap.get(recipeFluid.getFluid());

          while(remaining > 0 || failed >= 10) {
            final int sideIndex = this.rand.nextInt(sides.size());
            final Direction side = sides.get(sideIndex);

            final IFluidHandler fluidHandler = this.inputs.get(side);
            final FluidStack drained = fluidHandler.drain(Math.min(1, remaining), IFluidHandler.FluidAction.EXECUTE);

            if(!drained.isEmpty()) {
              remaining -= drained.getAmount();
              this.flowing.put(side, drained);
            } else {
              failed++;
            }
          }
        }

        final FluidStack output = new FluidStack(recipe.getFluidOutput().getFluid(), recipe.getFluidOutput().getAmount());
        this.output.fill(output, IFluidHandler.FluidAction.EXECUTE);

        this.recipeTicks = this.getTicksForRecipe(recipe);
      } else {
        this.recipeTicks--;
      }
    } else {
      this.flowing.clear();
    }

    boolean different = false;

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      if(this.flowing.getOrDefault(side, FluidStack.EMPTY).getFluid() != this.lastFlowing.getOrDefault(side, FluidStack.EMPTY).getFluid()) {
        different = true;
        break;
      }
    }

    if(different) {
      this.sync();

      this.lastFlowing.clear();
      this.lastFlowing.putAll(this.flowing);
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

  @Nullable
  private IFluidHandler getFluidHandler(final IBlockReader world, final BlockPos pos, final Direction side) {
    final TileEntity output = world.getTileEntity(pos);

    if(output == null) {
      return null;
    }

    return output.getCapability(FLUID_HANDLER_CAPABILITY, side).orElse(null);
  }

  @Override
  public CompoundNBT write(final CompoundNBT tag) {
    final CompoundNBT flowing = new CompoundNBT();

    for(final Map.Entry<Direction, FluidStack> entry : this.flowing.entrySet()) {
      flowing.putString(entry.getKey().getName(), entry.getValue().getFluid().getRegistryName().toString());
    }

    final CompoundNBT nbt = super.write(tag);
    nbt.put("flowing", flowing);

    return nbt;
  }

  @Override
  public void read(final CompoundNBT tag) {
    super.read(tag);

    this.flowing.clear();
    final CompoundNBT flowing = tag.getCompound("flowing");

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      if(flowing.contains(side.getName())) {
        this.flowing.put(side, new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(flowing.getString(side.getName()))), GradientFluids.INGOT_AMOUNT));
      }
    }
  }
}
