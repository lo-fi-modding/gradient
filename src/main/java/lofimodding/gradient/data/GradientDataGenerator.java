package lofimodding.gradient.data;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientIds;
import lofimodding.gradient.GradientItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GradientDataGenerator {
  private GradientDataGenerator() { }

  @SubscribeEvent
  public static void gatherData(final GatherDataEvent event) {
    final DataGenerator gen = event.getGenerator();

    if(event.includeClient()) {
      gen.addProvider(new ItemModels(gen, event.getExistingFileHelper()));
      gen.addProvider(new EnglishLang(gen));
    }
  }

  public static class ItemModels extends ItemModelProvider {
    public ItemModels(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
      super(generator, Gradient.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
      this.singleTexture(GradientIds.FIBRE, this.mcLoc("item/generated"), "layer0", this.modLoc("item/" + GradientIds.FIBRE));
    }

    @Override
    public String getName() {
      return "Gradient item model generator";
    }
  }

  public static class EnglishLang extends LanguageProvider {
    public EnglishLang(final DataGenerator gen) {
      super(gen, Gradient.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
      this.add(GradientItems.FIBRE.get(), "Fibre");
    }
  }
}
