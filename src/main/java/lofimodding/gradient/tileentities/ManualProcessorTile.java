package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.tileentities.pieces.IProcessor;
import lofimodding.gradient.tileentities.pieces.ManualEnergySource;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class ManualProcessorTile extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IItemHandler.class)
  private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

  private final ItemStackHandler inventory = new ItemStackHandler(2);

  private final ManualEnergySource energySource = new ManualEnergySource();
  private final IProcessor processor;

  public ManualProcessorTile(final IProcessor processor) {
    super(GradientTileEntities.GRINDSTONE.get());
    this.processor = processor;
  }

  @Override
  public void tick() {
    if(this.world.isRemote) {
      return;
    }

    if(this.energySource.consumeEnergy()) {
      if(this.processor.tick()) {
        this.onProcessorTick();
      }
    }
  }

  protected abstract void onProcessorTick();
}
