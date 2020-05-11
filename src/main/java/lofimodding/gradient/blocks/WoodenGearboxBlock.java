package lofimodding.gradient.blocks;

import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.tileentities.WoodenGearboxTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class WoodenGearboxBlock extends Block {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  public WoodenGearboxBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);
    EnergyNetworkManager.getManager(world, STORAGE, TRANSFER).queueDisconnection(pos);
  }

  @Override
  public WoodenGearboxTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenGearboxTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
}
