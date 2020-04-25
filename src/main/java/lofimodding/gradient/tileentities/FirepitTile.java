package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class FirepitTile extends TileEntity implements ITickableTileEntity {
  public FirepitTile() {
    super(GradientTileEntities.FIREPIT.get());
  }

  @Override
  public void tick() {

  }
}
