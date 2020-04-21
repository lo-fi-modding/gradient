package lofimodding.gradient.items;

import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MulchItem extends Item {
  public MulchItem(final Item.Properties properties) {
    super(properties);
  }

  @Override
  public ActionResultType onItemUse(final ItemUseContext context) {
    final World world = context.getWorld();
    final BlockPos pos = context.getPos();
    final BlockPos offset = pos.offset(context.getFace());

    if(BoneMealItem.applyBonemeal(context.getItem(), world, pos, context.getPlayer())) {
      if(!world.isRemote) {
        world.playEvent(2005, pos, 0);
      }

      return ActionResultType.SUCCESS;
    }

    final BlockState state = world.getBlockState(pos);
    final boolean isSolid = state.isSolidSide(world, pos, context.getFace());

    if(isSolid && BoneMealItem.growSeagrass(context.getItem(), world, offset, context.getFace())) {
      if(!world.isRemote) {
        world.playEvent(2005, offset, 0);
      }

      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }
}
