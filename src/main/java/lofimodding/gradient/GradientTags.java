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
    public static final Tag<Block> PEBBLE_SOURCES = tag("pebble_sources");

    public static final Map<Ore, Tag<Block>> ORE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Ore ore : Ores.all()) {
        tags.put(ore, forgeTag("ores/" + ore.name));
      }
    }));

    public static final Map<Metal, Tag<Block>> STORAGE_BLOCK = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("storage_blocks/" + metal.name));
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

    public static final Tag<Item> FIBRE_TORCH_LIGHTERS = tag("fibre_torch_lighters");

    public static final Map<Ore, Tag<Item>> ORE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Ore ore : Ores.all()) {
        tags.put(ore, forgeTag("ores/" + ore.name));
      }
    }));

    public static final Tag<Item> CRUSHED_ORES = forgeTag("crushed_ores");
    public static final Map<Ore, Tag<Item>> CRUSHED_ORE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Ore ore : Ores.all()) {
        tags.put(ore, forgeTag("crushed_ores/" + ore.name));
      }
    }));

    public static final Tag<Item> PURIFIED_ORES = forgeTag("purified_ores");
    public static final Map<Ore, Tag<Item>> PURIFIED_ORE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Ore ore : Ores.all()) {
        tags.put(ore, forgeTag("purified_ores/" + ore.name));
      }
    }));

    public static final Map<Metal, Tag<Item>> DUST = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("dusts/" + metal.name));
      }
    }));

    public static final Map<Metal, Tag<Item>> INGOT = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("ingots/" + metal.name));
      }
    }));

    public static final Map<Metal, Tag<Item>> NUGGET = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("nuggets/" + metal.name));
      }
    }));

    public static final Tag<Item> PLATES = forgeTag("plates");
    public static final Map<Metal, Tag<Item>> PLATE = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("plates/" + metal.name));
      }
    }));

    public static final Map<Metal, Tag<Item>> STORAGE_BLOCK = Collections.unmodifiableMap(Util.make(new LinkedHashMap<>(), tags -> {
      for(final Metal metal : Metals.all()) {
        tags.put(metal, forgeTag("storage_blocks/" + metal.name));
      }
    }));

    public static final Tag<Item> PELTS = tag("pelts");
    public static final Tag<Item> PELTS_COW = tag("pelts/cow");
    public static final Tag<Item> PELTS_DONKEY = tag("pelts/donkey");
    public static final Tag<Item> PELTS_HORSE = tag("pelts/horse");
    public static final Tag<Item> PELTS_LLAMA = tag("pelts/llama");
    public static final Tag<Item> PELTS_MULE = tag("pelts/mule");
    public static final Tag<Item> PELTS_OCELOT = tag("pelts/ocelot");
    public static final Tag<Item> PELTS_PIG = tag("pelts/pig");
    public static final Tag<Item> PELTS_POLAR_BEAR = tag("pelts/polar_bear");
    public static final Tag<Item> PELTS_SHEEP = tag("pelts/sheep");
    public static final Tag<Item> PELTS_WOLF = tag("pelts/wolf");

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
