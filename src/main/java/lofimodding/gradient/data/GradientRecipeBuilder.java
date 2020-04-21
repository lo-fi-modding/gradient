package lofimodding.gradient.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.progression.Stage;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class GradientRecipeBuilder {
  private GradientRecipeBuilder() { }

  public static Grinding grinding(final IItemProvider item) {
    return new Grinding(item, 1);
  }

  public static Grinding grinding(final IItemProvider item, final int amount) {
    return new Grinding(item, amount);
  }

  public static class Grinding {
    private final Item result;
    private final int count;
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    public Grinding(final IItemProvider item, final int amount) {
      this.result = item.asItem();
      this.count = amount;
    }

    public Grinding stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public Grinding ticks(final int ticks) {
      this.ticks = ticks;
      return this;
    }

    public Grinding addIngredient(final Tag<Item> tag) {
      return this.addIngredient(Ingredient.fromTag(tag));
    }

    public Grinding addIngredient(final IItemProvider item) {
      return this.addIngredient(item, 1);
    }

    public Grinding addIngredient(final IItemProvider item, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.addIngredient(Ingredient.fromItems(item));
      }

      return this;
    }

    public Grinding addIngredient(final Ingredient ingredient) {
      return this.addIngredient(ingredient, 1);
    }

    public Grinding addIngredient(final Ingredient ingredient, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.ingredients.add(ingredient);
      }

      return this;
    }

    public Grinding addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Grinding setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished) {
      this.build(finished, this.result.getRegistryName());
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      final ResourceLocation name = this.result.getRegistryName();
      if(new ResourceLocation(save).equals(name)) {
        throw new IllegalStateException("Shapeless Staged Recipe " + save + " should remove its 'save' argument");
      }

      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Grinding.Result(save, this.stages, this.ticks, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + save.getPath())));
    }

    private void validate(final ResourceLocation name) {
      if(this.advancementBuilder.getCriteria().isEmpty()) {
        throw new IllegalStateException("No way of obtaining recipe " + name);
      }
    }

    public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Set<Stage> stages;
      private final int ticks;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(final ResourceLocation id, final Set<Stage> stages, final int ticks, final Item result, final int count, final String group, final List<Ingredient> ingredients, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.stages = stages;
        this.ticks = ticks;
        this.result = result;
        this.count = count;
        this.group = group;
        this.ingredients = ingredients;
        this.advancementBuilder = advancementBuilder;
        this.advancementId = advancementId;
      }

      @Override
      public void serialize(final JsonObject json) {
        if(!this.group.isEmpty()) {
          json.addProperty("group", this.group);
        }

        final JsonArray stages = new JsonArray();
        for(final Stage stage : this.stages) {
          stages.add(stage.getRegistryName().toString());
        }
        json.add("stages", stages);

        json.addProperty("ticks", this.ticks);

        final JsonArray array = new JsonArray();
        for(final Ingredient ingredient : this.ingredients) {
          array.add(ingredient.serialize());
        }

        json.add("ingredients", array);

        final JsonObject result = new JsonObject();
        result.addProperty("item", this.result.getRegistryName().toString());

        if(this.count > 1) {
          result.addProperty("count", this.count);
        }

        json.add("result", result);
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return GradientRecipeSerializers.GRINDING.get();
      }

      @Override
      public ResourceLocation getID() {
        return this.id;
      }

      @Override
      @Nullable
      public JsonObject getAdvancementJson() {
        return this.advancementBuilder.serialize();
      }

      @Override
      @Nullable
      public ResourceLocation getAdvancementID() {
        return this.advancementId;
      }
    }
  }
}
