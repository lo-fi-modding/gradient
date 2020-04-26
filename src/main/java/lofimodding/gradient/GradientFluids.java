package lofimodding.gradient;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientFluids {
  private GradientFluids() { }

  private static final DeferredRegister<Fluid> REGISTRY = new DeferredRegister<>(ForgeRegistries.FLUIDS, Gradient.MOD_ID);

  public static final RegistryObject<FlowingFluid> AIR = REGISTRY.register("air", () -> new ForgeFlowingFluid.Source(airProperties()));
  public static final RegistryObject<FlowingFluid> AIR_FLOWING = REGISTRY.register("air_flowing", () -> new ForgeFlowingFluid.Flowing(airProperties()));

  private static ForgeFlowingFluid.Properties airProperties() {
    return new ForgeFlowingFluid.Properties(AIR, AIR_FLOWING, FluidAttributes.builder(Gradient.loc("fluid/air"), Gradient.loc("fluid/air")).viscosity(1).density(1).gaseous());
  }

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering fluids...");
    REGISTRY.register(bus);
  }
}
