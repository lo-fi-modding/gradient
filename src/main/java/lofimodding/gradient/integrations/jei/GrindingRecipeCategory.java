package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.recipes.GrindingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class GrindingRecipeCategory implements IRecipeCategory<GrindingRecipe> {
  private static final ResourceLocation BACKGROUND_LOCATION = Gradient.loc("textures/gui/recipe_grinding.png");
  private final IGuiHelper guiHelper;

  public GrindingRecipeCategory(final IGuiHelper guiHelper) {
    this.guiHelper = guiHelper;
  }

  @Override
  public ResourceLocation getUid() {
    return GradientRecipeSerializers.GRINDING.getId();
  }

  @Override
  public Class<? extends GrindingRecipe> getRecipeClass() {
    return GrindingRecipe.class;
  }

  @Override
  public String getTitle() {
    return GradientBlocks.GRINDSTONE.get().getNameTextComponent().getFormattedText();
  }

  @Override
  public IDrawable getBackground() {
    return this.guiHelper.createDrawable(BACKGROUND_LOCATION, 0, 0, 166, 68);
  }

  @Override
  public IDrawable getIcon() {
    return this.guiHelper.createDrawableIngredient(new ItemStack(GradientItems.GRINDSTONE.get()));
  }

  @Override
  public void setIngredients(final GrindingRecipe recipe, final IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final GrindingRecipe recipe, final IIngredients ingredients) {
    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);

    guiItemStacks.init(0, true, 8, 25);
    guiItemStacks.set(0, inputs.get(0));

    guiItemStacks.init(1, true, 46, 25);
    guiItemStacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
  }

  @Override
  public void draw(final GrindingRecipe recipe, final double mouseX, final double mouseY) {
    final FontRenderer font = Minecraft.getInstance().fontRenderer;

    //TODO
//    final String age = I18n.format("jei.age." + recipe.age.value());
//    final String requirement = I18n.format("jei.requirement.age", age);
//    font.drawString(requirement, 9, 8, 0x404040);
//
//    font.drawString(I18n.format("jei.grinder.passes", recipe.passes), 9, 46, 0x404040);
    font.drawString(I18n.format("jei.grinder.ticks", recipe.getTicks()), 9, 60, 0x404040);
  }
}
