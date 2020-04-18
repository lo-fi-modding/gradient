package lofimodding.gradient;

import lofimodding.gradient.science.Ore;

public final class GradientIds {
  private GradientIds() { }

  public static String ORE(final Ore ore) {
    return ore.metal.name + "_ore";
  }

  public static final String FIBRE = "fibre";
}
