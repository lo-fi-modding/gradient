package lofimodding.gradient.entities;

import lofimodding.gradient.GradientEntities;
import lofimodding.gradient.GradientItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class PebbleEntity extends ProjectileItemEntity {
  public PebbleEntity(final EntityType<? extends ProjectileItemEntity> type, final World world) {
    super(type, world);
  }

  public PebbleEntity(final World world) {
    this(GradientEntities.PEBBLE.get(), world);
  }

  public PebbleEntity(final EntityType<? extends ProjectileItemEntity> type, final double x, final double y, final double z, final World world) {
    super(type, x, y, z, world);
  }

  public PebbleEntity(final double x, final double y, final double z, final World world) {
    this(GradientEntities.PEBBLE.get(), x, y, z, world);
  }

  public PebbleEntity(final EntityType<? extends ProjectileItemEntity> type, final LivingEntity thrower, final World world) {
    super(type, thrower, world);
  }

  public PebbleEntity(final LivingEntity thrower, final World world) {
    this(GradientEntities.PEBBLE.get(), thrower, world);
  }

  @Override
  protected Item getDefaultItem() {
    return GradientItems.PEBBLE.get();
  }

  @OnlyIn(Dist.CLIENT)
  private IParticleData makeParticle() {
    return new ItemParticleData(ParticleTypes.ITEM, new ItemStack(this.getDefaultItem()));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(final byte id) {
    if(id == 3) {
      final IParticleData iparticledata = this.makeParticle();

      for(int i = 0; i < 8; ++i) {
        this.world.addParticle(iparticledata, this.getPosX(), this.getPosY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
      }
    }
  }

  @Override
  protected void onImpact(final RayTraceResult result) {
    if(result.getType() == RayTraceResult.Type.ENTITY) {
      final Entity entity = ((EntityRayTraceResult)result).getEntity();
      entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.5f);
    }

    if(!this.world.isRemote) {
      this.world.setEntityState(this, (byte)3);
      this.remove();
    }
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
