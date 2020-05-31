package lofimodding.gradient.client.screens;

import lofimodding.gradient.containers.CreativeGeneratorContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CreativeGeneratorScreen extends ContainerScreen<CreativeGeneratorContainer> {
  public CreativeGeneratorScreen(final CreativeGeneratorContainer container, final PlayerInventory playerInv, final ITextComponent title) {
    super(container, playerInv, title);
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {

  }
}
