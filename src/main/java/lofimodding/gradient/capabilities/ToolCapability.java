package lofimodding.gradient.capabilities;

import lofimodding.progression.ProgressionMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class ToolCapability {
  private ToolCapability() { }

  public static final ResourceLocation ID = ProgressionMod.loc("tool");

  @CapabilityInject(Tool.class)
  public static Capability<Tool> CAPABILITY;

  public static void register() {
    CapabilityManager.INSTANCE.register(Tool.class, new Capability.IStorage<Tool>() {
      @Override
      public INBT writeNBT(final Capability<Tool> capability, final Tool instance, final Direction side) {
        final CompoundNBT tag = new CompoundNBT();
        return tag;
      }

      @Override
      public void readNBT(final Capability<Tool> capability, final Tool instance, final Direction side, final INBT base) {
        if(!(base instanceof CompoundNBT)) {
          return;
        }

      }
    }, Tool::new);
  }
}
