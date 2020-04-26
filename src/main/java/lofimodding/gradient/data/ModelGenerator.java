package lofimodding.gradient.data;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public final class ModelGenerator {
  private ModelGenerator() { }

  public static void clayFurnace(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation side, final ResourceLocation front, final ResourceLocation top, final ResourceLocation inside, final ResourceLocation particle) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("side", side)
      .texture("front", front)
      .texture("top", top)
      .texture("inside", inside)
      .texture("particle", particle)

      .element() // Left
      .from(0.0f, 0.0f, 1.0f)
      .to(2.0f, 15.0f, 15.0f)
      .face(Direction.NORTH).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("inside").end()
      .face(Direction.SOUTH).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 1.0f, 2.0f, 15.0f).texture("side").end()
      .face(Direction.DOWN).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("side").end()
      .end()

      .element() // Right
      .from(14.0f, 0.0f, 1.0f)
      .to(16.0f, 15.0f, 15.0f)
      .face(Direction.NORTH).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 16.0f).texture("inside").end()
      .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("side").end()
      .face(Direction.DOWN).uvs(0.0f, 1.0f, 2.0f, 15.0f).texture("side").end()
      .end()

      .element() // Back
      .from(1.0f, 0.0f, 14.0f)
      .to(15.0f, 15.0f, 16.0f)
      .face(Direction.NORTH).uvs(2.0f, 1.0f, 16.0f, 16.0f).texture("inside").end()
      .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(2.0f, 1.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("side").end()
      .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("side").end()
      .end()

      .element() // Front Right
      .from(1.0f, 0.0f, 0.0f)
      .to(4.0f, 15.0f, 2.0f)
      .face(Direction.NORTH).uvs(12.0f, 1.0f, 15.0f, 16.0f).texture("front").end()
      .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("inside").end()
      .face(Direction.SOUTH).uvs(1.0f, 1.0f, 4.0f, 16.0f).texture("inside").end()
      .face(Direction.WEST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(1.0f, 0.0f, 4.0f, 2.0f).texture("side").end()
      .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("side").end()
      .end()

      .element() // Front Left
      .from(12.0f, 0.0f, 0.0f)
      .to(15.0f, 15.0f, 2.0f)
      .face(Direction.NORTH).uvs(1.0f, 1.0f, 4.0f, 16.0f).texture("front").end()
      .face(Direction.EAST).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(12.0f, 1.0f, 15.0f, 16.0f).texture("inside").end()
      .face(Direction.WEST).uvs(14.0f, 1.0f, 16.0f, 16.0f).texture("inside").end()
      .face(Direction.UP).uvs(1.0f, 0.0f, 4.0f, 2.0f).texture("side").end()
      .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("side").end()
      .end()

      .element() // Front Middle
      .from(4.0f, 8.0f, 0.0f)
      .to(12.0f, 15.0f, 2.0f)
      .face(Direction.NORTH).uvs(4.0f, 1.0f, 12.0f, 8.0f).texture("front").end()
      .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(4.0f, 1.0f, 12.0f, 12.0f).texture("inside").end()
      .face(Direction.WEST).uvs(0.0f, 1.0f, 2.0f, 12.0f).texture("side").end()
      .face(Direction.UP).uvs(1.0f, 0.0f, 9.0f, 2.0f).texture("side").end()
      .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("inside").end()
      .end()

      .element() // Top Left
      .from(1.0f, 14.0f, 1.0f)
      .to(5.0f, 16.0f, 15.0f)
      .face(Direction.NORTH).uvs(11.0f, 13.0f, 15.0f, 15.0f).texture("side").end()
      .face(Direction.EAST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("inside").end()
      .face(Direction.SOUTH).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("side").end()
      .face(Direction.WEST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 1.0f, 4.0f, 15.0f).texture("top").end()
      .face(Direction.DOWN).uvs(12.0f, 1.0f, 16.0f, 15.0f).texture("inside").end()
      .end()

      .element() // Top Right
      .from(11.0f, 14.0f, 1.0f)
      .to(15.0f, 16.0f, 15.0f)
      .face(Direction.NORTH).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("side").end()
      .face(Direction.EAST).uvs(1.0f, 14.0f, 15.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(11.0f, 13.0f, 15.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("inside").end()
      .face(Direction.UP).uvs(12.0f, 1.0f, 16.0f, 15.0f).texture("top").end()
      .face(Direction.DOWN).uvs(0.0f, 1.0f, 4.0f, 15.0f).texture("inside").end()
      .end()

      .element() // Top Back
      .from(5.0f, 14.0f, 1.0f)
      .to(11.0f, 16.0f, 5.0f)
      .face(Direction.NORTH).uvs(5.0f, 13.0f, 11.0f, 15.0f).texture("side").end()
      .face(Direction.EAST).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(5.0f, 1.0f, 11.0f, 3.0f).texture("inside").end()
      .face(Direction.WEST).uvs(1.0f, 0.0f, 5.0f, 2.0f).texture("side").end()
      .face(Direction.UP).uvs(5.0f, 1.0f, 11.0f, 5.0f).texture("top").end()
      .face(Direction.DOWN).uvs(5.0f, 11.0f, 11.0f, 15.0f).texture("inside").end()
      .end()

      .element() // Top Front
      .from(5.0f, 14.0f, 11.0f)
      .to(11.0f, 16.0f, 15.0f)
      .face(Direction.NORTH).uvs(5.0f, 1.0f, 11.0f, 3.0f).texture("inside").end()
      .face(Direction.EAST).uvs(1.0f, 1.0f, 5.0f, 3.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(5.0f, 13.0f, 11.0f, 15.0f).texture("side").end()
      .face(Direction.WEST).uvs(1.0f, 0.0f, 5.0f, 2.0f).texture("side").end()
      .face(Direction.UP).uvs(5.0f, 11.0f, 11.0f, 15.0f).texture("top").end()
      .face(Direction.DOWN).uvs(5.0f, 1.0f, 11.0f, 5.0f).texture("inside").end()
      .end();
  }
}
