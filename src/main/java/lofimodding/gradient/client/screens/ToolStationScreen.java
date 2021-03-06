package lofimodding.gradient.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.containers.GradientContainer;
import lofimodding.gradient.containers.ToolStationContainer;
import lofimodding.gradient.tileentities.ToolStationTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class ToolStationScreen extends ContainerScreen<ToolStationContainer> {
  private static final ResourceLocation BG_TEXTURE = Gradient.loc("textures/gui/tool_station.png");
  private final ToolStationContainer container;
  private final ToolStationTile tile;

  private final List<String> problems = new ArrayList<>();
  private boolean tooSmall;
  private boolean missingTools;
  private boolean missingIngredients;

  public ToolStationScreen(final ToolStationContainer container, final PlayerInventory inv, final ITextComponent title) {
    super(container, inv, title);
    this.ySize = container.height;
    this.container = container;
    this.tile = container.tile;
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.problems.clear();

    final ToolStationTile tile = this.container.tile;

    this.tooSmall = false;
    this.missingTools = false;
    this.missingIngredients = false;

    if(tile.hasRecipe()) {
      if(!tile.canFit()) {
        this.problems.add(I18n.format(GradientBlocks.TOOL_STATION.get().getTranslationKey() + ".too_small"));
        this.tooSmall = true;
      }

      if(!tile.hasRequiredTools()) {
        this.problems.add(I18n.format(GradientBlocks.TOOL_STATION.get().getTranslationKey() + ".missing_tools"));
        this.missingTools = true;
      }

      if(tile.hasRequiredIngredients(1) == 0) {
        this.problems.add(I18n.format(GradientBlocks.TOOL_STATION.get().getTranslationKey() + ".missing_ingredients"));
        this.missingIngredients = true;
      }
    }

    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    this.minecraft.getTextureManager().bindTexture(BG_TEXTURE);
    final int x = (this.width  - this.xSize) / 2;
    final int y = (this.height - this.ySize) / 2;
    innerBlit(x, x + this.xSize, y, y + 4, this.getBlitOffset(), 0.0f, this.xSize / 256.0f, 0.0f, 4.0f / 256.0f);
    innerBlit(x, x + this.xSize, y + 4, y + this.ySize - 85, this.getBlitOffset(), 0.0f, this.xSize / 256.0f, 4.0f / 256.0f, 5.0f / 256.0f);
    innerBlit(x, x + this.xSize, y + this.ySize - 85, y + this.ySize - 1, this.getBlitOffset(), 0.0f, this.xSize / 256.0f, 5.0f / 256.0f, 89.0f / 256.0f);

    final int size = this.tile.getCraftingSize();

    // Recipe input
    for(int slot = 0; slot < this.tile.getRecipeInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + GradientContainer.SLOT_X_SPACING + 4 + slot % size * GradientContainer.SLOT_X_SPACING - 1, y + this.container.recipeY + slot / size * GradientContainer.SLOT_Y_SPACING - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }

    this.blit(x + GradientContainer.INV_SLOTS_X + GradientContainer.SLOT_X_SPACING + 4 + size * GradientContainer.SLOT_X_SPACING, y + this.container.recipeY + (size - 1) * GradientContainer.SLOT_Y_SPACING / 2 + 2, 176, 28, 16, 13);

    if(this.tooSmall) {
      RenderSystem.color3f(1.0f, 0.7f, 0.7f);
    }

    // Output
    for(int slot = 0; slot < this.tile.getOutputInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + GradientContainer.SLOT_X_SPACING + 4 + (size + 1) * GradientContainer.SLOT_X_SPACING - 1, y + this.container.recipeY + (size - this.tile.getOutputInv().getSlots()) * GradientContainer.SLOT_Y_SPACING / 2 + slot * GradientContainer.SLOT_Y_SPACING - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }

    RenderSystem.color3f(1.0f, 1.0f, 1.0f);

    if(this.missingTools) {
      RenderSystem.color3f(1.0f, 0.7f, 0.7f);
    }

    // Tools
    for(int slot = 0; slot < this.tile.getToolsInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X - 1, y + this.container.recipeY + slot * GradientContainer.SLOT_Y_SPACING - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);

      if(this.tile.getToolsInv().getStackInSlot(slot).isEmpty()) {
        this.blit(x + GradientContainer.INV_SLOTS_X - 1, y + this.container.recipeY + slot * GradientContainer.SLOT_Y_SPACING - 1, 194, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
      }
    }

    RenderSystem.color3f(1.0f, 1.0f, 1.0f);

    if(this.missingIngredients) {
      RenderSystem.color3f(1.0f, 0.7f, 0.7f);
    }

    // Storage
    for(int slot = 0; slot < this.tile.getStorageInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + slot % 9 * GradientContainer.SLOT_X_SPACING - 1, y + this.container.storageY + slot / 9 * GradientContainer.SLOT_X_SPACING - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }

    RenderSystem.color3f(1.0f, 1.0f, 1.0f);

    if(!this.problems.isEmpty()) {
      this.blit(x + this.xSize - 14, y + 4, 176, 18, 10, 10);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    final String text = this.getTitle().getFormattedText();
    this.font.drawString(I18n.format(GradientBlocks.TOOL_STATION.get().getTranslationKey() + ".storage"), 8.0f, this.container.storageY - 10, 0x404040);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, this.ySize - 96 + 3, 0x404040);
  }

  @Override
  protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
    if(!this.problems.isEmpty()) {
      final int x = (this.width  - this.xSize) / 2;
      final int y = (this.height - this.ySize) / 2;

      if(mouseX >= x + this.xSize - 14 && mouseX <  x + this.xSize - 4) {
        if(mouseY >= y + 4 && mouseY < y + 14) {
          this.renderTooltip(this.problems, mouseX, mouseY);
        }
      }
    }

    super.renderHoveredToolTip(mouseX, mouseY);
  }
}
