package lofimodding.gradient.tileentities;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientTileEntities;
import lofimodding.gradient.blocks.WoodenCrankBlock;
import lofimodding.gradient.energy.EnergyNetworkManager;
import lofimodding.gradient.energy.IEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyStorage;
import lofimodding.gradient.energy.kinetic.IKineticEnergyTransfer;
import lofimodding.gradient.energy.kinetic.KineticEnergyStorage;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class WoodenCrankTile extends TileEntity implements ITickableTileEntity {
  @CapabilityInject(IKineticEnergyStorage.class)
  private static Capability<IKineticEnergyStorage> STORAGE;

  @CapabilityInject(IKineticEnergyTransfer.class)
  private static Capability<IKineticEnergyTransfer> TRANSFER;

  private static final int MAX_WORKERS = 4;
  private static final double WORKER_DISTANCE = 4.0d;

  private final IKineticEnergyStorage energy = new KineticEnergyStorage(5.0f, 0.0f, 5.0f) {
    @Override
    public void onEnergyChanged() {
      WoodenCrankTile.this.markDirty();
    }
  };

  private final LazyOptional<IKineticEnergyStorage> lazyEnergy = LazyOptional.of(() -> this.energy);

  private final LinkedList<WorkerData> workers = new LinkedList<>();
  private float workerTargetTheta;
  private int workerRotationSegments;

  @Nullable
  private ListNBT workersDeferredNbt;

  private int crankTicks;
  private boolean cranking;

  public WoodenCrankTile() {
    super(GradientTileEntities.WOODEN_CRANK.get());
  }

  @Override
  public void onLoad() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueConnection(this.pos, this);
  }

  @Override
  public void remove() {
    if(this.world.isRemote) {
      return;
    }

    EnergyNetworkManager.getManager(this.world, STORAGE, TRANSFER).queueDisconnection(this.pos);

    for(final WorkerData worker : this.workers) {
      worker.worker.detachHome();
    }
  }

  public void crank() {
    this.cranking = true;
  }

  public boolean hasWorker() {
    return !this.workers.isEmpty();
  }

  public Stream<AnimalEntity> getWorkers() {
    return this.workers.stream().map(worker -> worker.worker);
  }

  public void attachWorker(final AnimalEntity worker) {
    if(this.workers.size() >= MAX_WORKERS) {
      return;
    }

    worker.clearLeashed(true, false);
    worker.setHomePosAndDistance(this.pos, 3);

    this.workers.push(new WorkerData(worker));
    this.lastTicks = 0;
    this.actualTicks = 0;
    this.workerTargetTheta = 0.0f;
    this.workerRotationSegments = 0;

    this.updateTargets();
    this.markDirty();
    WorldUtils.notifyUpdate(this.world, this.pos);
  }

  public void detachWorkers(final PlayerEntity detacher) {
    if(this.workers.isEmpty()) {
      Gradient.LOGGER.error("Attempted to detach worker, but no worker present ({})", this.pos);
      return;
    }

    for(final WorkerData worker : this.workers) {
      worker.worker.detachHome();
      worker.worker.setLeashHolder(detacher, true);
      worker.restoreGoals();
    }

    this.workers.clear();
    this.lastTicks = 0;
    this.actualTicks = 0;
    this.workerTargetTheta = 0.0f;
    this.workerRotationSegments = 0;

    this.markDirty();
    WorldUtils.notifyUpdate(this.world, this.pos);
  }

  private void removeInvalidWorkers() {
    this.workers.removeIf(worker -> !worker.worker.isAlive() || worker.worker.getLeashed());
  }

  private boolean areWorkersAtTargets() {
    for(final WorkerData worker : this.workers) {
      if(!worker.isAtTarget()) {
        return false;
      }
    }

    return true;
  }

  private void moveToTargets() {
    for(final WorkerData worker : this.workers) {
      if(!worker.isAtTarget()) {
        worker.moveToTarget();
      }
    }
  }

  private void updateTargets() {
    int workerIndex = 0;
    for(final WorkerData worker : this.workers) {
      worker.updateTarget(workerIndex);
      workerIndex++;
    }
  }

  private void preventEating() {
    for(final WorkerData worker : this.workers) {
      if(worker.worker instanceof AbstractHorseEntity) {
        final AbstractHorseEntity horse = (AbstractHorseEntity)worker.worker;
        if(horse.isEatingHaystack()) {
          horse.setEatingHaystack(false);
        }
      }
    }
  }

  private double getSlowestWorker() {
    double speed = Float.MAX_VALUE;

    for(final WorkerData worker : this.workers) {
      final double workerSpeed = worker.getSpeed();

      if(workerSpeed < speed) {
        speed = workerSpeed;
      }
    }

    return speed;
  }

  private int lastTicks;
  private int actualTicks;

  @Override
  public void tick() {
    if(this.workersDeferredNbt != null) {
      this.loadWorkers(this.workersDeferredNbt);
      this.workersDeferredNbt = null;
    }

    if(this.world.isRemote) {
      return;
    }

    if(this.hasWorker()) {
      this.removeInvalidWorkers();

      this.actualTicks++;

      if(this.lastTicks != 0 && this.actualTicks < this.lastTicks + 20) {
        final float energy = this.lastTicks / 20000.0f * this.workers.size();
        this.energy.addEnergy(energy, IEnergyStorage.Action.EXECUTE);
      }

      this.preventEating();
      this.moveToTargets();

      if(this.areWorkersAtTargets()) {
        this.workerTargetTheta += Math.PI / 4.0d;
        this.workerRotationSegments++;

        if(this.workerRotationSegments >= 8) {
          this.workerTargetTheta = 0.0f;
          this.workerRotationSegments = 0;
          this.lastTicks = this.actualTicks;
          this.actualTicks = 0;
        }

        this.updateTargets();
        this.markDirty();
      }
    } else if(this.cranking) {
      this.crankTicks++;

      if(this.crankTicks >= 4) {
        this.cranking = false;
        this.crankTicks = 0;
        this.energy.addEnergy(1.0f, IEnergyStorage.Action.EXECUTE);
        this.markDirty();
      }
    }
  }

  @Override
  public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing) {
    if(capability == STORAGE) {
      final BlockState state = this.world.getBlockState(this.pos);

      if(state.getBlock() == GradientBlocks.WOODEN_CRANK.get()) {
        if(facing == state.get(WoodenCrankBlock.FACING)) {
          return this.lazyEnergy.cast();
        }
      }
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public CompoundNBT write(final CompoundNBT nbt) {
    nbt.put("Energy", this.energy.write());

    final ListNBT workers = new ListNBT();
    for(final WorkerData worker : this.workers) {
      workers.add(NBTUtil.writeUniqueId(worker.worker.getUniqueID()));
    }
    nbt.put("Workers", workers);

    nbt.putFloat("Theta", this.workerTargetTheta);
    nbt.putInt("RotationSegments", this.workerRotationSegments);
    nbt.putInt("CrankTicks", this.crankTicks);
    nbt.putBoolean("Cranking", this.cranking);
    nbt.putInt("LastTicks", this.lastTicks);
    nbt.putInt("ActualTicks", this.actualTicks);

    return super.write(nbt);
  }

  @Override
  public void read(final CompoundNBT nbt) {
    this.workers.clear();

    final CompoundNBT energy = nbt.getCompound("Energy");
    this.energy.read(energy);

    final ListNBT workers = nbt.getList("Workers", Constants.NBT.TAG_COMPOUND);
    if(this.world != null && !this.world.isRemote) {
      this.loadWorkers(workers);
    } else {
      this.workersDeferredNbt = workers;
    }

    this.workerTargetTheta = nbt.getFloat("Theta");
    this.workerRotationSegments = nbt.getInt("RotationSegments");
    this.crankTicks = nbt.getInt("CrankTicks");
    this.cranking = nbt.getBoolean("Cranking");
    this.lastTicks = nbt.getInt("LastTicks");
    this.actualTicks = nbt.getInt("ActualTicks");

    super.read(nbt);
  }

  private void loadWorkers(final ListNBT workers) {
    for(int i = 0; i < workers.size(); i++) {
      final UUID uuid = NBTUtil.readUniqueId(workers.getCompound(i));
      final List<AnimalEntity> animals = this.world.getEntitiesWithinAABB(AnimalEntity.class, new AxisAlignedBB(this.pos.getX() - 15.0d, this.pos.getY() - 15.0d, this.pos.getZ() - 15.0d, this.pos.getX() + 15.0d, this.pos.getY() + 15.0d, this.pos.getZ() + 15.0d), e -> e.getUniqueID().equals(uuid));

      if(!animals.isEmpty()) {
        this.attachWorker(animals.get(0));
      }
    }
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    this.read(pkt.getNbtCompound());
  }

  private final class WorkerData {
    private final AnimalEntity worker;
    private final Set<PrioritizedGoal> goals;
    private Vec3d targetPos;

    private WorkerData(final AnimalEntity worker) {
      this.worker = worker;
      this.goals = new HashSet<>(worker.goalSelector.goals);
      this.worker.goalSelector.goals.clear();
    }

    private void restoreGoals() {
      this.worker.goalSelector.goals.clear();
      this.worker.goalSelector.goals.addAll(this.goals);
    }

    private double getSpeed() {
      return this.worker.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
    }

    private double getTargetX(final int workerIndex) {
      return Math.cos(WoodenCrankTile.this.workerTargetTheta + Math.PI * 2.0d / WoodenCrankTile.this.workers.size() * workerIndex);
    }

    private double getTargetZ(final int workerIndex) {
      return Math.sin(WoodenCrankTile.this.workerTargetTheta + Math.PI * 2.0d / WoodenCrankTile.this.workers.size() * workerIndex);
    }

    private void updateTarget(final int workerIndex) {
      final double x = WoodenCrankTile.this.pos.getX() + 0.5d + this.getTargetX(workerIndex) * WORKER_DISTANCE;
      final double z = WoodenCrankTile.this.pos.getZ() + 0.5d + this.getTargetZ(workerIndex) * WORKER_DISTANCE;
      final double y = WoodenCrankTile.this.pos.getY();
      this.targetPos = new Vec3d(x, y, z);
    }

    private void moveToTarget() {
      final double maxDistanceToWaypoint = (this.worker.getWidth() > 0.75f ? this.worker.getWidth() / 2.0d : 0.75d - this.worker.getWidth() / 2.0d) * 3.5d;

      // Pathfinding seems to have changed since 1.12, and doesn't move exactly to the destination.
      // If we're >= 3.5 blocks away, we pathfind, otherwise we just move directly.
      if(Math.abs(this.worker.getPosX() - (this.targetPos.x + (int)(this.worker.getWidth() + 1) / 2.0d)) < maxDistanceToWaypoint && Math.abs(this.worker.getPosZ() - (this.targetPos.z + (int)(this.worker.getWidth() + 1) / 2.0d)) < maxDistanceToWaypoint && Math.abs(this.worker.getPosY() - this.targetPos.y) < 1.0d) {
        this.worker.getMoveHelper().setMoveTo(this.targetPos.x, this.targetPos.y, this.targetPos.z, WoodenCrankTile.this.getSlowestWorker() / this.getSpeed());
      } else {
        this.worker.getNavigator().tryMoveToXYZ(this.targetPos.x, this.targetPos.y, this.targetPos.z, WoodenCrankTile.this.getSlowestWorker() / this.getSpeed());
      }
    }

    private boolean isAtTarget() {
      if(this.targetPos == null) {
        return true;
      }

      final double maxDistanceToWaypoint = (this.worker.getWidth() > 0.75f ? this.worker.getWidth() / 2.0d : 0.75d - this.worker.getWidth() / 2.0d) * 1.75d;
      return Math.abs(this.worker.getPosX() - (this.targetPos.x + (int)(this.worker.getWidth() + 1) / 2.0d)) < maxDistanceToWaypoint && Math.abs(this.worker.getPosZ() - (this.targetPos.z + (int)(this.worker.getWidth() + 1) / 2.0d)) < maxDistanceToWaypoint && Math.abs(this.worker.getPosY() - this.targetPos.y) < 1.0d;
    }
  }
}
