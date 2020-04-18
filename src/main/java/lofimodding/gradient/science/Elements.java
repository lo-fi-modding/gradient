package lofimodding.gradient.science;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Elements {
  private Elements() { }

  private static final Map<String, Element> elements = new LinkedHashMap<>();

  public static final Element HYDROGEN = add("hydrogen", e -> e.number(1).weight(1.00794f));
  public static final Element HELIUM = add("helium", e -> e.number(2).weight(4.0026f));
  public static final Element LITHIUM = add("lithium", e -> e.number(3).weight(6.94f));
  public static final Element BERYLLIUM = add("beryllium", e -> e.number(4).weight(9.0122f));
  public static final Element BORON = add("boron", e -> e.number(5).weight(10.81f));
  public static final Element CARBON = add("carbon", e -> e.number(6).weight(12.011f));
  public static final Element NITROGEN = add("nitrogen", e -> e.number(7).weight(14.007f));
  public static final Element OXYGEN = add("oxygen", e -> e.number(8).weight(15.999f));
  public static final Element FLUORINE = add("fluorine", e -> e.number(9).weight(18.998f));
  public static final Element NEON = add("neon", e -> e.number(10).weight(20.180f));
  public static final Element SODIUM = add("sodium", e -> e.number(11).weight(22.990f));
  public static final Element MAGNESIUM = add("magnesium", e -> e.number(12).weight(24.305f));
  public static final Element ALUMINIUM = add("aluminium", e -> e.number(13).weight(26.982f));
  public static final Element SILICON = add("silicon", e -> e.number(14).weight(28.085f));
  public static final Element PHOSPHORUS = add("phosphorus", e -> e.number(15).weight(30.974f));
  public static final Element SULFUR = add("sulfur", e -> e.number(16).weight(32.06f));
  public static final Element CHLORINE = add("chlorine", e -> e.number(17).weight(35.45f));
  public static final Element ARGON = add("argon", e -> e.number(18).weight(39.948f));
  public static final Element POTASSIUM = add("potassium", e -> e.number(19).weight(39.098f));
  public static final Element CALCIUM = add("calcium", e -> e.number(20).weight(40.078f));
  public static final Element SCANDIUM = add("scandium", e -> e.number(21).weight(44.956f));
  public static final Element TITANIUM = add("titanium", e -> e.number(22).weight(47.867f));
  public static final Element VANADIUM = add("vanadium", e -> e.number(23).weight(50.942f));
  public static final Element CHROMIUM = add("chromium", e -> e.number(24).weight(51.996f));
  public static final Element MANGANESE = add("manganese", e -> e.number(25).weight(54.938f));
  public static final Element IRON = add("iron", e -> e.number(26).weight(55.845f));
  public static final Element COBALT = add("cobalt", e -> e.number(27).weight(58.933f));
  public static final Element NICKEL = add("nickel", e -> e.number(28).weight(58.693f));
  public static final Element COPPER = add("copper", e -> e.number(29).weight(63.546f));
  public static final Element ZINC = add("zinc", e -> e.number(30).weight(65.38f));
  public static final Element GALLIUM = add("gallium", e -> e.number(31).weight(69.723f));
  public static final Element GERMANIUM = add("germanium", e -> e.number(32).weight(72.630f));
  public static final Element ARSENIC = add("arsenic", e -> e.number(33).weight(74.922f));
  public static final Element SELENIUM = add("selenium", e -> e.number(34).weight(78.971f));
  public static final Element BROMINE = add("bromine", e -> e.number(35).weight(79.904f));
  public static final Element KRYPTON = add("krypton", e -> e.number(36).weight(83.798f));

  public static final Element SILVER = add("silver", e -> e.number(47).weight(107.87f));
  public static final Element TIN = add("tin", e -> e.number(50).weight(118.71f));
  public static final Element TUNGSTEN = add("tungsten", e -> e.number(74).weight(183.84f));
  public static final Element IRIDIUM = add("iridium", e -> e.number(77).weight(192.22f));
  public static final Element PLATINUM = add("platinum", e -> e.number(78).weight(195.08f));
  public static final Element GOLD = add("gold", e -> e.number(79).weight(196.97f));
  public static final Element LEAD = add("lead", e -> e.number(82).weight(207.2f));

  public static Element add(final String name, final Consumer<ElementBuilder> builder) {
    final ElementBuilder eb = new ElementBuilder();
    builder.accept(eb);
    final Element element = new Element(name, eb.number, eb.weight);
    elements.put(name, element);
    return element;
  }

  public static Element get(final String loc) {
    return elements.get(loc);
  }

  public static Collection<Element> all() {
    return elements.values();
  }

  private static class ElementBuilder {
    private int number;
    private float weight;

    public ElementBuilder number(final int number) {
      this.number = number;
      return this;
    }

    public ElementBuilder weight(final float weight) {
      this.weight = weight;
      return this;
    }
  }
}
