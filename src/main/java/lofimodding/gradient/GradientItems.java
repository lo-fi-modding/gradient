package lofimodding.gradient;

import lofimodding.gradient.items.FlintKnifeItem;
import lofimodding.gradient.items.MetalItem;
import lofimodding.gradient.items.MulchItem;
import lofimodding.gradient.items.PebbleItem;
import lofimodding.gradient.items.StoneHammerItem;
import lofimodding.gradient.items.UnhardenedClayCastItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class GradientItems {
  private GradientItems() { }

  private static final DeferredRegister<Item> REGISTRY = new DeferredRegister<>(ForgeRegistries.ITEMS, Gradient.MOD_ID);

  public static final ItemGroup GROUP = new GradientItemGroup();

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
  public static final RegistryObject<Item> COW_PELT = REGISTRY.register(GradientIds.COW_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> DONKEY_PELT = REGISTRY.register(GradientIds.DONKEY_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> HORSE_PELT = REGISTRY.register(GradientIds.HORSE_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> LLAMA_PELT = REGISTRY.register(GradientIds.LLAMA_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> MULE_PELT = REGISTRY.register(GradientIds.MULE_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> OCELOT_PELT = REGISTRY.register(GradientIds.OCELOT_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> PIG_PELT = REGISTRY.register(GradientIds.PIG_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> POLAR_BEAR_PELT = REGISTRY.register(GradientIds.POLAR_BEAR_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> SHEEP_PELT = REGISTRY.register(GradientIds.SHEEP_PELT, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> WOLF_PELT = REGISTRY.register(GradientIds.WOLF_PELT, () -> new Item(new Item.Properties().group(GROUP)));

  public static final RegistryObject<Item> RAW_HIDE = REGISTRY.register(GradientIds.RAW_HIDE, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> SALTED_HIDE = REGISTRY.register(GradientIds.SALTED_HIDE, () -> new Item(new Item.Properties().group(GROUP)));
  public static final RegistryObject<Item> PRESERVED_HIDE = REGISTRY.register(GradientIds.PRESERVED_HIDE, () -> new Item(new Item.Properties().group(GROUP)));

  public static final RegistryObject<Item> FIRE_STARTER = REGISTRY.register(GradientIds.FIRE_STARTER, () -> new Item(new Item.Properties().group(GROUP).maxDamage(4)));
  public static final RegistryObject<StoneHammerItem> STONE_HAMMER = REGISTRY.register(GradientIds.STONE_HAMMER, StoneHammerItem::new);
  public static final RegistryObject<FlintKnifeItem> FLINT_KNIFE = REGISTRY.register(GradientIds.FLINT_KNIFE, FlintKnifeItem::new);

  public static final RegistryObject<BlockItem> FIREPIT = REGISTRY.register(GradientIds.FIREPIT, () -> new BlockItem(GradientBlocks.FIREPIT.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<WallOrFloorItem> UNLIT_FIBRE_TORCH = REGISTRY.register(GradientIds.UNLIT_FIBRE_TORCH, () -> new WallOrFloorItem(GradientBlocks.UNLIT_FIBRE_TORCH.get(), GradientBlocks.UNLIT_FIBRE_WALL_TORCH.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<WallOrFloorItem> LIT_FIBRE_TORCH = REGISTRY.register(GradientIds.LIT_FIBRE_TORCH, () -> new WallOrFloorItem(GradientBlocks.LIT_FIBRE_TORCH.get(), GradientBlocks.LIT_FIBRE_WALL_TORCH.get(), new Item.Properties().group(GROUP)));

  public static final RegistryObject<BlockItem> GRINDSTONE = REGISTRY.register(GradientIds.GRINDSTONE, () -> new BlockItem(GradientBlocks.GRINDSTONE.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> MIXING_BASIN = REGISTRY.register(GradientIds.MIXING_BASIN, () -> new BlockItem(GradientBlocks.MIXING_BASIN.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> DRYING_RACK = REGISTRY.register(GradientIds.DRYING_RACK, () -> new BlockItem(GradientBlocks.DRYING_RACK.get(), new Item.Properties().group(GROUP)));

  public static final RegistryObject<BlockItem> UNHARDENED_CLAY_FURNACE = REGISTRY.register(GradientIds.UNHARDENED_CLAY_FURNACE, () -> new BlockItem(GradientBlocks.UNHARDENED_CLAY_FURNACE.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> UNHARDENED_CLAY_CRUCIBLE = REGISTRY.register(GradientIds.UNHARDENED_CLAY_CRUCIBLE, () -> new BlockItem(GradientBlocks.UNHARDENED_CLAY_CRUCIBLE.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> UNHARDENED_CLAY_OVEN = REGISTRY.register(GradientIds.UNHARDENED_CLAY_OVEN, () -> new BlockItem(GradientBlocks.UNHARDENED_CLAY_OVEN.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> UNHARDENED_CLAY_MIXER = REGISTRY.register(GradientIds.UNHARDENED_CLAY_MIXER, () -> new BlockItem(GradientBlocks.UNHARDENED_CLAY_MIXER.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> UNHARDENED_CLAY_BUCKET = REGISTRY.register(GradientIds.UNHARDENED_CLAY_BUCKET, () -> new BlockItem(GradientBlocks.UNHARDENED_CLAY_BUCKET.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> UNHARDENED_CLAY_CAST_BLANK = REGISTRY.register(GradientIds.UNHARDENED_CLAY_CAST_BLANK, () -> new UnhardenedClayCastItem(GradientBlocks.UNHARDENED_CLAY_CAST_BLANK.get(), new Item.Properties().group(GROUP)));
  private static final Map<GradientCasts, RegistryObject<BlockItem>> UNHARDENED_CLAY_CASTS = new EnumMap<>(GradientCasts.class);

  static {
    for(final GradientCasts cast : GradientCasts.values()) {
      UNHARDENED_CLAY_CASTS.put(cast, REGISTRY.register(GradientIds.UNHARDENED_CLAY_CAST(cast), () -> new UnhardenedClayCastItem(GradientBlocks.UNHARDENED_CLAY_CAST(cast).get(), cast, new Item.Properties().group(GROUP))));
    }
  }

  public static final RegistryObject<BlockItem> CLAY_FURNACE = REGISTRY.register(GradientIds.CLAY_FURNACE, () -> new BlockItem(GradientBlocks.CLAY_FURNACE.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> CLAY_OVEN = REGISTRY.register(GradientIds.CLAY_OVEN, () -> new BlockItem(GradientBlocks.CLAY_OVEN.get(), new Item.Properties().group(GROUP)));

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

  public static RegistryObject<BlockItem> UNHARDENED_CLAY_CAST(final GradientCasts cast) {
    return UNHARDENED_CLAY_CASTS.get(cast);
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
