package lofimodding.gradient.items;

import lofimodding.gradient.GradientItemTiers;
import lofimodding.gradient.GradientItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class StoneMattockItem extends AxeItem {
  public StoneMattockItem() {
    super(GradientItemTiers.STONE, 3.0f, -3.2f, new Item.Properties().group(GradientItems.GROUP).addToolType(ToolType.AXE, 1).addToolType(ToolType.SHOVEL, 1));
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }

  @Override
  public ActionResultType onItemUse(final ItemUseContext context) {
    final ActionResultType axeResult = super.onItemUse(context);

    if(axeResult != ActionResultType.PASS) {
      return axeResult;
    }

    return Items.WOODEN_HOE.onItemUse(context);
  }

  @Override
  public boolean itemInteractionForEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity entity, final Hand hand) {
    if(entity.world.isRemote) {
      return false;
    }

    if(entity instanceof IShearable) {
      final IShearable target = (IShearable)entity;
      final BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());

      if(target.isShearable(stack, entity.world, pos)) {
        final List<ItemStack> drops = target.onSheared(stack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

        if(drops.isEmpty()) {
          return true;
        }

        final Random rand = new Random();
        final ItemStack drop = drops.get(rand.nextInt(drops.size()));
        final ItemEntity ent = entity.entityDropItem(drop, 1.0f);
        ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1f, rand.nextFloat() * 0.05f, (rand.nextFloat() - rand.nextFloat()) * 0.1f));
        stack.damageItem(1, entity, e -> e.sendBreakAnimation(hand));
      }

      return true;
    }

    return false;
  }
}
