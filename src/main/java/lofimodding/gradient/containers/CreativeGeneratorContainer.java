package lofimodding.gradient.containers;

import lofimodding.gradient.GradientContainers;
import lofimodding.gradient.tileentities.CreativeGeneratorTile;
import net.minecraft.entity.player.PlayerInventory;

public class CreativeGeneratorContainer extends GradientContainer {
  public final CreativeGeneratorTile generator;

  public CreativeGeneratorContainer(final int id, final PlayerInventory playerInv, final CreativeGeneratorTile generator) {
    super(GradientContainers.CREATIVE_GENERATOR.get(), id, playerInv, generator);
    this.generator = generator;
  }
}
