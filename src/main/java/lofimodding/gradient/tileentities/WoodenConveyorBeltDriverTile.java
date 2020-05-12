package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.WoodenConveyorBeltBlock;
import lofimodding.gradient.blocks.WoodenConveyorBeltDriverBlock;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WoodenConveyorBeltDriverTile extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private final IKineticEnergyStorage node = new KineticEnergyStorage(1.0f, 1.0f, 0.0f);
  private final LazyOptional<IKineticEnergyStorage> lazyNode = LazyOptional.of(() -> this.node);

  private final Map<Direction, List<WoodenConveyorBeltTile>> belts = new EnumMap<>(Direction.class);
  private final Map<Direction, AxisAlignedBB> movingBoxes = new EnumMap<>(Direction.class);
  private int beltCount;

  private boolean firstTick = true;

  public WoodenConveyorBeltDriverTile() {
    super(GradientTileEntities.WOODEN_CONVEYOR_BELT_DRIVER.get());
  }

  @Override
  public void onLoad() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueConnection(this.pos, this);
  }

  public void onRemove() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueDisconnection(this.pos);
  }

  public void addBelt(final Direction side) {
    BlockPos beltPos = this.pos.offset(side);
    BlockState belt = this.world.getBlockState(beltPos);

    if(belt.getBlock() != GradientBlocks.WOODEN_CONVEYOR_BELT.get()) {
      return;
    }

    WoodenConveyorBeltTile te = WorldUtils.getTileEntity(this.world, beltPos, WoodenConveyorBeltTile.class);

    final Direction beltFacing = belt.get(WoodenConveyorBeltBlock.FACING);
    final List<WoodenConveyorBeltTile> beltParts = this.belts.computeIfAbsent(side, key -> new ArrayList<>());

    double minX = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double minZ = Double.MAX_VALUE;
    double maxZ = -Double.MAX_VALUE;

    while(te != null && belt.getBlock() == GradientBlocks.WOODEN_CONVEYOR_BELT.get() && belt.get(WoodenConveyorBeltBlock.FACING) == beltFacing) {
      final VoxelShape collisionShape = belt.getCollisionShape(this.world, beltPos);

      if(minX > collisionShape.getBoundingBox().minX + beltPos.getX()) {
        minX = collisionShape.getBoundingBox().minX + beltPos.getX();
      }

      if(maxX < collisionShape.getBoundingBox().maxX + beltPos.getX()) {
        maxX = collisionShape.getBoundingBox().maxX + beltPos.getX();
      }

      if(minZ > collisionShape.getBoundingBox().minZ + beltPos.getZ()) {
        minZ = collisionShape.getBoundingBox().minZ + beltPos.getZ();
      }

      if(maxZ < collisionShape.getBoundingBox().maxZ + beltPos.getZ()) {
        maxZ = collisionShape.getBoundingBox().maxZ + beltPos.getZ();
      }

      beltParts.add(te);
      te.addDriver(this, side);
      beltPos = beltPos.offset(beltFacing);
      belt = this.world.getBlockState(beltPos);
      te = WorldUtils.getTileEntity(this.world, beltPos, WoodenConveyorBeltTile.class);
    }

    beltPos = this.pos.offset(side).offset(beltFacing.getOpposite());
    belt = this.world.getBlockState(beltPos);
    te = WorldUtils.getTileEntity(this.world, beltPos, WoodenConveyorBeltTile.class);

    while(te != null && belt.getBlock() == GradientBlocks.WOODEN_CONVEYOR_BELT.get() && belt.get(WoodenConveyorBeltBlock.FACING) == beltFacing) {
      final VoxelShape collisionShape = belt.getCollisionShape(this.world, beltPos);

      if(minX > collisionShape.getBoundingBox().minX + beltPos.getX()) {
        minX = collisionShape.getBoundingBox().minX + beltPos.getX();
      }

      if(maxX < collisionShape.getBoundingBox().maxX + beltPos.getX()) {
        maxX = collisionShape.getBoundingBox().maxX + beltPos.getX();
      }

      if(minZ > collisionShape.getBoundingBox().minZ + beltPos.getZ()) {
        minZ = collisionShape.getBoundingBox().minZ + beltPos.getZ();
      }

      if(maxZ < collisionShape.getBoundingBox().maxZ + beltPos.getZ()) {
        maxZ = collisionShape.getBoundingBox().maxZ + beltPos.getZ();
      }

      beltParts.add(te);
      te.addDriver(this, side);
      beltPos = beltPos.offset(beltFacing.getOpposite());
      belt = this.world.getBlockState(beltPos);
      te = WorldUtils.getTileEntity(this.world, beltPos, WoodenConveyorBeltTile.class);
    }

    if(!beltParts.isEmpty()) {
      this.beltCount += beltParts.size();
      this.movingBoxes.put(side, new AxisAlignedBB(minX, this.pos.getY(), minZ, maxX, this.pos.getY() + 1.0d, maxZ));
    }
  }

  public void removeBelt(final Direction side) {
    final List<WoodenConveyorBeltTile> belts = this.belts.computeIfAbsent(side, key -> new ArrayList<>());

    for(final WoodenConveyorBeltTile belt : belts) {
      belt.removeDriver(this);
    }

    this.beltCount -= belts.size();
    belts.clear();
  }

  @Override
  public void tick() {
    if(this.world.isRemote) {
      return;
    }

    if(this.firstTick) {
      for(final Direction side : Direction.Plane.HORIZONTAL) {
        if(this.world.getBlockState(this.pos.offset(side)).getBlock() == GradientBlocks.WOODEN_CONVEYOR_BELT.get()) {
          this.addBelt(side);
        }
      }

      this.firstTick = false;
    }

    if(this.beltCount == 0 || this.node.getEnergy() < 0.0001f) {
      return;
    }

    final float neededEnergy = 0.005f * this.beltCount;
    final float extractedEnergy = this.node.removeEnergy(neededEnergy, false);
    final double beltSpeedModifier = extractedEnergy / neededEnergy;

    //TODO
//    for(final Direction side : Direction.Plane.HORIZONTAL) {
//      final List<WoodenConveyorBeltTile> belts = this.belts.computeIfAbsent(side, key -> new ArrayList<>());
//
//      if(!belts.isEmpty()) {
//        final WoodenConveyorBeltTile belt = belts.get(0);
//        final Direction beltFacing = belt.getFacing();
//
//        for(final Entity entity : this.world.getEntitiesWithinAABB(Entity.class, this.movingBoxes.get(side))) {
//          if(beltFacing.getXOffset() != 0) {
//            entity.getMotion().add(beltFacing.getXOffset() * 0.05d * beltSpeedModifier, 0.0d, 0.0d);
//            entity.velocityChanged = true;
//          }
//
//          if(beltFacing.getZOffset() != 0) {
//            entity.getMotion().add(0.0d, 0.0d, beltFacing.getZOffset() * 0.05d * beltSpeedModifier);
//            entity.velocityChanged = true;
//          }
//        }
//      }
//    }
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == STORAGE) {
      final BlockState state = this.world.getBlockState(this.pos);

      if(state.getBlock() == GradientBlocks.WOODEN_CONVEYOR_BELT_DRIVER.get() && state.get(WoodenConveyorBeltDriverBlock.FACING) == facing) {
        return this.lazyNode.cast();
      }
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public CompoundNBT write(final CompoundNBT nbt) {
    nbt.put("Energy", this.node.serializeNbt());
    return super.write(nbt);
  }

  @Override
  public void read(final CompoundNBT nbt) {
    final CompoundNBT energy = nbt.getCompound("Energy");
    this.node.deserializeNbt(energy);
    super.read(nbt);
  }
}
