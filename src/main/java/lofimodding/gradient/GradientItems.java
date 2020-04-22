package lofimodding.gradient;

import lofimodding.gradient.items.MetalItem;
import lofimodding.gradient.items.MulchItem;
import lofimodding.gradient.items.PebbleItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class GradientItems {
  private GradientItems() { }

  private static final DeferredRegister<Item> REGISTRY = new DeferredRegister<>(ForgeRegistries.ITEMS, Gradient.MOD_ID);

  private static final ItemGroup GROUP = new GradientItemGroup();

  public static final RegistryObject<PebbleItem> PEBBLE = REGISTRY.register(GradientIds.PEBBLE, () -> new PebbleItem(new Item.Properties().group(GROUP)));

  private static final Map<Ore, RegistryObject<BlockItem>> ORES = new HashMap<>();

  static {
    for(final Ore ore : Ores.all()) {
      ORES.put(ore, REGISTRY.register(GradientIds.ORE(ore), () -> new BlockItem(GradientBlocks.ORE(ore).get(), new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalItem>> CRUSHED = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      CRUSHED.put(metal, REGISTRY.register(GradientIds.CRUSHED(metal), () -> new MetalItem(metal, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalItem>> PURIFIED = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      PURIFIED.put(metal, REGISTRY.register(GradientIds.PURIFIED(metal), () -> new MetalItem(metal, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalItem>> DUSTS = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      DUSTS.put(metal, REGISTRY.register(GradientIds.DUST(metal), () -> new MetalItem(metal, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalItem>> INGOTS = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      INGOTS.put(metal, REGISTRY.register(GradientIds.INGOT(metal), () -> new MetalItem(metal, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalItem>> NUGGETS = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      NUGGETS.put(metal, REGISTRY.register(GradientIds.NUGGET(metal), () -> new MetalItem(metal, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<MetalItem>> PLATES = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      PLATES.put(metal, REGISTRY.register(GradientIds.PLATE(metal), () -> new MetalItem(metal, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<Metal, RegistryObject<BlockItem>> METAL_BLOCKS = new HashMap<>();

  static {
    for(final Metal metal : Metals.all()) {
      METAL_BLOCKS.put(metal, REGISTRY.register(GradientIds.METAL_BLOCK(metal), () -> new BlockItem(GradientBlocks.METAL_BLOCK(metal).get(), new Item.Properties().group(GROUP))));
    }
  }

  public static final RegistryObject<BlockItem> SALT_BLOCK = REGISTRY.register(GradientIds.SALT_BLOCK, () -> new BlockItem(GradientBlocks.SALT_BLOCK.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> SALT = REGISTRY.register(GradientIds.SALT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> FIBRE = REGISTRY.register(GradientIds.FIBRE, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> TWINE = REGISTRY.register(GradientIds.TWINE, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> BARK = REGISTRY.register(GradientIds.BARK, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<MulchItem> MULCH = REGISTRY.register(GradientIds.MULCH, () -> new MulchItem(new Item.Properties().group(GROUP)));

  public static final RegistryObject<BlockItem> GRINDSTONE = REGISTRY.register(GradientIds.GRINDSTONE, () -> new BlockItem(GradientBlocks.GRINDSTONE.get(), new Item.Properties().group(GROUP)));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering items...");
    REGISTRY.register(bus);
  }

  public static RegistryObject<BlockItem> ORE(final Ore ore) {
    return ORES.get(ore);
  }

  public static RegistryObject<MetalItem> CRUSHED(final Metal metal) {
    return CRUSHED.get(metal);
  }

  public static RegistryObject<MetalItem> PURIFIED(final Metal metal) {
    return PURIFIED.get(metal);
  }

  public static RegistryObject<MetalItem> DUST(final Metal metal) {
    return DUSTS.get(metal);
  }

  public static RegistryObject<MetalItem> INGOT(final Metal metal) {
    return INGOTS.get(metal);
  }

  public static RegistryObject<MetalItem> NUGGET(final Metal metal) {
    return NUGGETS.get(metal);
  }

  public static RegistryObject<MetalItem> PLATE(final Metal metal) {
    return PLATES.get(metal);
  }

  public static RegistryObject<BlockItem> METAL_BLOCK(final Metal metal) {
    return METAL_BLOCKS.get(metal);
  }

  private static final class GradientItemGroup extends ItemGroup {
    public GradientItemGroup() {
      super(Gradient.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
      return ItemStack.EMPTY; //TODO
    }
  }
}
