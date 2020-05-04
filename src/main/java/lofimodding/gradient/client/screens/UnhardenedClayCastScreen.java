package lofimodding.gradient.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.client.widgets.ItemButton;
import lofimodding.gradient.network.SwitchCastPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class UnhardenedClayCastScreen extends Screen {
  private static final ResourceLocation TEX_BG = Gradient.loc("textures/gui/clay_cast.png");
  private static final int BG_WIDTH = 175;
  private static final int BG_HEIGHT = 83;

  private int left;
  private int top;

  private final Map<GradientCasts, Button> castButtons = new EnumMap<>(GradientCasts.class);
  @Nullable
  private GradientCasts selected;

  public UnhardenedClayCastScreen(@Nullable final GradientCasts selected) {
    super(new TranslationTextComponent("screens.gradient.unhardened_clay_cast"));
    this.selected = selected;
  }

  @Override
  protected void init() {
    super.init();

    this.castButtons.clear();

    this.left = (this.width - BG_WIDTH) / 2;
    this.top = (this.height - BG_HEIGHT) / 2;

    int x = this.left + 12;
    final int y = this.top + 33;
    for(final GradientCasts cast : GradientCasts.values()) {
      final ItemButton button = new ItemButton(this, x, y, 20, 20, new ItemStack(GradientItems.UNHARDENED_CLAY_CAST(cast).get()), cast.getUnlocalizedName().getFormattedText(), b -> {
        if(this.selected != null) {
          this.castButtons.get(this.selected).active = true;
        }

        b.active = false;
        this.selected = cast;

        SwitchCastPacket.sendToServer(cast);
      });

      if(cast == this.selected) {
        button.active = false;
      }

      this.addButton(button);
      this.castButtons.put(cast, button);

      x += 22;
    }
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.renderBackground();

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    this.bindTexture(TEX_BG);
    this.blit(this.left, this.top, 0, 0, BG_WIDTH, BG_HEIGHT);

    super.render(mouseX, mouseY, partialTicks);

    final String text = this.getTitle().getFormattedText();
    this.font.drawString(text, this.width / 2.0f - this.font.getStringWidth(text) / 2.0f, this.top + 10.0f, 0x404040);

    for(final Widget widget : this.buttons) {
      if(widget.isHovered()) {
        widget.renderToolTip(mouseX, mouseY);
      }
    }
  }

  private void bindTexture(final ResourceLocation loc) {
    this.minecraft.textureManager.bindTexture(loc);
  }
}
