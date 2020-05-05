package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientMaterials;
import lofimodding.gradient.fluids.MetalFluid;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ClayCrucibleBlock extends HeatSinkerBlock {
  private static final VoxelShape SHAPE = makeCuboidShape(1.0d, 0.0d, 1.0d, 15.0d, 15.0d, 15.0d);

  public ClayCrucibleBlock() {
    super(Properties.create(GradientMaterials.CLAY_MACHINE).hardnessAndResistance(1.0f, 5.0f).notSolid());
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip"));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public int getLightValue(final BlockState state, final IBlockReader world, final BlockPos pos) {
    final ClayCrucibleTile te = WorldUtils.getTileEntity(world, pos, ClayCrucibleTile.class);

    if(te != null) {
      return te.getLightLevel();
    }

    return super.getLightValue(state, world, pos);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return new ClayCrucibleTile();
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    if(!player.isSneaking()) {
      final ClayCrucibleTile te = WorldUtils.getTileEntity(world, pos, ClayCrucibleTile.class);

      if(te == null) {
        return ActionResultType.SUCCESS;
      }

      final ItemStack stack = player.getHeldItem(hand);

      // Cast item
      final Block heldBlock = Block.getBlockFromItem(stack.getItem());
      if(heldBlock instanceof ClayCastBlock) {
        final GradientCasts cast = ((ClayCastBlock)heldBlock).cast;

        if(te.getMoltenMetal() == null) {
          player.sendMessage(new TranslationTextComponent(this.getTranslationKey() + ".no_metal").applyTextStyle(TextFormatting.RED));
          return ActionResultType.SUCCESS;
        }

        final Metal metal = ((MetalFluid)te.getMoltenMetal().getFluid()).metal;
        //TODO
//        final int amount = cast.amountForMetal(metal);
//
//        if(te.getMoltenMetal().getAmount() < amount) {
//          player.sendMessage(new TranslationTextComponent(this.getTranslationKey() + ".not_enough_metal", amount).applyTextStyle(TextFormatting.RED));
//          return ActionResultType.SUCCESS;
//        }
//
//        if(!cast.isValidForMetal(metal)) {
//          player.sendMessage(new TranslationTextComponent(this.getTranslationKey() + ".metal_cant_make_tools").applyTextStyle(TextFormatting.RED));
//          return ActionResultType.SUCCESS;
//        }
//
//        if(!player.isCreative()) {
//          stack.shrink(1);
//
//          te.consumeMetal(amount);
//        }

        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(GradientItems.CASTED(cast, metal).get()));
        return ActionResultType.SUCCESS;
      }

      NetworkHooks.openGui((ServerPlayerEntity)player, te, pos);
    }

    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
    WorldUtils.dropInventory(world, pos);
    super.onReplaced(state, world, pos, newState, isMoving);
  }
}
