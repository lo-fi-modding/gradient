package lofimodding.gradient.inventory;

import lofimodding.gradient.Gradient;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gradient.MOD_ID)
public final class InventoryEvents {
  private static final int CRAFT_SIZE = 3;

  private InventoryEvents() { }

  static {
    ContainerScreen.INVENTORY_BACKGROUND = Gradient.loc("textures/gui/inventory_3x3.png");
  }

  @SubscribeEvent
  public static void modifyPlayerContainer(final EntityJoinWorldEvent event) {
    if(event.getEntity() instanceof PlayerEntity) {
      Gradient.LOGGER.info("Expanding crafting inventory for player {}", event.getEntity().getDisplayName().getFormattedText());

      final PlayerEntity player = (PlayerEntity)event.getEntity();

      if(player.container.craftMatrix.getWidth() == 2) {
        player.container.craftMatrix.stackList = NonNullList.withSize(CRAFT_SIZE * CRAFT_SIZE, ItemStack.EMPTY);
        player.container.craftMatrix.width = CRAFT_SIZE;
        player.container.craftMatrix.height = CRAFT_SIZE;

        for(int y = 0; y < CRAFT_SIZE; ++y) {
          for(int x = 0; x < CRAFT_SIZE; ++x) {
            if(y >= 2 || x >= 2) {
              player.container.addSlot(new Slot(player.container.craftMatrix, x + y * CRAFT_SIZE, 98 + x * 18, 18 + y * 18));
            } else {
              player.container.getSlot(x + y * 2 + 1).slotIndex = x + y * CRAFT_SIZE;
            }
          }
        }
      }
    }
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
