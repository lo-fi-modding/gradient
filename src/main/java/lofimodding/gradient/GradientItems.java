package lofimodding.gradient;

import lofimodding.gradient.items.CastedItem;
import lofimodding.gradient.items.EmptyWaterskinItem;
import lofimodding.gradient.items.FilledWaterskinItem;
import lofimodding.gradient.items.FlintKnifeItem;
import lofimodding.gradient.items.HideBeddingItem;
import lofimodding.gradient.items.MetalItem;
import lofimodding.gradient.items.MulchItem;
import lofimodding.gradient.items.PebbleItem;
import lofimodding.gradient.items.StoneHammerItem;
import lofimodding.gradient.items.UnhardenedClayCastItem;
import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Metals;
import lofimodding.gradient.science.Ore;
import lofimodding.gradient.science.Ores;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
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
  public static final RegistryObject<Item> TANNED_HIDE = REGISTRY.register(GradientIds.TANNED_HIDE, () -> new Item(new Item.Properties().group(GROUP)));

  public static final RegistryObject<Item> LEATHER_STRIP = REGISTRY.register(GradientIds.LEATHER_STRIP, () -> new Item(new Item.Properties().group(GROUP)));

  public static final RegistryObject<Item> FIRE_STARTER = REGISTRY.register(GradientIds.FIRE_STARTER, () -> new Item(new Item.Properties().group(GROUP).maxDamage(4)));
  public static final RegistryObject<StoneHammerItem> STONE_HAMMER = REGISTRY.register(GradientIds.STONE_HAMMER, StoneHammerItem::new);
  public static final RegistryObject<ToolItem> STONE_HATCHET = REGISTRY.register(GradientIds.STONE_HATCHET, StoneHammerItem::new);
  public static final RegistryObject<FlintKnifeItem> FLINT_KNIFE = REGISTRY.register(GradientIds.FLINT_KNIFE, FlintKnifeItem::new);
  public static final RegistryObject<Item> BONE_AWL = REGISTRY.register(GradientIds.BONE_AWL, () -> new Item(new Item.Properties().group(GROUP).maxDamage(20)));
  public static final RegistryObject<HideBeddingItem> HIDE_BEDDING = REGISTRY.register(GradientIds.HIDE_BEDDING, HideBeddingItem::new);
  public static final RegistryObject<EmptyWaterskinItem> EMPTY_WATERSKIN = REGISTRY.register(GradientIds.EMPTY_WATERSKIN, EmptyWaterskinItem::new);
  public static final RegistryObject<FilledWaterskinItem> FILLED_WATERSKIN = REGISTRY.register(GradientIds.FILLED_WATERSKIN, FilledWaterskinItem::new);

  public static final RegistryObject<ArmorItem> HIDE_HAT = REGISTRY.register(GradientIds.HIDE_HAT, () -> new ArmorItem(GradientMaterials.Armour.HIDE, EquipmentSlotType.HEAD, new Item.Properties().group(GROUP)));
  public static final RegistryObject<ArmorItem> HIDE_SHIRT = REGISTRY.register(GradientIds.HIDE_SHIRT, () -> new ArmorItem(GradientMaterials.Armour.HIDE, EquipmentSlotType.CHEST, new Item.Properties().group(GROUP)));
  public static final RegistryObject<ArmorItem> HIDE_PANTS = REGISTRY.register(GradientIds.HIDE_PANTS, () -> new ArmorItem(GradientMaterials.Armour.HIDE, EquipmentSlotType.LEGS, new Item.Properties().group(GROUP)));
  public static final RegistryObject<ArmorItem> HIDE_BOOTS = REGISTRY.register(GradientIds.HIDE_BOOTS, () -> new ArmorItem(GradientMaterials.Armour.HIDE, EquipmentSlotType.FEET, new Item.Properties().group(GROUP)));

  public static final RegistryObject<BlockItem> FIREPIT = REGISTRY.register(GradientIds.FIREPIT, () -> new BlockItem(GradientBlocks.FIREPIT.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<WallOrFloorItem> UNLIT_FIBRE_TORCH = REGISTRY.register(GradientIds.UNLIT_FIBRE_TORCH, () -> new WallOrFloorItem(GradientBlocks.UNLIT_FIBRE_TORCH.get(), GradientBlocks.UNLIT_FIBRE_WALL_TORCH.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<WallOrFloorItem> LIT_FIBRE_TORCH = REGISTRY.register(GradientIds.LIT_FIBRE_TORCH, () -> new WallOrFloorItem(GradientBlocks.LIT_FIBRE_TORCH.get(), GradientBlocks.LIT_FIBRE_WALL_TORCH.get(), new Item.Properties().group(GROUP)));
  public static final RegistryObject<BlockItem> TORCH_STAND = REGISTRY.register(GradientIds.TORCH_STAND, () -> new BlockItem(GradientBlocks.TORCH_STAND.get(), new Item.Properties().group(GROUP)));

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
  public static final RegistryObject<BlockItem> CLAY_CRUCIBLE = REGISTRY.register(GradientIds.CLAY_CRUCIBLE, () -> new BlockItem(GradientBlocks.CLAY_CRUCIBLE.get(), new Item.Properties().group(GROUP)));

  private static final Map<GradientCasts, RegistryObject<BlockItem>> CLAY_CASTS = new EnumMap<>(GradientCasts.class);

  static {
    for(final GradientCasts cast : GradientCasts.values()) {
      CLAY_CASTS.put(cast, REGISTRY.register(GradientIds.CLAY_CAST(cast), () -> new UnhardenedClayCastItem(GradientBlocks.CLAY_CAST(cast).get(), cast, new Item.Properties().group(GROUP))));
    }
  }

  private static final Map<GradientCasts, Map<Metal, RegistryObject<CastedItem>>> CASTED = new EnumMap<>(GradientCasts.class);

  static {
    for(final GradientCasts cast : GradientCasts.values()) {
      for(final Metal metal : Metals.all()) {
        CASTED.computeIfAbsent(cast, key -> new HashMap<>()).put(metal, REGISTRY.register(GradientIds.CASTED(cast, metal), () -> new CastedItem(cast, metal)));
      }
    }
  }

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

  public static RegistryObject<BlockItem> CLAY_CAST(final GradientCasts cast) {
    return CLAY_CASTS.get(cast);
  }

  public static RegistryObject<CastedItem> CASTED(final GradientCasts cast, final Metal metal) {
    return CASTED.get(cast).get(metal);
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
