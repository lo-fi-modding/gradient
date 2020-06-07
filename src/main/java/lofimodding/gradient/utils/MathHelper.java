package lofimodding.gradient.utils;

public final class MathHelper {
  private static final float FLOAT_TOLERANCE = 0.0001f;

  private MathHelper() { }

  public static boolean flEq(final float a, final float b, final float tolerance) {
     return Math.abs(a - b) <= tolerance;
  }

  public static boolean flEq(final float a, final float b) {
    return flEq(a, b, FLOAT_TOLERANCE);
  }

  /**
   * Checks if a is less than b
   */
  public static boolean flLess(final float a, final float b, final float tolerance) {
    return a - b <= -tolerance;
  }

  /**
   * Checks if a is less than b
   */
  public static boolean flLess(final float a, final float b) {
    return flLess(a, b, FLOAT_TOLERANCE);
  }

  /**
   * Checks if a is greater than b
   */
  public static boolean flGreater(final float a, final float b, final float tolerance) {
    return a - b >= tolerance;
  }

  /**
   * Checks if a is greater than b
   */
  public static boolean flGreater(final float a, final float b) {
    return flGreater(a, b, FLOAT_TOLERANCE);
  }
}
