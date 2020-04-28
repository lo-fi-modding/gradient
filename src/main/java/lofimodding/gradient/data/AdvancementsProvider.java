package lofimodding.gradient.data;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.advancements.criterion.AdvancementUnlockedTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

      final Advancement firepit = builder(GradientItems.FIREPIT.get(), "firepit", 64, 54, FrameType.TASK, basicMaterials)
        .withCriterion("has_firepit", InventoryChangeTrigger.Instance.forItems(GradientItems.FIREPIT.get()))
        .register(finished, loc("age1/firepit"));
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
      this.age1("firepit", "Open This Pit Up", "Craft a fire pit");
    }

    private void age1(final String key, final String title, final String description) {
      this.add("advancements.gradient.age1." + key + ".title", title);
      this.add("advancements.gradient.age1." + key + ".description", description);
    }
  }
}
