package lofimodding.gradient.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Utility class to help with managing registry entries.
 * Maintains a list of all suppliers for entries and registers them during the proper Register event.
 * Suppliers should return NEW instances every time.
 * <p>
 * Example Usage:
 * <pre>
 *   private static final DeferredRegister2<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
 *   private static final DeferredRegister2<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
 *
 *   public static final RegistryObject2<Block> ROCK_BLOCK = BLOCKS.register("rock", () -> new Block(Block.Properties.create(Material.ROCK)));
 *   public static final RegistryObject2<Item> ROCK_ITEM = ITEMS.register("rock", () -> new BlockItem(ROCK_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
 *
 *   public ExampleMod() {
 *       ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *       BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *   }
 * </pre>
 *
 * @param <T> The base registry type, must be a concrete base class, do not use subclasses or wild cards.
 */
public class DeferredRegister2<T extends IForgeRegistryEntry<T>> {
  private final Supplier<IForgeRegistry<T>> type;
  private final String modid;
  private final Map<RegistryObject2<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
  private final Set<RegistryObject2<T>> entriesView = Collections.unmodifiableSet(this.entries.keySet());

  public DeferredRegister2(final Supplier<IForgeRegistry<T>> reg, final String modid) {
    this.type = reg;
    this.modid = modid;
  }

  /**
   * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
   *
   * @param name The new entry's name, it will automatically have the modid prefixed.
   * @param sup  A factory for the new entry, it should return a new instance every time it is called.
   * @return A RegistryObject that will be updated with when the entries in the registry change.
   */
  @SuppressWarnings("unchecked")
  public <I extends T> RegistryObject2<I> register(final String name, final Supplier<? extends I> sup) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(sup);
    final ResourceLocation key = new ResourceLocation(this.modid, name);
    final RegistryObject2<I> ret = RegistryObject2.of(key, this.type);
    if(this.entries.putIfAbsent((RegistryObject2<T>)ret, () -> sup.get().setRegistryName(key)) != null) {
      throw new IllegalArgumentException("Duplicate registration " + name);
    }
    return ret;
  }

  /**
   * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
   * See the example usage.
   *
   * @param bus The Mod Specific event bus.
   */
  public void register(final IEventBus bus) {
    bus.addListener(this::addEntries);
  }

  /**
   * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
   */
  public Collection<RegistryObject2<T>> getEntries() {
    return this.entriesView;
  }

  private void addEntries(final RegistryEvent.Register<?> event) {
    if(event.getGenericType() == this.type.get().getRegistrySuperType()) {
      @SuppressWarnings("unchecked") final IForgeRegistry<T> reg = (IForgeRegistry<T>)event.getRegistry();
      for(final Map.Entry<RegistryObject2<T>, Supplier<? extends T>> e : this.entries.entrySet()) {
        reg.register(e.getValue().get());
        e.getKey().updateReference(reg);
      }
    }
  }
}
