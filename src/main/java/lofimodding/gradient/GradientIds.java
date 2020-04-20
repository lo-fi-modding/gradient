package lofimodding.gradient;

import lofimodding.gradient.science.Metal;
import lofimodding.gradient.science.Ore;

public final class GradientIds {
  private GradientIds() { }

  public static final String PEBBLE = "pebble";

  public static String PEBBLE(final Ore ore) {
    return ore.metal.name + "_pebble";
  }

  public static String ORE(final Ore ore) {
    return ore.metal.name + "_ore";
  }

  public static String CRUSHED(final Metal metal) {
    return metal.name + "_crushed";
  }

  public static String PURIFIED(final Metal metal) {
    return metal.name + "_purified";
  }

  public static String DUST(final Metal metal) {
    return metal.name + "_dust";
  }

  public static String INGOT(final Metal metal) {
    return metal.name + "_ingot";
  }

  public static String NUGGET(final Metal metal) {
    return metal.name + "_nugget";
  }

  public static String PLATE(final Metal metal) {
    return metal.name + "_plate";
  }

  public static String METAL_BLOCK(final Metal metal) {
    return metal.name + "_block";
  }

  public static final String FIBRE = "fibre";
  public static final String TWINE = "twine";
}
