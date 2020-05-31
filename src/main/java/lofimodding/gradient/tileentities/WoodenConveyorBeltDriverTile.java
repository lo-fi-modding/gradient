package lofimodding.gradient.tileentities;

import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.WoodenConveyorBeltBlock;
import lofimodding.gradient.blocks.WoodenConveyorBeltDriverBlock;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.IEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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
  private float beltSpeed;

  private boolean firstTick = true;

  public WoodenConveyorBeltDriverTile() {
    super(GradientTileEntities.WOODEN_CONVEYOR_BELT_DRIVER.get());
  }

  public float getBeltSpeed() {
    return this.beltSpeed;
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
      WorldUtils.notifyUpdate(this.world, this.pos);
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
    if(this.firstTick) {
      for(final Direction side : Direction.Plane.HORIZONTAL) {
        if(this.world.getBlockState(this.pos.offset(side)).getBlock() == GradientBlocks.WOODEN_CONVEYOR_BELT.get()) {
          this.addBelt(side);
        }
      }

      this.firstTick = false;
    }

    if(this.world.isRemote) {
      return;
    }

    if(this.beltCount == 0 || this.node.getEnergy() < 0.0001f) {
      if(this.beltSpeed != 0.0f) {
        this.beltSpeed = 0.0f;
        WorldUtils.notifyUpdate(this.world, this.pos);
      }

      this.beltSpeed = 0.0f;
      return;
    }

    final float neededEnergy = 0.005f * this.beltCount;
    final float extractedEnergy = this.node.removeEnergy(neededEnergy, IEnergyStorage.Action.EXECUTE);
    final float newBeltSpeed = extractedEnergy / neededEnergy;

    if(newBeltSpeed != this.beltSpeed) {
      this.beltSpeed = newBeltSpeed;
      WorldUtils.notifyUpdate(this.world, this.pos);
    }

    for(final Direction side : Direction.Plane.HORIZONTAL) {
      final List<WoodenConveyorBeltTile> belts = this.belts.computeIfAbsent(side, key -> new ArrayList<>());

      if(!belts.isEmpty()) {
        final WoodenConveyorBeltTile belt = belts.get(0);
        final Direction beltFacing = belt.getWorld().getBlockState(belt.getPos()).get(WoodenConveyorBeltBlock.FACING);

        for(final Entity entity : this.world.getEntitiesWithinAABB(Entity.class, this.movingBoxes.get(side))) {
          entity.setMotion(entity.getMotion().add(beltFacing.getXOffset() * 0.025d * this.beltSpeed, 0.0d, beltFacing.getZOffset() * 0.025d * this.beltSpeed));
          entity.velocityChanged = true;
        }
      }
    }
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    this.handleUpdateTag(pkt.getNbtCompound());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    final CompoundNBT tag = this.write(new CompoundNBT());
    tag.putFloat("BeltSpeed", this.beltSpeed);
    return tag;
  }

  @Override
  public void handleUpdateTag(final CompoundNBT tag) {
    super.handleUpdateTag(tag);
    this.beltSpeed = tag.getFloat("BeltSpeed");

    for(final Direction direction : Direction.Plane.HORIZONTAL) {
      this.removeBelt(direction);
      this.addBelt(direction);
    }
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
    nbt.put("Energy", this.node.write());
    return super.write(nbt);
  }

  @Override
  public void read(final CompoundNBT nbt) {
    final CompoundNBT energy = nbt.getCompound("Energy");
    this.node.read(energy);
    super.read(nbt);
  }
}
