package lofimodding.gradient.blocks;

import lofimodding.gradient.science.Ore;
import net.minecraft.block.Block;

public class OreBlock extends Block {
  public final Ore ore;

  public OreBlock(final Ore ore, final Properties properties) {
    super(properties);
    this.ore = ore;
  }
}
