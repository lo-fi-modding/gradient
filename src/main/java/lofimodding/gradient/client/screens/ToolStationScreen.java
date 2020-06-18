package lofimodding.gradient.client.screens;

import lofimodding.gradient.containers.ToolStationContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ToolStationScreen extends ContainerScreen<ToolStationContainer> {
  public ToolStationScreen(final ToolStationContainer screenContainer, final PlayerInventory inv, final ITextComponent title) {
    super(screenContainer, inv, title);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {

  }
}
