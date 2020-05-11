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

  public static String CRUSHED(final Ore ore) {
    return ore.metal.name + "_crushed";
  }

  public static String PURIFIED(final Ore ore) {
    return ore.metal.name + "_purified";
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

  public static final String HARDENED_LOG = "hardened_log";
  public static final String HARDENED_PLANKS = "hardened_planks";
  public static final String HARDENED_STICK = "hardened_stick";

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
  public static final String SALTED_HIDE = "salted_hide";
  public static final String PRESERVED_HIDE = "preserved_hide";
  public static final String TANNED_HIDE = "tanned_hide";

  public static final String LEATHER_STRIP = "leather_strip";

  public static final String FIRE_STARTER = "fire_starter";
  public static final String STONE_HAMMER = "stone_hammer";
  public static final String STONE_HATCHET = "stone_hatchet";
  public static final String STONE_MATTOCK = "stone_mattock";
  public static final String STONE_PICKAXE = "stone_pickaxe";
  public static final String FLINT_KNIFE = "flint_knife";
  public static final String BONE_AWL = "bone_awl";
  public static final String HIDE_BEDDING = "hide_bedding";
  public static final String EMPTY_WATERSKIN = "empty_waterskin";
  public static final String FILLED_WATERSKIN = "filled_waterskin";

  public static final String HIDE_HAT = "hide_hat";
  public static final String HIDE_SHIRT = "hide_shirt";
  public static final String HIDE_PANTS = "hide_pants";
  public static final String HIDE_BOOTS = "hide_boots";

  public static final String FIREPIT = "firepit";
  public static final String UNLIT_FIBRE_TORCH = "unlit_fibre_torch";
  public static final String UNLIT_FIBRE_WALL_TORCH = "unlit_fibre_wall_torch";
  public static final String LIT_FIBRE_TORCH = "lit_fibre_torch";
  public static final String LIT_FIBRE_WALL_TORCH = "lit_fibre_wall_torch";
  public static final String TORCH_STAND = "torch_stand";
  public static final String UNLIT_TORCH_STAND_TORCH = "unlit_torch_stand_torch";
  public static final String LIT_TORCH_STAND_TORCH = "lit_torch_stand_torch";

  public static final String GRINDSTONE = "grindstone";
  public static final String MIXING_BASIN = "mixing_basin";
  public static final String DRYING_RACK = "drying_rack";

  public static final String UNHARDENED_CLAY_FURNACE = "unhardened_clay_furnace";
  public static final String UNHARDENED_CLAY_CRUCIBLE = "unhardened_clay_crucible";
  public static final String UNHARDENED_CLAY_OVEN = "unhardened_clay_oven";
  public static final String UNHARDENED_CLAY_METAL_MIXER = "unhardened_clay_metal_mixer";
  public static final String UNHARDENED_CLAY_BUCKET = "unhardened_clay_bucket";
  public static final String UNHARDENED_CLAY_CAST_BLANK = "unhardened_clay_cast";

  public static String UNHARDENED_CLAY_CAST(final GradientCasts cast) {
    return "unhardened_clay_cast_" + cast.name;
  }

  public static final String CLAY_FURNACE = "clay_furnace";
  public static final String CLAY_OVEN = "clay_oven";
  public static final String CLAY_CRUCIBLE = "clay_crucible";
  public static final String CLAY_METAL_MIXER = "clay_metal_mixer";

  public static String CLAY_CAST(final GradientCasts cast) {
    return "clay_cast_" + cast.name;
  }

  public static String CASTED(final GradientCasts cast, final Metal metal) {
    return metal.name + '_' + cast.name;
  }

  public static String WOODEN_GEAR = "wooden_gear";
  public static String WOODEN_AXLE = "wooden_axle";
  public static String WOODEN_GEARBOX = "wooden_gearbox";
  public static String WOODEN_CONVEYOR_BELT = "wooden_conveyor_belt";
  public static String WOODEN_CONVEYOR_BELT_DRIVER = "wooden_conveyor_belt_driver";
  public static String WOODEN_CRANK = "wooden_crank";

  public static final String INFINICOAL = "infinicoal";
}
