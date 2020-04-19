package lofimodding.gradient.items;

import lofimodding.gradient.entities.PebbleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PebbleItem extends Item {
  public PebbleItem(final Properties properties) {
    super(properties);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    final ItemStack itemstack = player.getHeldItem(hand);
    world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

    if(!world.isRemote) {
      final PebbleEntity pebble = new PebbleEntity(player, world);
      pebble.setItem(itemstack);
      pebble.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
      world.addEntity(pebble);
    }

    player.addStat(Stats.ITEM_USED.get(this));
    if(!player.abilities.isCreativeMode) {
      itemstack.shrink(1);
    }

    return ActionResult.resultSuccess(itemstack);
  }
}
