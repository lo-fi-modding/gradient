package lofimodding.gradient.items;

import lofimodding.gradient.GradientItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
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

public class FilledClayBucketItem extends Item {
  public FilledClayBucketItem() {
    super(new Item.Properties().group(GradientItems.GROUP).maxStackSize(1));
  }

  @Override
  public ITextComponent getDisplayName(final ItemStack stack) {
    if(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) {
      return super.getDisplayName(stack);
    }

    return new TranslationTextComponent(this.getTranslationKey(stack), FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY).getDisplayName());
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    final ItemStack itemstack = player.getHeldItem(hand);
    final RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
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
      final BlockState blockstate = world.getBlockState(blockpos);
      final BlockPos blockpos2 = this.canBlockContainFluid(world, blockpos, blockstate, FluidUtil.getFluidContained(player.getHeldItem(hand)).orElse(FluidStack.EMPTY).getFluid()) ? blockpos : blockpos1;

      if(this.tryPlaceContainedLiquid(player, player.getHeldItem(hand), world, blockpos2, blockraytraceresult)) {
        if(player instanceof ServerPlayerEntity) {
          CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, blockpos2, itemstack);
        }

        player.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.resultSuccess(this.emptyBucket(itemstack, player));
      }

      return ActionResult.resultFail(itemstack);
    }

    return ActionResult.resultFail(itemstack);
  }

  protected ItemStack emptyBucket(final ItemStack stack, final PlayerEntity player) {
    return player.abilities.isCreativeMode ? stack : new ItemStack(GradientItems.EMPTY_CLAY_BUCKET.get());
  }

  private boolean canBlockContainFluid(final World world, final BlockPos pos, final BlockState state, final Fluid fluid) {
    return state.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)state.getBlock()).canContainFluid(world, pos, state, fluid);
  }

  public boolean tryPlaceContainedLiquid(@Nullable final PlayerEntity player, final ItemStack bucket, final World world, final BlockPos pos, @Nullable final BlockRayTraceResult trace) {
    final FluidStack fluid = FluidUtil.getFluidContained(bucket).orElse(FluidStack.EMPTY);
    final BlockState blockstate = world.getBlockState(pos);
    final Material material = blockstate.getMaterial();
    final boolean flag = blockstate.isReplaceable(fluid.getFluid());
    final boolean canContainFluid = this.canBlockContainFluid(world, pos, blockstate, fluid.getFluid());

    if(blockstate.isAir(world, pos) || flag || canContainFluid) {
      if(world.dimension.doesWaterVaporize() && fluid.getFluid().isIn(FluidTags.WATER)) {
        final int i = pos.getX();
        final int j = pos.getY();
        final int k = pos.getZ();
        world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

        for(int l = 0; l < 8; ++l) {
          world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
        }
      } else if(canContainFluid) {
        if(((ILiquidContainer)blockstate.getBlock()).receiveFluid(world, pos, blockstate, ((FlowingFluid)fluid.getFluid()).getStillFluidState(false))) {
          this.playEmptySound(player, fluid.getFluid(), world, pos);
        }
      } else {
        if(!world.isRemote && flag && !material.isLiquid()) {
          world.destroyBlock(pos, true);
        }

        this.playEmptySound(player, fluid.getFluid(), world, pos);
        world.setBlockState(pos, fluid.getFluid().getDefaultState().getBlockState(), 11);
      }

      return true;
    }

    return trace != null && this.tryPlaceContainedLiquid(player, bucket, world, trace.getPos().offset(trace.getFace()), null);
  }

  protected void playEmptySound(@Nullable final PlayerEntity player, final Fluid fluid, final IWorld world, final BlockPos pos) {
    SoundEvent soundevent = fluid.getAttributes().getEmptySound();

    if(soundevent == null) {
      soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
    }

    world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }

  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
    return new Handler();
  }

  private static class Handler implements IFluidHandlerItem, ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    private FluidStack fluid = FluidStack.EMPTY;

    @Nonnull
    @Override
    public ItemStack getContainer() {
      return new ItemStack(GradientItems.EMPTY_CLAY_BUCKET.get());
    }

    @Override
    public int getTanks() {
      return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(final int tank) {
      return this.fluid;
    }

    @Override
    public int getTankCapacity(final int tank) {
      return FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(final int tank, @Nonnull final FluidStack fluid) {
      if(fluid.getFluid() == Fluids.WATER || fluid.getFluid() == Fluids.LAVA) {
        return true;
      }

      return fluid.getFluid().getAttributes().getBucket(fluid) != null;
    }

    @Override
    public int fill(final FluidStack resource, final IFluidHandler.FluidAction action) {
      if(!this.fluid.isEmpty() || resource.getAmount() < FluidAttributes.BUCKET_VOLUME) {
        return 0;
      }

      if(action.execute()) {
        this.fluid = resource.copy();
      }

      return this.fluid.getAmount();
    }

    @Nonnull
    @Override
    public FluidStack drain(final FluidStack resource, final IFluidHandler.FluidAction action) {
      if(resource.getAmount() < FluidAttributes.BUCKET_VOLUME) {
        return FluidStack.EMPTY;
      }

      final FluidStack fluidStack = this.fluid;

      if(!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
        if(action.execute()) {
          this.fluid = FluidStack.EMPTY;
        }

        return fluidStack.copy();
      }

      return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(final int maxDrain, final IFluidHandler.FluidAction action) {
      if(maxDrain < FluidAttributes.BUCKET_VOLUME) {
        return FluidStack.EMPTY;
      }

      final FluidStack fluidStack = this.fluid;

      if(!fluidStack.isEmpty()) {
        if(action.execute()) {
          this.fluid = FluidStack.EMPTY;
        }

        return fluidStack.copy();
      }

      return FluidStack.EMPTY;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, @Nullable final Direction facing) {
      return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
    }

    @Override
    public CompoundNBT serializeNBT() {
      return this.fluid.writeToNBT(new CompoundNBT());
    }

    @Override
    public void deserializeNBT(final CompoundNBT tag) {
      this.fluid = FluidStack.loadFluidStackFromNBT(tag);
    }
  }
}
