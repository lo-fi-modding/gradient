package lofimodding.gradient.science;

public class Ore {
  public final String name;
  public final Metal metal;
  public final Metal simple;

  public Ore(final String name, final Metal metal, final Metal simple) {
    this.name = name;
    this.metal = metal;
    this.simple = simple;
  }

  public Ore(final String name, final Metal metal) {
    this(name, metal, metal);
  }
}
