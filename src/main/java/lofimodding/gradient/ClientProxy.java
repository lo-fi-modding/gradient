package lofimodding.gradient;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements Proxy {
  @Override
  public PlayerEntity getPlayerOrFakePlayer(World world) {
    return Minecraft.getInstance().player;
  }
}
