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

//TODO: how are fluid temperatures being handled?

public class ClayMetalMixerTile extends HeatSinkerTile {
  @CapabilityInject(IGradientFluidHandler.class)
  private static Capability<IGradientFluidHandler> FLUID_HANDLER_CAPABILITY;

  private static final int CYCLE_TICKS = 40;

  private boolean isAnimating;
  private int animationTicks;

  private final Map<Direction, IGradientFluidHandler> inputs = new EnumMap<>(Direction.class);
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

  public void inputUpdated(final Direction side) {
    if(this.inputs.containsKey(side)) {
      this.updateRecipe();
    }
  }

  public void outputUpdated() {
    if(this.world.isRemote) {
      if(this.output != null) {
        final GradientFluidStack fluidStack = this.output.drain(0.001f, IGradientFluidHandler.FluidAction.SIMULATE);

        if(!fluidStack.isEmpty()) {
          //TODO this.asm.transition("spinning");
          this.isAnimating = true;
          this.animationTicks = 0;
          return;
        }
      }

      //TODO this.asm.transition("idle");
      this.isAnimating = false;
    }
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
    this.output = this.getFluidHandler(this.world, this.pos.down());

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      this.inputs.put(side, this.getFluidHandler(this.world, this.pos.offset(side)));
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
  public void onLoad() {
    super.onLoad();
    //TODO: this is causing a world-loading deadlock
    this.updateAllSides();
    this.outputUpdated();

    if(!this.world.isRemote) {
      this.updateRecipe();
    }
  }

  @Override
  protected void tickBeforeCooldown(final float tickScale) {

  }

  @Override
  protected void tickAfterCooldown(final float tickScale) {
    if(this.world.isRemote) {
      if(this.isAnimating) {
        //TODO this.ticksValue.setValue((float)this.animationTicks / CYCLE_TICKS);
        this.animationTicks++;
        this.animationTicks %= CYCLE_TICKS;
      }

      return;
    }

    if(this.recipe != null && this.output != null) {
      if(this.recipeTicks == 0) {
        // Only mix if there's room
        if(this.output.fill(this.recipe.getFluidOutput(), IGradientFluidHandler.FluidAction.SIMULATE) < this.recipe.getFluidOutput().getAmount()) {
          return;
        }

        // Copy ref to recipe - it can be updated inside of the for loop by block updates
        final AlloyRecipe recipe = this.recipe;

        for(final GradientFluidStack recipeFluid : recipe.getFluidInputs()) {
          float remaining = recipeFluid.getAmount();
          int failed = 0;

          final List<Direction> sides = this.fluidSideMap.get(recipeFluid.getFluid());

          //TODO: need to rework fluid amounts
          while(remaining > 0.0f || failed >= 10) {
            final int sideIndex = this.rand.nextInt(sides.size());
            final Direction side = sides.get(sideIndex);

            final IGradientFluidHandler fluidHandler = this.inputs.get(side);
            final GradientFluidStack drained = fluidHandler.drain(1, IGradientFluidHandler.FluidAction.EXECUTE);

            if(!drained.isEmpty()) {
              remaining--;
            } else {
              failed++;
            }
          }
        }

        this.output.fill(recipe.getFluidOutput(), IGradientFluidHandler.FluidAction.EXECUTE);

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
  private IGradientFluidHandler getFluidHandler(final IBlockReader world, final BlockPos pos) {
    final TileEntity output = world.getTileEntity(pos);

    if(output == null) {
      return null;
    }

    return output.getCapability(FLUID_HANDLER_CAPABILITY, Direction.UP).orElse(null);
  }
}
