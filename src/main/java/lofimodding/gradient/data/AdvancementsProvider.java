package lofimodding.gradient.data;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientTags;
import lofimodding.gradient.advancements.criterion.AdvancementUnlockedTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AdvancementsProvider extends AdvancementProvider {
  public AdvancementsProvider(final DataGenerator generator) {
    super(generator);

    this.advancements = new ArrayList<>();
    this.advancements.add(new Age1());
  }

  public static class Age1 implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(final Consumer<Advancement> finished) {
      final Advancement root = Advancement.Builder.builder()
        .withDisplay(GradientItems.PEBBLE.get(), text("root.title"), text("root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false)
        .withCriterion("always", PositionTrigger.Instance.forLocation(LocationPredicate.ANY))
        .register(finished, loc("age1/root"));

      final Advancement basicMaterials = builder(Items.STICK, "basic_materials", 32, 27, FrameType.TASK, root)
        .withCriterion("has_sticks", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(Tags.Items.RODS_WOODEN).build()))
        .withCriterion("has_fibre", InventoryChangeTrigger.Instance.forItems(GradientItems.FIBRE.get()))
        .withCriterion("has_pebble", InventoryChangeTrigger.Instance.forItems(GradientItems.PEBBLE.get()))
        .register(finished, loc("age1/basic_materials"));

      final Advancement stoneHammer = builder(GradientItems.STONE_HAMMER.get(), "stone_hammer", 64, 27, FrameType.TASK, basicMaterials)
        .withCriterion("has_hammer", InventoryChangeTrigger.Instance.forItems(GradientItems.STONE_HAMMER.get()))
        .register(finished, loc("age1/stone_hammer"));

      final Advancement stoneHatchet = builder(GradientItems.STONE_HATCHET.get(), "stone_hatchet", 64, 0, FrameType.TASK, basicMaterials)
        .withCriterion("has_hatchet", InventoryChangeTrigger.Instance.forItems(GradientItems.STONE_HATCHET.get()))
        .register(finished, loc("age1/stone_hatchet"));

      final Advancement wood = builder(Blocks.OAK_LOG, "wood", 96, 27, FrameType.TASK, stoneHammer, stoneHatchet)
        .withCriterion("has_wood", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(ItemTags.LOGS).build()))
        .register(finished, loc("age1/wood"));

      final Advancement planks = builder(Blocks.OAK_PLANKS, "planks", 127, 27, FrameType.TASK, wood)
        .withCriterion("has_planks", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(ItemTags.PLANKS).build()))
        .register(finished, loc("age1/planks"));

      final Advancement grindstone = builder(GradientBlocks.GRINDSTONE.get(), "grindstone", 160, 40, FrameType.GOAL, planks)
        .withCriterion("has_grindstone", InventoryChangeTrigger.Instance.forItems(GradientItems.GRINDSTONE.get()))
        .register(finished, loc("age1/grindstone"));

      final Advancement mixingBasin = builder(GradientBlocks.MIXING_BASIN.get(), "mixing_basin", 160, 13, FrameType.GOAL, planks)
        .withCriterion("has_mixing_basin", InventoryChangeTrigger.Instance.forItems(GradientItems.MIXING_BASIN.get()))
        .register(finished, loc("age1/mixing_basin"));

      final Advancement firepit = builder(GradientItems.FIREPIT.get(), "firepit", 64, 54, FrameType.TASK, basicMaterials)
        .withCriterion("has_firepit", InventoryChangeTrigger.Instance.forItems(GradientItems.FIREPIT.get()))
        .register(finished, loc("age1/firepit"));

      final Advancement pelt = builder(GradientItems.COW_PELT.get(), "pelt", 32, 94, FrameType.TASK, root)
        .withCriterion("has_pelt", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().tag(GradientTags.Items.PELTS).build()))
        .register(finished, loc("age1/pelt"));
    }

    private static Advancement.Builder builder(final IItemProvider icon, final String id, final int x, final int y, final FrameType frame, final Advancement... parents) {
      final DisplayInfo display = new DisplayInfo(new ItemStack(icon), text(id + ".title"), text(id + ".description"), null, frame, true, true, false);
      display.setPosition(x, y);

      final Advancement.Builder builder = Advancement.Builder.builder().withDisplay(display);

      int i = 0;
      for(final Advancement parent : parents) {
        builder
          .withParent(parent)
          .withCriterion("has_parent_" + i++, AdvancementUnlockedTrigger.Instance.forAdvancement(parent));
      }

      return builder;
    }

    private static TranslationTextComponent text(final String key) {
      return new TranslationTextComponent("advancements.gradient.age1." + key);
    }
  }

  private static String loc(final String path) {
    return Gradient.loc(path).toString();
  }

  public static class EnglishLang extends LanguageProvider {
    public EnglishLang(final DataGenerator gen) {
      super(gen, Gradient.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
      this.age1("root", "Age 1: Stone Age", "Humankind's first steps");
      this.age1("basic_materials", "Basic Materials", "Gather sticks from leaves, fibre from grass, and pebbles from the ground");
      this.age1("stone_hammer", "Hammer Time!", "Craft a stone hammer");
      this.age1("stone_hatchet", "Just Jackin' It", "Craft a stone hatchet");
      this.age1("wood", "Getting Wood", "Use your new hatchet to gather wood");
      this.age1("planks", "Chop Chop", "Chop some wood into planks");
      this.age1("grindstone", "Dust to Dust", "Craft a grindstone");
      this.age1("mixing_basin", "Mix It Up", "Craft a mixing basin");
      this.age1("firepit", "Open This Pit Up", "Craft a fire pit");
      this.age1("pelt", "Animal Pelt", "Kill an animal and collect its pelt");
    }

    private void age1(final String key, final String title, final String description) {
      this.add("advancements.gradient.age1." + key + ".title", title);
      this.add("advancements.gradient.age1." + key + ".description", description);
    }
  }
}
