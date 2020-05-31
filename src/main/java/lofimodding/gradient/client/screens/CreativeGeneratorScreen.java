package lofimodding.gradient.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import lofimodding.gradient.Gradient;
import lofimodding.gradient.GradientBlocks;
import lofimodding.gradient.containers.CreativeGeneratorContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.text.DecimalFormat;

public class CreativeGeneratorScreen extends ContainerScreen<CreativeGeneratorContainer> {
  private static final ResourceLocation BG_TEXTURE = Gradient.loc("textures/gui/creative_generator.png");

  private static final DecimalFormat ENERGY_FORMAT = new DecimalFormat("#.###");

  public CreativeGeneratorScreen(final CreativeGeneratorContainer container, final PlayerInventory playerInv, final ITextComponent title) {
    super(container, playerInv, title);
  }

  @Override
  protected void init() {
    super.init();

    this.addButton(new Button(this.guiLeft +   4, this.guiTop + 26, 20, 20, "-10", button -> this.container.changeEnergy(-10.0f)));
    this.addButton(new Button(this.guiLeft +  24, this.guiTop + 26, 20, 20, "-1",  button -> this.container.changeEnergy( -1.0f)));
    this.addButton(new Button(this.guiLeft +  44, this.guiTop + 26, 20, 20, "-.1", button -> this.container.changeEnergy( -0.1f)));
    this.addButton(new Button(this.guiLeft +  64, this.guiTop + 26, 20, 20, "+.1", button -> this.container.changeEnergy(  0.1f)));
    this.addButton(new Button(this.guiLeft +  84, this.guiTop + 26, 20, 20, "+1",  button -> this.container.changeEnergy(  1.0f)));
    this.addButton(new Button(this.guiLeft + 104, this.guiTop + 26, 20, 20, "+10", button -> this.container.changeEnergy( 10.0f)));
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
    this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
    final String name = I18n.format(GradientBlocks.CREATIVE_GENERATOR.get().getTranslationKey());
    final String energy = I18n.format(GradientBlocks.CREATIVE_GENERATOR.get().getTranslationKey() + ".energy", ENERGY_FORMAT.format(this.container.getEnergyTransferred()), ENERGY_FORMAT.format(this.container.getEnergyAvailable()));

    this.font.drawString(name, (this.xSize - this.font.getStringWidth(name)) / 2, 6, 0x404040);
    this.font.drawString(energy, (this.xSize - this.font.getStringWidth(name)) / 2, 16, 0x404040);
  }
}
