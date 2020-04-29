package lofimodding.gradient.items;

import lofimodding.gradient.GradientItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmptyWaterskinItem extends Item {
  public EmptyWaterskinItem() {
    super(new Item.Properties().group(GradientItems.GROUP).maxStackSize(1));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    final ItemStack itemstack = player.getHeldItem(hand);
    final RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
    final ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, itemstack, raytraceresult);

    if(ret != null) {
      return ret;
    }

    if(raytraceresult.getType() == RayTraceResult.Type.MISS) {
      return ActionResult.resultPass(itemstack);
    }

    if(raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
      return ActionResult.resultPass(itemstack);
    }

    final BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
    final BlockPos blockpos = blockraytraceresult.getPos();
    final Direction direction = blockraytraceresult.getFace();
    final BlockPos blockpos1 = blockpos.offset(direction);

    if(world.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos1, direction, itemstack)) {
      final BlockState state = world.getBlockState(blockpos);

      if(state.getBlock() instanceof FlowingFluidBlock) {
        final FlowingFluidBlock block = (FlowingFluidBlock)state.getBlock();

        if(block.getFluid() == Fluids.WATER) {
          if(state.get(FlowingFluidBlock.LEVEL) == 0) {
            final Fluid fluid = ((IBucketPickupHandler)state.getBlock()).pickupFluid(world, blockpos, state);

            if(fluid != Fluids.EMPTY) {
              player.addStat(Stats.ITEM_USED.get(this));
              player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
              final ItemStack itemstack1 = this.fillBucket(itemstack, player, GradientItems.FILLED_WATERSKIN.get());

              if(!world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemstack1);
              }

              return ActionResult.resultSuccess(itemstack1);
            }
          }
        }
      }

      return FluidUtil.getFluidHandler(world, blockpos, blockraytraceresult.getFace()).map(handler -> {
        final FluidStack fluid = handler.drain(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.SIMULATE);

        if(fluid.getAmount() == 1000) {
          player.addStat(Stats.ITEM_USED.get(this));
          player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
          final ItemStack itemstack1 = this.fillBucket(itemstack, player, GradientItems.FILLED_WATERSKIN.get());

          if(!world.isRemote) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemstack1);
          }

          return ActionResult.resultSuccess(itemstack1);
        }

        return ActionResult.resultFail(itemstack);
      }).orElse(ActionResult.resultFail(itemstack));
    }

    return ActionResult.resultFail(itemstack);
  }

  private ItemStack fillBucket(final ItemStack emptyBuckets, final PlayerEntity player, final Item fullBucket) {
    if(player.abilities.isCreativeMode) {
      return emptyBuckets;
    }

    emptyBuckets.shrink(1);

    if(emptyBuckets.isEmpty()) {
      return new ItemStack(fullBucket);
    }

    if(!player.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
      player.dropItem(new ItemStack(fullBucket), false);
    }

    return emptyBuckets;
  }

  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
    return new Handler();
  }

  private static class Handler implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @Nonnull
    @Override
    public ItemStack getContainer() {
      return new ItemStack(GradientItems.FILLED_WATERSKIN.get());
    }

    @Override
    public int getTanks() {
      return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(final int tank) {
      return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(final int tank) {
      return 1000;
    }

    @Override
    public boolean isFluidValid(final int tank, @Nonnull final FluidStack stack) {
      return true;
    }

    @Override
    public int fill(final FluidStack resource, final FluidAction doFill) {
      return 1000;
    }

    @Nonnull
    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {
      return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
      return FluidStack.EMPTY;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, @Nullable final Direction facing) {
      return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
    }
  }
}
