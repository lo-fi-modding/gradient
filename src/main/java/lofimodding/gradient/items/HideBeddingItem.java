package lofimodding.gradient.items;

import com.mojang.datafixers.util.Either;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeDimension;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public class HideBeddingItem extends Item {
  public HideBeddingItem() {
    super(new Properties().group(GradientItems.GROUP).defaultMaxDamage(4));
  }

  @Override
  public ActionResultType onItemUse(final ItemUseContext context) {
    final World world = context.getWorld();

    if(world.isRemote()) {
      return ActionResultType.SUCCESS;
    }

    final PlayerEntity player = context.getPlayer();

    if(player == null) {
      return ActionResultType.SUCCESS;
    }

    final BlockPos pos = player.getPosition();

    final IForgeDimension.SleepResult sleepResult = world.getDimension().canSleepAt(player, pos);
    if(sleepResult == IForgeDimension.SleepResult.BED_EXPLODES) {
      world.createExplosion(null, DamageSource.netherBedExplosion(), pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 5.0f, true, Explosion.Mode.DESTROY);
    }

    if(sleepResult == IForgeDimension.SleepResult.DENY) {
      return ActionResultType.SUCCESS;
    }

    this.trySleep(world, player, pos).ifLeft(result -> {
      if(result.getMessage() != null) {
        player.sendStatusMessage(result.getMessage(), true);
      }
    }).ifRight(unit -> {
      sleeping.add(player);
      context.getItem().damageItem(1, player, e -> e.sendBreakAnimation(e.getActiveHand()));
    });

    return ActionResultType.SUCCESS;
  }

  public Either<PlayerEntity.SleepResult, Unit> trySleep(final World world, final PlayerEntity player, final BlockPos at) {
    final Optional<BlockPos> optAt = Optional.of(at);
    final PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, optAt);

    if(ret != null) {
      return Either.left(ret);
    }

    if(!world.isRemote) {
      if(player.isSleeping() || !player.isAlive()) {
        return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
      }

      if(!world.dimension.isSurfaceWorld()) {
        return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
      }

      if(!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, optAt)) {
        player.setSpawnPoint(at, false, true, player.dimension);
        return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
      }

      if(!player.isCreative()) {
        final Vec3d vec3d = new Vec3d(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D);
        if(!world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB(vec3d.getX() - 8.0D, vec3d.getY() - 5.0D, vec3d.getZ() - 8.0D, vec3d.getX() + 8.0D, vec3d.getY() + 5.0D, vec3d.getZ() + 8.0D), p_213820_1_ -> p_213820_1_.isPreventingPlayerRest(player)).isEmpty()) {
          return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
        }
      }
    }

    player.startSleeping(at);
    player.sleepTimer = 0;
    if(world instanceof ServerWorld) {
      ((ServerWorld)world).updateAllPlayersSleepingFlag();
    }

    return Either.right(Unit.INSTANCE);
  }

  private static final List<LivingEntity> sleeping = new ArrayList<>();

  @SubscribeEvent
  public static void onSleepingLocationCheck(final SleepingLocationCheckEvent event) {
    if(sleeping.contains(event.getEntityLiving())) {
      event.setResult(Event.Result.ALLOW);
    }
  }

  @SubscribeEvent
  public static void onWakeUp(final PlayerWakeUpEvent event) {
    sleeping.remove(event.getPlayer());
  }
}
