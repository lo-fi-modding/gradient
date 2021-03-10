package lofimodding.gradient;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface Proxy {
  PlayerEntity getPlayerOrFakePlayer(final World world);
}
