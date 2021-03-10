package lofimodding.gradient;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

public class ServerProxy implements Proxy {
  @Override
  public PlayerEntity getPlayerOrFakePlayer(final World world) {
    return FakePlayerFactory.getMinecraft((ServerWorld)world);
  }
}
