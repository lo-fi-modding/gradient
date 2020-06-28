package lofimodding.gradient.integrations.jei;

import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.GradientRecipeSerializers;
import lofimodding.gradient.recipes.ShapelessToolStationRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolStationRecipeCategory implements IRecipeCategory<ShapelessToolStationRecipe> {
  private static final ResourceLocation BACKGROUND_LOCATION = Gradient.loc("textures/gui/recipe_tool_station.png");
  private static final Map<ToolType, IDrawable> TOOL_ICONS = new HashMap<>();
  private final IGuiHelper guiHelper;

  public ToolStationRecipeCategory(final IGuiHelper guiHelper) {
    this.guiHelper = guiHelper;
  }

  @Override
  public ResourceLocation getUid() {
    return GradientRecipeSerializers.SHAPELESS_TOOL_STATION.getId();
  }

  @Override
  public Class<? extends ShapelessToolStationRecipe> getRecipeClass() {
    return ShapelessToolStationRecipe.class;
  }

  @Override
  public String getTitle() {
    return I18n.format("jei.tool_station.name");
  }

  @Override
  public IDrawable getBackground() {
    return this.guiHelper.createDrawable(BACKGROUND_LOCATION, 0, 0, 150, 90);
  }

  @Override
  public IDrawable getIcon() {
    return this.guiHelper.createDrawableIngredient(new ItemStack(GradientItems.TOOL_STATION.get()));
  }

  @Override
  public void setIngredients(final ShapelessToolStationRecipe recipe, final IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputs());
  }

  @Override
  public void setRecipe(final IRecipeLayout recipeLayout, final ShapelessToolStationRecipe recipe, final IIngredients ingredients) {
    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
    final List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

    for(int slot = 0; slot < recipe.getIngredients().size(); slot++) {
      final int x = slot % 5;
      final int y = slot / 5;

      guiItemStacks.init(slot, true, 22 + x * 18, y * 18);
      guiItemStacks.set(slot, inputs.get(slot));
    }

    int slotIndex = 5 * 5;

    for(int slot = 0; slot < recipe.getOutputs().size(); slot++) {
      guiItemStacks.init(slotIndex, false, 132, 18 + slot * 18);
      guiItemStacks.set(slotIndex, outputs.get(slot));
      slotIndex++;
    }
  }

  @Override
  public void draw(final ShapelessToolStationRecipe recipe, final double mouseX, final double mouseY) {
    for(int slot = 0; slot < recipe.getTools().size(); slot++) {
      final ToolType type = recipe.getTools().get(slot);
      final IDrawable drawable = TOOL_ICONS.computeIfAbsent(type, key -> this.guiHelper.drawableBuilder(Gradient.loc("textures/gui/tool_type_" + key.getName() + ".png"), 0, 0, 16, 16).setTextureSize(16, 16).build());

      drawable.draw(1, 1 + slot * 18);
    }
  }

  @Override
  public List<String> getTooltipStrings(final ShapelessToolStationRecipe recipe, final double mouseX, final double mouseY) {
    final List<String> tooltip = new ArrayList<>();

    for(int slot = 0; slot < recipe.getTools().size(); slot++) {
      if(mouseX >= 0 && mouseX < 18) {
        if(mouseY >= slot * 18 && mouseY < (slot + 1) * 18) {
          tooltip.add(I18n.format("jei.tool_station.tool." + recipe.getTools().get(slot).getName()));
        }
      }
    }

    return tooltip;
  }
}
