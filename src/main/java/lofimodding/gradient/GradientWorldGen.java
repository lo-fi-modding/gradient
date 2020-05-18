package lofimodding.gradient;

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SphereReplaceConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;

public final class GradientWorldGen {
  private GradientWorldGen() { }

  public static final BlockClusterFeatureConfig PEBBLE_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(GradientBlocks.PEBBLE.get().getDefaultState()), new SimpleBlockPlacer()).tries(10).build();

  public static void addWorldGeneration() {
    for(final Biome biome : Biome.BIOMES) {
      biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.withConfiguration(new SphereReplaceConfig(GradientBlocks.SALT_BLOCK.get().getDefaultState(), 4, 1, Lists.newArrayList(Blocks.DIRT.getDefaultState(), GradientBlocks.SALT_BLOCK.get().getDefaultState()))).withPlacement(Placement.COUNT_TOP_SOLID.configure(new FrequencyConfig(1))));
      biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(PEBBLE_CONFIG).withPlacement(Placement.CHANCE_TOP_SOLID_HEIGHTMAP.configure(new ChanceConfig(5))));
    }
  }
}
