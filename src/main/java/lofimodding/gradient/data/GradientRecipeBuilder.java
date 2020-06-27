package lofimodding.gradient.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.utils.RecipeUtils;
import lofimodding.progression.Stage;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

  public static Mixing mixing(final IItemProvider item) {
    return new Mixing(item, 1);
  }

  public static Mixing mixing(final IItemProvider item, final int amount) {
    return new Mixing(item, amount);
  }

  public static Drying drying(final IItemProvider item) {
    return new Drying(item, 1);
  }

  public static Drying drying(final IItemProvider item, final int amount) {
    return new Drying(item, amount);
  }

  public static Melting melting() {
    return new Melting();
  }

  public static Alloy alloy(final FluidStack stack) {
    return new Alloy(stack);
  }

  public static ShapelessToolStation shapelessToolStation() {
    return new ShapelessToolStation();
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
    private int ticks;
    private float ignitionTemp;
    private float burnTemp;
    private float heatPerSecond;
    private Ingredient ingredient;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Fuel() { }

    public Fuel ticks(final int ticks) {
      this.ticks = ticks;
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
      finished.accept(new Result(save, this.ticks, this.ignitionTemp, this.burnTemp, this.heatPerSecond, this.group == null ? "" : this.group, this.ingredient, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + save.getPath())));
    }

    private void validate(final ResourceLocation name) {
      if(this.advancementBuilder.getCriteria().isEmpty()) {
        throw new IllegalStateException("No way of obtaining recipe " + name);
      }
    }

    private static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final int ticks;
      private final float ignitionTemp;
      private final float burnTemp;
      private final float heatPerSec;
      private final String group;
      private final Ingredient ingredient;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(final ResourceLocation id, final int ticks, final float ignitionTemp, final float burnTemp, final float heatPerSec, final String group, final Ingredient ingredient, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.ticks = ticks;
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

        json.addProperty("ticks", this.ticks);
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

  public static class Mixing {
    private final Item result;
    private final int count;
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private FluidStack fluid = FluidStack.EMPTY;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Mixing(final IItemProvider item, final int amount) {
      this.result = item.asItem();
      this.count = amount;
    }

    public Mixing stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public Mixing ticks(final int ticks) {
      this.ticks = ticks;
      return this;
    }

    public Mixing addIngredient(final Tag<Item> tag) {
      return this.addIngredient(Ingredient.fromTag(tag));
    }

    public Mixing addIngredient(final IItemProvider item) {
      return this.addIngredient(item, 1);
    }

    public Mixing addIngredient(final IItemProvider item, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.addIngredient(Ingredient.fromItems(item));
      }

      return this;
    }

    public Mixing addIngredient(final Ingredient ingredient) {
      return this.addIngredient(ingredient, 1);
    }

    public Mixing addIngredient(final Ingredient ingredient, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.ingredients.add(ingredient);
      }

      return this;
    }

    public Mixing fluid(final FluidStack fluid) {
      this.fluid = fluid;
      return this;
    }

    public Mixing addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Mixing setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished) {
      this.build(finished, this.result.getRegistryName());
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      final ResourceLocation name = this.result.getRegistryName();
      if(new ResourceLocation(save).equals(name)) {
        throw new IllegalStateException("Mixing Recipe " + save + " should remove its 'save' argument");
      }

      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Result(save, this.stages, this.ticks, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.fluid, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + this.result.getGroup().getPath() + '/' + save.getPath())));
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
      private final FluidStack fluid;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      protected Result(final ResourceLocation id, final Set<Stage> stages, final int ticks, final Item result, final int count, final String group, final List<Ingredient> ingredients, final FluidStack fluid, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.stages = stages;
        this.ticks = ticks;
        this.result = result;
        this.count = count;
        this.group = group;
        this.ingredients = ingredients;
        this.fluid = fluid;
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

        final JsonObject fluid = new JsonObject();
        fluid.addProperty("fluid", this.fluid.getFluid().getRegistryName().toString());
        fluid.addProperty("amount", this.fluid.getAmount());
        json.add("fluid", fluid);

        final JsonObject result = new JsonObject();
        result.addProperty("item", this.result.getRegistryName().toString());

        if(this.count > 1) {
          result.addProperty("count", this.count);
        }

        json.add("result", result);
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return GradientRecipeSerializers.MIXING.get();
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

  public static class Drying {
    private final Item result;
    private final int count;
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    protected Drying(final IItemProvider item, final int amount) {
      this.result = item.asItem();
      this.count = amount;
    }

    public Drying stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public Drying ticks(final int ticks) {
      this.ticks = ticks;
      return this;
    }

    public Drying addIngredient(final Tag<Item> tag) {
      return this.addIngredient(Ingredient.fromTag(tag));
    }

    public Drying addIngredient(final IItemProvider item) {
      return this.addIngredient(item, 1);
    }

    public Drying addIngredient(final IItemProvider item, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.addIngredient(Ingredient.fromItems(item));
      }

      return this;
    }

    public Drying addIngredient(final Ingredient ingredient) {
      return this.addIngredient(ingredient, 1);
    }

    public Drying addIngredient(final Ingredient ingredient, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.ingredients.add(ingredient);
      }

      return this;
    }

    public Drying addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Drying setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished) {
      this.build(finished, this.result.getRegistryName());
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      final ResourceLocation name = this.result.getRegistryName();
      if(new ResourceLocation(save).equals(name)) {
        throw new IllegalStateException("Drying Recipe " + save + " should remove its 'save' argument");
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
        return GradientRecipeSerializers.DRYING.get();
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

  public static class Melting {
    private final Set<Stage> stages = new HashSet<>();
    private int ticks;
    private float temperature;
    private Ingredient ingredient;
    private FluidStack fluid = FluidStack.EMPTY;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    public Melting stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public Melting ticks(final int ticks) {
      this.ticks = ticks;
      return this;
    }

    public Melting temperature(final float temperature) {
      this.temperature = temperature;
      return this;
    }

    public Melting ingredient(final Tag<Item> tag) {
      return this.ingredient(Ingredient.fromTag(tag));
    }

    public Melting ingredient(final IItemProvider item) {
      this.ingredient(Ingredient.fromItems(item));
      return this;
    }

    public Melting ingredient(final Ingredient ingredient) {
      this.ingredient = ingredient;
      return this;
    }

    public Melting fluid(final FluidStack fluid) {
      this.fluid = fluid;
      return this;
    }

    public Melting addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Melting setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Result(save, this.stages, this.ticks, this.temperature, this.group == null ? "" : this.group, this.ingredient, this.fluid, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + save.getPath())));
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
      private final String group;
      private final Ingredient ingredient;
      private final FluidStack output;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      protected Result(final ResourceLocation id, final Set<Stage> stages, final int ticks, final float temperature, final String group, final Ingredient ingredient, final FluidStack output, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.stages = stages;
        this.ticks = ticks;
        this.temperature = temperature;
        this.group = group;
        this.ingredient = ingredient;
        this.output = output;
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
        json.add("ingredient", this.ingredient.serialize());
        json.add("fluid", RecipeUtils.writeFluidStackToJson(new JsonObject(), this.output));
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return GradientRecipeSerializers.MELTING.get();
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

  public static class Alloy {
    private final FluidStack output;
    private final NonNullList<FluidStack> inputs = NonNullList.create();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    public Alloy(final FluidStack output) {
      this.output = output;
    }

    public Alloy addInput(final FluidStack stack) {
      this.inputs.add(stack);
      return this;
    }

    public Alloy addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public Alloy setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new Result(save, this.group == null ? "" : this.group, this.output, this.inputs, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + save.getPath())));
    }

    private void validate(final ResourceLocation name) {
      if(this.advancementBuilder.getCriteria().isEmpty()) {
        throw new IllegalStateException("No way of obtaining recipe " + name);
      }
    }

    public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final FluidStack output;
      private final NonNullList<FluidStack> inputs;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      protected Result(final ResourceLocation id, final String group, final FluidStack output, final NonNullList<FluidStack> inputs, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.group = group;
        this.output = output;
        this.inputs = inputs;
        this.advancementBuilder = advancementBuilder;
        this.advancementId = advancementId;
      }

      @Override
      public void serialize(final JsonObject json) {
        if(!this.group.isEmpty()) {
          json.addProperty("group", this.group);
        }

        json.add("output", RecipeUtils.writeFluidStackToJson(new JsonObject(), this.output));

        final JsonArray inputs = new JsonArray();
        for(final FluidStack input : this.inputs) {
          inputs.add(RecipeUtils.writeFluidStackToJson(new JsonObject(), input));
        }
        json.add("inputs", inputs);
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return GradientRecipeSerializers.ALLOY.get();
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

  public static class ShapelessToolStation {
    private final Set<Stage> stages = new HashSet<>();
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<ToolType> tools = new ArrayList<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    public ShapelessToolStation stage(final Stage stage) {
      this.stages.add(stage);
      return this;
    }

    public ShapelessToolStation addIngredient(final Tag<Item> tag) {
      return this.addIngredient(Ingredient.fromTag(tag));
    }

    public ShapelessToolStation addIngredient(final IItemProvider item) {
      return this.addIngredient(item, 1);
    }

    public ShapelessToolStation addIngredient(final IItemProvider item, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.addIngredient(Ingredient.fromItems(item));
      }

      return this;
    }

    public ShapelessToolStation addIngredient(final Ingredient ingredient) {
      return this.addIngredient(ingredient, 1);
    }

    public ShapelessToolStation addIngredient(final Ingredient ingredient, final int amount) {
      for(int i = 0; i < amount; ++i) {
        this.ingredients.add(ingredient);
      }

      return this;
    }

    public ShapelessToolStation addToolType(final ToolType toolType) {
      this.tools.add(toolType);
      return this;
    }

    public ShapelessToolStation addOutput(final ItemStack output) {
      this.outputs.add(output);
      return this;
    }

    public ShapelessToolStation addCriterion(final String key, final ICriterionInstance criterion) {
      this.advancementBuilder.withCriterion(key, criterion);
      return this;
    }

    public ShapelessToolStation setGroup(final String group) {
      this.group = group;
      return this;
    }

    public void build(final Consumer<IFinishedRecipe> finished, final String save) {
      this.build(finished, new ResourceLocation(save));
    }

    public void build(final Consumer<IFinishedRecipe> finished, final ResourceLocation save) {
      this.validate(save);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(save)).withRewards(AdvancementRewards.Builder.recipe(save)).withRequirementsStrategy(IRequirementsStrategy.OR);
      finished.accept(new ShapelessToolStation.Result(save, this.stages, this.group == null ? "" : this.group, this.ingredients, this.tools, this.outputs, this.advancementBuilder, new ResourceLocation(save.getNamespace(), "recipes/" + this.outputs.get(0).getItem().getGroup().getPath() + '/' + save.getPath())));
    }

    private void validate(final ResourceLocation name) {
      if(this.advancementBuilder.getCriteria().isEmpty()) {
        throw new IllegalStateException("No way of obtaining recipe " + name);
      }
    }

    public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Set<Stage> stages;
      private final String group;
      private final List<Ingredient> ingredients;
      private final List<ToolType> tools;
      private final List<ItemStack> outputs;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      protected Result(final ResourceLocation id, final Set<Stage> stages, final String group, final List<Ingredient> ingredients, final List<ToolType> tools, final List<ItemStack> outputs, final Advancement.Builder advancementBuilder, final ResourceLocation advancementId) {
        this.id = id;
        this.stages = stages;
        this.group = group;
        this.ingredients = ingredients;
        this.tools = tools;
        this.outputs = outputs;
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

        final JsonArray ingredients = new JsonArray();
        for(final Ingredient ingredient : this.ingredients) {
          ingredients.add(ingredient.serialize());
        }

        json.add("ingredients", ingredients);

        final JsonArray tools = new JsonArray();
        for(final ToolType toolType : this.tools) {
          tools.add(toolType.getName());
        }

        json.add("tools", tools);

        final JsonArray outputs = new JsonArray();
        for(final ItemStack output : this.outputs) {
          final JsonObject obj = new JsonObject();
          obj.addProperty("item", output.getItem().getRegistryName().toString());

          if(output.getCount() > 1) {
            obj.addProperty("count", output.getCount());
          }

          outputs.add(obj);
        }

        json.add("outputs", outputs);
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return GradientRecipeSerializers.SHAPELESS_TOOL_STATION.get();
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
