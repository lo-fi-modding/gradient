package lofimodding.gradient;

import lofimodding.gradient.fluids.MetalFlowingFluid;
import lofimodding.gradient.fluids.MetalSourceFluid;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Minerals;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Util;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class GradientFluids {
  private GradientFluids() { }

  public static final int NUGGET_AMOUNT    =   16;
  public static final int CRUSHED_AMOUNT   =   72;
  public static final int PURIFIED_AMOUNT  =  108;
  public static final int INGOT_AMOUNT     =  144;
  public static final int BLOCK_AMOUNT     = 1296;

  private static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, Gradient.MOD_ID);

  public static final RegistryObject<FlowingFluid> AIR = REGISTRY.register("air", () -> new ForgeFlowingFluid.Source(airProperties()));
  public static final RegistryObject<FlowingFluid> AIR_FLOWING = REGISTRY.register("air_flowing", () -> new ForgeFlowingFluid.Flowing(airProperties()));

  private static final Map<Metal, RegistryObject<MetalSourceFluid>> METALS = Util.make(new HashMap<>(), metals -> {
    for(final Metal metal : Minerals.metals()) {
      metals.put(metal, REGISTRY.register(metal.name, () -> new MetalSourceFluid(metal, metalProperties(metal))));
    }
  });

  private static final Map<Metal, RegistryObject<MetalFlowingFluid>> FLOWING_METALS = Util.make(new HashMap<>(), metals -> {
    for(final Metal metal : Minerals.metals()) {
      metals.put(metal, REGISTRY.register(metal.name, () -> new MetalFlowingFluid(metal, metalProperties(metal))));
    }
  });

  private static ForgeFlowingFluid.Properties airProperties() {
    return new ForgeFlowingFluid.Properties(AIR, AIR_FLOWING, FluidAttributes.builder(Gradient.loc("fluid/air"), Gradient.loc("fluid/air")).viscosity(1).density(1).gaseous());
  }

  private static ForgeFlowingFluid.Properties metalProperties(final Metal metal) {
    return new ForgeFlowingFluid.Properties(METALS.get(metal), FLOWING_METALS.get(metal), FluidAttributes
      .builder(Gradient.loc("fluid/metal_still"), Gradient.loc("fluid/metal_flowing"))
      .viscosity(5000) //TODO
      .density(3000) //TODO
      .color(metal.colourSpecular)
      .temperature((int)(metal.meltTemp + 273.15f))
      .luminosity(9)
    );
  }

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering fluids...");
    REGISTRY.register(bus);
  }

  public static RegistryObject<MetalSourceFluid> METAL(final Metal metal) {
    return METALS.get(metal);
  }
}
