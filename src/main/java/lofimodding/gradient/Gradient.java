package lofimodding.gradient;

import lofimodding.gradient.client.GradientClient;
import net.minecraft.block.Blocks;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Gradient.MOD_ID)
public class Gradient {
  public static final String MOD_ID = "gradient";
  public static final Logger LOGGER = LogManager.getLogger();

  private static RecipeManager RECIPE_MANAGER;

  public Gradient() {
    final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    bus.addListener(this::setup);
    bus.addListener(this::enqueueIMC);
    bus.addListener(this::clientSetup);
    bus.addListener(this::serverSetup);

    GradientBlocks.init(bus);
    GradientItems.init(bus);
    GradientEntities.init(bus);
    GradientLoot.init(bus);
    GradientRecipeSerializers.init(bus);
    GradientTileEntities.init(bus);
  }

  private void setup(final FMLCommonSetupEvent event) {
    LOGGER.info("HELLO FROM PREINIT");
    LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    LOGGER.info("Loading client-only features...");
    MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
    GradientClient.clientSetup(event);
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    InterModComms.sendTo("examplemod", "helloworld", () -> {
      LOGGER.info("Hello world from the MDK");
      return "Hello world";
    });
  }

  private void onWorldLoad(final WorldEvent.Load event) {
    // Set the recipe manager for clients
    RECIPE_MANAGER = ((World)event.getWorld()).getRecipeManager();
  }

  // Set the recipe manager for servers
  private void serverSetup(final FMLDedicatedServerSetupEvent event) {
    RECIPE_MANAGER = event.getServerSupplier().get().getRecipeManager();
  }

  public static ResourceLocation loc(final String path) {
    return new ResourceLocation(MOD_ID, path);
  }

  public static RecipeManager getRecipeManager() {
    return RECIPE_MANAGER;
  }
}
