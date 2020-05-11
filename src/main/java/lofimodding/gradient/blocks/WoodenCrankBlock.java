package lofimodding.gradient.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public class WoodenCrankBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public WoodenCrankBlock() {
    super(Properties.create(Material.WOOD).hardnessAndResistance(1.0f, 5.0f).sound(SoundType.WOOD));
  }
}
