package lofimodding.gradient.utils;

public final class MathHelper {
  private MathHelper() { }

  public static boolean flEq(final float a, final float b, final float tolerance) {
     return Math.abs(a - b) <= tolerance;
  }

  public static boolean flEq(final float a, final float b) {
    return flEq(a, b, 0.0001f);
  }
}
