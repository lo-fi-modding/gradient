package lofimodding.gradient.items;

import lofimodding.gradient.GradientCasts;
import lofimodding.gradient.GradientItems;
import lofimodding.gradient.science.Metal;

public class CastedItem extends MetalItem {
  public final GradientCasts cast;

  public CastedItem(final GradientCasts cast, final Metal metal) {
    super(metal, new Properties().group(GradientItems.GROUP));
    this.cast = cast;
  }
}
