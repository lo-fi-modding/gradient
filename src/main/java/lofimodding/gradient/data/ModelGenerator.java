package lofimodding.gradient.data;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;

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

  public static void clayCrucible(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation hardened, final ResourceLocation seared, final ResourceLocation searedGradient, final ResourceLocation searedGradientLight, final ResourceLocation particle) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("hardened", hardened)
      .texture("seared", seared)
      .texture("seared_gradient", searedGradient)
      .texture("seared_gradient_light", searedGradientLight)
      .texture("particle", particle)

      .element() // Right
      .from(0.0f, 12.001f, 1.0f)
      .to(2.0f, 15.001f, 15.0f)
      .face(Direction.NORTH).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient").end()
      .face(Direction.SOUTH).uvs(0.0f, 1.0f, 2.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("hardened").end()
      .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("seared_gradient_light").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
      .end()

      .element() // Back
      .from(1.0f, 12.0f, 14.0f)
      .to(15.0f, 15.0f, 16.0f)
      .face(Direction.NORTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient").end()
      .face(Direction.EAST).uvs(0.0f, 1.0f, 2.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.SOUTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("hardened").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("seared_gradient_light").end()
      .end()

      .element() // Left
      .from(14.0f, 12.001f, 1.0f)
      .to(16.0f, 15.001f, 15.0f)
      .face(Direction.NORTH).uvs(0.0f, 1.0f, 2.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.EAST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.SOUTH).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient").end()
      .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("hardened").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("seared_gradient_light").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .end()

      .element() // Front
      .from(1.0f, 12.0f, 0.0f)
      .to(15.0f, 15.0f, 2.0f)
      .face(Direction.NORTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.EAST).uvs(14.0f, 1.0f, 16.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.SOUTH).uvs(1.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(0.0f, 1.0f, 15.0f, 4.0f).texture("seared_gradient").end()
      .face(Direction.UP).uvs(14.0f, 1.0f, 16.0f, 15.0f).texture("hardened").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(1.0f, 0.0f, 15.0f, 2.0f).texture("seared_gradient_light").end()
      .end()

      .element() // Right
      .from(1.0f, 0.001f, 2.0f)
      .to(3.0f, 13.001f, 14.0f)
      .face(Direction.NORTH).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.EAST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient").end()
      .face(Direction.SOUTH).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.UP).uvs(2.0f, 2.0f, 14.0f, 4.0f).texture("seared_gradient").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(13.0f, 2.0f, 15.0f, 14.0f).texture("hardened").end()
      .end()

      .element() // Back
      .from(2.0f, 0.0f, 13.0f)
      .to(14.0f, 13.0f, 15.0f)
      .face(Direction.NORTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient").end()
      .face(Direction.EAST).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.SOUTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.UP).uvs(2.0f, 2.0f, 13.0f, 4.0f).texture("seared_gradient").end()
      .face(Direction.DOWN).uvs(1.0f, 2.0f, 3.0f, 14.0f).texture("hardened").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .end()

      .element() // Left
      .from(13.0f, 0.001f, 2.0f)
      .to(15.0f, 13.001f, 14.0f)
      .face(Direction.NORTH).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.EAST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.SOUTH).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.WEST).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient").end()
      .face(Direction.UP).uvs(2.0f, 2.0f, 14.0f, 4.0f).texture("seared_gradient").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(1.0f, 2.0f, 3.0f, 14.0f).texture("hardened").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Front
      .from(2.0f, 0.0f, 1.0f)
      .to(14.0f, 13.0f, 3.0f)
      .face(Direction.NORTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.EAST).uvs(13.0f, 3.0f, 15.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.SOUTH).uvs(2.0f, 3.0f, 14.0f, 16.0f).texture("seared_gradient").end()
      .face(Direction.WEST).uvs(1.0f, 3.0f, 3.0f, 16.0f).texture("seared_gradient_light").end()
      .face(Direction.UP).uvs(2.0f, 2.0f, 14.0f, 4.0f).texture("seared_gradient").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(1.0f, 2.0f, 3.0f, 14.0f).texture("hardened").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
      .end()

      .element() // Bottom
      .from(3.0f, 0.0f, 3.0f)
      .to(13.0f, 1.0f, 13.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("hardened").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("hardened").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("hardened").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("hardened").end()
      .face(Direction.UP).uvs(3.0f, 3.0f, 13.0f, 13.0f).texture("seared").end()
      .face(Direction.DOWN).uvs(3.0f, 3.0f, 13.0f, 13.0f).texture("hardened").end()
      .end();
  }

  public static void clayOven(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation inside, final ResourceLocation seared, final ResourceLocation clay, final ResourceLocation particle) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("inside", inside)
      .texture("seared", seared)
      .texture("clay", clay)
      .texture("particle", particle)

      .element() // Right
      .from(3.0f, 0.0f, 6.0f)
      .to(6.0f, 1.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(10.0f, 6.0f, 13.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(10.0f, 6.0f, 13.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Left
      .from(10.0f, 0.0f, 6.0f)
      .to(13.0f, 1.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.UP).uvs(3.0f, 6.0f, 6.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(3.0f, 6.0f, 6.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Up
      .from(3.0f, 0.0f, 10.0f)
      .to(13.0f, 1.0f, 14.0f)
      .face(Direction.NORTH).uvs(2.0f, 15.0f, 14.0f, 16.0f).texture("seared").end()
      .face(Direction.EAST).uvs(2.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(2.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
      .face(Direction.WEST).uvs(10.0f, 10.0f, 14.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(3.0f, 2.0f, 13.0f, 6.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(3.0f, 9.0f, 13.0f, 13.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Down
      .from(3.0f, 0.0f, 2.0f)
      .to(13.0f, 1.0f, 6.0f)
      .face(Direction.NORTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.EAST).uvs(10.0f, 10.0f, 14.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(2.0f, 15.0f, 14.0f, 16.0f).texture("seared").end()
      .face(Direction.WEST).uvs(2.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(3.0f, 10.0f, 13.0f, 14.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(3.0f, 3.0f, 13.0f, 7.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Bar 1
      .from(8.875f, 0.0f, 6.0f)
      .to(9.375f, 1.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.UP).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("inside").end()
      .end()

      .element() // Bar 2
      .from(7.75f, 0.0f, 6.0f)
      .to(8.25f, 1.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.UP).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("inside").end()
      .end()

      .element() // Bar 3
      .from(6.625f, 0.0f, 6.0f)
      .to(7.125f, 1.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 15.0f, 10.0f, 16.0f).texture("seared").end()
      .face(Direction.UP).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("inside").end()
      .end()

      .element() // Left
      .from(13.0f, 0.0f, 3.0f)
      .to(14.0f, 5.0f, 13.0f)
      .face(Direction.NORTH).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
      .face(Direction.EAST).uvs(3.0f, 6.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.WEST).uvs(2.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.UP).uvs(2.0f, 2.0f, 3.0f, 14.0f).texture("clay").end()
      .face(Direction.DOWN).uvs(2.0f, 3.0f, 3.0f, 13.0f).texture("inside").end()
      .end()

      .element() // Right
      .from(2.0f, 0.0f, 3.0f)
      .to(3.0f, 5.0f, 13.0f)
      .face(Direction.NORTH).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.EAST).uvs(2.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
      .face(Direction.WEST).uvs(3.0f, 6.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(13.0f, 2.0f, 14.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(13.0f, 3.0f, 14.0f, 13.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element()
      .from(3.0f, 1.0f, 13.0f)
      .to(13.0f, 5.0f, 14.0f)
      .face(Direction.NORTH).uvs(3.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
      .face(Direction.EAST).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(3.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
      .face(Direction.WEST).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.UP).uvs(2.0f, 2.0f, 12.0f, 3.0f).texture("clay").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 10.0f, 1.0f).texture("clay").end()
      .end()

      .element() // Right
      .from(2.0f, 5.0f, 6.0f)
      .to(6.0f, 6.0f, 10.0f)
      .face(Direction.NORTH).uvs(10.0f, 5.0f, 14.0f, 6.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(2.0f, 5.0f, 6.0f, 6.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(10.0f, 6.0f, 14.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(12.0f, 6.0f, 16.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Left
      .from(10.0f, 5.0f, 6.0f)
      .to(14.0f, 6.0f, 10.0f)
      .face(Direction.NORTH).uvs(2.0f, 5.0f, 6.0f, 6.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(10.0f, 5.0f, 14.0f, 6.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(2.0f, 6.0f, 6.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(0.0f, 6.0f, 4.0f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Up
      .from(3.0f, 5.0f, 10.0f)
      .to(13.0f, 6.0f, 13.0f)
      .face(Direction.NORTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("seared").end()
      .face(Direction.EAST).uvs(3.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.WEST).uvs(10.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(3.0f, 3.0f, 13.0f, 6.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(2.0f, 12.0f, 14.0f, 16.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Down
      .from(3.0f, 5.0f, 3.0f)
      .to(13.0f, 6.0f, 6.0f)
      .face(Direction.NORTH).uvs(3.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.EAST).uvs(10.0f, 10.0f, 13.0f, 11.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(2.0f, 10.0f, 14.0f, 11.0f).texture("seared").end()
      .face(Direction.WEST).uvs(3.0f, 10.0f, 6.0f, 11.0f).texture("clay").end()
      .face(Direction.UP).uvs(3.0f, 10.0f, 13.0f, 13.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(2.0f, 0.0f, 14.0f, 4.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Bar 1
      .from(8.875f, 5.0f, 6.0f)
      .to(9.375f, 6.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.UP).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(6.625f, 6.0f, 7.125f, 10.0f).texture("inside").end()
      .end()

      .element() // Bar 2
      .from(7.75f, 5.0f, 6.0f)
      .to(8.25f, 6.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.UP).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(7.75f, 6.0f, 8.25f, 10.0f).texture("inside").end()
      .end()

      .element() // Bar 3
      .from(6.625f, 5.0f, 6.0f)
      .to(7.125f, 6.0f, 10.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.EAST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 0.5f, 1.0f).texture("clay").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("seared").end()
      .face(Direction.UP).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("inside").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(8.875f, 6.0f, 9.375f, 10.0f).texture("inside").end()
      .end()

      .element() // Left
      .from(11.0f, 1.0f, 2.0f)
      .to(13.0f, 5.0f, 3.0f)
      .face(Direction.NORTH).uvs(3.0f, 6.0f, 5.0f, 10.0f).texture("clay").end()
      .face(Direction.EAST).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(11.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
      .face(Direction.WEST).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
      .face(Direction.UP).uvs(3.0f, 13.0f, 5.0f, 14.0f).texture("clay").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("clay").end()
      .end()

      .element() // Right
      .from(3.0f, 1.0f, 2.0f)
      .to(5.0f, 5.0f, 3.0f)
      .face(Direction.NORTH).uvs(11.0f, 6.0f, 13.0f, 10.0f).texture("clay").end()
      .face(Direction.EAST).uvs(2.0f, 6.0f, 3.0f, 10.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(3.0f, 6.0f, 5.0f, 10.0f).texture("clay").end()
      .face(Direction.WEST).uvs(13.0f, 6.0f, 14.0f, 10.0f).texture("clay").end()
      .face(Direction.UP).uvs(11.0f, 13.0f, 13.0f, 14.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("clay").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element() // Middle
      .from(5.0f, 4.0f, 2.0f)
      .to(11.0f, 5.0f, 3.0f)
      .face(Direction.NORTH).uvs(5.0f, 6.0f, 11.0f, 7.0f).texture("clay").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 1.0f).texture("clay").end()
      .face(Direction.SOUTH).uvs(5.0f, 6.0f, 11.0f, 7.0f).texture("clay").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 1.0f).texture("clay").end()
      .face(Direction.UP).uvs(5.0f, 13.0f, 11.0f, 14.0f).texture("clay").end()
      .face(Direction.DOWN).uvs(5.0f, 2.0f, 11.0f, 3.0f).texture("clay").end()
      .end();
  }

  public static void clayMixer(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation side, final ResourceLocation particle) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("side", side)
      .texture("particle", particle)

      .element() // Corner 1
      .from(13.0f, -2.0f, 0.0f)
      .to(16.0f, 2.0f, 3.0f)
      .face(Direction.NORTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 13.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.DOWN).uvs(13.0f, 13.0f, 16.0f, 16.0f).texture("side").end()
      .end()

      .element() // Corner 2
      .from(13.0f, -2.0f, 13.0f)
      .to(16.0f, 2.0f, 16.0f)
      .face(Direction.NORTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("side").end()
      .face(Direction.DOWN).uvs(13.0f, 0.0f, 16.0f, 3.0f).texture("side").end()
      .end()

      .element() // Corner 3
      .from(0.0f, -2.0f, 13.0f)
      .to(3.0f, 2.0f, 16.0f)
      .face(Direction.NORTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(13.0f, 0.0f, 16.0f, 3.0f).texture("side").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("side").end()
      .end()

      .element() // Corner 4
      .from(0.0f, -2.0f, 0.0f)
      .to(3.0f, 2.0f, 3.0f)
      .face(Direction.NORTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(13.0f, 13.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.DOWN).uvs(0.0f, 13.0f, 3.0f, 16.0f).texture("side").end()
      .end()

      .element() // Cross 1
      .from(7.0f, -1.0f, -2.0f)
      .to(9.0f, 1.0f, 17.0f)
      .rotation().angle(45.0f).axis(Direction.Axis.Y).origin(8.0f, 6.0f, 8.0f).end()
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 7.0f, 16.0f, 9.0f).texture("side").end()
      .face(Direction.UP).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
      .face(Direction.DOWN).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
      .end()

      .element() // Cross 2
      .from(7.0f, -1.0f, -2.0f)
      .to(9.0f, 1.0f, 17.0f)
      .rotation().angle(-45.0f).axis(Direction.Axis.Y).origin(8.0f, 6.0f, 8.0f).end()
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 7.0f, 16.0f, 9.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 2.0f, 2.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 16.0f, 2.0f).texture("side").end()
      .face(Direction.UP).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
      .face(Direction.DOWN).uvs(7.0f, 0.0f, 9.0f, 16.0f).texture("side").end()
      .end()

      .element() // Middle
      .from(5.0f, -2.0f, 5.0f)
      .to(11.0f, 2.0f, 11.0f)
      .face(Direction.NORTH).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(5.0f, 5.0f, 11.0f, 11.0f).texture("side").end()
      .face(Direction.DOWN).uvs(5.0f, 5.0f, 11.0f, 11.0f).texture("side").end()
      .end()

      .element() // Middle Cap
      .from(6.0f, 2.0f, 6.0f)
      .to(10.0f, 3.0f, 10.0f)
      .face(Direction.NORTH).uvs(6.0f, 11.0f, 10.0f, 12.0f).texture("side").end()
      .face(Direction.EAST).uvs(6.0f, 11.0f, 10.0f, 12.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("side").end()
      .face(Direction.WEST).uvs(6.0f, 10.0f, 10.0f, 11.0f).texture("side").end()
      .face(Direction.UP).uvs(6.0f, 6.0f, 10.0f, 10.0f).texture("side").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 4.0f, 4.0f).texture("side").end()
      .end();
  }
}
