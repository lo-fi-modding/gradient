package lofimodding.gradient.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.progression.Stage;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
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

  public static Fuel fuel() {
    return new Fuel();
  }

  public static Cooking cooking(final IItemProvider item) {
    return new Cooking(item, 1);
  }

  public static Cooking cooking(final IItemProvider item, final int amount) {
    return new Cooking(item, amount);
  }

  public static Hardening hardening(final IItemProvider item) {
    return new Hardening(item, 1);
  }

  public static Hardening hardening(final IItemProvider item, final int amount) {
    return new Hardening(item, amount);
  }

  public static class Grinding {
    private final Item result;
    private final int count;
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Grinding(final IItemProvider item, final int amount) {
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
        throw new IllegalStateException("Grinding Recipe " + save + " should remove its 'save' argument");
      }

      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Grinding.Result(save, this.stages, this.ticks, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + this.result.getGroup().getPath() + '/' + save.getPath())));
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

      protected Result(final ResourceLocation id, final Set<Stage> stages, final int ticks, final Item result, final int count, final String group, final List<Ingredient> ingredients, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
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

  public static class Fuel {
    private int duration;
    private float ignitionTemp;
    private float burnTemp;
    private float heatPerSecond;
    private Ingredient ingredient;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Fuel() { }

    public Fuel duration(final int duration) {
      this.duration = duration;
      return this;
    }

    public Fuel ignitionTemp(final float ignitionTemp) {
      this.ignitionTemp = ignitionTemp;
      return this;
    }

    public Fuel burnTemp(final float burnTemp) {
      this.burnTemp = burnTemp;
      return this;
    }

    public Fuel heatPerSecond(final float heatPerSecond) {
      this.heatPerSecond = heatPerSecond;
      return this;
    }

    public Fuel ingredient(final Tag<Item> tag) {
      return this.ingredient(Ingredient.fromTag(tag));
    }

    public Fuel ingredient(final IItemProvider item) {
      return this.ingredient(Ingredient.fromItems(item));
    }

    public Fuel ingredient(final Ingredient ingredient) {
      this.ingredient = ingredient;
      return this;
    }

    public Fuel addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Fuel setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Result(save, this.duration, this.ignitionTemp, this.burnTemp, this.heatPerSecond, this.group == null ? "" : this.group, this.ingredient, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + save.getPath())));
    }

    private void validate(final ResourceLocation name) {
      if(this.advancementBuilder.getCriteria().isEmpty()) {
        throw new IllegalStateException("No way of obtaining recipe " + name);
      }
    }

    private static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final int duration;
      private final float ignitionTemp;
      private final float burnTemp;
      private final float heatPerSec;
      private final String group;
      private final Ingredient ingredient;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(final ResourceLocation id, final int duration, final float ignitionTemp, final float burnTemp, final float heatPerSec, final String group, final Ingredient ingredient, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.duration = duration;
        this.ignitionTemp = ignitionTemp;
        this.burnTemp = burnTemp;
        this.heatPerSec = heatPerSec;
        this.group = group;
        this.ingredient = ingredient;
        this.advancementBuilder = advancementBuilder;
        this.advancementId = advancementId;
      }

      @Override
      public void serialize(final JsonObject json) {
        if(!this.group.isEmpty()) {
          json.addProperty("group", this.group);
        }

        json.addProperty("duration", this.duration);
        json.addProperty("ignition_temp", this.ignitionTemp);
        json.addProperty("burn_temp", this.burnTemp);
        json.addProperty("heat_per_sec", this.heatPerSec);
        json.add("ingredient", this.ingredient.serialize());
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return GradientRecipeSerializers.FUEL.get();
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

  public static class Cooking {
    private final Item result;
    private final int count;
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private float temperature;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Cooking(final IItemProvider item, final int amount) {
      this.result = item.asItem();
      this.count = amount;
    }

    public Cooking stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public Cooking ticks(final int ticks) {
      this.ticks = ticks;
      return this;
    }

    public Cooking temperature(final float temperature) {
      this.temperature = temperature;
      return this;
    }

    public Cooking addIngredient(final Tag<Item> tag) {
      return this.addIngredient(Ingredient.fromTag(tag));
    }

    public Cooking addIngredient(final IItemProvider item) {
      return this.addIngredient(item, 1);
    }

    public Cooking addIngredient(final IItemProvider item, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.addIngredient(Ingredient.fromItems(item));
      }

      return this;
    }

    public Cooking addIngredient(final Ingredient ingredient) {
      return this.addIngredient(ingredient, 1);
    }

    public Cooking addIngredient(final Ingredient ingredient, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.ingredients.add(ingredient);
      }

      return this;
    }

    public Cooking addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Cooking setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished) {
      this.build(finished, this.result.getRegistryName());
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      final ResourceLocation name = this.result.getRegistryName();
      if(new ResourceLocation(save).equals(name)) {
        throw new IllegalStateException("Cooking Recipe " + save + " should remove its 'save' argument");
      }

      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Result(save, this.stages, this.ticks, this.temperature, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + this.result.getGroup().getPath() + '/' + save.getPath())));
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
      private final float temperature;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      protected Result(final ResourceLocation id, final Set<Stage> stages, final int ticks, final float temperature, final Item result, final int count, final String group, final List<Ingredient> ingredients, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.stages = stages;
        this.ticks = ticks;
        this.temperature = temperature;
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
        json.addProperty("temperature", this.temperature);

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
        return GradientRecipeSerializers.COOKING.get();
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

  public static class Hardening {
    private final Item result;
    private final int count;
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Hardening(final IItemProvider item, final int amount) {
      this.result = item.asItem();
      this.count = amount;
    }

    public Hardening stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public Hardening ticks(final int ticks) {
      this.ticks = ticks;
      return this;
    }

    public Hardening addIngredient(final Tag<Item> tag) {
      return this.addIngredient(Ingredient.fromTag(tag));
    }

    public Hardening addIngredient(final IItemProvider item) {
      return this.addIngredient(item, 1);
    }

    public Hardening addIngredient(final IItemProvider item, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.addIngredient(Ingredient.fromItems(item));
      }

      return this;
    }

    public Hardening addIngredient(final Ingredient ingredient) {
      return this.addIngredient(ingredient, 1);
    }

    public Hardening addIngredient(final Ingredient ingredient, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.ingredients.add(ingredient);
      }

      return this;
    }

    public Hardening addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Hardening setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished) {
      this.build(finished, this.result.getRegistryName());
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      final ResourceLocation name = this.result.getRegistryName();
      if(new ResourceLocation(save).equals(name)) {
        throw new IllegalStateException("Hardening Recipe " + save + " should remove its 'save' argument");
      }

      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Result(save, this.stages, this.ticks, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + this.result.getGroup().getPath() + '/' + save.getPath())));
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

      protected Result(final ResourceLocation id, final Set<Stage> stages, final int ticks, final Item result, final int count, final String group, final List<Ingredient> ingredients, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
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
        return GradientRecipeSerializers.HARDENING.get();
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
