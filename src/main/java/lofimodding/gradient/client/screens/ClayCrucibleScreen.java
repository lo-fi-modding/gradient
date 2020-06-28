package lofimodding.gradient.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.client.screens.widgets.FluidWidget;
import lofimodding.gradient.containers.ClayCrucibleContainer;
import lofimodding.gradient.containers.GradientContainer;
import lofimodding.gradient.tileentities.ClayCrucibleTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ClayCrucibleScreen extends ContainerScreen<ClayCrucibleContainer> {
  private static final ResourceLocation BG_TEXTURE = Gradient.loc("textures/gui/clay_crucible.png");

  private final ClayCrucibleTile te;
  private final PlayerInventory playerInv;

  public ClayCrucibleScreen(final ClayCrucibleContainer container, final PlayerInventory playerInv, final ITextComponent title) {
    super(container, playerInv, title);
    this.te = container.tile;
    this.playerInv = playerInv;
  }

  @Override
  protected void init() {
    super.init();

    final int x = (this.width  - this.xSize) / 2;
    final int y = (this.height - this.ySize) / 2;
    this.addButton(new FluidWidget(this, this.te.tank, x + 148, y + 19, 12, 47));
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
    this.blit(x, y, 0, 0, this.xSize, this.ySize);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    for(int slot = 0; slot < ClayCrucibleTile.METAL_SLOTS_COUNT; slot++) {
      if(this.te.isMelting(slot)) {
        final int x = ClayCrucibleContainer.METAL_SLOTS_X + slot % 5 * (GradientContainer.SLOT_X_SPACING + 8) + 20;
        final int y = ClayCrucibleContainer.METAL_SLOTS_Y + slot / 5 * (GradientContainer.SLOT_Y_SPACING + 2);
        final float percent = this.te.getMeltingMetal(slot).meltPercent();

        fill(x, (int)(y + percent * 16), x + 2, y + 16, 0xFF01FE00);
      }
    }

    final String name = I18n.format(GradientBlocks.CLAY_CRUCIBLE.get().getTranslationKey());
    final String heat = I18n.format(GradientBlocks.FIREPIT.get().getTranslationKey() + ".heat", (int)this.te.getHeat());

    this.font.drawString(name, (this.xSize - this.font.getStringWidth(name)) / 2, 6, 0x404040);
    this.font.drawString(this.playerInv.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 94, 0x404040);

    this.font.drawString(heat, ClayCrucibleContainer.FUEL_SLOTS_X, 58, 0x404040);
  }

  @Override
  protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
    super.renderHoveredToolTip(mouseX, mouseY);

    for(final Widget widget : this.buttons) {
      if(widget.isMouseOver(mouseX, mouseY)) {
        widget.renderToolTip(mouseX, mouseY);
      }
    }
  }
}
