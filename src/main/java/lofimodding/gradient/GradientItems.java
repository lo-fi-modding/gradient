package lofimodding.gradient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GradientItems {
  private GradientItems() { }

  private static final DeferredRegister<Item> REGISTRY = new DeferredRegister<>(ForgeRegistries.ITEMS, Gradient.MOD_ID);

  private static final ItemGroup GROUP = new GradientItemGroup();

  public static final RegistryObject<Item> FIBRE = REGISTRY.register(GradientIds.FIBRE, () -> new Item(new Item.Properties().group(GROUP)));

  static void init(final IEventBus bus) {
    Gradient.LOGGER.info("Registering items...");
    REGISTRY.register(bus);
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
