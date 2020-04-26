package lofimodding.gradient.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientLoot;
import lofimodding.gradient.GradientStages;
import lofimodding.gradient.GradientTags;
import lofimodding.gradient.blocks.MetalBlock;
import lofimodding.gradient.items.PebbleItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import lofimodding.progression.recipes.StagedRecipeBuilder;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.ValidationTracker;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.Inverted;
import net.minecraft.world.storage.loot.conditions.MatchTool;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GradientDataGenerator {
  private GradientDataGenerator() { }

  @SubscribeEvent
  public static void gatherData(final GatherDataEvent event) {
    final DataGenerator gen = event.getGenerator();

    if(event.includeClient()) {
      gen.addProvider(new BlockModels(gen, event.getExistingFileHelper()));
      gen.addProvider(new ItemModels(gen, event.getExistingFileHelper()));
      gen.addProvider(new BlockStates(gen, event.getExistingFileHelper()));
      gen.addProvider(new EnglishLang(gen));
    }

    if(event.includeServer()) {
      gen.addProvider(new BlockTagProvider(gen));
      gen.addProvider(new ItemTagProvider(gen));
      gen.addProvider(new Recipes(gen));
      gen.addProvider(new Loot(gen));
    }
  }

  public static class BlockModels extends BlockModelProvider {
    public BlockModels(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
      super(generator, Gradient.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
      this.getBuilder(GradientIds.PEBBLE)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/andesite"))
        .texture("stone", this.mcLoc("block/andesite"))
        .element()
        .from(4.0f, 0.0f, 6.0f)
        .to(12.0f, 2.0f, 11.0f)
        .face(Direction.DOWN ).texture("stone").uvs(0.0f, 0.0f, 8.0f, 5.0f).end()
        .face(Direction.UP   ).texture("stone").uvs(0.0f, 0.0f, 8.0f, 5.0f).end()
        .face(Direction.NORTH).texture("stone").uvs(0.0f, 0.0f, 8.0f, 2.0f).end()
        .face(Direction.SOUTH).texture("stone").uvs(8.0f, 2.0f, 0.0f, 0.0f).end()
        .face(Direction.WEST ).texture("stone").uvs(0.0f, 0.0f, 5.0f, 2.0f).end()
        .face(Direction.EAST ).texture("stone").uvs(0.0f, 0.0f, 5.0f, 2.0f).end()
        .end()
        .element()
        .from(5.0f, 2.0f, 7.0f)
        .to(11.0f, 3.0f, 10.0f)
        .face(Direction.DOWN ).texture("stone").uvs(0.0f, 0.0f, 6.0f, 3.0f).end()
        .face(Direction.UP   ).texture("stone").uvs(0.0f, 0.0f, 6.0f, 3.0f).end()
        .face(Direction.NORTH).texture("stone").uvs(0.0f, 0.0f, 6.0f, 1.0f).end()
        .face(Direction.SOUTH).texture("stone").uvs(0.0f, 0.0f, 6.0f, 1.0f).end()
        .face(Direction.WEST ).texture("stone").uvs(0.0f, 0.0f, 3.0f, 1.0f).end()
        .face(Direction.EAST ).texture("stone").uvs(0.0f, 0.0f, 3.0f, 1.0f).end()
        .end()
        .element()
        .from(5.0f, 0.0f, 5.0f)
        .to(11.0f, 1.0f, 12.0f)
        .face(Direction.DOWN ).texture("stone").uvs(0.0f, 0.0f, 6.0f, 7.0f).end()
        .face(Direction.UP   ).texture("stone").uvs(0.0f, 0.0f, 6.0f, 7.0f).end()
        .face(Direction.NORTH).texture("stone").uvs(0.0f, 0.0f, 6.0f, 1.0f).end()
        .face(Direction.SOUTH).texture("stone").uvs(0.0f, 0.0f, 6.0f, 1.0f).end()
        .face(Direction.WEST ).texture("stone").uvs(0.0f, 0.0f, 7.0f, 1.0f).end()
        .face(Direction.EAST ).texture("stone").uvs(0.0f, 0.0f, 7.0f, 1.0f).end()
        .end();

      for(final Ore ore : Ores.all()) {
        this.getBuilder(GradientIds.PEBBLE(ore)).parent(this.getExistingFile(this.modLoc("block/" + GradientIds.PEBBLE)));
      }

      this.getBuilder("ore")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/stone"))
        .texture("background", this.mcLoc("block/stone"))
        .texture("diffuse", this.modLoc("block/ore_diffuse"))
        .texture("specular", this.modLoc("block/ore_specular"))
        .texture("shadow_1", this.modLoc("block/ore_shadow_1"))
        .texture("shadow_2", this.modLoc("block/ore_shadow_2"))
        .texture("edge_1", this.modLoc("block/ore_edge_1"))
        .texture("edge_2", this.modLoc("block/ore_edge_2"))
        .texture("edge_3", this.modLoc("block/ore_edge_3"))
        .element().allFaces(this.addTexture("background").andThen((dir, f) -> f.cullface(dir))).end()
        .element().allFaces(this.addTexture("diffuse").andThen((dir, f) -> f.cullface(dir).tintindex(1))).end()
        .element().allFaces(this.addTexture("specular").andThen((dir, f) -> f.cullface(dir).tintindex(2))).end()
        .element().allFaces(this.addTexture("shadow_1").andThen((dir, f) -> f.cullface(dir).tintindex(3))).end()
        .element().allFaces(this.addTexture("shadow_2").andThen((dir, f) -> f.cullface(dir).tintindex(4))).end()
        .element().allFaces(this.addTexture("edge_1").andThen((dir, f) -> f.cullface(dir).tintindex(5))).end()
        .element().allFaces(this.addTexture("edge_2").andThen((dir, f) -> f.cullface(dir).tintindex(6))).end()
        .element().allFaces(this.addTexture("edge_3").andThen((dir, f) -> f.cullface(dir).tintindex(7))).end()
      ;

      for(final Ore ore : Ores.all()) {
        this.getBuilder(GradientIds.ORE(ore)).parent(this.getExistingFile(this.modLoc("block/ore")));
      }

      this.getBuilder("metal_block")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.modLoc("block/metal_block"))
        .texture("all", this.modLoc("block/metal_block"))
        .element().allFaces(this.addTexture("all").andThen((dir, f) -> f.cullface(dir).tintindex(1))).end()
      ;

      for(final Metal metal : Metals.all()) {
        this.getBuilder(GradientIds.METAL_BLOCK(metal)).parent(this.getExistingFile(this.modLoc("block/metal_block")));
      }

      this.cubeAll(GradientIds.SALT_BLOCK, this.modLoc("block/salt_block"));

      this.getBuilder(GradientIds.FIREPIT)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.modLoc("block/fire_pit_log"))
        .texture("end", this.modLoc("block/fire_pit_log_end"))
        .texture("side", this.modLoc("block/fire_pit_log"))

        .element()
        .from(5.0f, 0.0f, 2.0f)
        .to(6.5f, 1.5f, 15.0f)
        .rotation().angle(22.5f).axis(Direction.Axis.Y).origin(4.0f, 1.0f, 5.5f).end()
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.EAST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.WEST).uvs(1.0f, 5.0f, 15.0f, 7.0f).texture("side").end()
        .face(Direction.UP).uvs(1.0f, 3.0f, 15.0f, 5.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(1.0f, 7.0f, 15.0f, 9.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .end()

        .element()
        .from(-1.5f, 0.001f, 8.5f)
        .to(11.5f, 1.501f, 10.0f)
        .rotation().angle(45.0f).axis(Direction.Axis.Y).origin(4.0f, 1.0f, 5.5f).end()
        .face(Direction.NORTH).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.SOUTH).uvs(1.0f, 5.0f, 15.0f, 7.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.UP).uvs(1.0f, 3.0f, 15.0f, 5.0f).texture("side").end()
        .face(Direction.DOWN).uvs(1.0f, 7.0f, 15.0f, 9.0f).texture("side").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element()
        .from(1.5f, 0.0005f, 4.5f)
        .to(14.5f, 1.5005f, 6.0f)
        .face(Direction.NORTH).uvs(1.0f, 5.0f, 15.0f, 7.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.SOUTH).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.UP).uvs(1.0f, 3.0f, 15.0f, 5.0f).texture("side").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(1.0f, 7.0f, 15.0f, 9.0f).texture("side").end()
        .end()

        .element()
        .from(2.5f, -0.001f, 5.5f)
        .to(15.5f, 1.499f, 7.0f)
        .rotation().angle(-22.5f).axis(Direction.Axis.Y).origin(4.0f, 1.0f, 5.5f).end()
        .face(Direction.NORTH).uvs(1.0f, 5.0f, 15.0f, 7.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.SOUTH).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("end").end()
        .face(Direction.UP).uvs(1.0f, 3.0f, 15.0f, 5.0f).texture("side").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(1.0f, 7.0f, 15.0f, 9.0f).texture("side").end()
        .end();

      this.getBuilder(GradientIds.GRINDSTONE)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/polished_andesite"))
        .texture("surface", this.mcLoc("block/polished_andesite"))
        .texture("case", this.mcLoc("block/stone"))

        .element() // south
        .from(5.0f, 0.0f, 14.0f)
        .to(11.0f, 3.31f, 15.0f)
        .face(Direction.NORTH).uvs(5.0f, 13.0f, 11.0f, 16.0f).texture("case").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("case").end()
        .face(Direction.SOUTH).uvs(6.0f, 13.0f, 12.0f, 16.0f).texture("case").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("case").end()
        .face(Direction.UP).uvs(5.0f, 11.0f, 11.0f, 12.0f).texture("case").end()
        .face(Direction.DOWN).uvs(5.0f, 14.0f, 11.0f, 15.0f).texture("case").end()
        .end()

        .element() // bottom east
        .from(7.0f, 0.5f, 2.0f)
        .to(10.5f, 2.5f, 14.0f)
        .rotation().angle(-22.5f).axis(Direction.Axis.Z).origin(8.0f, 8.0f, 8.0f).end()
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 12.0f, 2.0f).texture("surface").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 12.0f, 2.0f).texture("surface").end()
        .face(Direction.UP).uvs(0.0f, 2.0f, 4.0f, 14.0f).texture("surface").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 4.0f, 14.0f).texture("surface").end()
        .end()

        .element() // bottom west
        .from(5.5f, 0.5f, 2.0f)
        .to(9.0f, 2.5f, 14.0f)
        .rotation().angle(22.5f).axis(Direction.Axis.Z).origin(8.0f, 8.0f, 8.0f).end()
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
        .face(Direction.EAST).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("surface").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 12.0f, 2.0f).texture("surface").end()
        .face(Direction.UP).uvs(12.0f, 2.0f, 16.0f, 14.0f).texture("surface").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 4.0f, 12.0f).texture("surface").end()
        .end()

        .element() // east
        .from(4.0f, 0.0f, 1.0f)
        .to(5.0f, 3.31f, 15.0f)
        .face(Direction.NORTH).uvs(11.0f, 13.0f, 12.0f, 16.0f).texture("case").end()
        .face(Direction.EAST).uvs(1.0f, 0.0f, 15.0f, 3.0f).texture("case").end()
        .face(Direction.SOUTH).uvs(5.0f, 13.0f, 6.0f, 16.0f).texture("case").end()
        .face(Direction.WEST).uvs(1.0f, 13.0f, 15.0f, 16.0f).texture("case").end()
        .face(Direction.UP).uvs(1.0f, 10.0f, 15.0f, 11.0f).texture("case").end()
        .face(Direction.DOWN).uvs(11.0f, 1.0f, 12.0f, 15.0f).texture("case").end()
        .end()

        .element() // west
        .from(11.0f, 0.0f, 1.0f)
        .to(12.0f, 3.31f, 15.0f)
        .face(Direction.NORTH).uvs(4.0f, 13.0f, 5.0f, 16.0f).texture("case").end()
        .face(Direction.EAST).uvs(1.0f, 13.0f, 15.0f, 16.0f).texture("case").end()
        .face(Direction.SOUTH).uvs(12.0f, 13.0f, 15.0f, 16.0f).texture("case").end()
        .face(Direction.WEST).uvs(1.0f, 0.0f, 15.0f, 3.0f).texture("case").end()
        .face(Direction.UP).uvs(1.0f, 11.0f, 15.0f, 12.0f).texture("case").end()
        .face(Direction.DOWN).uvs(4.0f, 1.0f, 5.0f, 15.0f).texture("case").end()
        .end()

        .element() // north
        .from(5.0f, 0.0f, 1.0f)
        .to(11.0f, 3.31f, 2.0f)
        .face(Direction.NORTH).uvs(5.0f, 13.0f, 11.0f, 16.0f).texture("case").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("case").end()
        .face(Direction.SOUTH).uvs(5.0f, 13.0f, 11.0f, 16.0f).texture("case").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("case").end()
        .face(Direction.UP).uvs(5.0f, 3.0f, 11.0f, 4.0f).texture("case").end()
        .face(Direction.DOWN).uvs(5.0f, 1.0f, 11.0f, 2.0f).texture("case").end()
        .end()

        .element() // south
        .from(5.0f, 0.0f, 2.0f)
        .to(11.0f, 1.0f, 14.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 6.0f, 1.0f).texture("case").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 12.0f, 1.0f).texture("case").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 6.0f, 1.0f).texture("case").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 12.0f, 1.0f).texture("case").end()
        .face(Direction.UP).uvs(0.0f, 0.0f, 6.0f, 12.0f).texture("case").end()
        .face(Direction.DOWN).uvs(5.0f, 2.0f, 11.0f, 14.0f).texture("case").end()
        .end();

      this.getBuilder(GradientIds.GRINDSTONE + "_wheel")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("surface", this.mcLoc("block/polished_andesite"))
        .texture("wheel", this.mcLoc("block/polished_andesite"))
        .texture("rod", this.mcLoc("block/oak_log"))

        .element() // wheel
        .from(7.0f, -2.0f, -2.0f)
        .to(9.0f, 2.0f, 2.0f)
        .face(Direction.NORTH).uvs(0.0f, 8.0f, 16.0f, 16.0f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("wheel").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("surface").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 16.0f, 8.0f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("wheel").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("surface").end()
        .face(Direction.UP).uvs(0.0f, 8.0f, 16.0f, 16.0f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("wheel").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 16.0f, 8.0f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("wheel").end()
        .end()

        .element() // handle
        .from(4.0f, -0.5f, -0.5f)
        .to(12.0f, 0.5f, 0.5f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("rod").end()
        .face(Direction.EAST).uvs(5.0f, 0.0f, 7.0f, 2.0f).texture("rod").end()
        .face(Direction.SOUTH).uvs(0.0f, 4.0f, 16.0f, 6.0f).texture("rod").end()
        .face(Direction.WEST).uvs(10.0f, 5.0f, 12.0f, 7.0f).texture("rod").end()
        .face(Direction.UP).uvs(0.0f, 2.0f, 16.0f, 4.0f).texture("rod").end()
        .face(Direction.DOWN).uvs(0.0f, 6.0f, 16.0f, 8.0f).texture("rod").end()
        .end();

      this.getBuilder(GradientIds.UNHARDENED_CLAY_FURNACE)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/clay"))
        .texture("clay", this.mcLoc("block/clay"))

        .element() // Left
        .from(0.0f, 0.0f, 1.0f)
        .to(2.0f, 15.0f, 15.0f)
        .face(Direction.NORTH).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(0.0f, 1.0f, 2.0f, 15.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("clay").end()
        .end()

        .element() // Right
        .from(14.0f, 0.0f, 1.0f)
        .to(16.0f, 15.0f, 15.0f)
        .face(Direction.NORTH).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(0.0f, 1.0f, 2.0f, 15.0f).texture("clay").end()
        .end()

        .element() // Back
        .from(1.0f, 0.0f, 14.0f)
        .to(15.0f, 15.0f, 16.0f)
        .face(Direction.NORTH).uvs(2.0f, 1.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 1.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("clay").end()
        .end()

        .element() // Front Right
        .from(1.0f, 0.0f, 0.0f)
        .to(4.0f, 15.0f, 2.0f)
        .face(Direction.NORTH).uvs(12.0f, 1.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(1.0f, 1.0f, 4.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(1.0f, 0.0f, 4.0f, 2.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("clay").end()
        .end()

        .element() // Front Left
        .from(12.0f, 0.0f, 0.0f)
        .to(15.0f, 15.0f, 2.0f)
        .face(Direction.NORTH).uvs(1.0f, 1.0f, 4.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(12.0f, 1.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(1.0f, 0.0f, 4.0f, 2.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("clay").end()
        .end()

        .element() // Front Middle
        .from(4.0f, 8.0f, 0.0f)
        .to(12.0f, 15.0f, 2.0f)
        .face(Direction.NORTH).uvs(4.0f, 1.0f, 12.0f, 8.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(4.0f, 1.0f, 12.0f, 12.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 1.0f, 2.0f, 12.0f).texture("clay").end()
        .face(Direction.UP).uvs(1.0f, 0.0f, 9.0f, 2.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("clay").end()
        .end()

        .element() // Top Left
        .from(1.0f, 14.0f, 1.0f)
        .to(5.0f, 16.0f, 15.0f)
        .face(Direction.NORTH).uvs(11.0f, 13.0f, 15.0f, 15.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").end()
        .face(Direction.UP).uvs(0.0f, 1.0f, 4.0f, 15.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(12.0f, 1.0f, 16.0f, 15.0f).texture("clay").end()
        .end()

        .element() // Top Right
        .from(11.0f, 14.0f, 1.0f)
        .to(15.0f, 16.0f, 15.0f)
        .face(Direction.NORTH).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 14.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(11.0f, 13.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").end()
        .face(Direction.UP).uvs(12.0f, 1.0f, 16.0f, 15.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(0.0f, 1.0f, 4.0f, 15.0f).texture("clay").end()
        .end()

        .element() // Top Back
        .from(5.0f, 14.0f, 1.0f)
        .to(11.0f, 16.0f, 5.0f)
        .face(Direction.NORTH).uvs(5.0f, 13.0f, 11.0f, 15.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(5.0f, 1.0f, 11.0f, 3.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 0.0f, 5.0f, 2.0f).texture("clay").end()
        .face(Direction.UP).uvs(5.0f, 1.0f, 11.0f, 5.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(5.0f, 11.0f, 11.0f, 15.0f).texture("clay").end()
        .end()

        .element() // Top Front
        .from(5.0f, 14.0f, 11.0f)
        .to(11.0f, 16.0f, 15.0f)
        .face(Direction.NORTH).uvs(5.0f, 1.0f, 11.0f, 3.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(5.0f, 13.0f, 11.0f, 15.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 0.0f, 5.0f, 2.0f).texture("clay").end()
        .face(Direction.UP).uvs(5.0f, 11.0f, 11.0f, 15.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(5.0f, 1.0f, 11.0f, 5.0f).texture("clay").end()
        .end();

      this.getBuilder(GradientIds.UNHARDENED_CLAY_CRUCIBLE)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/clay"))
        .texture("clay", this.mcLoc("block/clay"))

        .element() // Right
        .from(0.0f, 12.001f, 1.0f)
        .to(2.0f, 15.001f, 15.0f)
        .face(Direction.NORTH).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 1.0f, 2.0f, 4.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
        .end()

        .element() // Back
        .from(1.0f, 12.0f, 14.0f)
        .to(15.0f, 15.0f, 16.0f)
        .face(Direction.NORTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 4.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.WEST).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("clay").end()
        .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("clay").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").end()
        .end()

        .element() // Left
        .from(14.0f, 12.001f, 1.0f)
        .to(16.0f, 15.001f, 15.0f)
        .face(Direction.NORTH).uvs(0.0f, 1.0f, 2.0f, 4.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .end()

        .element() // Front
        .from(1.0f, 12.0f, 0.0f)
        .to(15.0f, 15.0f, 2.0f)
        .face(Direction.NORTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.EAST).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 1.0f, 15.0f, 4.0f).texture("clay").end()
        .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("clay").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("clay").end()
        .end()

        .element() // Right
        .from(1.0f, 0.001f, 2.0f)
        .to(3.0f, 13.001f, 14.0f)
        .face(Direction.NORTH).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 2.0f, 14.0f, 4.0f).texture("clay").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(13.0f, 2.0f, 15.0f, 14.0f).texture("clay").end()
        .end()

        .element() // Back
        .from(2.0f, 0.0f, 13.0f)
        .to(14.0f, 13.0f, 15.0f)
        .face(Direction.NORTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 2.0f, 13.0f, 4.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(1.0f, 2.0f, 3.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .end()

        .element() // Left
        .from(13.0f, 0.001f, 2.0f)
        .to(15.0f, 13.001f, 14.0f)
        .face(Direction.NORTH).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 2.0f, 14.0f, 4.0f).texture("clay").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(1.0f, 2.0f, 3.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Front
        .from(2.0f, 0.0f, 1.0f)
        .to(14.0f, 13.0f, 3.0f)
        .face(Direction.NORTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 2.0f, 14.0f, 4.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(1.0f, 2.0f, 3.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
        .end()

        .element() // Bottom
        .from(3.0f, 0.0f, 3.0f)
        .to(13.0f, 1.0f, 13.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 3.0f, 13.0f, 13.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(3.0f, 3.0f, 13.0f, 13.0f).texture("clay").end()
        .end();

      this.getBuilder(GradientIds.UNHARDENED_CLAY_OVEN)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/clay"))
        .texture("clay", this.mcLoc("block/clay"))

        .element() // Right
        .from(3.0f, 0.0f, 6.0f)
        .to(6.0f, 1.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(10.0f, 6.0f, 13.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(10.0f, 6.0f, 13.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Left
        .from(10.0f, 0.0f, 6.0f)
        .to(13.0f, 1.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 6.0f, 6.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(3.0f, 6.0f, 6.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Up
        .from(3.0f, 0.0f, 10.0f)
        .to(13.0f, 1.0f, 14.0f)
        .face(Direction.NORTH).uvs(2.0f, 15.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.EAST).uvs(2.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
        .face(Direction.WEST).uvs(10.0f, 10.0f, 14.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 2.0f, 13.0f, 6.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(3.0f, 9.0f, 13.0f, 13.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Down
        .from(3.0f, 0.0f, 2.0f)
        .to(13.0f, 1.0f, 6.0f)
        .face(Direction.NORTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.EAST).uvs(10.0f, 10.0f, 14.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 15.0f, 14.0f, 16.0f).texture("clay").end()
        .face(Direction.WEST).uvs(2.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 10.0f, 13.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(3.0f, 3.0f, 13.0f, 7.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Bar 1
        .from(8.875f, 0.0f, 6.0f)
        .to(9.375f, 1.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("clay").end()
        .end()

        .element() // Bar 2
        .from(7.75f, 0.0f, 6.0f)
        .to(8.25f, 1.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("clay").end()
        .end()

        .element() // Bar 3
        .from(6.625f, 0.0f, 6.0f)
        .to(7.125f, 1.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("clay").end()
        .face(Direction.UP).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("clay").end()
        .end()

        .element() // Left
        .from(13.0f, 0.0f, 3.0f)
        .to(14.0f, 5.0f, 13.0f)
        .face(Direction.NORTH).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
        .face(Direction.EAST).uvs(3.0f, 6.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.WEST).uvs(2.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 2.0f, 3.0f, 14.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(2.0f, 3.0f, 3.0f, 13.0f).texture("clay").end()
        .end()

        .element() // Right
        .from(2.0f, 0.0f, 3.0f)
        .to(3.0f, 5.0f, 13.0f)
        .face(Direction.NORTH).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.EAST).uvs(2.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
        .face(Direction.WEST).uvs(3.0f, 6.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(13.0f, 2.0f, 14.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(13.0f, 3.0f, 14.0f, 13.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element()
        .from(3.0f, 1.0f, 13.0f)
        .to(13.0f, 5.0f, 14.0f)
        .face(Direction.NORTH).uvs(3.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
        .face(Direction.EAST).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(3.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
        .face(Direction.WEST).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 2.0f, 12.0f, 3.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("clay").end()
        .end()

        .element() // Right
        .from(2.0f, 5.0f, 6.0f)
        .to(6.0f, 6.0f, 10.0f)
        .face(Direction.NORTH).uvs(10.0f, 5.0f, 14.0f, 6.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 5.0f, 6.0f, 6.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(10.0f, 6.0f, 14.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(12.0f, 6.0f, 16.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Left
        .from(10.0f, 5.0f, 6.0f)
        .to(14.0f, 6.0f, 10.0f)
        .face(Direction.NORTH).uvs(2.0f, 5.0f, 6.0f, 6.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(10.0f, 5.0f, 14.0f, 6.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(2.0f, 6.0f, 6.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(0.0f, 6.0f, 4.0f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Up
        .from(3.0f, 5.0f, 10.0f)
        .to(13.0f, 6.0f, 13.0f)
        .face(Direction.NORTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.EAST).uvs(3.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.WEST).uvs(10.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 3.0f, 13.0f, 6.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(2.0f, 12.0f, 14.0f, 16.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Down
        .from(3.0f, 5.0f, 3.0f)
        .to(13.0f, 6.0f, 6.0f)
        .face(Direction.NORTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.EAST).uvs(10.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(2.0f, 10.0f, 14.0f, 11.0f).texture("clay").end()
        .face(Direction.WEST).uvs(3.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 10.0f, 13.0f, 13.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 4.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Bar 1
        .from(8.875f, 5.0f, 6.0f)
        .to(9.375f, 6.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("clay").end()
        .end()

        .element() // Bar 2
        .from(7.75f, 5.0f, 6.0f)
        .to(8.25f, 6.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("clay").end()
        .end()

        .element() // Bar 3
        .from(6.625f, 5.0f, 6.0f)
        .to(7.125f, 6.0f, 10.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
        .face(Direction.UP).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("clay").end()
        .end()

        .element() // Left
        .from(11.0f, 1.0f, 2.0f)
        .to(13.0f, 5.0f, 3.0f)
        .face(Direction.NORTH).uvs(3.0f, 6.0f, 5.0f, 10.0f).texture("clay").end()
        .face(Direction.EAST).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(11.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
        .face(Direction.WEST).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
        .face(Direction.UP).uvs(3.0f, 13.0f, 5.0f, 14.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("clay").end()
        .end()

        .element() // Right
        .from(3.0f, 1.0f, 2.0f)
        .to(5.0f, 5.0f, 3.0f)
        .face(Direction.NORTH).uvs(11.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
        .face(Direction.EAST).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(3.0f, 6.0f, 5.0f, 10.0f).texture("clay").end()
        .face(Direction.WEST).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
        .face(Direction.UP).uvs(11.0f, 13.0f, 13.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // Middle
        .from(5.0f, 4.0f, 2.0f)
        .to(11.0f, 5.0f, 3.0f)
        .face(Direction.NORTH).uvs(5.0f, 6.0f, 11.0f, 7.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 1.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(5.0f, 6.0f, 11.0f, 7.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 1.0f).texture("clay").end()
        .face(Direction.UP).uvs(5.0f, 13.0f, 11.0f, 14.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(5.0f, 2.0f, 11.0f, 3.0f).texture("clay").end()
        .end();

      this.getBuilder(GradientIds.UNHARDENED_CLAY_MIXER)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/clay"))
        .texture("side", this.mcLoc("block/clay"))

        .element() // Corner 1
        .from(13.0f, -2.0f, 0.0f)
        .to(16.0f, 2.0f, 3.0f)
        .face(Direction.NORTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.EAST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.UP).uvs(0.0f, 13.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.DOWN).uvs(13.0f, 13.0f, 16.0f, 16.0f).texture("side").end()
        .end()

        .element() // Corner 2
        .from(13.0f, -2.0f, 13.0f)
        .to(16.0f, 2.0f, 16.0f)
        .face(Direction.NORTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.WEST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.UP).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("side").end()
        .face(Direction.DOWN).uvs(13.0f, 0.0f, 16.0f, 3.0f).texture("side").end()
        .end()

        .element() // Corner 3
        .from(0.0f, -2.0f, 13.0f)
        .to(3.0f, 2.0f, 16.0f)
        .face(Direction.NORTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.WEST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.UP).uvs(13.0f, 0.0f, 16.0f, 3.0f).texture("side").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("side").end()
        .end()

        .element() // Corner 4
        .from(0.0f, -2.0f, 0.0f)
        .to(3.0f, 2.0f, 3.0f)
        .face(Direction.NORTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.EAST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
        .face(Direction.UP).uvs(13.0f, 13.0f, 16.0f, 16.0f).texture("side").end()
        .face(Direction.DOWN).uvs(0.0f, 13.0f, 3.0f, 16.0f).texture("side").end()
        .end()

        .element() // Cross 1
        .from(7.0f, -1.0f, -2.0f)
        .to(9.0f, 1.0f, 17.0f)
        .rotation().angle(45.0f).axis(Direction.Axis.Y).origin(8.0f, 6.0f, 8.0f).end()
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 7.0f, 16.0f, 9.0f).texture("side").end()
        .face(Direction.UP).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
        .face(Direction.DOWN).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
        .end()

        .element() // Cross 2
        .from(7.0f, -1.0f, -2.0f)
        .to(9.0f, 1.0f, 17.0f)
        .rotation().angle(-45.0f).axis(Direction.Axis.Y).origin(8.0f, 6.0f, 8.0f).end()
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
        .face(Direction.EAST).uvs(0.0f, 7.0f, 16.0f, 9.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("side").end()
        .face(Direction.UP).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
        .face(Direction.DOWN).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
        .end()

        .element() // Middle
        .from(5.0f, -2.0f, 5.0f)
        .to(11.0f, 2.0f, 11.0f)
        .face(Direction.NORTH).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
        .face(Direction.EAST).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
        .face(Direction.WEST).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
        .face(Direction.UP).uvs(5.0f, 5.0f, 11.0f, 11.0f).texture("side").end()
        .face(Direction.DOWN).uvs(5.0f, 5.0f, 11.0f, 11.0f).texture("side").end()
        .end()

        .element() // Middle Cap
        .from(6.0f, 2.0f, 6.0f)
        .to(10.0f, 3.0f, 10.0f)
        .face(Direction.NORTH).uvs(6.0f, 11.0f, 10.0f, 12.0f).texture("side").end()
        .face(Direction.EAST).uvs(6.0f, 11.0f, 10.0f, 12.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("side").end()
        .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("side").end()
        .face(Direction.UP).uvs(6.0f, 6.0f, 10.0f, 10.0f).texture("side").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 4.0f, 4.0f).texture("side").end()
        .end();

      this.getBuilder(GradientIds.UNHARDENED_CLAY_CAST_BLANK)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.mcLoc("block/clay"))
        .texture("clay", this.mcLoc("block/clay"))

        .element()
        .from(0.0f, 0.0f, 0.0f)
        .to(16.0f, 2.0f, 16.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("clay").end()
        .face(Direction.EAST).uvs(0.0f, 6.0f, 16.0f, 8.0f).texture("clay").end()
        .face(Direction.SOUTH).uvs(0.0f, 2.0f, 16.0f, 4.0f).texture("clay").end()
        .face(Direction.WEST).uvs(0.0f, 4.0f, 16.0f, 6.0f).texture("clay").end()
        .face(Direction.UP).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("clay").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("clay").end()
        .end();

      for(final GradientCasts cast : GradientCasts.values()) {
        this.getBuilder(GradientIds.UNHARDENED_CLAY_CAST(cast))
          .parent(this.getExistingFile(this.mcLoc("block/block")))
          .texture("particle", this.mcLoc("block/clay"))
          .texture("clay", this.mcLoc("block/clay"))
          .texture("cast", this.modLoc("block/unhardened_clay_cast_" + cast.name().toLowerCase()))

          .element()
          .from(0.0f, 0.0f, 0.0f)
          .to(16.0f, 2.0f, 16.0f)
          .face(Direction.NORTH).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("clay").end()
          .face(Direction.EAST).uvs(0.0f, 6.0f, 16.0f, 8.0f).texture("clay").end()
          .face(Direction.SOUTH).uvs(0.0f, 2.0f, 16.0f, 4.0f).texture("clay").end()
          .face(Direction.WEST).uvs(0.0f, 4.0f, 16.0f, 6.0f).texture("clay").end()
          .face(Direction.UP).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("clay").end()
          .face(Direction.DOWN).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("clay").end()
          .end()

          .element()
          .from(0.0f, 2.001f, 0.0f)
          .to(16.0f, 2.002f, 16.0f)
          .face(Direction.UP).uvs(0.0f, 0.0f, 16.0f, 16.0f).texture("cast").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
          .end();
      }
    }

    private BiConsumer<Direction, ModelBuilder<BlockModelBuilder>.ElementBuilder.FaceBuilder> addTexture(final String texture) {
      return ($, f) -> f.texture(texture);
    }

    @Override
    public String getName() {
      return "Gradient block model generator";
    }
  }

  public static class ItemModels extends ItemModelProvider {
    public ItemModels(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
      super(generator, Gradient.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
      this.singleTexture(GradientIds.PEBBLE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.PEBBLE));

      for(final Ore ore : Ores.all()) {
        this.getBuilder(GradientIds.ORE(ore)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.ORE(ore))));
      }

      this.getBuilder("crushed")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/crushed_background"))
        .texture("layer1", this.modLoc("item/crushed_diffuse"))
        .texture("layer2", this.modLoc("item/crushed_specular"))
        .texture("layer3", this.modLoc("item/crushed_shadow_1"))
        .texture("layer4", this.modLoc("item/crushed_shadow_2"))
        .texture("layer5", this.modLoc("item/crushed_edge_1"))
        .texture("layer6", this.modLoc("item/crushed_edge_2"))
        .texture("layer7", this.modLoc("item/crushed_edge_3"))
      ;

      this.getBuilder("purified")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/purified_background"))
        .texture("layer1", this.modLoc("item/purified_diffuse"))
        .texture("layer2", this.modLoc("item/purified_specular"))
        .texture("layer3", this.modLoc("item/purified_shadow_1"))
        .texture("layer4", this.modLoc("item/purified_shadow_2"))
        .texture("layer5", this.modLoc("item/purified_edge_1"))
        .texture("layer6", this.modLoc("item/purified_edge_2"))
        .texture("layer7", this.modLoc("item/purified_edge_3"))
      ;

      this.getBuilder("dust")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/dust_background"))
        .texture("layer1", this.modLoc("item/dust_diffuse"))
        .texture("layer2", this.modLoc("item/dust_specular"))
        .texture("layer3", this.modLoc("item/dust_shadow_1"))
        .texture("layer4", this.modLoc("item/dust_shadow_2"))
        .texture("layer5", this.modLoc("item/dust_edge_1"))
        .texture("layer6", this.modLoc("item/dust_edge_2"))
        .texture("layer7", this.modLoc("item/dust_edge_3"))
      ;

      this.getBuilder("ingot")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/ingot_background"))
        .texture("layer1", this.modLoc("item/ingot_diffuse"))
        .texture("layer2", this.modLoc("item/ingot_specular"))
        .texture("layer3", this.modLoc("item/ingot_shadow_1"))
        .texture("layer4", this.modLoc("item/ingot_shadow_2"))
        .texture("layer5", this.modLoc("item/ingot_edge_1"))
        .texture("layer6", this.modLoc("item/ingot_edge_2"))
        .texture("layer7", this.modLoc("item/ingot_edge_3"))
      ;

      this.getBuilder("nugget")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/nugget_background"))
        .texture("layer1", this.modLoc("item/nugget_diffuse"))
        .texture("layer2", this.modLoc("item/nugget_specular"))
        .texture("layer3", this.modLoc("item/nugget_shadow_1"))
        .texture("layer4", this.modLoc("item/nugget_shadow_2"))
        .texture("layer5", this.modLoc("item/nugget_edge_1"))
        .texture("layer6", this.modLoc("item/nugget_edge_2"))
        .texture("layer7", this.modLoc("item/nugget_edge_3"))
      ;

      this.getBuilder("plate")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/plate_background"))
        .texture("layer1", this.modLoc("item/plate_diffuse"))
        .texture("layer2", this.modLoc("item/plate_specular"))
        .texture("layer3", this.modLoc("item/plate_shadow_1"))
        .texture("layer4", this.modLoc("item/plate_shadow_2"))
        .texture("layer5", this.modLoc("item/plate_edge_1"))
        .texture("layer6", this.modLoc("item/plate_edge_2"))
        .texture("layer7", this.modLoc("item/plate_edge_3"))
      ;

      for(final Metal metal : Metals.all()) {
        this.getBuilder(GradientIds.CRUSHED(metal)).parent(this.getExistingFile(this.modLoc("item/crushed")));
        this.getBuilder(GradientIds.PURIFIED(metal)).parent(this.getExistingFile(this.modLoc("item/purified")));
        this.getBuilder(GradientIds.DUST(metal)).parent(this.getExistingFile(this.modLoc("item/dust")));
        this.getBuilder(GradientIds.INGOT(metal)).parent(this.getExistingFile(this.modLoc("item/ingot")));
        this.getBuilder(GradientIds.NUGGET(metal)).parent(this.getExistingFile(this.modLoc("item/nugget")));
        this.getBuilder(GradientIds.PLATE(metal)).parent(this.getExistingFile(this.modLoc("item/plate")));
      }

      for(final Metal metal : Metals.all()) {
        this.getBuilder(GradientIds.METAL_BLOCK(metal)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.METAL_BLOCK(metal))));
      }

      this.getBuilder(GradientIds.SALT_BLOCK).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.SALT_BLOCK)));
      this.singleTexture(GradientIds.SALT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.SALT));
      this.singleTexture(GradientIds.FIBRE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FIBRE));
      this.singleTexture(GradientIds.TWINE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.TWINE));
      this.singleTexture(GradientIds.BARK, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.BARK));
      this.singleTexture(GradientIds.MULCH, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.MULCH));
      this.singleTexture(GradientIds.COW_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.COW_PELT));
      this.singleTexture(GradientIds.DONKEY_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.DONKEY_PELT));
      this.singleTexture(GradientIds.HORSE_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HORSE_PELT));
      this.singleTexture(GradientIds.LLAMA_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.LLAMA_PELT));
      this.singleTexture(GradientIds.MULE_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.MULE_PELT));
      this.singleTexture(GradientIds.OCELOT_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.OCELOT_PELT));
      this.singleTexture(GradientIds.PIG_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.PIG_PELT));
      this.singleTexture(GradientIds.POLAR_BEAR_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.POLAR_BEAR_PELT));
      this.singleTexture(GradientIds.SHEEP_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.SHEEP_PELT));
      this.singleTexture(GradientIds.WOLF_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.WOLF_PELT));

      this.singleTexture(GradientIds.RAW_HIDE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.RAW_HIDE));

      this.singleTexture(GradientIds.FIRE_STARTER, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FIRE_STARTER));
      this.singleTexture(GradientIds.STONE_HAMMER, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.STONE_HAMMER));
      this.singleTexture(GradientIds.FLINT_KNIFE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FLINT_KNIFE));

      this.getBuilder(GradientIds.FIREPIT).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)));
      this.getBuilder(GradientIds.GRINDSTONE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.GRINDSTONE)));

      this.getBuilder(GradientIds.UNHARDENED_CLAY_FURNACE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_FURNACE)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_CRUCIBLE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CRUCIBLE)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_OVEN).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_OVEN)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_MIXER).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_MIXER)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_BUCKET).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_BUCKET)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_CAST_BLANK).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST_BLANK)));

      for(final GradientCasts cast : GradientCasts.values()) {
        this.getBuilder(GradientIds.UNHARDENED_CLAY_CAST(cast)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST(cast))));
      }
    }

    @Override
    public String getName() {
      return "Gradient item model generator";
    }
  }

  public static class BlockStates extends BlockStateProvider {
    public BlockStates(final DataGenerator gen, final ExistingFileHelper exFileHelper) {
      super(gen, Gradient.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
      this.simpleBlock(GradientBlocks.PEBBLE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.PEBBLE)));

      for(final Ore ore : Ores.all()) {
        this.simpleBlock(GradientBlocks.PEBBLE(ore).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.PEBBLE(ore))));
      }

      for(final Ore ore : Ores.all()) {
        this.simpleBlock(GradientBlocks.ORE(ore).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.ORE(ore))));
      }

      for(final Metal metal : Metals.all()) {
        this.simpleBlock(GradientBlocks.METAL_BLOCK(metal).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.METAL_BLOCK(metal))));
      }

      this.simpleBlock(GradientBlocks.SALT_BLOCK.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.SALT_BLOCK)));

      this.horizontalBlock(GradientBlocks.FIREPIT.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)));
      this.horizontalBlock(GradientBlocks.GRINDSTONE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.GRINDSTONE)));

      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_FURNACE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_FURNACE)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CRUCIBLE)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_OVEN.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_OVEN)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_MIXER.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_MIXER)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_BUCKET.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_BUCKET)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_CAST_BLANK.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST_BLANK)));

      for(final GradientCasts cast : GradientCasts.values()) {
        this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST(cast))));
      }
    }
  }

  public static class EnglishLang extends LanguageProvider {
    public EnglishLang(final DataGenerator gen) {
      super(gen, Gradient.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
      this.add("stage.gradient.age_1", "Age 1 (Nomadic)");
      this.add("stage.gradient.age_2", "Age 2 (Agricultural)");

      this.add("unhardened_clay.tooltip", "Place next to a fire pit to harden");

      this.add("screens.gradient.unhardened_clay_cast", "Cast Selection");

      for(final GradientCasts cast : GradientCasts.values()) {
        this.add("screens.gradient.unhardened_clay_cast." + cast.name().toLowerCase(), StringUtils.capitalize(cast.name().toLowerCase()));
      }

      this.add("fluids.gradient.air", "Air");

      this.add(GradientBlocks.PEBBLE.get(), "Pebble");
      this.add(GradientItems.PEBBLE.get(), "Pebble");

      for(final Ore ore : Ores.all()) {
        this.add(GradientBlocks.PEBBLE(ore).get(), StringUtils.capitalize(ore.name) + " Pebble");
        this.add(GradientBlocks.ORE(ore).get(), StringUtils.capitalize(ore.name) + " Ore");
      }

      for(final Metal metal : Metals.all()) {
        this.add(GradientItems.CRUSHED(metal).get(), "Crushed " + StringUtils.capitalize(metal.name) + " Ore");
        this.add(GradientItems.PURIFIED(metal).get(), "Purified " + StringUtils.capitalize(metal.name) + " Ore");
        this.add(GradientItems.DUST(metal).get(), StringUtils.capitalize(metal.name) + " Dust");
        this.add(GradientItems.INGOT(metal).get(), StringUtils.capitalize(metal.name) + " Ingot");
        this.add(GradientItems.NUGGET(metal).get(), StringUtils.capitalize(metal.name) + " Nugget");
        this.add(GradientItems.PLATE(metal).get(), StringUtils.capitalize(metal.name) + " Plate");
        this.add(GradientItems.METAL_BLOCK(metal).get(), "Block of " + StringUtils.capitalize(metal.name));
      }

      this.add(GradientItems.SALT_BLOCK.get(), "Block of Salt");
      this.add(GradientItems.SALT.get(), "Salt");
      this.add(GradientItems.FIBRE.get(), "Fibre");
      this.add(GradientItems.TWINE.get(), "Twine");
      this.add(GradientItems.BARK.get(), "Bark");
      this.add(GradientItems.MULCH.get(), "Mulch");
      this.add(GradientItems.COW_PELT.get(), "Cow Pelt");
      this.add(GradientItems.DONKEY_PELT.get(), "Donkey Pelt");
      this.add(GradientItems.HORSE_PELT.get(), "Horse Pelt");
      this.add(GradientItems.LLAMA_PELT.get(), "Llama Pelt");
      this.add(GradientItems.MULE_PELT.get(), "Mule Pelt");
      this.add(GradientItems.OCELOT_PELT.get(), "Ocelot Pelt");
      this.add(GradientItems.PIG_PELT.get(), "Pig Pelt");
      this.add(GradientItems.POLAR_BEAR_PELT.get(), "Polar Bear Pelt");
      this.add(GradientItems.SHEEP_PELT.get(), "Sheep Pelt");
      this.add(GradientItems.WOLF_PELT.get(), "Wolf Pelt");

      this.add(GradientItems.RAW_HIDE.get(), "Rawhide");

      this.add(GradientItems.FIRE_STARTER.get(), "Fire Starter");
      this.add(GradientItems.STONE_HAMMER.get(), "Stone Hammer");
      this.add(GradientItems.STONE_HAMMER.get().getTranslationKey() + ".tooltip", "Use on ores to get metal nuggets");
      this.add(GradientItems.FLINT_KNIFE.get(), "Flint Knife");

      this.add(GradientItems.FIREPIT.get(), "Firepit");
      this.add(GradientItems.FIREPIT.get().getTranslationKey() + ".heat", "%d °C");
      this.add(GradientItems.GRINDSTONE.get(), "Grindstone");

      this.add(GradientItems.UNHARDENED_CLAY_FURNACE.get(), "Unhardened Clay Furnace");
      this.add(GradientItems.UNHARDENED_CLAY_CRUCIBLE.get(), "Unhardened Clay Crucible");
      this.add(GradientItems.UNHARDENED_CLAY_OVEN.get(), "Unhardened Clay Oven");
      this.add(GradientItems.UNHARDENED_CLAY_MIXER.get(), "Unhardened Clay Mixer");
      this.add(GradientItems.UNHARDENED_CLAY_BUCKET.get(), "Unhardened Clay Bucket");
      this.add(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get(), "Unhardened Clay Cast (Blank)");
      this.add(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get().getTranslationKey() + ".tooltip", "Right click to switch cast");

      for(final GradientCasts cast : GradientCasts.values()) {
        this.add(GradientItems.UNHARDENED_CLAY_CAST(cast).get(), "Unhardened Clay Cast (" + StringUtils.capitalize(cast.name().toLowerCase()) + ')');
      }
    }
  }

  public static class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(final DataGenerator gen) {
      super(gen);
    }

    @Override
    protected void registerTags() {
      this.getBuilder(GradientTags.Blocks.FIBRE_SOURCES)
        .add(Tags.Blocks.DIRT)
        .add(BlockTags.CROPS)
        .add(BlockTags.LEAVES)
        .add(Blocks.GRASS)
        .add(Blocks.TALL_GRASS);

      this.getBuilder(GradientTags.Blocks.PEBBLE_SOURCES)
        .add(Tags.Blocks.GRAVEL);

      for(final Ore ore : Ores.all()) {
        this.getBuilder(GradientTags.Blocks.ORE.get(ore)).add(GradientBlocks.ORE(ore).get());
        this.getBuilder(Tags.Blocks.ORES).add(GradientTags.Blocks.ORE.get(ore));
      }

      for(final Metal metal : Metals.all()) {
        this.getBuilder(GradientTags.Blocks.STORAGE_BLOCK.get(metal)).add(GradientBlocks.METAL_BLOCK(metal).get());
        this.getBuilder(Tags.Blocks.STORAGE_BLOCKS).add(GradientTags.Blocks.STORAGE_BLOCK.get(metal));
      }
    }
  }

  public static class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(final DataGenerator gen) {
      super(gen);
    }

    @Override
    protected void registerTags() {
      for(final Ore ore : Ores.all()) {
        this.getBuilder(GradientTags.Items.ORE.get(ore)).add(GradientItems.ORE(ore).get());
        this.getBuilder(Tags.Items.ORES).add(GradientTags.Items.ORE.get(ore));
      }

      for(final Metal metal : Metals.all()) {
        this.getBuilder(GradientTags.Items.CRUSHED_ORE.get(metal)).add(GradientItems.CRUSHED(metal).get());
        this.getBuilder(GradientTags.Items.CRUSHED_ORES).add(GradientTags.Items.CRUSHED_ORE.get(metal));

        this.getBuilder(GradientTags.Items.PURIFIED_ORE.get(metal)).add(GradientItems.PURIFIED(metal).get());
        this.getBuilder(GradientTags.Items.PURIFIED_ORES).add(GradientTags.Items.PURIFIED_ORE.get(metal));

        this.getBuilder(GradientTags.Items.DUST.get(metal)).add(GradientItems.DUST(metal).get());
        this.getBuilder(Tags.Items.DUSTS).add(GradientTags.Items.DUST.get(metal));

        this.getBuilder(GradientTags.Items.INGOT.get(metal)).add(GradientItems.INGOT(metal).get());
        this.getBuilder(Tags.Items.INGOTS).add(GradientTags.Items.INGOT.get(metal));

        this.getBuilder(GradientTags.Items.NUGGET.get(metal)).add(GradientItems.NUGGET(metal).get());
        this.getBuilder(Tags.Items.NUGGETS).add(GradientTags.Items.NUGGET.get(metal));

        this.getBuilder(GradientTags.Items.PLATE.get(metal)).add(GradientItems.PLATE(metal).get());
        this.getBuilder(GradientTags.Items.PLATES).add(GradientTags.Items.PLATE.get(metal));

        this.getBuilder(GradientTags.Items.STORAGE_BLOCK.get(metal)).add(GradientItems.METAL_BLOCK(metal).get());
        this.getBuilder(Tags.Items.STORAGE_BLOCKS).add(GradientTags.Items.STORAGE_BLOCK.get(metal));
      }

      this.getBuilder(Tags.Items.STRING).add(GradientItems.TWINE.get());

      this.getBuilder(GradientTags.Items.PELTS).add(GradientTags.Items.PELTS_COW, GradientTags.Items.PELTS_DONKEY, GradientTags.Items.PELTS_HORSE, GradientTags.Items.PELTS_LLAMA, GradientTags.Items.PELTS_MULE, GradientTags.Items.PELTS_OCELOT, GradientTags.Items.PELTS_PIG, GradientTags.Items.PELTS_POLAR_BEAR, GradientTags.Items.PELTS_SHEEP, GradientTags.Items.PELTS_WOLF);
      this.getBuilder(GradientTags.Items.PELTS_COW).add(GradientItems.COW_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_DONKEY).add(GradientItems.DONKEY_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_HORSE).add(GradientItems.HORSE_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_LLAMA).add(GradientItems.LLAMA_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_MULE).add(GradientItems.MULE_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_OCELOT).add(GradientItems.OCELOT_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_PIG).add(GradientItems.PIG_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_POLAR_BEAR).add(GradientItems.POLAR_BEAR_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_SHEEP).add(GradientItems.SHEEP_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_WOLF).add(GradientItems.WOLF_PELT.get());
    }
  }

  public static class Recipes extends RecipeProvider {
    public Recipes(final DataGenerator gen) {
      super(gen);
    }

    @Override
    protected void registerRecipes(final Consumer<IFinishedRecipe> finished) {
      this.registerFuelRecipes(finished);
      this.registerCookingRecipes(finished);

      ShapedRecipeBuilder
        .shapedRecipe(GradientItems.SALT_BLOCK.get())
        .patternLine("SS")
        .patternLine("SS")
        .key('S', GradientItems.SALT.get())
        .addCriterion("has_salt", this.hasItem(GradientItems.SALT.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.SALT_BLOCK));

      ShapelessRecipeBuilder
        .shapelessRecipe(GradientItems.TWINE.get())
        .addIngredient(GradientItems.FIBRE.get(), 4)
        .addCriterion("has_fibre", this.hasItem(GradientItems.FIBRE.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.TWINE));

      GradientRecipeBuilder
        .grinding(GradientItems.MULCH.get())
        .stage(GradientStages.AGE_2)
        .ticks(40)
        .addIngredient(GradientItems.BARK.get())
        .addCriterion("has_bark", this.hasItem(GradientItems.BARK.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.MULCH));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.RAW_HIDE.get())
        .addIngredient(GradientTags.Items.PELTS)
        .addIngredient(GradientItems.FLINT_KNIFE.get())
        .addCriterion("has_hide", this.hasItem(GradientTags.Items.PELTS))
        .build(finished, Gradient.loc("age2/" + GradientIds.RAW_HIDE));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.FIRE_STARTER.get())
        .addIngredient(Tags.Items.RODS_WOODEN)
        .addIngredient(Tags.Items.STRING)
        .addIngredient(Tags.Items.RODS_WOODEN)
        .addCriterion("has_sticks", this.hasItem(Tags.Items.RODS_WOODEN))
        .build(finished, Gradient.loc("age1/" + GradientIds.FIRE_STARTER));

      StagedRecipeBuilder
        .shaped(GradientItems.STONE_HAMMER.get())
        .stage(GradientStages.AGE_1)
        .patternLine("P")
        .patternLine("F")
        .patternLine("S")
        .key('P', GradientItems.PEBBLE.get())
        .key('F', Tags.Items.STRING)
        .key('S', Tags.Items.RODS_WOODEN)
        .addCriterion("has_pebble", this.hasItem(GradientItems.PEBBLE.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.STONE_HAMMER));

      StagedRecipeBuilder
        .shaped(GradientItems.FLINT_KNIFE.get())
        .stage(GradientStages.AGE_1)
        .patternLine("F")
        .patternLine("S")
        .patternLine("W")
        .key('F', Items.FLINT)
        .key('S', Tags.Items.STRING)
        .key('W', Tags.Items.RODS_WOODEN)
        .addCriterion("has_flint", this.hasItem(Items.FLINT))
        .build(finished, Gradient.loc("age1/" + GradientIds.FLINT_KNIFE));

      StagedRecipeBuilder
        .shaped(GradientItems.FIREPIT.get())
        .stage(GradientStages.AGE_1)
        .patternLine(" S ")
        .patternLine("SSS")
        .patternLine(" S ")
        .key('S', Tags.Items.RODS_WOODEN)
        .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
        .build(finished, Gradient.loc("age1/" + GradientIds.FIREPIT));

      StagedRecipeBuilder
        .shaped(GradientItems.GRINDSTONE.get())
        .stage(GradientStages.AGE_1)
        .patternLine(" P ")
        .patternLine("S S")
        .patternLine("CSC")
        .key('P', GradientItems.PEBBLE.get())
        .key('S', Tags.Items.COBBLESTONE)
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_pebble", this.hasItem(GradientItems.PEBBLE.get()))
        .addCriterion("has_cobblestone", this.hasItem(Tags.Items.COBBLESTONE))
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age1/" + GradientIds.GRINDSTONE));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_FURNACE.get())
        .stage(GradientStages.AGE_2)
        .patternLine("CCC")
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_FURNACE));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_CRUCIBLE.get())
        .stage(GradientStages.AGE_2)
        .patternLine("C C")
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_CRUCIBLE));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_OVEN.get())
        .stage(GradientStages.AGE_2)
        .patternLine(" C ")
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_OVEN));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_MIXER.get())
        .stage(GradientStages.AGE_2)
        .patternLine("CCC")
        .patternLine("PCP")
        .patternLine("PCP")
        .key('C', Items.CLAY_BALL)
        .key('P', GradientItems.PEBBLE.get())
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_MIXER));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_BUCKET.get())
        .stage(GradientStages.AGE_2)
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_BUCKET));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get())
        .stage(GradientStages.AGE_2)
        .addIngredient(Items.CLAY_BALL)
        .addIngredient(ItemTags.SAND)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_CAST_BLANK));
    }

    private void registerFuelRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .fuel()
        .duration(1200)
        .ignitionTemp(700.0f)
        .burnTemp(2700.0f)
        .heatPerSecond(1.5f)
        .ingredient(ItemTags.COALS)
        .addCriterion("has_coal", this.hasItem(ItemTags.COALS))
        .build(finished, Gradient.loc("fuel/coal"));

      GradientRecipeBuilder
        .fuel()
        .duration(5)
        .ignitionTemp(50.0f)
        .burnTemp(125.0f)
        .heatPerSecond(18.0f)
        .ingredient(GradientItems.FIBRE.get())
        .addCriterion("has_fibre", this.hasItem(GradientItems.FIBRE.get()))
        .build(finished, Gradient.loc("fuel/" + GradientIds.FIBRE));

      GradientRecipeBuilder
        .fuel()
        .duration(900)
        .ignitionTemp(300.0f)
        .burnTemp(750.0f)
        .heatPerSecond(0.76f)
        .ingredient(ItemTags.LOGS)
        .addCriterion("has_log", this.hasItem(ItemTags.LOGS))
        .build(finished, Gradient.loc("fuel/log"));

      GradientRecipeBuilder
        .fuel()
        .duration(600)
        .ignitionTemp(230.0f)
        .burnTemp(750.0f)
        .heatPerSecond(1.04f)
        .ingredient(ItemTags.PLANKS)
        .addCriterion("has_planks", this.hasItem(ItemTags.PLANKS))
        .build(finished, Gradient.loc("fuel/planks"));

      GradientRecipeBuilder
        .fuel()
        .duration(60)
        .ignitionTemp(150.0f)
        .burnTemp(350.0f)
        .heatPerSecond(1.38f)
        .ingredient(ItemTags.SAPLINGS)
        .addCriterion("has_sapling", this.hasItem(ItemTags.SAPLINGS))
        .build(finished, Gradient.loc("fuel/sapling"));

      GradientRecipeBuilder
        .fuel()
        .duration(30)
        .ignitionTemp(100.0f)
        .burnTemp(350.0f)
        .heatPerSecond(2.16f)
        .ingredient(Tags.Items.RODS_WOODEN)
        .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
        .build(finished, Gradient.loc("fuel/stick"));

      GradientRecipeBuilder
        .fuel()
        .duration(20)
        .ignitionTemp(50.0f)
        .burnTemp(125.0f)
        .heatPerSecond(18.0f)
        .ingredient(Tags.Items.STRING)
        .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
        .build(finished, Gradient.loc("fuel/string"));
    }

    private void registerCookingRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .cooking(Items.COOKED_BEEF)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.BEEF)
        .addCriterion("has_beef", this.hasItem(Items.BEEF))
        .build(finished, Gradient.loc("cooking/age1/beef"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_CHICKEN)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.CHICKEN)
        .addCriterion("has_chicken", this.hasItem(Items.CHICKEN))
        .build(finished, Gradient.loc("cooking/age1/chicken"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_COD)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.COD)
        .addCriterion("has_cod", this.hasItem(Items.COD))
        .build(finished, Gradient.loc("cooking/age1/cod"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_MUTTON)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.MUTTON)
        .addCriterion("has_mutton", this.hasItem(Items.MUTTON))
        .build(finished, Gradient.loc("cooking/age1/mutton"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_PORKCHOP)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.PORKCHOP)
        .addCriterion("has_porkchop", this.hasItem(Items.PORKCHOP))
        .build(finished, Gradient.loc("cooking/age1/porkchop"));

      GradientRecipeBuilder
        .cooking(Items.BAKED_POTATO)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.POTATO)
        .addCriterion("has_potato", this.hasItem(Items.POTATO))
        .build(finished, Gradient.loc("cooking/age1/potato"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_RABBIT)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.RABBIT)
        .addCriterion("has_rabbit", this.hasItem(Items.RABBIT))
        .build(finished, Gradient.loc("cooking/age1/rabbit"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_SALMON)
        .stage(GradientStages.AGE_1)
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.SALMON)
        .addCriterion("has_salmon", this.hasItem(Items.SALMON))
        .build(finished, Gradient.loc("cooking/age1/salmon"));
    }
  }

  public static class Loot extends LootTableProvider {
    //TODO: register a custom item predicate that accepts the hammer tool type
    private static final ILootCondition.IBuilder STONE_HAMMER = MatchTool.builder(ItemPredicate.Builder.create().item(GradientItems.STONE_HAMMER.get()));
    private static final ILootCondition.IBuilder NOT_STONE_HAMMER = STONE_HAMMER.inverted();

    public Loot(final DataGenerator gen) {
      super(gen);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
      return ImmutableList.of(
        Pair.of(FibreAdditionsLootTable::new, LootParameterSets.BLOCK),
        Pair.of(PebbleAdditionsLootTable::new, LootParameterSets.BLOCK),
        Pair.of(() -> new PeltDropsTable(GradientIds.COW_PELT, GradientItems.COW_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.DONKEY_PELT, GradientItems.DONKEY_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.HORSE_PELT, GradientItems.HORSE_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.LLAMA_PELT, GradientItems.LLAMA_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.MULE_PELT, GradientItems.MULE_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.OCELOT_PELT, GradientItems.OCELOT_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.PIG_PELT, GradientItems.PIG_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.POLAR_BEAR_PELT, GradientItems.POLAR_BEAR_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.SHEEP_PELT, GradientItems.SHEEP_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.WOLF_PELT, GradientItems.WOLF_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(BlockTables::new, LootParameterSets.BLOCK)
      );
    }

    @Override
    protected void validate(final Map<ResourceLocation, LootTable> map, final ValidationTracker validationtracker) {

    }

    public static class FibreAdditionsLootTable implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
      @Override
      public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        builder.accept(GradientLoot.FIBRE_ADDITIONS, LootTable.builder().addLootPool(
          LootPool.builder().addEntry(
            ItemLootEntry
              .builder(GradientItems.FIBRE.get())
              .acceptCondition(
                Inverted.builder(
                  MatchTool.builder(ItemPredicate.Builder.create().item(Items.SHEARS))
                )
              )
              .acceptCondition(
                RandomChance.builder(0.125f)
              )
              .acceptFunction(
                ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2)
              )
              .acceptFunction(
                ExplosionDecay.builder()
              )
          )
        ));
      }
    }

    public static class PebbleAdditionsLootTable implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
      @Override
      public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        builder.accept(GradientLoot.PEBBLE_ADDITIONS, LootTable.builder().addLootPool(
          LootPool.builder().addEntry(
            ItemLootEntry
              .builder(GradientItems.PEBBLE.get())
              .acceptCondition(RandomChance.builder(0.125f))
              .acceptFunction(ExplosionDecay.builder())
          ).rolls(ConstantRange.of(3))
        ));
      }
    }

    public static class PeltDropsTable implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
      private final String name;
      private final Item pelt;

      public PeltDropsTable(final String name, final Item pelt) {
        this.name = name;
        this.pelt = pelt;
      }

      @Override
      public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        builder.accept(Gradient.loc("entities/" + this.name + "_additions"), LootTable.builder().addLootPool(
          LootPool.builder().addEntry(
            ItemLootEntry
              .builder(this.pelt)
              .acceptFunction(ExplosionDecay.builder())
          )
        ));
      }
    }

    public static class BlockTables extends BlockLootTables {
      private static LootTable.Builder pebbleDrops() {
        return LootTable.builder()
          .addLootPool(
            withSurvivesExplosion(
              GradientItems.PEBBLE.get(),
              LootPool.builder()
                .rolls(RandomValueRange.of(0, 3))
                .addEntry(ItemLootEntry.builder(GradientItems.PEBBLE.get()).weight(5))
                .addEntry(ItemLootEntry.builder(Items.FLINT).weight(1))
            )
          );
      }

      private static LootTable.Builder metalPebbleDrops(final Metal metal) {
        return pebbleDrops()
          .addLootPool(
            withSurvivesExplosion(
              GradientItems.NUGGET(metal).get(),
              LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(GradientItems.NUGGET(metal).get()))
                .acceptCondition(RandomChance.builder(0.5f))
            )
          );
      }

      private static LootTable.Builder oreDrops(final Ore ore) {
        final MetalBlock block = GradientBlocks.ORE(ore).get();
        final Item nugget = GradientItems.NUGGET(ore.metal).get();
        final PebbleItem pebble = GradientItems.PEBBLE.get();

        return LootTable.builder()
          .addLootPool(
            withSurvivesExplosion(
              block,
              LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block))
            )
            .acceptCondition(NOT_STONE_HAMMER)
          )
          .addLootPool(
            withSurvivesExplosion(
              block,
              LootPool.builder()
                .rolls(RandomValueRange.of(1, 4))
                .addEntry(ItemLootEntry.builder(pebble))
            )
            .acceptCondition(STONE_HAMMER)
          )
          .addLootPool(
            withSurvivesExplosion(
              block,
              LootPool.builder()
                .rolls(RandomValueRange.of(3, 7))
                .addEntry(ItemLootEntry.builder(nugget))
            )
            .acceptCondition(STONE_HAMMER)
          );
      }

      private static LootTable.Builder hammerDrops(final Block block) {
        final PebbleItem pebble = GradientItems.PEBBLE.get();

        return LootTable.builder()
          .addLootPool(
            withSurvivesExplosion(
              block,
              LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block))
            )
            .acceptCondition(NOT_STONE_HAMMER)
          )
          .addLootPool(
            withSurvivesExplosion(
              block,
              LootPool.builder()
                .rolls(RandomValueRange.of(1, 6))
                .addEntry(ItemLootEntry.builder(pebble))
            )
            .acceptCondition(STONE_HAMMER)
          );
      }

      @Override
      protected void addTables() {
        this.registerLootTable(Blocks.STONE, hammerDrops(Blocks.STONE));
        this.registerLootTable(Blocks.ANDESITE, hammerDrops(Blocks.ANDESITE));
        this.registerLootTable(Blocks.DIORITE, hammerDrops(Blocks.DIORITE));
        this.registerLootTable(Blocks.GRANITE, hammerDrops(Blocks.GRANITE));
        this.registerLootTable(Blocks.COBBLESTONE, hammerDrops(Blocks.COBBLESTONE));

        this.registerLootTable(GradientBlocks.PEBBLE.get(), pebbleDrops());

        for(final Ore ore : Ores.all()) {
          this.registerLootTable(GradientBlocks.PEBBLE(ore).get(), metalPebbleDrops(ore.metal));
          this.registerLootTable(GradientBlocks.ORE(ore).get(), oreDrops(ore));
        }

        for(final Metal metal : Metals.all()) {
          this.registerDropSelfLootTable(GradientBlocks.METAL_BLOCK(metal).get());
        }

        this.registerLootTable(GradientBlocks.SALT_BLOCK.get(), block -> droppingWithSilkTouchOrRandomly(block, GradientItems.SALT.get(), ConstantRange.of(4)));

        this.registerLootTable(GradientBlocks.FIREPIT.get(), droppingRandomly(Items.STICK, RandomValueRange.of(2, 5)));
        this.registerDropSelfLootTable(GradientBlocks.GRINDSTONE.get());

        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_FURNACE.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_OVEN.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_MIXER.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_BUCKET.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_CAST_BLANK.get());

        for(final GradientCasts cast : GradientCasts.values()) {
          this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get());
        }
      }

      private final List<Block> blocks = new ArrayList<>();

      @Override
      protected void registerLootTable(final Block block, final LootTable.Builder table) {
        super.registerLootTable(block, table);
        this.blocks.add(block);
      }

      @Override
      protected Iterable<Block> getKnownBlocks() {
        return Streams.stream(super.getKnownBlocks()).filter(block -> Gradient.MOD_ID.equals(block.getRegistryName().getNamespace()) || this.blocks.contains(block)).collect(Collectors.toCollection(ArrayList::new));
      }
    }
  }
}
