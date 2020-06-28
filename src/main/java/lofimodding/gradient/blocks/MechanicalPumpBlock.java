package lofimodding.gradient.blocks;

import lofimodding.gradient.tileentities.MechanicalPumpTile;
import lofimodding.gradient.tileentities.pieces.KineticEnergySource;
import lofimodding.gradient.tileentities.pieces.ProcessorTier;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockReader;

public class MechanicalPumpBlock extends ProcessorBlock<KineticEnergySource, MechanicalPumpTile> {
  public MechanicalPumpBlock() {
    super(MechanicalPumpTile.class, ProcessorTier.BASIC, Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }

  @Override
  public MechanicalPumpTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new MechanicalPumpTile();
  }
}
