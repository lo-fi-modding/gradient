package lofimodding.gradient.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DisabledFurnaceBlock extends FurnaceBlock {
  public DisabledFurnaceBlock() {
    super(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F).lightValue(13));
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(final IBlockReader world) {
    return null;
  }

  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
    if(world.isRemote) {
      player.sendMessage(new TranslationTextComponent("gradient.furnace_disabled"));
    }

    return ActionResultType.SUCCESS;
  }

  @Override
  public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
    super.addInformation(stack, world, tooltip, flag);
    tooltip.add(new TranslationTextComponent("gradient.furnace_disabled"));
  }
}
