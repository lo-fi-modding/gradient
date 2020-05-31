package lofimodding.gradient.blocks;

import lofimodding.gradient.Config;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.tileentities.WoodenAxleTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WoodenAxleBlock extends RotatedPillarBlock {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private static final VoxelShape SHAPE_X = makeCuboidShape(0.0d, 5.0d, 5.0d, 16.0d, 11.0d, 11.0d);
  private static final VoxelShape SHAPE_Y = makeCuboidShape(5.0d, 0.0d, 5.0d, 11.0d, 16.0d, 11.0d);
  private static final VoxelShape SHAPE_Z = makeCuboidShape(5.0d, 5.0d, 0.0d, 11.0d, 11.0d, 16.0d);

  public WoodenAxleBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD).notSolid());
  }

  @Override
  public void animateTick(final BlockState state, final World world, final BlockPos pos, final Random rand) {
    super.animateTick(state, world, pos, rand);

    final WoodenAxleTile axle = WorldUtils.getTileEntity(world, pos, WoodenAxleTile.class);

    if(axle != null) {
      final double maxEnergy = Config.ENET.WOODEN_AXLE_MAX_ENERGY.get();

      if(axle.getAverageEnergy() >= maxEnergy * 0.8f) {
        final Direction.Axis axis = state.get(AXIS);

        final double x;
        final double y;
        final double z;

        switch(axis) {
          case X:
            x = pos.getX() + rand.nextFloat();
            y = pos.getY() + MathHelper.nextFloat(rand, 0.25f, 0.75f);
            z = pos.getZ() + MathHelper.nextFloat(rand, 0.25f, 0.75f);
            break;

          case Y:
            x = pos.getX() + MathHelper.nextFloat(rand, 0.25f, 0.75f);
            y = pos.getY() + rand.nextFloat();
            z = pos.getZ() + MathHelper.nextFloat(rand, 0.25f, 0.75f);
            break;

          default:
            x = pos.getX() + MathHelper.nextFloat(rand, 0.25f, 0.75f);
            y = pos.getY() + MathHelper.nextFloat(rand, 0.25f, 0.75f);
            z = pos.getZ() + rand.nextFloat();
            break;
        }

        if(axle.getAverageEnergy() >= maxEnergy * 0.9f) {
          world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0d, 0.0d, 0.0d);
        } else {
          world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0d, 0.0d, 0.0d);
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockRenderType getRenderType(final BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip", Config.ENET.WOODEN_AXLE_MAX_ENERGY.get()));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    super.onReplaced(state, world, pos, newState, isMoving);
    EnergyNetworkManager.getManager(world, STORAGE, TRANSFER).queueDisconnection(pos);
  }

  @Override
  public WoodenAxleTile createTileEntity(final BlockState state, final IBlockReader world) {
    return new WoodenAxleTile();
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    switch(state.get(AXIS)) {
      case X:
        return SHAPE_X;

      case Z:
        return SHAPE_Z;
    }

    return SHAPE_Y;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public boolean allowsMovement(final BlockState state, final IBlockReader world, final BlockPos pos, final PathType type) {
    return false;
  }
}
