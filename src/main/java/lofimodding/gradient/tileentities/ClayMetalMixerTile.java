package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.fluids.GradientFluid;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.IGradientFluidHandler;
import lofimodding.gradient.network.UpdateClayMetalMixerNeighboursPacket;
import lofimodding.gradient.recipes.AlloyRecipe;
import lofimodding.gradient.utils.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClayMetalMixerTile extends HeatSinkerTile {
  @CapabilityInject(IGradientFluidHandler.class)
  private static Capability<IGradientFluidHandler> FLUID_HANDLER_CAPABILITY;

  private final Map<Direction, IGradientFluidHandler> inputs = new EnumMap<>(Direction.class);
  private final Map<Direction, GradientFluidStack> flowing = new EnumMap<>(Direction.class);
  private final Map<GradientFluid, List<Direction>> fluidSideMap = new HashMap<>();

  @Nullable
  private IGradientFluidHandler output;

  @Nullable
  private AlloyRecipe recipe;
  private int recipeTicks;

  private final Random rand = new Random();

  public ClayMetalMixerTile() {
    super(GradientTileEntities.CLAY_METAL_MIXER.get());
  }

  public boolean isConnected(final Direction side) {
    return this.inputs.get(side) != null;
  }

  public GradientFluidStack getFlowingFluid(final Direction side) {
    return this.flowing.getOrDefault(side, GradientFluidStack.EMPTY);
  }

  public void inputUpdated(final Direction side) {
    if(this.inputs.containsKey(side)) {
      this.updateRecipe();
    }
  }

  public void outputUpdated() {

  }

  public void inputChanged(final Direction side, @Nullable final IGradientFluidHandler fluidHandler) {
    this.inputs.put(side, fluidHandler);

    UpdateClayMetalMixerNeighboursPacket.send(this.world.getDimension().getType(), this.pos);
  }

  public void outputChanged(@Nullable final IGradientFluidHandler fluidHandler) {
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

    final NonNullList<GradientFluidStack> fluids = NonNullList.create();

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      final IGradientFluidHandler handler = this.inputs.get(side);

      if(handler != null) {
        final GradientFluidStack fluidStack = handler.drain(Float.MAX_VALUE, IGradientFluidHandler.FluidAction.SIMULATE);

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
  public void tick() {
    this.flowing.clear();
    super.tick();
  }

  @Override
  protected void tickBeforeCooldown() {

  }

  @Override
  protected void tickAfterCooldown() {
    if(this.recipe != null && this.output != null) {
      if(this.recipeTicks == 0) {
        // Only mix if there's room
        if(this.output.fill(this.recipe.getFluidOutput(), IGradientFluidHandler.FluidAction.SIMULATE) < this.recipe.getFluidOutput().getAmount()) {
          return;
        }

        // Copy ref to recipe - it can be updated inside of the for loop by block updates
        final AlloyRecipe recipe = this.recipe;
        float temperature = 0.0f;
        float total = 0.0f;

        for(final GradientFluidStack recipeFluid : recipe.getFluidInputs()) {
          float remaining = recipeFluid.getAmount();
          int failed = 0;

          final List<Direction> sides = this.fluidSideMap.get(recipeFluid.getFluid());

          while(remaining > 0.0f || failed >= 10) {
            final int sideIndex = this.rand.nextInt(sides.size());
            final Direction side = sides.get(sideIndex);

            final IGradientFluidHandler fluidHandler = this.inputs.get(side);
            final GradientFluidStack drained = fluidHandler.drain(Math.min(0.001f, remaining), IGradientFluidHandler.FluidAction.EXECUTE);

            if(!drained.isEmpty()) {
              temperature += drained.getTemperature() * drained.getAmount();
              total += drained.getAmount();
              remaining -= drained.getAmount();
              this.flowing.put(side, drained);
            } else {
              failed++;
            }
          }
        }

        final GradientFluidStack output = new GradientFluidStack(recipe.getFluidOutput().getFluid(), recipe.getFluidOutput().getAmount(), temperature / total);
        this.output.fill(output, IGradientFluidHandler.FluidAction.EXECUTE);

        this.recipeTicks = this.getTicksForRecipe(recipe);
      } else {
        this.recipeTicks--;
      }
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
  private IGradientFluidHandler getFluidHandler(final IBlockReader world, final BlockPos pos, final Direction side) {
    final TileEntity output = world.getTileEntity(pos);

    if(output == null) {
      return null;
    }

    return output.getCapability(FLUID_HANDLER_CAPABILITY, side).orElse(null);
  }
}
