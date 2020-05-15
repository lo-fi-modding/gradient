package lofimodding.gradient;

import lofimodding.gradient.science.Metal;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.stream.Stream;

public enum GradientCasts {
  MATTOCK_HEAD(1.0f),
  PICKAXE_HEAD(1.0f),
  HAMMER_HEAD(1.0f),
  SWORD_BLADE(1.0f),
  INGOT(1.0f, metal -> GradientItems.INGOT(metal).get(), GradientTags.Items.INGOT::get),
  ;

  public static Stream<GradientCasts> stream() {
    return Stream.of(values());
  }

  public final String name;
  public final float metalAmount;
  @Nullable
  private final Function<Metal, Item> itemGetter;
  private final Function<Metal, Tag<Item>> tagGetter;

  GradientCasts(final float metalAmount, @Nullable final Function<Metal, Item> itemGetter, final Function<Metal, Tag<Item>> tagGetter) {
    this.name = this.name().toLowerCase();
    this.metalAmount = metalAmount;
    this.itemGetter = itemGetter;
    this.tagGetter = tagGetter;
  }

  GradientCasts(final float metalAmount) {
    this(metalAmount, null, metal -> null);
  }

  public boolean usesDefaultItem() {
    return this.itemGetter == null;
  }

  public Item getItem(final Metal metal) {
    if(this.itemGetter == null) {
      return GradientItems.CASTED(this, metal).get();
    }

    return this.itemGetter.apply(metal);
  }

  public Ingredient getIngredient(final Metal metal) {
    final Tag<Item> tag = this.getTag(metal);

    if(tag != null) {
      return Ingredient.fromTag(tag);
    }

    return Ingredient.fromItems(this.getItem(metal));
  }

  @Nullable
  public Tag<Item> getTag(final Metal metal) {
    return this.tagGetter.apply(metal);
  }

  public String getTranslationKey() {
    return "cast.gradient." + this.name;
  }

  public ITextComponent getUnlocalizedName() {
    return new TranslationTextComponent(this.getTranslationKey());
  }
}
