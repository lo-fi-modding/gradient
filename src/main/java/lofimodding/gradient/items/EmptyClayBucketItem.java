package lofimodding.gradient.items;

import lofimodding.gradient.GradientItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
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
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmptyClayBucketItem extends Item {
  public EmptyClayBucketItem() {
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

      if(state.getBlock() instanceof IBucketPickupHandler) {
        final Fluid fluid = ((IBucketPickupHandler)state.getBlock()).pickupFluid(world, blockpos, state);

        if(fluid != Fluids.EMPTY) {
          player.addStat(Stats.ITEM_USED.get(this));
          player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
          final ItemStack itemstack1 = this.fillBucket(itemstack, player, fluid.getFluid());

          if(!world.isRemote) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemstack1);
          }

          return ActionResult.resultSuccess(itemstack1);
        }
      }

      return FluidUtil.getFluidHandler(world, blockpos, blockraytraceresult.getFace()).map(handler -> {
        final FluidStack fluid = handler.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);

        if(fluid.getAmount() == FluidAttributes.BUCKET_VOLUME) {
          player.addStat(Stats.ITEM_USED.get(this));
          player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
          final ItemStack itemstack1 = this.fillBucket(itemstack, player, fluid.getFluid());

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

  private ItemStack fillBucket(final ItemStack emptyBuckets, final PlayerEntity player, final Fluid fluid) {
    if(player.abilities.isCreativeMode) {
      return emptyBuckets;
    }

    emptyBuckets.shrink(1);

    final ItemStack stack = new ItemStack(GradientItems.FILLED_CLAY_BUCKET.get());
    FluidUtil.getFluidHandler(stack).ifPresent(handler -> handler.fill(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE));

    if(emptyBuckets.isEmpty()) {
      return stack;
    }

    if(!player.inventory.addItemStackToInventory(stack)) {
      player.dropItem(stack, false);
    }

    return emptyBuckets;
  }

  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT tag) {
    return new Handler();
  }

  private static class Handler implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    // This is a pretty big hack
    private FluidStack lastFluidFilled = FluidStack.EMPTY;

    @Nonnull
    @Override
    public ItemStack getContainer() {
      final ItemStack filled = new ItemStack(GradientItems.FILLED_CLAY_BUCKET.get());
      FluidUtil.getFluidHandler(filled).ifPresent(handler -> handler.fill(new FluidStack(this.lastFluidFilled, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE));
      this.lastFluidFilled = FluidStack.EMPTY;
      return filled;
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
      return FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(final int tank, @Nonnull final FluidStack stack) {
      return true;
    }

    @Override
    public int fill(final FluidStack resource, final IFluidHandler.FluidAction action) {
      if(action.execute()) {
        this.lastFluidFilled = resource.copy();
      }

      return FluidAttributes.BUCKET_VOLUME;
    }

    @Nonnull
    @Override
    public FluidStack drain(final FluidStack resource, final IFluidHandler.FluidAction action) {
      return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final IFluidHandler.FluidAction action) {
      return FluidStack.EMPTY;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, @Nullable final Direction facing) {
      return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
    }
  }
}
