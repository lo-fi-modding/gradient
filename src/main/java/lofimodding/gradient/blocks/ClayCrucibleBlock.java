package lofimodding.gradient.blocks;

import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientMaterials;
import lofimodding.gradient.items.UnhardenedClayCastItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import lofimodding.gradient.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
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

    return state.getLightValue(world, pos);
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
      if(stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof UnhardenedClayCastItem) && ((BlockItem)stack.getItem()).getBlock() instanceof BlockClayCast) {
        final GradientCasts.Cast cast = ((BlockClayCast)((BlockItem)stack.getItem()).getBlock()).cast;

        if(te.getMoltenMetal() == null) {
          player.sendMessage(new TranslationTextComponent(this.getTranslationKey() + ".no_metal").applyTextStyle(TextFormatting.RED));
          return ActionResultType.SUCCESS;
        }

        final Metal metal = Metals.get(te.getMoltenMetal().getFluid());
        final int amount = cast.amountForMetal(metal);

        if(te.getMoltenMetal().getAmount() < amount) {
          player.sendMessage(new TranslationTextComponent(this.getTranslationKey() + ".not_enough_metal", amount).applyTextStyle(TextFormatting.RED));
          return ActionResultType.SUCCESS;
        }

        if(!cast.isValidForMetal(metal)) {
          player.sendMessage(new TranslationTextComponent(this.getTranslationKey() + ".metal_cant_make_tools").applyTextStyle(TextFormatting.RED));
          return ActionResultType.SUCCESS;
        }

        if(!player.isCreative()) {
          stack.shrink(1);

          te.consumeMetal(amount);
        }

        ItemHandlerHelper.giveItemToPlayer(player, GradientItems.castItem(cast, metal, 1));
        return ActionResultType.SUCCESS;
      }

      if(FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null) {
        final FluidStack fluid = FluidUtil.getFluidContained(player.getHeldItem(hand));

        // Make sure the fluid handler is either empty, or contains metal
        if(fluid != null) {
          final Metal metal = Metals.get(fluid.getFluid());

          if(metal == Metals.INVALID_METAL) {
            return ActionResultType.SUCCESS;
          }
        }

        FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
        return ActionResultType.SUCCESS;
      }

      player.openGui(GradientMod.instance, GradientGuiHandler.CLAY_CRUCIBLE, world, pos.getX(), pos.getY(), pos.getZ());
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
