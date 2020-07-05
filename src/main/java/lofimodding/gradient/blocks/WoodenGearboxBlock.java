package lofimodding.gradient.blocks;

import lofimodding.gradient.Config;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.tileentities.WoodenGearboxTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WoodenGearboxBlock extends Block {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  public WoodenGearboxBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent("gradient.mu_per_tick", Config.ENET.WOODEN_GEARBOX_MAX_ENERGY.get()));

    final double loss = Config.ENET.WOODEN_GEARBOX_LOSS_PER_BLOCK.get();

    if(loss == 0.0f) {
      tooltip.add(new TranslationTextComponent("gradient.mu_lossless"));
    } else {
      tooltip.add(new TranslationTextComponent("gradient.mu_loss", loss));
    }
  }

  @Override
  public void animateTick(final BlockState state, final World world, final BlockPos pos, final Random rand) {
    super.animateTick(state, world, pos, rand);

    final WoodenGearboxTile axle = WorldUtils.getTileEntity(world, pos, WoodenGearboxTile.class);

    if(axle != null) {
      final double maxEnergy = Config.ENET.WOODEN_GEARBOX_MAX_ENERGY.get();

      if(axle.getAverageEnergy() >= maxEnergy * 0.8f) {
        for(int i = 0; i < 3; i++) {
          final Direction side = Direction.random(this.RANDOM);

          final double x = pos.getX() + 0.5f + side.getXOffset() * 0.55f + (side.getAxis() == Direction.Axis.X ? 0.0f : MathHelper.nextFloat(this.RANDOM, -0.5f, 0.5f));
          final double y = pos.getY() + 0.5f + side.getYOffset() * 0.55f + (side.getAxis() == Direction.Axis.Y ? 0.0f : MathHelper.nextFloat(this.RANDOM, -0.5f, 0.5f));
          final double z = pos.getZ() + 0.5f + side.getZOffset() * 0.55f + (side.getAxis() == Direction.Axis.Z ? 0.0f : MathHelper.nextFloat(this.RANDOM, -0.5f, 0.5f));

          if(axle.getAverageEnergy() >= maxEnergy * 0.9f) {
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0d, 0.0d, 0.0d);
          } else {
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0d, 0.0d, 0.0d);
          }
        }
      }
    }
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
