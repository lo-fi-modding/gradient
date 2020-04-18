package lofimodding.gradient;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

public final class GradientTags {
  private GradientTags() { }

  public static final class Blocks {
    private Blocks() { }

    public static final Tag<Block> FIBRE_SOURCES = tag("fibre_sources");

    private static Tag<Block> tag(final String name) {
      return new BlockTags.Wrapper(Gradient.loc(name));
    }
  }
}
