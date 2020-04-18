package lofimodding.gradient.blocks;

import lofimodding.gradient.science.Metal;
import net.minecraft.block.Block;

public class MetalBlock extends Block {
  public final Metal metal;

  public MetalBlock(final Metal metal, final Properties properties) {
    super(properties);
    this.metal = metal;
  }
}
