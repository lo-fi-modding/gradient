package lofimodding.gradient.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientLoot;
import lofimodding.gradient.GradientTags;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationTracker;
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
import java.util.function.Function;
import java.util.function.Supplier;

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
      gen.addProvider(new Loot(gen));
    }
  }

  public static class BlockModels extends BlockModelProvider {
    public BlockModels(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
      super(generator, Gradient.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
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
    }

    private BiConsumer<Direction, ModelBuilder<BlockModelBuilder>.ElementBuilder.FaceBuilder> addTexture(final String texture) {
      return ($, f) -> f.texture(texture);
    }

    @Override
    public String getName() {
      return "Thaumcraft block model generator";
    }
  }

  public static class ItemModels extends ItemModelProvider {
    public ItemModels(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
      super(generator, Gradient.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
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

      this.singleTexture(GradientIds.FIBRE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FIBRE));
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
      for(final Ore ore : Ores.all()) {
        this.simpleBlock(GradientBlocks.ORE(ore).get(), new ModelFile.UncheckedModelFile(this.modLoc("block/" + GradientIds.ORE(ore))));
      }
    }
  }

  public static class EnglishLang extends LanguageProvider {
    public EnglishLang(final DataGenerator gen) {
      super(gen, Gradient.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
      for(final Ore ore : Ores.all()) {
        this.add(GradientBlocks.ORE(ore).get(), StringUtils.capitalize(ore.name) + " Ore");
      }

      for(final Metal metal : Metals.all()) {
        this.add(GradientItems.CRUSHED(metal).get(), "Crushed " + StringUtils.capitalize(metal.name) + " Ore");
        this.add(GradientItems.PURIFIED(metal).get(), "Purified " + StringUtils.capitalize(metal.name) + " Ore");
        this.add(GradientItems.DUST(metal).get(), StringUtils.capitalize(metal.name) + " Dust");
        this.add(GradientItems.INGOT(metal).get(), StringUtils.capitalize(metal.name) + " Ingot");
        this.add(GradientItems.NUGGET(metal).get(), StringUtils.capitalize(metal.name) + " Nugget");
        this.add(GradientItems.PLATE(metal).get(), StringUtils.capitalize(metal.name) + " Plate");
      }

      this.add(GradientItems.FIBRE.get(), "Fibre");
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

      for(final Ore ore : Ores.all()) {
        this.getBuilder(GradientTags.Blocks.ORE.get(ore)).add(GradientBlocks.ORE(ore).get());
        this.getBuilder(Tags.Blocks.ORES).add(GradientTags.Blocks.ORE.get(ore));
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
      }
    }
  }

  public static class Loot extends LootTableProvider {
    public Loot(final DataGenerator gen) {
      super(gen);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
      return ImmutableList.of(
        Pair.of(FibreAdditionsLootTable::new, LootParameterSets.BLOCK),
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

    public static class BlockTables extends BlockLootTables {
      private final List<Block> knownBlocks = new ArrayList<>();

      @Override
      protected void addTables() {
        for(final Ore ore : Ores.all()) {
          this.registerDropSelfLootTable(GradientBlocks.ORE(ore).get());
        }
      }

      //TODO: remove these overrides once all blocks are implemented
      @Override
      public void registerFlowerPot(final Block flowerPot) {
        super.registerFlowerPot(flowerPot);
        this.knownBlocks.add(flowerPot);
      }

      @Override
      public void registerSilkTouch(final Block block, final Block silkTouchDrop) {
        super.registerSilkTouch(block, silkTouchDrop);
        this.knownBlocks.add(block);
      }

      @Override
      public void registerDropping(final Block block, final IItemProvider drop) {
        super.registerDropping(block, drop);
        this.knownBlocks.add(block);
      }

      @Override
      public void registerSilkTouch(final Block block) {
        super.registerSilkTouch(block);
        this.knownBlocks.add(block);
      }

      @Override
      public void registerDropSelfLootTable(final Block block) {
        super.registerDropSelfLootTable(block);
        this.knownBlocks.add(block);
      }

      @Override
      protected void registerLootTable(final Block block, final Function<Block, LootTable.Builder> factory) {
        super.registerLootTable(block, factory);
        this.knownBlocks.add(block);
      }

      @Override
      protected void registerLootTable(final Block block, final LootTable.Builder table) {
        super.registerLootTable(block, table);
        this.knownBlocks.add(block);
      }

      @Override
      protected Iterable<Block> getKnownBlocks() {
        return this.knownBlocks;
        //TODO return Streams.stream(super.getKnownBlocks()).filter(block -> Thaumcraft.MOD_ID.equals(block.getRegistryName().getNamespace())).collect(Collectors.toCollection(ArrayList::new));
      }
    }
  }
}
