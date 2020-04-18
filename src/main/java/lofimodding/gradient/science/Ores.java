package lofimodding.gradient.science;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Ores {
  private Ores() { }

  private static final Map<String, Ore> ORES = new LinkedHashMap<>();

  public static final Ore INVALID_ORE_METAL = new Ore("invalid", Metals.INVALID_METAL, Metals.INVALID_METAL);

  public static final Ore AZURITE     = addOre("azurite", o -> o.metal(Metals.AZURITE).basic(Metals.COPPER));
  public static final Ore CASSITERITE = addOre("cassiterite", o -> o.metal(Metals.CASSITERITE).basic(Metals.TIN));
  public static final Ore COPPER      = addOre("copper", o -> o.metal(Metals.COPPER));
  public static final Ore GOLD        = addOre("gold", o -> o.metal(Metals.GOLD));
  public static final Ore GRAPHITE    = addOre("graphite", o -> o.metal(Metals.GRAPHITE));
  public static final Ore HEMATITE    = addOre("hematite", o -> o.metal(Metals.HEMATITE).basic(Metals.IRON));
  public static final Ore PYRITE      = addOre("pyrite", o -> o.metal(Metals.PYRITE));
  public static final Ore SPHALERITE  = addOre("sphalerite", o -> o.metal(Metals.SPHALERITE).basic(Metals.ZINC));

  public static Ore addOre(final String name, final Consumer<OreBuilder> builder) {
    final OreBuilder mb = new OreBuilder();
    builder.accept(mb);
    final Ore ore = new Ore(name, mb.metal, mb.basic);
    ORES.put(name, ore);
    return ore;
  }

  public static Collection<Ore> all() {
    return ORES.values();
  }

  private static class OreBuilder {
    private Metal metal;
    private Metal basic;

    public OreBuilder metal(final Metal metal) {
      this.metal = metal;

      if(this.basic == null) {
        this.basic = metal;
      }

      return this;
    }

    public OreBuilder basic(final Metal basic) {
      this.basic = basic;
      return this;
    }
  }
}
