package lofimodding.gradient;

import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Gradient.MOD_ID)
public class Gradient {
  public static final String MOD_ID = "gradient";
  public static final Logger LOGGER = LogManager.getLogger();

  public Gradient() {
    final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    bus.addListener(this::setup);
    bus.addListener(this::enqueueIMC);
    bus.addListener(this::doClientStuff);

    GradientBlocks.init(bus);
    GradientItems.init(bus);
    GradientLoot.init(bus);
  }

  private void setup(final FMLCommonSetupEvent event) {
    LOGGER.info("HELLO FROM PREINIT");
    LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    InterModComms.sendTo("examplemod", "helloworld", () -> {
      LOGGER.info("Hello world from the MDK");
      return "Hello world";
    });
  }

  public static ResourceLocation loc(final String path) {
    return new ResourceLocation(MOD_ID, path);
  }
}
