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

  public static final String SALT_BLOCK = "salt_block";
  public static final String SALT = "salt";

  public static final String FIBRE = "fibre";
  public static final String TWINE = "twine";
  public static final String BARK = "bark";
  public static final String MULCH = "mulch";
  public static final String COW_PELT = "cow_pelt";
  public static final String DONKEY_PELT = "donkey_pelt";
  public static final String HORSE_PELT = "horse_pelt";
  public static final String LLAMA_PELT = "llama_pelt";
  public static final String MULE_PELT = "mule_pelt";
  public static final String OCELOT_PELT = "ocelot_pelt";
  public static final String PIG_PELT = "pig_pelt";
  public static final String POLAR_BEAR_PELT = "polar_bear_pelt";
  public static final String SHEEP_PELT = "sheep_pelt";
  public static final String WOLF_PELT = "wolf_pelt";

  public static final String RAW_HIDE = "raw_hide";

  public static final String STONE_HAMMER = "stone_hammer";
  public static final String FLINT_KNIFE = "flint_knife";

  public static final String FIREPIT = "firepit";
  public static final String GRINDSTONE = "grindstone";

  public static final String UNHARDENED_CLAY_FURNACE = "unhardened_clay_furnace";
  public static final String UNHARDENED_CLAY_CRUCIBLE = "unhardened_clay_crucible";
}
