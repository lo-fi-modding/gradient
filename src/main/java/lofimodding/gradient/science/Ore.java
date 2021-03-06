package lofimodding.gradient.science;

public class Ore {
  public final String name;
  public final Metal metal;
  public final int meltTime;
  public final float meltTemp;
  public final float hardness;
  public final float weight;

  public final int harvestLevel;

  public final int colourDiffuse;
  public final int colourSpecular;
  public final int colourShadow1;
  public final int colourShadow2;
  public final int colourEdge1;
  public final int colourEdge2;
  public final int colourEdge3;


  public Ore(final String name, final Metal metal, final float meltTemp, final float hardness, final float weight, final int harvestLevel, final int colourDiffuse, final int colourSpecular, final int colourShadow1, final int colourShadow2, final int colourEdge1, final int colourEdge2, final int colourEdge3) {
    this.name = name;
    this.metal = metal;
    this.meltTime = Math.round(hardness * 7.5f * 20.0f);
    this.meltTemp = meltTemp;
    this.hardness = hardness;
    this.weight = weight;

    this.harvestLevel = harvestLevel;

    this.colourDiffuse = colourDiffuse;
    this.colourSpecular = colourSpecular;
    this.colourShadow1 = colourShadow1;
    this.colourShadow2 = colourShadow2;
    this.colourEdge1 = colourEdge1;
    this.colourEdge2 = colourEdge2;
    this.colourEdge3 = colourEdge3;
  }
}
