package lofimodding.gradient.integrations.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.recipes.MixingRecipe;
import lofimodding.gradient.tileentities.MixingBasinTile;
import lofimodding.progression.Stage;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
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

public class MixingRecipeCategory implements IRecipeCategory<MixingRecipe> {
  private static final ResourceLocation BACKGROUND_LOCATION = Gradient.loc("textures/gui/recipe_mixing.png");
  private final IGuiHelper guiHelper;

  public MixingRecipeCategory(final IGuiHelper guiHelper) {
    this.guiHelper = guiHelper;
  }

  @Override
  public ResourceLocation getUid() {
    return GradientRecipeSerializers.MIXING.getId();
  }

  @Override
  public Class<? extends MixingRecipe> getRecipeClass() {
    return MixingRecipe.class;
  }

  @Override
  public String getTitle() {
    return I18n.format("jei.mixing.name");
  }

  @Override
  public IDrawable getBackground() {
    return this.guiHelper.createDrawable(BACKGROUND_LOCATION, 0, 0, 156, 43);
  }

  @Override
  public IDrawable getIcon() {
    return this.guiHelper.createDrawableIngredient(new ItemStack(GradientItems.MIXING_BASIN.get()));
  }

  @Override
  public void setIngredients(final MixingRecipe recipe, final IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setInput(VanillaTypes.FLUID, recipe.getFluidOutput(0));
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getItemOutput(0));
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final MixingRecipe recipe, final IIngredients ingredients) {
    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
    final List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);

    for(int slot = 0; slot < Math.min(MixingBasinTile.INPUT_SIZE, inputs.size()); slot++) {
      guiItemStacks.init(slot, true, slot * 20, 0);
      guiItemStacks.set(slot, inputs.get(slot));
    }

    guiFluidStacks.init(MixingBasinTile.INPUT_SIZE + 1, true, 101, 1);
    guiFluidStacks.set(MixingBasinTile.INPUT_SIZE + 1, ingredients.getInputs(VanillaTypes.FLUID).get(0));

    guiItemStacks.init(MixingBasinTile.INPUT_SIZE + 2, true, 138, 0);
    guiItemStacks.set(MixingBasinTile.INPUT_SIZE + 2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
  }

  @Override
  public void draw(final MixingRecipe recipe, final double mouseX, final double mouseY) {
    final FontRenderer font = Minecraft.getInstance().fontRenderer;

    font.drawString(I18n.format("jei.mixing.tier", recipe.getTier()), 1, 23, 0x404040);
    font.drawString(I18n.format("jei.mixing.ticks", recipe.getTicks()), 1, 35, 0x404040);

    RenderSystem.pushMatrix();
    RenderSystem.translatef(143.0f, 31.0f, 0.0f);
    RenderSystem.scalef(0.75f, 0.75f, 0.0f);

    for(final Stage stage : recipe.getStages()) {
      Minecraft.getInstance().textureManager.bindTexture(stage.getIcon());
      AbstractGui.blit(0, 0, 0, 0.0f, 0.0f, 16, 16, 16, 16);
      RenderSystem.translatef(-16.0f, 0.0f, 0.0f);
    }

    RenderSystem.popMatrix();
  }

  @Override
  public List<String> getTooltipStrings(final MixingRecipe recipe, final double mouseX, final double mouseY) {
    if(mouseY >= 31 && mouseY <= 43) {
      for(int i = 0; i < recipe.getStages().size(); i++) {
        if(mouseX >= 143 - i * 16 && mouseX <= 155 - i * 16) {
          final ITextComponent stage = recipe.getStages().get(i).getName();
          final ITextComponent requirement = new TranslationTextComponent("jei.stage.requirement", stage);
          return Lists.newArrayList(requirement.getFormattedText());
        }
      }
    }

    return Collections.emptyList();
  }
}
