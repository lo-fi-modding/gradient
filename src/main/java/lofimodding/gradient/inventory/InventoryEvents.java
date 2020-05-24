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

        player.container.getSlot(0).yPos += 8.0f;

        for(int slotIndex = 0; slotIndex < 4; slotIndex++) {
          final Slot slot = player.container.getSlot(slotIndex + 1);
          slot.xPos = 98 + slotIndex % 3 * 18;
          slot.yPos = 18 + slotIndex / 3 * 18;
          slot.slotIndex = slotIndex;
        }

        for(int slotIndex = 4; slotIndex < CRAFT_SIZE * CRAFT_SIZE; slotIndex++) {
          player.container.addSlot(new Slot(player.container.craftMatrix, slotIndex, 98 + slotIndex % 3 * 18, 18 + slotIndex / 3 * 18));
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
