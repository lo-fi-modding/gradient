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
import lofimodding.gradient.blocks.ClayMetalMixerBlock;
import lofimodding.gradient.blocks.DryingRackBlock;
import lofimodding.gradient.blocks.FirepitBlock;
import lofimodding.gradient.blocks.MixingBasinBlock;
import lofimodding.gradient.blocks.OreBlock;
import lofimodding.gradient.fluids.GradientFluid;
import lofimodding.gradient.fluids.GradientFluidStack;
import lofimodding.gradient.fluids.GradientFluids;
import lofimodding.gradient.items.PebbleItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Minerals;
import lofimodding.gradient.science.Ore;
import lofimodding.progression.recipes.StagedRecipeBuilder;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
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
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
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
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.Inverted;
import net.minecraft.world.storage.loot.conditions.MatchTool;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
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
import net.minecraftforge.fluids.FluidStack;
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
      gen.addProvider(new AdvancementsProvider(gen));
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

      for(final Ore ore : Minerals.ores()) {
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

      for(final Ore ore : Minerals.ores()) {
        this.getBuilder(GradientIds.ORE(ore)).parent(this.getExistingFile(this.modLoc("block/ore")));
      }

      this.getBuilder("metal_block")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("particle", this.modLoc("block/metal_block"))
        .texture("all", this.modLoc("block/metal_block"))
        .element().allFaces(this.addTexture("all").andThen((dir, f) -> f.cullface(dir).tintindex(1))).end()
      ;

      for(final Metal metal : Minerals.metals()) {
        this.getBuilder(GradientIds.METAL_BLOCK(metal)).parent(this.getExistingFile(this.modLoc("block/metal_block")));
      }

      this.cubeAll(GradientIds.SALT_BLOCK, this.modLoc("block/salt_block"));
      this.cubeColumn(GradientIds.HARDENED_LOG, this.modLoc("block/hardened_log"), this.modLoc("block/hardened_log_top"));
      this.cubeAll(GradientIds.HARDENED_PLANKS, this.modLoc("block/hardened_planks"));

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

      this.torch(GradientIds.UNLIT_FIBRE_TORCH, this.modLoc("block/unlit_fibre_torch"));
      this.torchWall(GradientIds.UNLIT_FIBRE_WALL_TORCH, this.modLoc("block/unlit_fibre_torch"));
      this.torch(GradientIds.LIT_FIBRE_TORCH, this.modLoc("block/lit_fibre_torch"));
      this.torchWall(GradientIds.LIT_FIBRE_WALL_TORCH, this.modLoc("block/lit_fibre_torch"));
      ModelGenerator.torchStand(this, GradientIds.TORCH_STAND);
      this.torch(GradientIds.UNLIT_TORCH_STAND_TORCH, this.modLoc("block/unlit_fibre_torch"));
      this.torch(GradientIds.LIT_TORCH_STAND_TORCH, this.modLoc("block/lit_fibre_torch"));

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

      this.getBuilder(GradientIds.MIXING_BASIN)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("side", this.mcLoc("block/dark_oak_log"))
        .texture("particle", this.mcLoc("block/dark_oak_log"))

        .element() // bottom
        .from(3.0f, 0.0f, 3.0f)
        .to(13.0f, 1.0f, 13.0f)
        .face(Direction.NORTH).uvs(3.0f, 15.0f, 13.0f, 16.0f).texture("side").end()
        .face(Direction.EAST).uvs(3.0f, 15.0f, 13.0f, 16.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(3.0f, 15.0f, 13.0f, 16.0f).texture("side").end()
        .face(Direction.WEST).uvs(3.0f, 15.0f, 13.0f, 16.0f).texture("side").end()
        .face(Direction.UP).uvs(3.0f, 3.0f, 13.0f, 13.0f).texture("side").end()
        .face(Direction.DOWN).uvs(3.0f, 3.0f, 13.0f, 13.0f).texture("side").end()
        .end()

        .element() // west
        .from(2.0f, 1.0f, 3.0f)
        .to(3.0f, 8.0f, 13.0f)
        .face(Direction.NORTH).uvs(13.0f, 8.0f, 14.0f, 15.0f).texture("side").end()
        .face(Direction.EAST).uvs(3.0f, 7.0f, 13.0f, 14.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(1.0f, 8.0f, 2.0f, 15.0f).texture("side").end()
        .face(Direction.WEST).uvs(3.0f, 8.0f, 13.0f, 15.0f).texture("side").end()
        .face(Direction.UP).uvs(2.0f, 4.0f, 3.0f, 14.0f).texture("side").end()
        .face(Direction.DOWN).uvs(13.0f, 3.0f, 14.0f, 13.0f).texture("side").end()
        .end()

        .element() // south
        .from(3.0f, 1.0f, 13.0f)
        .to(13.0f, 8.0f, 14.0f)
        .face(Direction.NORTH).uvs(3.0f, 7.0f, 13.0f, 14.0f).texture("side").end()
        .face(Direction.EAST).uvs(1.0f, 8.0f, 2.0f, 15.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(3.0f, 8.0f, 13.0f, 15.0f).texture("side").end()
        .face(Direction.WEST).uvs(13.0f, 8.0f, 14.0f, 15.0f).texture("side").end()
        .face(Direction.UP).uvs(2.0f, 4.0f, 3.0f, 14.0f).texture("side").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(13.0f, 3.0f, 14.0f, 13.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .end()

        .element() // east
        .from(13.0f, 1.0f, 3.0f)
        .to(14.0f, 8.0f, 13.0f)
        .face(Direction.NORTH).uvs(1.0f, 8.0f, 2.0f, 15.0f).texture("side").end()
        .face(Direction.EAST).uvs(3.0f, 8.0f, 13.0f, 15.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(13.0f, 8.0f, 14.0f, 15.0f).texture("side").end()
        .face(Direction.WEST).uvs(3.0f, 7.0f, 13.0f, 14.0f).texture("side").end()
        .face(Direction.UP).uvs(2.0f, 4.0f, 3.0f, 14.0f).texture("side").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .face(Direction.DOWN).uvs(13.0f, 3.0f, 14.0f, 13.0f).texture("side").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
        .end()

        .element() // north
        .from(3.0f, 1.0f, 2.0f)
        .to(13.0f, 8.0f, 3.0f)
        .face(Direction.NORTH).uvs(3.0f, 8.0f, 13.0f, 15.0f).texture("side").end()
        .face(Direction.EAST).uvs(13.0f, 8.0f, 14.0f, 15.0f).texture("side").end()
        .face(Direction.SOUTH).uvs(3.0f, 7.0f, 13.0f, 14.0f).texture("side").end()
        .face(Direction.WEST).uvs(1.0f, 8.0f, 2.0f, 15.0f).texture("side").end()
        .face(Direction.UP).uvs(2.0f, 4.0f, 3.0f, 14.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
        .face(Direction.DOWN).uvs(13.0f, 3.0f, 14.0f, 13.0f).texture("side").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
        .end();

      this.getBuilder(GradientIds.MIXING_BASIN + "_rod")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("rod", this.mcLoc("block/dark_oak_log"))

        .element()
        .from(7.5f, 2.0f, 7.5f)
        .to(8.5f, 14.0f, 8.5f)
        .face(Direction.NORTH).uvs(1.0f, 2.0f, 2.0f, 14.0f).texture("rod").end()
        .face(Direction.EAST).uvs(0.0f, 2.0f, 1.0f, 14.0f).texture("rod").end()
        .face(Direction.SOUTH).uvs(3.0f, 2.0f, 4.0f, 14.0f).texture("rod").end()
        .face(Direction.WEST).uvs(2.0f, 2.0f, 3.0f, 14.0f).texture("rod").end()
        .face(Direction.UP).uvs(0.0f, 0.0f, 1.0f, 1.0f).texture("rod").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 1.0f, 1.0f).texture("rod").end()
        .end();

      this.getBuilder(GradientIds.MIXING_BASIN + "_water")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("water", this.mcLoc("block/water_still"))

        .element()
        .from(3.0f, 2.0f, 3.0f)
        .to(13.0f, 7.0f, 13.0f)
        .face(Direction.UP).texture("water").tintindex(0).cullface(Direction.UP)
        .end();

      this.getBuilder(GradientIds.DRYING_RACK)
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("all", this.mcLoc("block/oak_planks"))
        .texture("particle", this.mcLoc("block/oak_planks"))

        .element()
        .from(0.0f, 13.0f, 0.0f)
        .to(16.0f, 16.0f, 2.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 16.0f, 3.0f).texture("all").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 2.0f, 3.0f).texture("all").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 16.0f, 3.0f).texture("all").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 2.0f, 3.0f).texture("all").end()
        .face(Direction.UP).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("all").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("all").end()
        .end();

      this.getBuilder(GradientIds.DRYING_RACK + "_roof")
        .parent(this.getExistingFile(this.mcLoc("block/block")))
        .texture("all", this.mcLoc("block/oak_planks"))
        .texture("particle", this.mcLoc("block/oak_planks"))

        .element()
        .from(0.0f, 13.0f, 7.0f)
        .to(16.0f, 16.0f, 9.0f)
        .face(Direction.NORTH).uvs(0.0f, 0.0f, 16.0f, 3.0f).texture("all").end()
        .face(Direction.EAST).uvs(0.0f, 0.0f, 2.0f, 3.0f).texture("all").end()
        .face(Direction.SOUTH).uvs(0.0f, 0.0f, 16.0f, 3.0f).texture("all").end()
        .face(Direction.WEST).uvs(0.0f, 0.0f, 2.0f, 3.0f).texture("all").end()
        .face(Direction.UP).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("all").end()
        .face(Direction.DOWN).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("all").end()
        .end();

      ModelGenerator.clayFurnace(this, GradientIds.UNHARDENED_CLAY_FURNACE, this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"));
      ModelGenerator.clayCrucible(this, GradientIds.UNHARDENED_CLAY_CRUCIBLE, this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"));
      ModelGenerator.clayOven(this, GradientIds.UNHARDENED_CLAY_OVEN, this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"), this.mcLoc("block/clay"));
      ModelGenerator.clayMixer(this, GradientIds.UNHARDENED_CLAY_METAL_MIXER, this.mcLoc("block/clay"), this.mcLoc("block/clay"));

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
          .texture("cast", this.modLoc("block/unhardened_clay_cast_" + cast.name))

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

      ModelGenerator.clayFurnace(this, GradientIds.CLAY_FURNACE, this.mcLoc("block/terracotta"), this.mcLoc("block/terracotta"), this.mcLoc("block/terracotta"), this.mcLoc("block/terracotta"), this.mcLoc("block/terracotta"));
      ModelGenerator.clayFurnace(this, GradientIds.CLAY_FURNACE + "_used", this.mcLoc("block/terracotta"), this.modLoc("block/clay_furnace_front"), this.modLoc("block/clay_furnace_top"), this.modLoc("block/clay_seared"), this.mcLoc("block/terracotta"));
      ModelGenerator.clayOven(this, GradientIds.CLAY_OVEN, this.modLoc("block/clay_oven_inside"), this.modLoc("block/clay_seared"), this.mcLoc("block/terracotta"), this.modLoc("block/clay_oven_inside"));
      ModelGenerator.clayCrucible(this, GradientIds.CLAY_CRUCIBLE, this.mcLoc("block/terracotta"), this.modLoc("block/clay_seared"), this.modLoc("block/clay_seared_gradient"), this.modLoc("block/clay_seared_gradient_light"), this.mcLoc("block/terracotta"));
      ModelGenerator.clayMixer(this, GradientIds.CLAY_METAL_MIXER, this.mcLoc("block/terracotta"), this.mcLoc("block/terracotta"));
      ModelGenerator.clayMixerPipe(this, GradientIds.CLAY_METAL_MIXER + "_pipe", this.mcLoc("block/terracotta"));

      for(final GradientCasts cast : GradientCasts.values()) {
        this.getBuilder(GradientIds.CLAY_CAST(cast))
          .parent(this.getExistingFile(this.mcLoc("block/block")))
          .texture("particle", this.mcLoc("block/terracotta"))
          .texture("clay", this.mcLoc("block/terracotta"))
          .texture("cast", this.modLoc("block/clay_cast_" + cast.name))

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

      ModelGenerator.woodenAxle(this, GradientIds.WOODEN_AXLE, this.mcLoc("block/oak_planks"), this.mcLoc("block/oak_planks"));
      ModelGenerator.woodenConveyorBelt(this, GradientIds.WOODEN_CONVEYOR_BELT, this.modLoc("block/wooden_conveyor_belt"), this.modLoc("block/wooden_conveyor_belt"), this.mcLoc("block/oak_planks"));
      this.orientable(GradientIds.WOODEN_CONVEYOR_BELT_DRIVER, this.modLoc("block/wooden_conveyor_belt_driver_side"), this.modLoc("block/wooden_conveyor_belt_driver_side"), this.modLoc("block/wooden_gearbox"));
      this.orientable(GradientIds.WOODEN_CRANK, this.modLoc("block/salt_block"), this.modLoc("block/salt_block"), this.modLoc("block/hardened_planks"));
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

      this.getBuilder("crushed")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/crushed/background"))
        .texture("layer1", this.modLoc("item/crushed/diffuse"))
        .texture("layer2", this.modLoc("item/crushed/specular"))
        .texture("layer3", this.modLoc("item/crushed/shadow_1"))
        .texture("layer4", this.modLoc("item/crushed/shadow_2"))
        .texture("layer5", this.modLoc("item/crushed/edge_1"))
        .texture("layer6", this.modLoc("item/crushed/edge_2"))
        .texture("layer7", this.modLoc("item/crushed/edge_3"))
      ;

      this.getBuilder("purified")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/purified/background"))
        .texture("layer1", this.modLoc("item/purified/diffuse"))
        .texture("layer2", this.modLoc("item/purified/specular"))
        .texture("layer3", this.modLoc("item/purified/shadow_1"))
        .texture("layer4", this.modLoc("item/purified/shadow_2"))
        .texture("layer5", this.modLoc("item/purified/edge_1"))
        .texture("layer6", this.modLoc("item/purified/edge_2"))
        .texture("layer7", this.modLoc("item/purified/edge_3"))
      ;

      this.getBuilder("dust")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/dust/background"))
        .texture("layer1", this.modLoc("item/dust/diffuse"))
        .texture("layer2", this.modLoc("item/dust/specular"))
        .texture("layer3", this.modLoc("item/dust/shadow_1"))
        .texture("layer4", this.modLoc("item/dust/shadow_2"))
        .texture("layer5", this.modLoc("item/dust/edge_1"))
        .texture("layer6", this.modLoc("item/dust/edge_2"))
        .texture("layer7", this.modLoc("item/dust/edge_3"))
      ;

      this.getBuilder("ingot")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/ingot/background"))
        .texture("layer1", this.modLoc("item/ingot/diffuse"))
        .texture("layer2", this.modLoc("item/ingot/specular"))
        .texture("layer3", this.modLoc("item/ingot/shadow_1"))
        .texture("layer4", this.modLoc("item/ingot/shadow_2"))
        .texture("layer5", this.modLoc("item/ingot/edge_1"))
        .texture("layer6", this.modLoc("item/ingot/edge_2"))
        .texture("layer7", this.modLoc("item/ingot/edge_3"))
      ;

      this.getBuilder("nugget")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/nugget/background"))
        .texture("layer1", this.modLoc("item/nugget/diffuse"))
        .texture("layer2", this.modLoc("item/nugget/specular"))
        .texture("layer3", this.modLoc("item/nugget/shadow_1"))
        .texture("layer4", this.modLoc("item/nugget/shadow_2"))
        .texture("layer5", this.modLoc("item/nugget/edge_1"))
        .texture("layer6", this.modLoc("item/nugget/edge_2"))
        .texture("layer7", this.modLoc("item/nugget/edge_3"))
      ;

      this.getBuilder("plate")
        .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
        .texture("layer0", this.modLoc("item/plate/background"))
        .texture("layer1", this.modLoc("item/plate/diffuse"))
        .texture("layer2", this.modLoc("item/plate/specular"))
        .texture("layer3", this.modLoc("item/plate/shadow_1"))
        .texture("layer4", this.modLoc("item/plate/shadow_2"))
        .texture("layer5", this.modLoc("item/plate/edge_1"))
        .texture("layer6", this.modLoc("item/plate/edge_2"))
        .texture("layer7", this.modLoc("item/plate/edge_3"))
      ;

      for(final Ore ore : Minerals.ores()) {
        this.getBuilder(GradientIds.ORE(ore)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.ORE(ore))));
        this.getBuilder(GradientIds.CRUSHED(ore)).parent(this.getExistingFile(this.modLoc("item/crushed")));
        this.getBuilder(GradientIds.PURIFIED(ore)).parent(this.getExistingFile(this.modLoc("item/purified")));
      }

      for(final Metal metal : Minerals.metals()) {
        this.getBuilder(GradientIds.DUST(metal)).parent(this.getExistingFile(this.modLoc("item/dust")));
        this.getBuilder(GradientIds.INGOT(metal)).parent(this.getExistingFile(this.modLoc("item/ingot")));
        this.getBuilder(GradientIds.NUGGET(metal)).parent(this.getExistingFile(this.modLoc("item/nugget")));
        this.getBuilder(GradientIds.PLATE(metal)).parent(this.getExistingFile(this.modLoc("item/plate")));

        this.getBuilder(GradientIds.METAL_BLOCK(metal)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.METAL_BLOCK(metal))));
      }

      GradientCasts.stream().filter(GradientCasts::usesDefaultItem).forEach(cast -> {
        this.getBuilder(cast.name)
          .parent(new ModelFile.ExistingModelFile(this.mcLoc("item/generated"), this.existingFileHelper))
          .texture("layer0", this.modLoc("item/" + cast.name + "/background"))
          .texture("layer1", this.modLoc("item/" + cast.name + "/diffuse"))
          .texture("layer2", this.modLoc("item/" + cast.name + "/specular"))
          .texture("layer3", this.modLoc("item/" + cast.name + "/shadow_1"))
          .texture("layer4", this.modLoc("item/" + cast.name + "/shadow_2"))
          .texture("layer5", this.modLoc("item/" + cast.name + "/edge_1"))
          .texture("layer6", this.modLoc("item/" + cast.name + "/edge_2"))
          .texture("layer7", this.modLoc("item/" + cast.name + "/edge_3"))
        ;

        for(final Metal metal : Minerals.metals()) {
          this.getBuilder(GradientIds.CASTED(cast, metal)).parent(this.getExistingFile(this.modLoc("item/" + cast.name)));
        }
      });

      this.getBuilder(GradientIds.SALT_BLOCK).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.SALT_BLOCK)));
      this.singleTexture(GradientIds.SALT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.SALT));
      this.singleTexture(GradientIds.FIBRE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FIBRE));
      this.singleTexture(GradientIds.TWINE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.TWINE));
      this.getBuilder(GradientIds.HARDENED_LOG).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.HARDENED_LOG)));
      this.getBuilder(GradientIds.HARDENED_PLANKS).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.HARDENED_PLANKS)));
      this.singleTexture(GradientIds.HARDENED_STICK, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HARDENED_STICK));
      this.getBuilder(GradientIds.HARDENED_LOG_SLAB).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.HARDENED_LOG_SLAB)));
      this.getBuilder(GradientIds.HARDENED_PLANKS_SLAB).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.HARDENED_PLANKS_SLAB)));
      this.singleTexture(GradientIds.BARK, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.BARK));
      this.singleTexture(GradientIds.MULCH, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.MULCH));
      this.singleTexture(GradientIds.CAT_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.CAT_PELT));
      this.singleTexture(GradientIds.COW_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.COW_PELT));
      this.singleTexture(GradientIds.DONKEY_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.DONKEY_PELT));
      this.singleTexture(GradientIds.FOX_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FOX_PELT));
      this.singleTexture(GradientIds.HORSE_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HORSE_PELT));
      this.singleTexture(GradientIds.LLAMA_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.LLAMA_PELT));
      this.singleTexture(GradientIds.MULE_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.MULE_PELT));
      this.singleTexture(GradientIds.OCELOT_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.OCELOT_PELT));
      this.singleTexture(GradientIds.PANDA_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.PANDA_PELT));
      this.singleTexture(GradientIds.PIG_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.PIG_PELT));
      this.singleTexture(GradientIds.POLAR_BEAR_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.POLAR_BEAR_PELT));
      this.singleTexture(GradientIds.SHEEP_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.SHEEP_PELT));
      this.singleTexture(GradientIds.WOLF_PELT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.WOLF_PELT));

      this.singleTexture(GradientIds.RAW_HIDE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.RAW_HIDE));
      this.singleTexture(GradientIds.SALTED_HIDE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.SALTED_HIDE));
      this.singleTexture(GradientIds.PRESERVED_HIDE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.PRESERVED_HIDE));
      this.singleTexture(GradientIds.TANNED_HIDE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.TANNED_HIDE));

      this.singleTexture(GradientIds.LEATHER_STRIP, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.LEATHER_STRIP));

      this.singleTexture(GradientIds.FIRE_STARTER, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FIRE_STARTER));
      this.singleTexture(GradientIds.STONE_HAMMER, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.STONE_HAMMER));
      this.singleTexture(GradientIds.STONE_HATCHET, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.STONE_HATCHET));
      this.singleTexture(GradientIds.STONE_MATTOCK, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.STONE_MATTOCK));
      this.singleTexture(GradientIds.STONE_PICKAXE, this.mcLoc("item/generated"), "layer0", this.mcLoc("item/stone_pickaxe"));
      this.singleTexture(GradientIds.FLINT_KNIFE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FLINT_KNIFE));
      this.singleTexture(GradientIds.BONE_AWL, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.BONE_AWL));
      this.singleTexture(GradientIds.HIDE_BEDDING, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HIDE_BEDDING));
      this.singleTexture(GradientIds.EMPTY_WATERSKIN, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.EMPTY_WATERSKIN));
      this.singleTexture(GradientIds.FILLED_WATERSKIN, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FILLED_WATERSKIN));

      this.singleTexture(GradientIds.HIDE_HAT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HIDE_HAT));
      this.singleTexture(GradientIds.HIDE_SHIRT, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HIDE_SHIRT));
      this.singleTexture(GradientIds.HIDE_PANTS, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HIDE_PANTS));
      this.singleTexture(GradientIds.HIDE_BOOTS, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.HIDE_BOOTS));

      this.getBuilder(GradientIds.FIREPIT).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)));
      this.singleTexture(GradientIds.UNLIT_FIBRE_TORCH, this.mcLoc("item/generated"), "layer0", this.modLoc("block/" + GradientIds.UNLIT_FIBRE_TORCH));
      this.singleTexture(GradientIds.LIT_FIBRE_TORCH, this.mcLoc("item/generated"), "layer0", this.modLoc("block/" + GradientIds.LIT_FIBRE_TORCH));
      this.getBuilder(GradientIds.TORCH_STAND).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.TORCH_STAND)));

      this.getBuilder(GradientIds.GRINDSTONE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.GRINDSTONE)));
      this.getBuilder(GradientIds.MIXING_BASIN).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.MIXING_BASIN)));
      this.getBuilder(GradientIds.DRYING_RACK).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.DRYING_RACK)));

      this.getBuilder(GradientIds.UNHARDENED_CLAY_FURNACE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_FURNACE)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_CRUCIBLE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CRUCIBLE)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_OVEN).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_OVEN)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_METAL_MIXER).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_METAL_MIXER)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_BUCKET).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_BUCKET)));
      this.getBuilder(GradientIds.UNHARDENED_CLAY_CAST_BLANK).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST_BLANK)));

      for(final GradientCasts cast : GradientCasts.values()) {
        this.getBuilder(GradientIds.UNHARDENED_CLAY_CAST(cast)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST(cast))));
      }

      this.getBuilder(GradientIds.CLAY_FURNACE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_FURNACE)));
      this.getBuilder(GradientIds.CLAY_OVEN).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_OVEN)));
      this.getBuilder(GradientIds.CLAY_CRUCIBLE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_CRUCIBLE)));
      this.getBuilder(GradientIds.CLAY_METAL_MIXER).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_METAL_MIXER)));

      for(final GradientCasts cast : GradientCasts.values()) {
        this.getBuilder(GradientIds.CLAY_CAST(cast)).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_CAST(cast))));
      }

      this.singleTexture(GradientIds.WOODEN_GEAR, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.WOODEN_GEAR));
      this.getBuilder(GradientIds.WOODEN_AXLE).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_AXLE)));
      this.getBuilder(GradientIds.WOODEN_GEARBOX).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_GEARBOX)));
      this.getBuilder(GradientIds.WOODEN_CONVEYOR_BELT).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_CONVEYOR_BELT)));
      this.getBuilder(GradientIds.WOODEN_CONVEYOR_BELT_DRIVER).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_CONVEYOR_BELT_DRIVER)));
      this.getBuilder(GradientIds.WOODEN_CRANK).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_CRANK)));

      this.singleTexture(GradientIds.INFINICOAL, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.INFINICOAL));
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

      for(final Ore ore : Minerals.ores()) {
        this.simpleBlock(GradientBlocks.PEBBLE(ore).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.PEBBLE(ore))));
        this.simpleBlock(GradientBlocks.ORE(ore).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.ORE(ore))));
      }

      for(final Metal metal : Minerals.metals()) {
        this.simpleBlock(GradientBlocks.METAL_BLOCK(metal).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.METAL_BLOCK(metal))));
      }

      this.simpleBlock(GradientBlocks.SALT_BLOCK.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.SALT_BLOCK)));
      this.logBlock(GradientBlocks.HARDENED_LOG.get());
      this.simpleBlock(GradientBlocks.HARDENED_PLANKS.get());
      this.slabBlock(GradientBlocks.HARDENED_LOG_SLAB.get(), this.modLoc("block/" + GradientIds.HARDENED_LOG), this.modLoc("block/" + GradientIds.HARDENED_LOG), this.modLoc("block/" + GradientIds.HARDENED_LOG + "_top"), this.modLoc("block/" + GradientIds.HARDENED_LOG + "_top"));
      this.slabBlock(GradientBlocks.HARDENED_PLANKS_SLAB.get(), this.modLoc("block/" + GradientIds.HARDENED_PLANKS), this.modLoc("block/" + GradientIds.HARDENED_PLANKS));

      this.getMultipartBuilder(GradientBlocks.FIREPIT.get())
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)))
        .addModel()
        .condition(FirepitBlock.FACING, Direction.NORTH)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)))
        .rotationY(90)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.EAST)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)))
        .rotationY(180)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.SOUTH)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.FIREPIT)))
        .rotationY(270)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.WEST)
        .end()

        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_FURNACE + "_used")))
        .addModel()
        .condition(FirepitBlock.FACING, Direction.NORTH)
        .condition(FirepitBlock.HAS_FURNACE, true)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_FURNACE + "_used")))
        .rotationY(90)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.EAST)
        .condition(FirepitBlock.HAS_FURNACE, true)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_FURNACE + "_used")))
        .rotationY(180)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.SOUTH)
        .condition(FirepitBlock.HAS_FURNACE, true)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_FURNACE + "_used")))
        .rotationY(270)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.WEST)
        .condition(FirepitBlock.HAS_FURNACE, true)
        .end();
      this.simpleBlock(GradientBlocks.UNLIT_FIBRE_TORCH.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNLIT_FIBRE_TORCH)));
      this.horizontalBlock(GradientBlocks.UNLIT_FIBRE_WALL_TORCH.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNLIT_FIBRE_WALL_TORCH)), 90);
      this.simpleBlock(GradientBlocks.LIT_FIBRE_TORCH.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.LIT_FIBRE_TORCH)));
      this.horizontalBlock(GradientBlocks.LIT_FIBRE_WALL_TORCH.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.LIT_FIBRE_WALL_TORCH)), 90);
      this.simpleBlock(GradientBlocks.TORCH_STAND.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.TORCH_STAND)));
      this.simpleBlock(GradientBlocks.UNLIT_TORCH_STAND_TORCH.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNLIT_TORCH_STAND_TORCH)));
      this.simpleBlock(GradientBlocks.LIT_TORCH_STAND_TORCH.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.LIT_TORCH_STAND_TORCH)));

      this.horizontalBlock(GradientBlocks.GRINDSTONE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.GRINDSTONE)));
      this.getMultipartBuilder(GradientBlocks.MIXING_BASIN.get())
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.MIXING_BASIN)))
        .addModel()
        .condition(FirepitBlock.FACING, Direction.NORTH)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.MIXING_BASIN)))
        .rotationY(90)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.EAST)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.MIXING_BASIN)))
        .rotationY(180)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.SOUTH)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.MIXING_BASIN)))
        .rotationY(270)
        .addModel()
        .condition(FirepitBlock.FACING, Direction.WEST)
        .end()

        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.MIXING_BASIN + "_water")))
        .addModel()
        .condition(MixingBasinBlock.HAS_WATER, true)
        .end();

      this.horizontalBlock(GradientBlocks.DRYING_RACK.get(), state -> new ModelFile.UncheckedModelFile(this.modLoc(state.get(DryingRackBlock.ROOF) ? "block/" + GradientIds.DRYING_RACK + "_roof" : "block/" + GradientIds.DRYING_RACK)));

      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_FURNACE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_FURNACE)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CRUCIBLE)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_OVEN.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_OVEN)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_METAL_MIXER.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_METAL_MIXER)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_BUCKET.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_BUCKET)));
      this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_CAST_BLANK.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST_BLANK)));

      for(final GradientCasts cast : GradientCasts.values()) {
        this.horizontalBlock(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.UNHARDENED_CLAY_CAST(cast))));
      }

      this.horizontalBlock(GradientBlocks.CLAY_FURNACE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_FURNACE)));
      this.horizontalBlock(GradientBlocks.CLAY_OVEN.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_OVEN)));
      this.simpleBlock(GradientBlocks.CLAY_CRUCIBLE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_CRUCIBLE)));
      this.getMultipartBuilder(GradientBlocks.CLAY_METAL_MIXER.get())
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_METAL_MIXER)))
        .addModel()
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_METAL_MIXER + "_pipe")))
        .addModel()
        .condition(ClayMetalMixerBlock.CONNECTED.get(Direction.NORTH), Boolean.TRUE)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_METAL_MIXER + "_pipe")))
        .rotationY(180)
        .addModel()
        .condition(ClayMetalMixerBlock.CONNECTED.get(Direction.SOUTH), Boolean.TRUE)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_METAL_MIXER + "_pipe")))
        .rotationY(270)
        .addModel()
        .condition(ClayMetalMixerBlock.CONNECTED.get(Direction.WEST), Boolean.TRUE)
        .end()
        .part()
        .modelFile(new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_METAL_MIXER + "_pipe")))
        .rotationY(90)
        .addModel()
        .condition(ClayMetalMixerBlock.CONNECTED.get(Direction.EAST), Boolean.TRUE)
        .end();

      for(final GradientCasts cast : GradientCasts.values()) {
        this.horizontalBlock(GradientBlocks.CLAY_CAST(cast).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.CLAY_CAST(cast))));
      }

      this.axisBlock(GradientBlocks.WOODEN_AXLE.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_AXLE)));
      this.simpleBlock(GradientBlocks.WOODEN_GEARBOX.get());

      this.horizontalBlock(GradientBlocks.WOODEN_CONVEYOR_BELT.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_CONVEYOR_BELT)));
      this.directionalBlock(GradientBlocks.WOODEN_CONVEYOR_BELT_DRIVER.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_CONVEYOR_BELT_DRIVER)));
      this.directionalBlock(GradientBlocks.WOODEN_CRANK.get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.WOODEN_CRANK)));
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

      this.add("screens.gradient.unhardened_clay_cast", "Cast Selection");

      this.add("fluids.gradient.air", "Air");

      for(final Metal metal : Minerals.metals()) {
        final String metalName = StringUtils.capitalize(metal.name);
        this.add(GradientFluids.METAL(metal).get(), "Molten " + metalName);
      }

      this.add("meltable.melt_temp", "Melting Point: %f °C");
      this.add("meltable.melt_time", "Melting Time: %d ticks");
      this.add("meltable.amount", "Amount: %f B");
      this.add("meltable.fluid", "Fluid: %s");
      this.add("meltable.display", "%f/%f B (%f °C)");

      this.add(GradientBlocks.PEBBLE.get(), "Pebble");
      this.add(GradientItems.PEBBLE.get(), "Pebble");

      for(final Ore ore : Minerals.ores()) {
        final String oreName = StringUtils.capitalize(ore.name);

        this.add(GradientBlocks.PEBBLE(ore).get(), oreName + " Pebble");
        this.add(GradientBlocks.ORE(ore).get(), oreName + " Ore");
        this.add(GradientItems.CRUSHED(ore).get(), "Crushed " + oreName + " Ore");
        this.add(GradientItems.PURIFIED(ore).get(), "Purified " + oreName + " Ore");
      }

      for(final Metal metal : Minerals.metals()) {
        final String metalName = StringUtils.capitalize(metal.name);

        this.add(GradientItems.DUST(metal).get(), metalName + " Dust");
        this.add(GradientItems.PLATE(metal).get(), metalName + " Plate");
        this.add(GradientItems.METAL_BLOCK(metal).get(), "Block of " + metalName);
      }

      this.add(GradientItems.SALT_BLOCK.get(), "Block of Salt");
      this.add(GradientItems.SALT.get(), "Salt");
      this.add(GradientItems.HARDENED_LOG.get(), "Hardened Log");
      this.add(GradientItems.HARDENED_PLANKS.get(), "Hardened Planks");
      this.add(GradientItems.HARDENED_LOG_SLAB.get(), "Hardened Log Slab");
      this.add(GradientItems.HARDENED_PLANKS_SLAB.get(), "Hardened Planks Slab");
      this.add(GradientItems.HARDENED_STICK.get(), "Hardened Stick");
      this.add(GradientItems.FIBRE.get(), "Fibre");
      this.add(GradientItems.TWINE.get(), "Twine");
      this.add(GradientItems.BARK.get(), "Bark");
      this.add(GradientItems.MULCH.get(), "Mulch");
      this.add(GradientItems.CAT_PELT.get(), "Cat Pelt");
      this.add(GradientItems.COW_PELT.get(), "Cow Pelt");
      this.add(GradientItems.DONKEY_PELT.get(), "Donkey Pelt");
      this.add(GradientItems.FOX_PELT.get(), "Fox Pelt");
      this.add(GradientItems.HORSE_PELT.get(), "Horse Pelt");
      this.add(GradientItems.LLAMA_PELT.get(), "Llama Pelt");
      this.add(GradientItems.MULE_PELT.get(), "Mule Pelt");
      this.add(GradientItems.OCELOT_PELT.get(), "Ocelot Pelt");
      this.add(GradientItems.PANDA_PELT.get(), "Panda Pelt");
      this.add(GradientItems.PIG_PELT.get(), "Pig Pelt");
      this.add(GradientItems.POLAR_BEAR_PELT.get(), "Polar Bear Pelt");
      this.add(GradientItems.SHEEP_PELT.get(), "Sheep Pelt");
      this.add(GradientItems.WOLF_PELT.get(), "Wolf Pelt");

      this.add(GradientItems.RAW_HIDE.get(), "Rawhide");
      this.add(GradientItems.SALTED_HIDE.get(), "Salted Hide");
      this.add(GradientItems.PRESERVED_HIDE.get(), "Preserved Hide");
      this.add(GradientItems.TANNED_HIDE.get(), "Tanned Hide");
      this.add(GradientItems.LEATHER_STRIP.get(), "Leather Strip");

      this.add(GradientItems.FIRE_STARTER.get(), "Fire Starter");
      this.add(GradientItems.STONE_HAMMER.get(), "Stone Hammer");
      this.add(GradientItems.STONE_HAMMER.get().getTranslationKey() + ".tooltip", "Use on ores to get metal nuggets");
      this.add(GradientItems.STONE_HATCHET.get(), "Stone Hatchet");
      this.add(GradientItems.STONE_HATCHET.get().getTranslationKey() + ".tooltip", "Right click to strip logs");
      this.add(GradientItems.STONE_MATTOCK.get(), "Stone Mattock");
      this.add(GradientItems.STONE_MATTOCK.get().getTranslationKey() + ".tooltip", "Right click to till soil, strip logs, or shear animals");
      this.add(GradientItems.STONE_PICKAXE.get(), "Stone Pickaxe");
      this.add(GradientItems.FLINT_KNIFE.get(), "Flint Knife");
      this.add(GradientItems.BONE_AWL.get(), "Bone Awl");
      this.add(GradientItems.HIDE_BEDDING.get(), "Hide Bedding");
      this.add(GradientItems.EMPTY_WATERSKIN.get(), "Empty Waterskin");
      this.add(GradientItems.FILLED_WATERSKIN.get(), "Filled Waterskin");

      this.add(GradientItems.HIDE_HAT.get(), "Hide Hat");
      this.add(GradientItems.HIDE_SHIRT.get(), "Hide Shirt");
      this.add(GradientItems.HIDE_PANTS.get(), "Hide Pants");
      this.add(GradientItems.HIDE_BOOTS.get(), "Hide Boots");

      this.add(GradientItems.FIREPIT.get(), "Firepit");
      this.add(GradientItems.FIREPIT.get().getTranslationKey() + ".heat", "%d °C");
      this.add(GradientBlocks.UNLIT_FIBRE_TORCH.get(), "Unlit Fibre Torch");
      this.add(GradientBlocks.UNLIT_FIBRE_TORCH.get().getTranslationKey() + ".tooltip", "Light on a fire pit or with another torch");
      this.add(GradientBlocks.LIT_FIBRE_TORCH.get(), "Lit Fibre Torch");
      this.add(GradientBlocks.TORCH_STAND.get(), "Torch Stand");
      this.add(GradientBlocks.LIT_TORCH_STAND_TORCH.get(), "Lit Fibre Torch");
      this.add(GradientBlocks.UNLIT_TORCH_STAND_TORCH.get(), "Unlit Fibre Torch");

      this.add(GradientItems.GRINDSTONE.get(), "Grindstone");
      this.add(GradientItems.MIXING_BASIN.get(), "Mixing Basin");
      this.add(GradientItems.DRYING_RACK.get(), "Drying Rack");

      this.add(GradientItems.UNHARDENED_CLAY_FURNACE.get(), "Unhardened Clay Furnace");
      this.add(GradientItems.UNHARDENED_CLAY_FURNACE.get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden");
      this.add(GradientItems.UNHARDENED_CLAY_CRUCIBLE.get(), "Unhardened Clay Crucible");
      this.add(GradientItems.UNHARDENED_CLAY_CRUCIBLE.get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden");
      this.add(GradientItems.UNHARDENED_CLAY_OVEN.get(), "Unhardened Clay Oven");
      this.add(GradientItems.UNHARDENED_CLAY_OVEN.get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden");
      this.add(GradientItems.UNHARDENED_CLAY_METAL_MIXER.get(), "Unhardened Clay Metal Mixer");
      this.add(GradientItems.UNHARDENED_CLAY_METAL_MIXER.get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden");
      this.add(GradientItems.UNHARDENED_CLAY_BUCKET.get(), "Unhardened Clay Bucket");
      this.add(GradientItems.UNHARDENED_CLAY_BUCKET.get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden");
      this.add(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get(), "Unhardened Clay Cast (Blank)");
      this.add(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden, right click to switch cast");

      for(final GradientCasts cast : GradientCasts.values()) {
        final String[] words = StringUtils.split(cast.name, '_');

        for(int i = 0; i < words.length; i++) {
          words[i] = StringUtils.capitalize(words[i]);
        }

        final String text = StringUtils.join(words, ' ');

        this.add(cast.getTranslationKey(), text);
        this.add(GradientItems.UNHARDENED_CLAY_CAST(cast).get(), "Unhardened Clay Cast (" + text + ')');
        this.add(GradientItems.UNHARDENED_CLAY_CAST(cast).get().getTranslationKey() + ".tooltip", "Place next to a fire pit to harden, right click to switch cast");
        this.add(GradientItems.CLAY_CAST(cast).get(), "Clay Cast (" + text + ')');
        this.add(GradientItems.CLAY_CAST(cast).get().getTranslationKey() + ".tooltip", "Right click on a crucible to cast");

        for(final Metal metal : Minerals.metals()) {
          final String metalName = StringUtils.capitalize(metal.name);
          this.add(cast.getItem(metal), metalName + ' ' + text);
        }
      }

      this.add(GradientItems.CLAY_FURNACE.get(), "Clay Oven");
      this.add(GradientItems.CLAY_FURNACE.get().getTranslationKey() + ".tooltip", "Right click on a firepit to attach");
      this.add(GradientItems.CLAY_OVEN.get(), "Clay Oven");
      this.add(GradientItems.CLAY_OVEN.get().getTranslationKey() + ".tooltip", "Place on top of a firepit/furnace to use its heat");
      this.add(GradientItems.CLAY_CRUCIBLE.get(), "Clay Crucible");
      this.add(GradientItems.CLAY_CRUCIBLE.get().getTranslationKey() + ".tooltip", "Place on top of a firepit/furnace to use its heat");
      this.add(GradientItems.CLAY_CRUCIBLE.get().getTranslationKey() + ".not_enough_metal", "This cast requires %d B");
      this.add(GradientItems.CLAY_METAL_MIXER.get(), "Clay Metal Mixer");
      this.add(GradientItems.CLAY_METAL_MIXER.get().getTranslationKey() + ".tooltip", "Place between two crucibles, with another crucible underneath");

      this.add(GradientItems.WOODEN_GEAR.get(), "Wooden Gear");
      this.add(GradientItems.WOODEN_AXLE.get(), "Wooden Axle");
      this.add(GradientItems.WOODEN_GEARBOX.get(), "Wooden Gearbox");
      this.add(GradientItems.WOODEN_CONVEYOR_BELT.get(), "Wooden Conveyor Belt");
      this.add(GradientItems.WOODEN_CONVEYOR_BELT_DRIVER.get(), "Wooden Conveyor Belt Driver");
      this.add(GradientItems.WOODEN_CRANK.get(), "Wooden Crank");

      this.add(GradientItems.INFINICOAL.get(), "Infinicoal (Debug Item)");

      this.add("jei.cooking.name", "Cooking");
      this.add("jei.cooking.temperature", "%f °C");
      this.add("jei.cooking.ticks", "%d ticks");

      this.add("jei.drying.name", "Drying");
      this.add("jei.drying.ticks", "%d ticks");

      this.add("jei.fuel.name", "Fuel");
      this.add("jei.fuel.ticks", "Burn time: %d ticks");
      this.add("jei.fuel.ignition_temp", "Ignition temperature: %d °C");
      this.add("jei.fuel.burn_temp", "Max temperature: %d °C");
      this.add("jei.fuel.heat_per_sec", "Heat: %d °C/s");

      this.add("jei.grinding.name", "Grinding");
      this.add("jei.grinding.ticks", "%d ticks");

      this.add("jei.hardening.name", "Hardening");
      this.add("jei.hardening.ticks", "%d ticks");
      this.add("jei.hardening.instructions", "Place next to a lit firepit or furnace");

      this.add("jei.melting.name", "Melting");
      this.add("jei.melting.temperature", "%f °C");
      this.add("jei.melting.ticks", "%d ticks");

      this.add("jei.mixing.name", "Mixing");
      this.add("jei.mixing.ticks", "%d ticks");

      this.add("jei.alloy.name", "Alloying");

      this.add("jei.casting.name", "Casting");

      this.add("jei.gradient_fluid.amount", "Amount: %d B");
      this.add("jei.gradient_fluid.amount_with_capacity", "Amount: %d/%d B");

      this.add("jei.information." + GradientIds.FIREPIT + ".1", "The firepit is one of the most important fixtures of an early civilisation. Provided with fuel to burn, it can both cook food and produce heat.");
      this.add("jei.information." + GradientIds.FIREPIT + ".2", "To light a firepit, fill it with fuel and right-click on it with a fire starter. Each fuel has a minimum temperature required to ignite. The easiest way to get it up to heat is with a cycle of fibre, then a cycle of sticks, then a few cycles of planks until it's hot enough to ignite coal. Experimentation is encouraged.");
      this.add("jei.information." + GradientIds.FIREPIT + ".3", "Placing a clay furnace on a firepit will insulate it, helping it maintain its heat and achieve higher temperatures. Multiple firepits/furnaces adjacent to each other will share heat.");

      this.age1("root", "Age 1: Stone Age", "Humankind's first steps");
      this.age1("basic_materials", "Basic Materials", "Gather sticks from leaves, fibre from grass, and pebbles from the ground");
      this.age1("stone_hammer", "Hammer Time!", "Craft a stone hammer");
      this.age1("stone_hatchet", "Just Jackin' It", "Craft a stone hatchet");
      this.age1("wood", "Getting Wood", "Use your new hatchet to gather wood");
      this.age1("planks", "Chop Chop", "Chop some wood into planks");
      this.age1("grindstone", "Dust to Dust", "Craft a grindstone");
      this.age1("mixing_basin", "Mix It Up", "Craft a mixing basin");
      this.age1("firepit", "Open This Pit Up", "Craft a fire pit");
      this.age1("fire_starter", "Fire", "Craft a fire starter and use it to light your fire pit");
      this.age1("fibre_torch", "Let There Be a Little Light", "Craft a fibre torch and light it");
      this.age1("pelt", "Animal Pelt", "Kill an animal and collect its pelt");
      this.age1("bone_awl", "Awl Be Back", "Craft an awl for working with animal pelts");
      this.age1("waterskin", "It's Made of What Now?", "Craft a waterskin to transport water");
      this.age1("hide_bedding", "Dirt Nap", "Craft some bedding for sleeping on the go");
      this.age1("hide_armour", "The Buttflappening", "Craft a full set of hide armour. You'll be rewarded with a handful of torches that don't go out!");
      this.age1("goal", "Settle Down", "Age 1 complete!");

      this.age2("root", "Age 2: Agriculture", "Settling down");
      this.age2("clay", "Pottery and Other More Useful Things", "Find some clay");
      this.age2("drying_rack", "Hang It Up", "Craft a drying rack");
      this.age2("flint_knife", "Work It", "Craft a flint knife");
      this.age2("hardened_log", "Hard Wood", "Harden a log by placing it next to a firepit");
      this.age2("standing_torch", "Let There Be Slightly More Light", "Make a torch stand to improve your fibre torches");
    }

    private void age1(final String key, final String title, final String description) {
      this.add("advancements.gradient.age1." + key + ".title", title);
      this.add("advancements.gradient.age1." + key + ".description", description);
    }

    private void age2(final String key, final String title, final String description) {
      this.add("advancements.gradient.age2." + key + ".title", title);
      this.add("advancements.gradient.age2." + key + ".description", description);
    }

    private void add(final GradientFluid fluid, final String translation) {
      this.add(fluid.getTranslationKey(), translation);
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

      for(final Ore ore : Minerals.ores()) {
        this.getBuilder(GradientTags.Blocks.ORE.get(ore)).add(GradientBlocks.ORE(ore).get());
        this.getBuilder(Tags.Blocks.ORES).add(GradientTags.Blocks.ORE.get(ore));
      }

      for(final Metal metal : Minerals.metals()) {
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
      this.getBuilder(GradientTags.Items.AXES).add(GradientItems.STONE_HATCHET.get(), GradientItems.STONE_MATTOCK.get());
      this.getBuilder(GradientTags.Items.HAMMERS).add(GradientItems.STONE_HAMMER.get());

      this.getBuilder(GradientTags.Items.FIBRE_TORCH_LIGHTERS).add(GradientItems.LIT_FIBRE_TORCH.get(), Items.TORCH, Items.FLINT_AND_STEEL);

      for(final Ore ore : Minerals.ores()) {
        this.getBuilder(GradientTags.Items.ORE.get(ore)).add(GradientItems.ORE(ore).get());
        this.getBuilder(Tags.Items.ORES).add(GradientTags.Items.ORE.get(ore));

        this.getBuilder(GradientTags.Items.CRUSHED_ORE.get(ore)).add(GradientItems.CRUSHED(ore).get());
        this.getBuilder(GradientTags.Items.CRUSHED_ORES).add(GradientTags.Items.CRUSHED_ORE.get(ore));

        this.getBuilder(GradientTags.Items.PURIFIED_ORE.get(ore)).add(GradientItems.PURIFIED(ore).get());
        this.getBuilder(GradientTags.Items.PURIFIED_ORES).add(GradientTags.Items.PURIFIED_ORE.get(ore));
      }

      for(final Metal metal : Minerals.metals()) {
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

      this.getBuilder(GradientTags.Items.PELTS).add(GradientTags.Items.PELTS_COW, GradientTags.Items.PELTS_CAT, GradientTags.Items.PELTS_DONKEY, GradientTags.Items.PELTS_FOX, GradientTags.Items.PELTS_HORSE, GradientTags.Items.PELTS_LLAMA, GradientTags.Items.PELTS_MULE, GradientTags.Items.PELTS_OCELOT, GradientTags.Items.PELTS_PANDA, GradientTags.Items.PELTS_PIG, GradientTags.Items.PELTS_POLAR_BEAR, GradientTags.Items.PELTS_RABBIT, GradientTags.Items.PELTS_SHEEP, GradientTags.Items.PELTS_WOLF);
      this.getBuilder(GradientTags.Items.PELTS_CAT).add(GradientItems.CAT_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_COW).add(GradientItems.COW_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_DONKEY).add(GradientItems.DONKEY_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_FOX).add(GradientItems.FOX_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_HORSE).add(GradientItems.HORSE_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_LLAMA).add(GradientItems.LLAMA_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_MULE).add(GradientItems.MULE_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_OCELOT).add(GradientItems.OCELOT_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_PANDA).add(GradientItems.PANDA_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_PIG).add(GradientItems.PIG_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_POLAR_BEAR).add(GradientItems.POLAR_BEAR_PELT.get());
      this.getBuilder(GradientTags.Items.PELTS_RABBIT).add(Items.RABBIT_HIDE);
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
      this.registerHardeningRecipes(finished);
      this.registerDryingRecipes(finished);
      this.registerMeltingRecipes(finished);
      this.registerAlloyRecipes(finished);

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

      StagedRecipeBuilder
        .shaped(Blocks.COBBLESTONE)
        .stage(GradientStages.AGE_1.get())
        .patternLine("PPP")
        .patternLine("PCP")
        .patternLine("PPP")
        .key('P', GradientItems.PEBBLE.get())
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_pebble", this.hasItem(GradientItems.PEBBLE.get()))
        .build(finished, Gradient.loc("age1/cobblestone_from_pebbles"));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.HARDENED_PLANKS.get(), 2)
        .stage(GradientStages.AGE_2.get())
        .addIngredient(GradientItems.HARDENED_LOG.get())
        .addIngredient(GradientTags.Items.AXES)
        .addCriterion("has_hardened_log", this.hasItem(GradientItems.HARDENED_LOG.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.HARDENED_PLANKS));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.HARDENED_STICK.get(), 2)
        .stage(GradientStages.AGE_2.get())
        .addIngredient(GradientItems.HARDENED_PLANKS.get())
        .addIngredient(GradientTags.Items.AXES)
        .addCriterion("has_hardened_planks", this.hasItem(GradientItems.HARDENED_PLANKS.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.HARDENED_STICK));

      StagedRecipeBuilder
        .shaped(GradientItems.HARDENED_LOG_SLAB.get(), 6)
        .stage(GradientStages.AGE_2.get())
        .patternLine("SSS")
        .key('S', GradientItems.HARDENED_LOG.get())
        .addCriterion("has_hardened_log", this.hasItem(GradientItems.HARDENED_LOG.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.HARDENED_LOG_SLAB));

      StagedRecipeBuilder
        .shaped(GradientItems.HARDENED_PLANKS_SLAB.get(), 6)
        .stage(GradientStages.AGE_2.get())
        .patternLine("SSS")
        .key('S', GradientItems.HARDENED_PLANKS.get())
        .addCriterion("has_hardened_planks", this.hasItem(GradientItems.HARDENED_PLANKS.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.HARDENED_PLANKS_SLAB));

      GradientRecipeBuilder
        .grinding(GradientItems.MULCH.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(40)
        .addIngredient(GradientItems.BARK.get())
        .addCriterion("has_bark", this.hasItem(GradientItems.BARK.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.MULCH));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.RAW_HIDE.get())
        .stage(GradientStages.AGE_2.get())
        .addIngredient(GradientTags.Items.PELTS)
        .addIngredient(GradientItems.FLINT_KNIFE.get())
        .addCriterion("has_hide", this.hasItem(GradientTags.Items.PELTS))
        .build(finished, Gradient.loc("age2/" + GradientIds.RAW_HIDE));

      GradientRecipeBuilder
        .mixing(GradientItems.SALTED_HIDE.get(), 3)
        .stage(GradientStages.AGE_2.get())
        .ticks(60)
        .addIngredient(GradientItems.RAW_HIDE.get(), 3)
        .addIngredient(GradientItems.SALT.get())
        .fluid(new FluidStack(Fluids.WATER, 1000))
        .addCriterion("has_rawhide", this.hasItem(GradientItems.RAW_HIDE.get()))
        .build(finished, Gradient.loc("mixing/" + GradientIds.SALTED_HIDE));

      GradientRecipeBuilder
        .mixing(GradientItems.TANNED_HIDE.get(), 3)
        .stage(GradientStages.AGE_2.get())
        .ticks(60)
        .addIngredient(GradientItems.PRESERVED_HIDE.get(), 3)
        .addIngredient(GradientItems.MULCH.get())
        .fluid(new FluidStack(Fluids.WATER, 1000))
        .addCriterion("has_preserved_hide", this.hasItem(GradientItems.PRESERVED_HIDE.get()))
        .build(finished, Gradient.loc("mixing/" + GradientIds.TANNED_HIDE));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.LEATHER_STRIP.get(), 3)
        .stage(GradientStages.AGE_2.get())
        .addIngredient(Tags.Items.LEATHER)
        .addIngredient(GradientItems.FLINT_KNIFE.get())
        .addCriterion("has_leather", this.hasItem(Tags.Items.LEATHER))
        .build(finished, Gradient.loc("age2/" + GradientIds.LEATHER_STRIP));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.FIRE_STARTER.get())
        .stage(GradientStages.AGE_1.get())
        .addIngredient(Tags.Items.RODS_WOODEN)
        .addIngredient(Tags.Items.STRING)
        .addIngredient(Tags.Items.RODS_WOODEN)
        .addCriterion("has_sticks", this.hasItem(Tags.Items.RODS_WOODEN))
        .build(finished, Gradient.loc("age1/" + GradientIds.FIRE_STARTER));

      StagedRecipeBuilder
        .shaped(GradientItems.STONE_HAMMER.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("P")
        .patternLine("F")
        .patternLine("S")
        .key('P', GradientItems.PEBBLE.get())
        .key('F', Tags.Items.STRING)
        .key('S', Tags.Items.RODS_WOODEN)
        .addCriterion("has_pebble", this.hasItem(GradientItems.PEBBLE.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.STONE_HAMMER));

      StagedRecipeBuilder
        .shaped(GradientItems.STONE_HATCHET.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("PF")
        .patternLine(" S")
        .key('P', GradientItems.PEBBLE.get())
        .key('F', Tags.Items.STRING)
        .key('S', Tags.Items.RODS_WOODEN)
        .addCriterion("has_pebble", this.hasItem(GradientItems.PEBBLE.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.STONE_HATCHET));

      StagedRecipeBuilder
        .shaped(GradientItems.STONE_MATTOCK.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("P  ")
        .patternLine("PLP")
        .patternLine(" S ")
        .key('P', GradientItems.PEBBLE.get())
        .key('L', GradientItems.LEATHER_STRIP.get())
        .key('S', GradientItems.HARDENED_STICK.get())
        .addCriterion("has_hardened_stick", this.hasItem(GradientItems.HARDENED_STICK.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.STONE_MATTOCK));

      StagedRecipeBuilder
        .shaped(GradientItems.STONE_PICKAXE.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("PPP")
        .patternLine(" L ")
        .patternLine(" S ")
        .key('P', GradientItems.PEBBLE.get())
        .key('L', GradientItems.LEATHER_STRIP.get())
        .key('S', GradientItems.HARDENED_STICK.get())
        .addCriterion("has_hardened_stick", this.hasItem(GradientItems.HARDENED_STICK.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.STONE_PICKAXE));

      StagedRecipeBuilder
        .shaped(GradientItems.FLINT_KNIFE.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("F")
        .patternLine("S")
        .patternLine("W")
        .key('F', Items.FLINT)
        .key('S', Tags.Items.STRING)
        .key('W', Tags.Items.RODS_WOODEN)
        .addCriterion("has_flint", this.hasItem(Items.FLINT))
        .build(finished, Gradient.loc("age1/" + GradientIds.FLINT_KNIFE));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.BONE_AWL.get())
        .stage(GradientStages.AGE_1.get())
        .addIngredient(Tags.Items.BONES)
        .addIngredient(GradientTags.Items.HAMMERS)
        .addCriterion("has_bone", this.hasItem(Tags.Items.BONES))
        .build(finished, Gradient.loc("age1/" + GradientIds.BONE_AWL));

      StagedRecipeBuilder
        .shaped(GradientItems.HIDE_BEDDING.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("PPP")
        .patternLine("FFF")
        .key('P', GradientTags.Items.PELTS)
        .key('F', GradientItems.FIBRE.get())
        .addCriterion("has_pelt", this.hasItem(GradientTags.Items.PELTS))
        .build(finished, Gradient.loc("age1/" + GradientIds.HIDE_BEDDING));

      StagedRecipeBuilder
        .shaped(GradientItems.EMPTY_WATERSKIN.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("SHS")
        .patternLine("HAH")
        .patternLine("SHS")
        .key('S', Tags.Items.STRING)
        .key('H', GradientTags.Items.PELTS)
        .key('A', GradientItems.BONE_AWL.get())
        .addCriterion("has_awl", this.hasItem(GradientItems.BONE_AWL.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.EMPTY_WATERSKIN));

      StagedRecipeBuilder
        .shaped(GradientItems.HIDE_HAT.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("SHS")
        .patternLine("HAH")
        .key('S', Tags.Items.STRING)
        .key('H', GradientTags.Items.PELTS)
        .key('A', GradientItems.BONE_AWL.get())
        .addCriterion("has_awl", this.hasItem(GradientItems.BONE_AWL.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.HIDE_HAT));

      StagedRecipeBuilder
        .shaped(GradientItems.HIDE_SHIRT.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("HAH")
        .patternLine("SHS")
        .patternLine("HHH")
        .key('S', Tags.Items.STRING)
        .key('H', GradientTags.Items.PELTS)
        .key('A', GradientItems.BONE_AWL.get())
        .addCriterion("has_awl", this.hasItem(GradientItems.BONE_AWL.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.HIDE_SHIRT));

      StagedRecipeBuilder
        .shaped(GradientItems.HIDE_PANTS.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("SHS")
        .patternLine("HAH")
        .patternLine("H H")
        .key('S', Tags.Items.STRING)
        .key('H', GradientTags.Items.PELTS)
        .key('A', GradientItems.BONE_AWL.get())
        .addCriterion("has_awl", this.hasItem(GradientItems.BONE_AWL.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.HIDE_PANTS));

      StagedRecipeBuilder
        .shaped(GradientItems.HIDE_BOOTS.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine("S S")
        .patternLine("HAH")
        .key('S', Tags.Items.STRING)
        .key('H', GradientTags.Items.PELTS)
        .key('A', GradientItems.BONE_AWL.get())
        .addCriterion("has_awl", this.hasItem(GradientItems.BONE_AWL.get()))
        .build(finished, Gradient.loc("age1/" + GradientIds.HIDE_BOOTS));

      StagedRecipeBuilder
        .shaped(GradientItems.FIREPIT.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine(" S ")
        .patternLine("SSS")
        .patternLine(" S ")
        .key('S', Tags.Items.RODS_WOODEN)
        .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
        .build(finished, Gradient.loc("age1/" + GradientIds.FIREPIT));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.UNLIT_FIBRE_TORCH.get())
        .stage(GradientStages.AGE_1.get())
        .addIngredient(Tags.Items.STRING)
        .addIngredient(Tags.Items.STRING)
        .addIngredient(Tags.Items.STRING)
        .addIngredient(Tags.Items.RODS_WOODEN)
        .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
        .build(finished, Gradient.loc("age1/" + GradientIds.UNLIT_FIBRE_TORCH));

      StagedRecipeBuilder
        .shaped(GradientItems.TORCH_STAND.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine(" T ")
        .patternLine("FSF")
        .patternLine(" P ")
        .key('T', Ingredient.fromItems(GradientItems.LIT_FIBRE_TORCH.get(), GradientItems.UNLIT_FIBRE_TORCH.get()))
        .key('F', Tags.Items.STRING)
        .key('S', Tags.Items.RODS_WOODEN)
        .key('P', ItemTags.WOODEN_SLABS)
        .addCriterion("has_lit_torch", this.hasItem(GradientItems.LIT_FIBRE_TORCH.get()))
        .addCriterion("has_unlit_torch", this.hasItem(GradientItems.UNLIT_FIBRE_TORCH.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.TORCH_STAND));

      StagedRecipeBuilder
        .shaped(GradientItems.GRINDSTONE.get())
        .stage(GradientStages.AGE_1.get())
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
        .shaped(GradientItems.MIXING_BASIN.get())
        .stage(GradientStages.AGE_1.get())
        .patternLine(" S ")
        .patternLine("W W")
        .patternLine("CWC")
        .key('S', Tags.Items.RODS_WOODEN)
        .key('W', ItemTags.LOGS)
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age1/" + GradientIds.MIXING_BASIN));

      StagedRecipeBuilder
        .shaped(GradientItems.DRYING_RACK.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("PP")
        .patternLine("SS")
        .key('P', ItemTags.PLANKS)
        .key('S', Tags.Items.STRING)
        .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
        .build(finished, Gradient.loc("age2/" + GradientIds.DRYING_RACK));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_FURNACE.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("CCC")
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_FURNACE));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_CRUCIBLE.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("C C")
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_CRUCIBLE));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_OVEN.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine(" C ")
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_OVEN));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_METAL_MIXER.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("CCC")
        .patternLine("PCP")
        .patternLine("PCP")
        .key('C', Items.CLAY_BALL)
        .key('P', GradientItems.PEBBLE.get())
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_METAL_MIXER));

      StagedRecipeBuilder
        .shaped(GradientItems.UNHARDENED_CLAY_BUCKET.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("C C")
        .patternLine("CCC")
        .key('C', Items.CLAY_BALL)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_BUCKET));

      StagedRecipeBuilder
        .shapelessRecipe(GradientItems.UNHARDENED_CLAY_CAST_BLANK.get())
        .stage(GradientStages.AGE_2.get())
        .addIngredient(Items.CLAY_BALL)
        .addIngredient(ItemTags.SAND)
        .addCriterion("has_clay_ball", this.hasItem(Items.CLAY_BALL))
        .build(finished, Gradient.loc("age2/" + GradientIds.UNHARDENED_CLAY_CAST_BLANK));

      StagedRecipeBuilder
        .shaped(GradientItems.WOODEN_GEAR.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("FSF")
        .patternLine("S S")
        .patternLine("FSF")
        .key('S', GradientItems.HARDENED_STICK.get())
        .key('F', Items.FLINT)
        .addCriterion("has_hardened_stick", this.hasItem(GradientItems.HARDENED_STICK.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.WOODEN_GEAR));

      StagedRecipeBuilder
        .shaped(GradientItems.WOODEN_AXLE.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine(" C ")
        .patternLine("SSS")
        .patternLine(" C ")
        .key('S', GradientItems.HARDENED_STICK.get())
        .key('C', GradientItems.LEATHER_STRIP.get())
        .addCriterion("has_hardened_stick", this.hasItem(GradientItems.HARDENED_STICK.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.WOODEN_AXLE));

      StagedRecipeBuilder
        .shaped(GradientItems.WOODEN_GEARBOX.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("PGP")
        .patternLine("GAG")
        .patternLine("PGP")
        .key('P', GradientItems.HARDENED_PLANKS.get())
        .key('G', GradientItems.WOODEN_GEAR.get())
        .key('A', GradientItems.WOODEN_AXLE.get())
        .addCriterion("has_wooden_gear", this.hasItem(GradientItems.WOODEN_GEAR.get()))
        .addCriterion("has_wooden_axle", this.hasItem(GradientItems.WOODEN_AXLE.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.WOODEN_GEARBOX));

      StagedRecipeBuilder
        .shaped(GradientItems.WOODEN_CONVEYOR_BELT.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("SSS")
        .patternLine("AGA")
        .patternLine("SSS")
        .key('S', GradientItems.HARDENED_PLANKS_SLAB.get())
        .key('G', GradientItems.WOODEN_GEAR.get())
        .key('A', GradientItems.WOODEN_AXLE.get())
        .addCriterion("has_wooden_gear", this.hasItem(GradientItems.WOODEN_GEAR.get()))
        .addCriterion("has_wooden_axle", this.hasItem(GradientItems.WOODEN_AXLE.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.WOODEN_CONVEYOR_BELT));

      StagedRecipeBuilder
        .shaped(GradientItems.WOODEN_CONVEYOR_BELT_DRIVER.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("PAP")
        .patternLine("GGG")
        .patternLine("APA")
        .key('P', GradientItems.HARDENED_PLANKS.get())
        .key('G', GradientItems.WOODEN_GEAR.get())
        .key('A', GradientItems.WOODEN_AXLE.get())
        .addCriterion("has_wooden_gear", this.hasItem(GradientItems.WOODEN_GEAR.get()))
        .addCriterion("has_wooden_axle", this.hasItem(GradientItems.WOODEN_AXLE.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.WOODEN_CONVEYOR_BELT_DRIVER));

      StagedRecipeBuilder
        .shaped(GradientItems.WOODEN_CRANK.get())
        .stage(GradientStages.AGE_2.get())
        .patternLine("S S")
        .patternLine("PPP")
        .patternLine("PGP")
        .key('S', GradientItems.HARDENED_STICK.get())
        .key('P', GradientItems.HARDENED_PLANKS.get())
        .key('G', GradientItems.WOODEN_GEAR.get())
        .addCriterion("has_wooden_gear", this.hasItem(GradientItems.WOODEN_GEAR.get()))
        .addCriterion("has_hardened_planks", this.hasItem(GradientItems.HARDENED_PLANKS.get()))
        .build(finished, Gradient.loc("age2/" + GradientIds.WOODEN_CRANK));
    }

    private void registerFuelRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .fuel()
        .ticks(24000)
        .ignitionTemp(700.0f)
        .burnTemp(2700.0f)
        .heatPerSecond(1.5f)
        .ingredient(ItemTags.COALS)
        .addCriterion("has_coal", this.hasItem(ItemTags.COALS))
        .build(finished, Gradient.loc("fuel/coal"));

      GradientRecipeBuilder
        .fuel()
        .ticks(100)
        .ignitionTemp(50.0f)
        .burnTemp(125.0f)
        .heatPerSecond(18.0f)
        .ingredient(GradientItems.FIBRE.get())
        .addCriterion("has_fibre", this.hasItem(GradientItems.FIBRE.get()))
        .build(finished, Gradient.loc("fuel/" + GradientIds.FIBRE));

      GradientRecipeBuilder
        .fuel()
        .ticks(18000)
        .ignitionTemp(300.0f)
        .burnTemp(750.0f)
        .heatPerSecond(0.76f)
        .ingredient(ItemTags.LOGS)
        .addCriterion("has_log", this.hasItem(ItemTags.LOGS))
        .build(finished, Gradient.loc("fuel/log"));

      GradientRecipeBuilder
        .fuel()
        .ticks(12000)
        .ignitionTemp(230.0f)
        .burnTemp(750.0f)
        .heatPerSecond(1.04f)
        .ingredient(ItemTags.PLANKS)
        .addCriterion("has_planks", this.hasItem(ItemTags.PLANKS))
        .build(finished, Gradient.loc("fuel/planks"));

      GradientRecipeBuilder
        .fuel()
        .ticks(1200)
        .ignitionTemp(150.0f)
        .burnTemp(350.0f)
        .heatPerSecond(1.38f)
        .ingredient(ItemTags.SAPLINGS)
        .addCriterion("has_sapling", this.hasItem(ItemTags.SAPLINGS))
        .build(finished, Gradient.loc("fuel/sapling"));

      GradientRecipeBuilder
        .fuel()
        .ticks(600)
        .ignitionTemp(100.0f)
        .burnTemp(350.0f)
        .heatPerSecond(2.16f)
        .ingredient(Tags.Items.RODS_WOODEN)
        .addCriterion("has_stick", this.hasItem(Tags.Items.RODS_WOODEN))
        .build(finished, Gradient.loc("fuel/stick"));

      GradientRecipeBuilder
        .fuel()
        .ticks(400)
        .ignitionTemp(50.0f)
        .burnTemp(125.0f)
        .heatPerSecond(18.0f)
        .ingredient(Tags.Items.STRING)
        .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
        .build(finished, Gradient.loc("fuel/string"));

      GradientRecipeBuilder
        .fuel()
        .ticks(Integer.MAX_VALUE)
        .ignitionTemp(0.0f)
        .burnTemp(2700.0f)
        .heatPerSecond(1.5f)
        .ingredient(GradientItems.INFINICOAL.get())
        .addCriterion("has_infinicoal", this.hasItem(GradientItems.INFINICOAL.get()))
        .build(finished, Gradient.loc("fuel/infinicoal"));
    }

    private void registerCookingRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .cooking(Items.COOKED_BEEF)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.BEEF)
        .addCriterion("has_beef", this.hasItem(Items.BEEF))
        .build(finished, Gradient.loc("cooking/age1/beef"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_CHICKEN)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.CHICKEN)
        .addCriterion("has_chicken", this.hasItem(Items.CHICKEN))
        .build(finished, Gradient.loc("cooking/age1/chicken"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_COD)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.COD)
        .addCriterion("has_cod", this.hasItem(Items.COD))
        .build(finished, Gradient.loc("cooking/age1/cod"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_MUTTON)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.MUTTON)
        .addCriterion("has_mutton", this.hasItem(Items.MUTTON))
        .build(finished, Gradient.loc("cooking/age1/mutton"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_PORKCHOP)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.PORKCHOP)
        .addCriterion("has_porkchop", this.hasItem(Items.PORKCHOP))
        .build(finished, Gradient.loc("cooking/age1/porkchop"));

      GradientRecipeBuilder
        .cooking(Items.BAKED_POTATO)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.POTATO)
        .addCriterion("has_potato", this.hasItem(Items.POTATO))
        .build(finished, Gradient.loc("cooking/age1/potato"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_RABBIT)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.RABBIT)
        .addCriterion("has_rabbit", this.hasItem(Items.RABBIT))
        .build(finished, Gradient.loc("cooking/age1/rabbit"));

      GradientRecipeBuilder
        .cooking(Items.COOKED_SALMON)
        .stage(GradientStages.AGE_1.get())
        .ticks(2400)
        .temperature(200.0f)
        .addIngredient(Items.SALMON)
        .addCriterion("has_salmon", this.hasItem(Items.SALMON))
        .build(finished, Gradient.loc("cooking/age1/salmon"));
    }

    private void registerHardeningRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .hardening(GradientItems.HARDENED_LOG.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(1200)
        .addIngredient(ItemTags.LOGS)
        .addCriterion("has_log", this.hasItem(ItemTags.LOGS))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.HARDENED_LOG));

      GradientRecipeBuilder
        .hardening(GradientItems.HARDENED_PLANKS.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(1200)
        .addIngredient(ItemTags.PLANKS)
        .addCriterion("has_planks", this.hasItem(ItemTags.PLANKS))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.HARDENED_PLANKS));

      GradientRecipeBuilder
        .hardening(GradientItems.HARDENED_PLANKS_SLAB.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(600)
        .addIngredient(ItemTags.WOODEN_SLABS)
        .addCriterion("has_wooden_slabs", this.hasItem(ItemTags.WOODEN_SLABS))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.HARDENED_PLANKS_SLAB));

      GradientRecipeBuilder
        .hardening(GradientItems.CLAY_FURNACE.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(3600)
        .addIngredient(GradientBlocks.UNHARDENED_CLAY_FURNACE.get())
        .addCriterion("has_unhardened_clay_furnace", this.hasItem(GradientBlocks.UNHARDENED_CLAY_FURNACE.get()))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.CLAY_FURNACE));

      GradientRecipeBuilder
        .hardening(GradientItems.CLAY_OVEN.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(3600)
        .addIngredient(GradientBlocks.UNHARDENED_CLAY_OVEN.get())
        .addCriterion("has_unhardened_clay_oven", this.hasItem(GradientBlocks.UNHARDENED_CLAY_OVEN.get()))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.CLAY_OVEN));

      GradientRecipeBuilder
        .hardening(GradientItems.CLAY_CRUCIBLE.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(3600)
        .addIngredient(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get())
        .addCriterion("has_unhardened_clay_crucible", this.hasItem(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get()))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.CLAY_CRUCIBLE));

      GradientRecipeBuilder
        .hardening(GradientItems.CLAY_METAL_MIXER.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(3600)
        .addIngredient(GradientBlocks.UNHARDENED_CLAY_METAL_MIXER.get())
        .addCriterion("has_unhardened_clay_metal_mixer", this.hasItem(GradientBlocks.UNHARDENED_CLAY_METAL_MIXER.get()))
        .build(finished, Gradient.loc("hardening/age2/" + GradientIds.CLAY_METAL_MIXER));

      for(final GradientCasts cast : GradientCasts.values()) {
        GradientRecipeBuilder
          .hardening(GradientItems.CLAY_CAST(cast).get())
          .stage(GradientStages.AGE_2.get())
          .ticks(1200)
          .addIngredient(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get())
          .addCriterion("has_unhardened_clay_cast", this.hasItem(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get()))
          .build(finished, Gradient.loc("hardening/age2/" + GradientIds.CLAY_CAST(cast)));
      }
    }

    private void registerDryingRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .drying(GradientItems.PRESERVED_HIDE.get())
        .stage(GradientStages.AGE_2.get())
        .ticks(1800)
        .addIngredient(GradientItems.SALTED_HIDE.get())
        .addCriterion("has_salted_hide", this.hasItem(GradientItems.SALTED_HIDE.get()))
        .build(finished, Gradient.loc("drying/age2/" + GradientIds.PRESERVED_HIDE));

      GradientRecipeBuilder
        .drying(Items.LEATHER)
        .stage(GradientStages.AGE_2.get())
        .ticks(1800)
        .addIngredient(GradientItems.TANNED_HIDE.get())
        .addCriterion("has_tanned_hide", this.hasItem(GradientItems.TANNED_HIDE.get()))
        .build(finished, Gradient.loc("drying/age2/leather"));
    }

    private void registerMeltingRecipes(final Consumer<IFinishedRecipe> finished) {
      for(final Ore ore : Minerals.ores()) {
        GradientRecipeBuilder
          .melting()
          .stage(GradientStages.AGE_2.get())
          .ticks((int)(ore.meltTime * 1.2f))
          .temperature(ore.meltTemp)
          .fluid(new GradientFluidStack(GradientFluids.METAL(ore.metal).get(), 0.5f, ore.meltTemp))
          .ingredient(GradientTags.Items.ORE.get(ore))
          .addCriterion("has_ore", this.hasItem(GradientTags.Items.ORE.get(ore)))
          .build(finished, Gradient.loc("melting/" + ore.name + "_ore"));

        GradientRecipeBuilder
          .melting()
          .stage(GradientStages.AGE_2.get())
          .ticks(ore.meltTime)
          .temperature(ore.meltTemp)
          .fluid(new GradientFluidStack(GradientFluids.METAL(ore.metal).get(), 0.75f, ore.meltTemp))
          .ingredient(GradientTags.Items.CRUSHED_ORE.get(ore))
          .addCriterion("has_crushed_ore", this.hasItem(GradientTags.Items.CRUSHED_ORE.get(ore)))
          .build(finished, Gradient.loc("melting/" + ore.name + "_crushed_ore"));

        GradientRecipeBuilder
          .melting()
          .stage(GradientStages.AGE_2.get())
          .ticks(ore.meltTime)
          .temperature(ore.meltTemp)
          .fluid(new GradientFluidStack(GradientFluids.METAL(ore.metal).get(), 1.0f, ore.meltTemp))
          .ingredient(GradientTags.Items.PURIFIED_ORE.get(ore))
          .addCriterion("has_purified_ore", this.hasItem(GradientTags.Items.PURIFIED_ORE.get(ore)))
          .build(finished, Gradient.loc("melting/" + ore.name + "_purified_ore"));
      }

      for(final Metal metal : Minerals.metals()) {
        GradientRecipeBuilder
          .melting()
          .stage(GradientStages.AGE_2.get())
          .ticks(metal.meltTime)
          .temperature(metal.meltTemp)
          .fluid(new GradientFluidStack(GradientFluids.METAL(metal).get(), 1.0f, metal.meltTemp))
          .ingredient(GradientTags.Items.DUST.get(metal))
          .addCriterion("has_dust", this.hasItem(GradientTags.Items.DUST.get(metal)))
          .build(finished, Gradient.loc("melting/" + metal.name + "_dust"));

        GradientRecipeBuilder
          .melting()
          .stage(GradientStages.AGE_2.get())
          .ticks(metal.meltTime)
          .temperature(metal.meltTemp)
          .fluid(new GradientFluidStack(GradientFluids.METAL(metal).get(), 1.0f, metal.meltTemp))
          .ingredient(GradientTags.Items.PLATE.get(metal))
          .addCriterion("has_plate", this.hasItem(GradientTags.Items.PLATE.get(metal)))
          .build(finished, Gradient.loc("melting/" + metal.name + "_plate"));

        GradientRecipeBuilder
          .melting()
          .stage(GradientStages.AGE_2.get())
          .ticks(metal.meltTime * 9)
          .temperature(metal.meltTemp)
          .fluid(new GradientFluidStack(GradientFluids.METAL(metal).get(), 9.0f, metal.meltTemp))
          .ingredient(GradientTags.Items.STORAGE_BLOCK.get(metal))
          .addCriterion("has_block", this.hasItem(GradientTags.Items.STORAGE_BLOCK.get(metal)))
          .build(finished, Gradient.loc("melting/" + metal.name + "_block"));

        for(final GradientCasts cast : GradientCasts.values()) {
          final Tag<Item> tag = cast.getTag(metal);

          GradientRecipeBuilder
            .melting()
            .stage(GradientStages.AGE_2.get())
            .ticks((int)(metal.meltTime * cast.metalAmount))
            .temperature(metal.meltTemp)
            .fluid(new GradientFluidStack(GradientFluids.METAL(metal).get(), cast.metalAmount, metal.meltTemp))
            .ingredient(cast.getIngredient(metal))
            .addCriterion("has_" + cast.name, tag != null ? this.hasItem(tag) : this.hasItem(cast.getItem(metal)))
            .build(finished, Gradient.loc("melting/" + metal.name + '_' + cast.name));
        }
      }
    }

    private void registerAlloyRecipes(final Consumer<IFinishedRecipe> finished) {
      GradientRecipeBuilder
        .alloy(new GradientFluidStack(GradientFluids.METAL(Minerals.BRONZE).get(), 0.004f))
        .addInput(new GradientFluidStack(GradientFluids.METAL(Minerals.COPPER).get(), 0.003f))
        .addInput(new GradientFluidStack(GradientFluids.METAL(Minerals.TIN).get(), 0.001f))
        .addCriterion("impossible", new ImpossibleTrigger.Instance())
        .build(finished, Gradient.loc("alloy/bronze_from_copper_and_tin"));
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
        Pair.of(HideArmourAdvancementLootTable::new, LootParameterSets.ADVANCEMENT),
        Pair.of(FibreAdditionsLootTable::new, LootParameterSets.BLOCK),
        Pair.of(PebbleAdditionsLootTable::new, LootParameterSets.BLOCK),
        Pair.of(BoneDropsTable::new, LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.CAT_PELT, GradientItems.CAT_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.COW_PELT, GradientItems.COW_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.DONKEY_PELT, GradientItems.DONKEY_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.FOX_PELT, GradientItems.FOX_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.HORSE_PELT, GradientItems.HORSE_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.LLAMA_PELT, GradientItems.LLAMA_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.MULE_PELT, GradientItems.MULE_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.OCELOT_PELT, GradientItems.OCELOT_PELT.get()), LootParameterSets.ENTITY),
        Pair.of(() -> new PeltDropsTable(GradientIds.PANDA_PELT, GradientItems.PANDA_PELT.get()), LootParameterSets.ENTITY),
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

    public static class HideArmourAdvancementLootTable implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
      @Override
      public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        builder.accept(GradientLoot.HIDE_ARMOUR_ADVANCEMENT, LootTable.builder().addLootPool(
          LootPool.builder().addEntry(ItemLootEntry.builder(Items.TORCH)).rolls(ConstantRange.of(4))
        ));
      }
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
          ).addEntry(
            ItemLootEntry
              .builder(GradientItems.FIBRE.get())
              .acceptCondition(BlockStateProperty.builder(Blocks.WHEAT).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CropsBlock.AGE, ((CropsBlock)Blocks.WHEAT).getMaxAge())))
              .acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2))
              .acceptFunction(ExplosionDecay.builder())
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

    public static class BoneDropsTable implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
      @Override
      public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        builder.accept(Gradient.loc("entities/bone_additions"), LootTable.builder().addLootPool(
          LootPool.builder().addEntry(
            ItemLootEntry
              .builder(Items.BONE)
              .acceptCondition(RandomChance.builder(0.5f))
              .acceptFunction(ExplosionDecay.builder())
              .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0f, 1.0f)))
          )
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
        final OreBlock block = GradientBlocks.ORE(ore).get();
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

        for(final Ore ore : Minerals.ores()) {
          this.registerLootTable(GradientBlocks.PEBBLE(ore).get(), metalPebbleDrops(ore.metal));
          this.registerLootTable(GradientBlocks.ORE(ore).get(), oreDrops(ore));
        }

        for(final Metal metal : Minerals.metals()) {
          this.registerDropSelfLootTable(GradientBlocks.METAL_BLOCK(metal).get());
        }

        this.registerLootTable(GradientBlocks.SALT_BLOCK.get(), block -> droppingWithSilkTouchOrRandomly(block, GradientItems.SALT.get(), ConstantRange.of(4)));
        this.registerDropSelfLootTable(GradientBlocks.HARDENED_LOG.get());
        this.registerDropSelfLootTable(GradientBlocks.HARDENED_PLANKS.get());
        this.registerLootTable(GradientBlocks.HARDENED_LOG_SLAB.get(), BlockLootTables::droppingSlab);
        this.registerLootTable(GradientBlocks.HARDENED_PLANKS_SLAB.get(), BlockLootTables::droppingSlab);

        this.registerLootTable(GradientBlocks.FIREPIT.get(), droppingRandomly(Items.STICK, RandomValueRange.of(2, 5)));
        this.registerDropSelfLootTable(GradientBlocks.UNLIT_FIBRE_TORCH.get());
        this.registerDropping(GradientBlocks.UNLIT_FIBRE_WALL_TORCH.get(), GradientBlocks.UNLIT_FIBRE_TORCH.get());
        this.registerDropSelfLootTable(GradientBlocks.LIT_FIBRE_TORCH.get());
        this.registerDropping(GradientBlocks.LIT_FIBRE_WALL_TORCH.get(), GradientBlocks.LIT_FIBRE_TORCH.get());
        this.registerDropSelfLootTable(GradientBlocks.TORCH_STAND.get());
        this.registerDropping(GradientBlocks.LIT_TORCH_STAND_TORCH.get(), Items.AIR);
        this.registerDropping(GradientBlocks.UNLIT_TORCH_STAND_TORCH.get(), Items.AIR);

        this.registerDropSelfLootTable(GradientBlocks.GRINDSTONE.get());
        this.registerDropSelfLootTable(GradientBlocks.MIXING_BASIN.get());
        this.registerDropSelfLootTable(GradientBlocks.DRYING_RACK.get());

        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_FURNACE.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_OVEN.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_METAL_MIXER.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_BUCKET.get());
        this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_CAST_BLANK.get());

        for(final GradientCasts cast : GradientCasts.values()) {
          this.registerDropSelfLootTable(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get());
        }

        this.registerDropSelfLootTable(GradientBlocks.CLAY_FURNACE.get());
        this.registerDropSelfLootTable(GradientBlocks.CLAY_OVEN.get());
        this.registerDropSelfLootTable(GradientBlocks.CLAY_CRUCIBLE.get());
        this.registerDropSelfLootTable(GradientBlocks.CLAY_METAL_MIXER.get());

        for(final GradientCasts cast : GradientCasts.values()) {
          this.registerDropSelfLootTable(GradientBlocks.CLAY_CAST(cast).get());
        }

        this.registerDropSelfLootTable(GradientBlocks.WOODEN_AXLE.get());
        this.registerDropSelfLootTable(GradientBlocks.WOODEN_GEARBOX.get());
        this.registerDropSelfLootTable(GradientBlocks.WOODEN_CONVEYOR_BELT.get());
        this.registerDropSelfLootTable(GradientBlocks.WOODEN_CONVEYOR_BELT_DRIVER.get());
        this.registerDropSelfLootTable(GradientBlocks.WOODEN_CRANK.get());
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
