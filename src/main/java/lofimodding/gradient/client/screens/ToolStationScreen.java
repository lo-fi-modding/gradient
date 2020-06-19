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

public class ToolStationScreen extends ContainerScreen<ToolStationContainer> {
  private static final ResourceLocation BG_TEXTURE = Gradient.loc("textures/gui/tool_station.png");
  private final ToolStationContainer container;
  private final ToolStationTile tile;

  public ToolStationScreen(final ToolStationContainer container, final PlayerInventory inv, final ITextComponent title) {
    super(container, inv, title);
    this.ySize = container.height;
    this.container = container;
    this.tile = container.tile;
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
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
    for(int slot = 0; slot < this.tile.getMergedRecipeInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + slot % size * GradientContainer.SLOT_X_SPACING - 1, y + this.container.recipeY + slot / size * GradientContainer.SLOT_Y_SPACING - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }

    // Output
    for(int slot = 0; slot < this.tile.getMergedOutputInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + (size + 1) * GradientContainer.SLOT_X_SPACING - 1, y + this.container.recipeY + (size - 1) * GradientContainer.SLOT_Y_SPACING / 2 - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }

    // Tools
    for(int slot = 0; slot < this.tile.getMergedToolsInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + slot * GradientContainer.SLOT_X_SPACING - 1, y + this.container.toolsY - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }

    // Storage
    for(int slot = 0; slot < this.tile.getMergedStorageInv().getSlots(); slot++) {
      this.blit(x + GradientContainer.INV_SLOTS_X + slot % 9 * GradientContainer.SLOT_X_SPACING - 1, y + this.container.storageY - 1, 176, 0, GradientContainer.SLOT_X_SPACING, GradientContainer.SLOT_Y_SPACING);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    final String text = this.getTitle().getFormattedText();
    this.font.drawString(text, (this.xSize - this.font.getStringWidth(text)) / 2.0f, 7.0f, 0x404040);
    this.font.drawString(I18n.format(GradientBlocks.TOOL_STATION.get().getTranslationKey() + ".tools"), 8.0f, this.container.toolsY - 10, 0x404040);
    this.font.drawString(I18n.format(GradientBlocks.TOOL_STATION.get().getTranslationKey() + ".storage"), 8.0f, this.container.storageY - 10, 0x404040);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, this.ySize - 96 + 3, 0x404040);
  }
}
