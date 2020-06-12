package lofimodding.gradient.tileentities.pieces;

import java.util.function.Consumer;

public class PumpProcessor extends Processor {
  public PumpProcessor(final ProcessorItemHandler.Callback onItemChange, final ProcessorFluidTank.Callback onFluidChange, final Consumer<Builder> builder) {
    super(onItemChange, onFluidChange, builder);
  }
``
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
