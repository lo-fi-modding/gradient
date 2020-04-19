package lofimodding.gradient;

import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class GradientTags {
  private GradientTags() { }

  public static final class Blocks {
    private Blocks() { }

    public static final Tag<Block> FIBRE_SOURCES = tag("fibre_sources");

    public static final Map<Ore, Tag<Block>> ORE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Ore ore : Ores.all()) {
        tags.put(ore, forgeTag("ores/" + ore.name));
      }
    }));

    private static Tag<Block> tag(final String name) {
      return tag(Gradient.loc(name));
    }

    private static Tag<Block> tag(final ResourceLocation name) {
      return new BlockTags.Wrapper(name);
    }

    private static Tag<Block> forgeTag(final String name) {
      return tag(new ResourceLocation("forge", name));
    }
  }

  public static final class Items {
    private Items() { }

    public static final Map<Ore, Tag<Item>> ORE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Ore ore : Ores.all()) {
        tags.put(ore, forgeTag("ores/" + ore.name));
      }
    }));

    public static final Map<Metal, Tag<Item>> NUGGET = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("nuggets/" + metal.name));
      }
    }));

    private static Tag<Item> tag(final String name) {
      return tag(Gradient.loc(name));
    }

    private static Tag<Item> tag(final ResourceLocation name) {
      return new ItemTags.Wrapper(name);
    }

    private static Tag<Item> forgeTag(final String name) {
      return tag(new ResourceLocation("forge", name));
    }
  }
}
