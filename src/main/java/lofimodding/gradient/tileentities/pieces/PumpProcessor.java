package lofimodding.gradient.tileentities.pieces;

import lofimodding.gradient.tileentities.ProcessorTile;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class PumpProcessor extends Processor {
  private final Map<Direction, LazyOptional<IFluidHandler>> outputs = new EnumMap<>(Direction.class);

  public PumpProcessor(final ProcessorTile<?> tile, final ProcessorItemHandler.Callback onItemChange, final ProcessorFluidTank.Callback onFluidChange, final Consumer<Builder> builder) {
    super(tile, onItemChange, onFluidChange, builder);
  }

  @Override
  public void onAddToWorld() {
    super.onAddToWorld();

    this.outputs.clear();

    for(final Direction direction : Direction.values()) {
      if(direction == Direction.DOWN) {
        continue;
      }

      this.outputs.put(direction, FluidUtil.getFluidHandler(this.tile.getWorld(), this.tile.getPos().offset(direction), direction.getOpposite()));
    }
  }

  @Override
  public void onRemoveFromWorld() {
    super.onRemoveFromWorld();
    this.outputs.clear();
  }

  //TODO: update on neighbour changed

  @Override
  public boolean tick(final boolean isClient) {
    return false;
  }

  @Override
  public int getTicks() {
    return 0;
  }

  @Override
  public boolean hasWork() {
    return false;
  }

  @Override
  protected void onInputsChanged() {

  }
}
