package lofimodding.gradient.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;

public class ItemButton extends Button {
  private final Screen screen;
  private final ItemStack stack;
  private final String tooltip;

  public ItemButton(final Screen screen, final int x, final int y, final int width, final int height, final ItemStack stack, final String tooltip, final IPressable onPress) {
    super(x, y, width, height, "", onPress);
    this.screen = screen;
    this.stack = stack;
    this.tooltip = tooltip;
  }

  @Override
  protected void renderBg(final Minecraft minecraft, final int mouseX, final int mouseY) {
    super.renderBg(minecraft, mouseX, mouseY);
    minecraft.getItemRenderer().renderItemAndEffectIntoGUI(this.stack, this.x + 2, this.y + 2);
  }

  @Override
  public void renderToolTip(final int mouseX, final int mouseY) {
    super.renderToolTip(mouseX, mouseY);

    if(!this.tooltip.isEmpty()) {
      this.screen.renderTooltip(this.tooltip, mouseX, mouseY);
    }
  }
}
