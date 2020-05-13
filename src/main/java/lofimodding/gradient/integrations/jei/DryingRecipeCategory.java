package lofimodding.gradient.integrations.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.recipes.DryingRecipe;
import lofimodding.progression.Stage;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.List;

public class DryingRecipeCategory implements IRecipeCategory<DryingRecipe> {
  private static final ResourceLocation BACKGROUND_LOCATION = Gradient.loc("textures/gui/recipe_drying.png");
  private final IGuiHelper guiHelper;

  public DryingRecipeCategory(final IGuiHelper guiHelper) {
    this.guiHelper = guiHelper;
  }

  @Override
  public ResourceLocation getUid() {
    return GradientRecipeSerializers.DRYING.getId();
  }

  @Override
  public Class<? extends DryingRecipe> getRecipeClass() {
    return DryingRecipe.class;
  }

  @Override
  public String getTitle() {
    return I18n.format("jei.drying.name");
  }

  @Override
  public IDrawable getBackground() {
    return this.guiHelper.createDrawable(BACKGROUND_LOCATION, 0, 0, 116, 43);
  }

  @Override
  public IDrawable getIcon() {
    return this.guiHelper.createDrawableIngredient(new ItemStack(GradientItems.DRYING_RACK.get()));
  }

  @Override
  public void setIngredients(final DryingRecipe recipe, final IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final DryingRecipe recipe, final IIngredients ingredients) {
    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);

    guiItemStacks.init(0, true, 0, 0);
    guiItemStacks.set(0, inputs.get(0));

    guiItemStacks.init(1, true, 38, 0);
    guiItemStacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
  }

  @Override
  public void draw(final DryingRecipe recipe, final double mouseX, final double mouseY) {
    final FontRenderer font = Minecraft.getInstance().fontRenderer;

    font.drawString(I18n.format("jei.drying.ticks", recipe.getTicks()), 1, 22, 0x404040);

    RenderSystem.pushMatrix();
    RenderSystem.translatef(103.0f, 31.0f, 0.0f);
    RenderSystem.scalef(0.75f, 0.75f, 0.0f);

    for(final Stage stage : recipe.getStages()) {
      Minecraft.getInstance().textureManager.bindTexture(stage.getIcon());
      AbstractGui.blit(0, 0, 0, 0.0f, 0.0f, 16, 16, 16, 16);
      RenderSystem.translatef(-16.0f, 0.0f, 0.0f);
    }

    RenderSystem.popMatrix();
  }

  @Override
  public List<String> getTooltipStrings(final DryingRecipe recipe, final double mouseX, final double mouseY) {
    if(mouseY >= 31 && mouseY <= 43) {
      for(int i = 0; i < recipe.getStages().size(); i++) {
        if(mouseX >= 103 - i * 16 && mouseX <= 115 - i * 16) {
          final ITextComponent stage = recipe.getStages().get(i).getName();
          final ITextComponent requirement = new TranslationTextComponent("jei.stage.requirement", stage);
          return Lists.newArrayList(requirement.getFormattedText());
        }
      }
    }

    return Collections.emptyList();
  }
}
