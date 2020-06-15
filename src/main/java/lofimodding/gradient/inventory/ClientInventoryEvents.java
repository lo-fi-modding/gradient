package lofimodding.gradient.inventory;

import lofimodding.gradient.Gradient;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID, value = Dist.CLIENT)
public final class ClientInventoryEvents {
  private ClientInventoryEvents() { }

  static {
    ContainerScreen.INVENTORY_BACKGROUND = Gradient.loc("textures/gui/inventory_3x3.png");
  }

  @SubscribeEvent
  public static void hideCraftingBookButton(final GuiScreenEvent.InitGuiEvent event) {
    if(event.getGui() instanceof InventoryScreen) {
      for(final Widget widget : event.getWidgetList()) {
        if(widget instanceof ImageButton) {
          event.removeWidget(widget);
          return;
        }
      }
    }
  }
}
