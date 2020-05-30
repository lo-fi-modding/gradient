package lofimodding.gradient.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.containers.WoodenHopperContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class WoodenHopperScreen extends ContainerScreen<WoodenHopperContainer> {
  private static final ResourceLocation HOPPER_GUI_TEXTURE = Gradient.loc("textures/gui/wooden_hopper.png");

  public WoodenHopperScreen(final WoodenHopperContainer container, final PlayerInventory playerInv, final ITextComponent name) {
    super(container, playerInv, name);
    this.passEvents = false;
    this.ySize = 133;
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 0x404040);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, this.ySize - 96 + 2, 0x404040);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
    final int x = (this.width - this.xSize) / 2;
    final int y = (this.height - this.ySize) / 2;
    this.blit(x, y, 0, 0, this.xSize, this.ySize);
  }
}
