package lofimodding.gradient.data;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;

public final class ModelGenerator {
  private ModelGenerator() { }

  public static void torchStand(final GradientDataGenerator.BlockModels generator, final String id) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("rod", generator.mcLoc("block/oak_planks"))
      .texture("holder", generator.mcLoc("block/farmland"))
      .texture("particle", generator.mcLoc("block/oak_planks"))

      .element() // Centre
      .from(7.0f, 3.0f, 7.0f)
      .to(9.0f, 14.0f, 9.0f)
      .face(Direction.NORTH).uvs(8.0f, 1.0f, 10.0f, 12.0f).texture("rod").end()
      .face(Direction.EAST).uvs(10.0f, 1.0f, 12.0f, 12.0f).texture("rod").end()
      .face(Direction.SOUTH).uvs(4.0f, 1.0f, 6.0f, 12.0f).texture("rod").end()
      .face(Direction.WEST).uvs(6.0f, 1.0f, 8.0f, 12.0f).texture("rod").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 2.0f, 16.0f).texture("rod").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 16.0f).texture("rod").end()
      .end()

      .element() // Wrap
      .from(6.375f, 14.0f, 6.375f)
      .to(9.625f, 16.0f, 9.625f)
      .face(Direction.NORTH).uvs(9.0f, 6.0f, 11.0f, 9.0f).texture("holder").end()
      .face(Direction.EAST).uvs(9.0f, 9.0f, 11.0f, 12.0f).texture("holder").end()
      .face(Direction.SOUTH).uvs(9.0f, 0.0f, 11.0f, 3.0f).texture("holder").end()
      .face(Direction.WEST).uvs(9.0f, 3.0f, 11.0f, 6.0f).texture("holder").end()
      .face(Direction.UP).uvs(10.0f, 3.0f, 13.0f, 6.0f).texture("holder").end()
      .face(Direction.DOWN).uvs(10.0f, 3.0f, 13.0f, 6.0f).texture("holder").end()
      .end()

      .element() // Base
      .from(6.0f, 0.0f, 6.0f)
      .to(10.0f, 3.0f, 10.0f)
      .face(Direction.NORTH).uvs(7.0f, 13.0f, 11.0f, 16.0f).texture("rod").end()
      .face(Direction.EAST).uvs(9.0f, 13.0f, 13.0f, 16.0f).texture("rod").end()
      .face(Direction.SOUTH).uvs(3.0f, 13.0f, 7.0f, 16.0f).texture("rod").end()
      .face(Direction.WEST).uvs(5.0f, 13.0f, 9.0f, 16.0f).texture("rod").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 4.0f, 4.0f).texture("rod").end()
      .face(Direction.DOWN).uvs(2.0f, 12.0f, 6.0f, 16.0f).texture("rod").end()
      .end()

      .element()
      .from(6.5f, 15.0f, 6.0f)
      .to(9.5f, 18.0f, 6.5f)
      .face(Direction.NORTH).uvs(4.5f, 0.0f, 7.5f, 3.0f).texture("rod").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 0.5f, 3.0f).texture("rod").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("rod").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 0.5f, 3.0f).texture("rod").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 3.0f, 0.5f).texture("rod").end()
      .face(Direction.DOWN).uvs(4.5f, 3.0f, 7.5f, 3.5f).texture("rod").end()
      .end()

      .element()
      .from(6.0f, 15.0f, 6.0f)
      .to(6.5f, 18.0f, 10.0f)
      .face(Direction.NORTH).uvs(7.5f, 0.0f, 8.0f, 3.0f).texture("rod").end()
      .face(Direction.EAST).uvs(8.5f, 0.0f, 12.5f, 3.0f).texture("rod").end()
      .face(Direction.SOUTH).uvs(12.0f, 0.0f, 12.5f, 3.0f).texture("rod").end()
      .face(Direction.WEST).uvs(8.0f, 0.0f, 12.0f, 3.0f).texture("rod").end()
      .face(Direction.UP).uvs(8.5f, 0.0f, 12.5f, 1.0f).texture("rod").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(8.0f, 3.0f, 12.0f, 3.5f).texture("rod").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .end()

      .element()
      .from(6.5f, 15.0f, 9.5f)
      .to(9.5f, 18.0f, 10.0f)
      .face(Direction.NORTH).uvs(6.0f, 0.0f, 9.0f, 3.0f).texture("rod").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 0.5f, 3.0f).texture("rod").end()
      .face(Direction.SOUTH).uvs(12.5f, 0.0f, 15.5f, 3.0f).texture("rod").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 0.5f, 3.0f).texture("rod").end()
      .face(Direction.UP).uvs(6.0f, 0.0f, 9.0f, 0.5f).texture("rod").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .face(Direction.DOWN).uvs(12.5f, 3.0f, 15.5f, 3.5f).texture("rod").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()

      .element()
      .from(9.5f, 15.0f, 6.0f)
      .to(10.0f, 18.0f, 10.0f)
      .face(Direction.NORTH).uvs(4.0f, 0.0f, 4.5f, 3.0f).texture("rod").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 4.0f, 3.0f).texture("rod").end()
      .face(Direction.SOUTH).uvs(15.5f, 0.0f, 16.0f, 3.0f).texture("rod").end()
      .face(Direction.WEST).uvs(2.5f, 0.0f, 6.5f, 3.0f).texture("rod").end()
      .face(Direction.UP).uvs(2.5f, 0.0f, 6.5f, 1.0f).texture("rod").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(0.0f, 3.0f, 4.0f, 3.5f).texture("rod").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
      .end();
  }

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
      .from(13.0f, -2.0f, -0.001f)
      .to(16.001f, 2.0f, 3.0f)
      .face(Direction.NORTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 13.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.DOWN).uvs(13.0f, 13.0f, 16.0f, 16.0f).texture("side").end()
      .end()

      .element() // Corner 2
      .from(13.0f, -2.0f, 13.0f)
      .to(16.001f, 2.0f, 16.001f)
      .face(Direction.NORTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("side").end()
      .face(Direction.DOWN).uvs(13.0f, 0.0f, 16.0f, 3.0f).texture("side").end()
      .end()

      .element() // Corner 3
      .from(-0.001f, -2.0f, 13.0f)
      .to(3.0f, 2.0f, 16.001f)
      .face(Direction.NORTH).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 12.0f, 3.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(13.0f, 12.0f, 16.0f, 16.0f).texture("side").end()
      .face(Direction.UP).uvs(13.0f, 0.0f, 16.0f, 3.0f).texture("side").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 3.0f, 3.0f).texture("side").end()
      .end()

      .element() // Corner 4
      .from(-0.001f, -2.0f, -0.001f)
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
      .end()

      .transforms()
      .transform(ModelBuilder.Perspective.GUI)
      .rotation(30.0f, 225.0f, 0.0f)
      .translation(0.0f, 2.0f, 0.0f)
      .scale(0.625f)
      .end()
      .end();
  }

  public static void clayMixerPipe(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation side) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("side", side)

      .element() // Cap
      .from(5.75f, -0.25f, -1.0f)
      .to(10.25f, 3.25f, 1.0f)
      .face(Direction.NORTH).uvs(5.75f, 5.25f, 10.25f, 8.75f).texture("side").end()
      .face(Direction.EAST).uvs(4.0f, 6.0f, 6.0f, 9.5f).texture("side").end()
      .face(Direction.SOUTH).uvs(5.75f, 5.25f, 10.25f, 8.75f).texture("side").end()
      .face(Direction.WEST).uvs(10.0f, 6.0f, 12.0f, 9.5f).texture("side").end()
      .face(Direction.UP).uvs(5.75f, 4.0f, 10.25f, 6.0f).texture("side").end()
      .face(Direction.DOWN).uvs(5.75f, 0.0f, 10.25f, 2.0f).texture("side").end()
      .end()

      .element() // Pipe 1
      .from(6.0f, 0.0f, 1.0f)
      .to(10.0f, 3.0f, 3.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 3.0f).texture("side").end()
      .face(Direction.EAST).uvs(2.0f, 6.0f, 4.0f, 9.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 3.0f).texture("side").end()
      .face(Direction.WEST).uvs(12.0f, 6.0f, 14.0f, 9.0f).texture("side").end()
      .face(Direction.UP).uvs(6.0f, 2.0f, 10.0f, 4.0f).texture("side").end()
      .face(Direction.DOWN).uvs(6.0f, 2.0f, 10.0f, 4.0f).texture("side").end()
      .end()

      .element() // Pipe 2
      .from(6.0f, -1.0f, 2.0f)
      .to(10.0f, 2.0f, 4.0f)
      .face(Direction.NORTH).uvs(6.0f, 1.0f, 10.0f, 4.0f).texture("side").end()
      .face(Direction.EAST).uvs(1.0f, 7.0f, 3.0f, 10.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(6.0f, 7.0f, 10.0f, 10.0f).texture("side").end()
      .face(Direction.WEST).uvs(13.0f, 7.0f, 15.0f, 10.0f).texture("side").end()
      .face(Direction.UP).uvs(6.0f, 1.0f, 10.0f, 3.0f).texture("side").end()
      .face(Direction.DOWN).uvs(6.0f, 4.0f, 10.0f, 6.0f).texture("side").end()
      .end();
  }

  public static void clayMixerAuger(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation side, final ResourceLocation blade) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("side", side)
      .texture("blade", blade)

      .element() // Rod 1
      .from(6.5f, -12.0f, 7.0f)
      .to(9.5f, -2.0f, 9.0f)
      .face(Direction.NORTH).uvs(6.5f, 0.0f, 9.5f, 10.0f).texture("side").end()
      .face(Direction.EAST).uvs(7.0f, 0.0f, 9.0f, 10.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(6.5f, 0.0f, 9.5f, 10.0f).texture("side").end()
      .face(Direction.WEST).uvs(7.0f, 0.0f, 9.0f, 10.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 3.0f, 2.0f).texture("side").end()
      .face(Direction.DOWN).uvs(6.5f, 7.0f, 9.5f, 9.0f).texture("side").end()
      .end()

      .element() // Rod 2
      .from(7.0f, -12.0f, 6.5f)
      .to(9.0f, -2.0f, 9.5f)
      .face(Direction.NORTH).uvs(7.0f, 0.0f, 9.0f, 10.0f).texture("side").end()
      .face(Direction.EAST).uvs(6.5f, 0.0f, 9.5f, 10.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(7.0f, 0.0f, 9.0f, 10.0f).texture("side").end()
      .face(Direction.WEST).uvs(6.5f, 0.0f, 9.5f, 10.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 3.0f, 2.0f).texture("side").end()
      .face(Direction.DOWN).uvs(7.0f, 6.5f, 9.0f, 9.5f).texture("side").end()
      .end()

      .element() // Blade 1
      .from(5.0f, -5.0f, 7.5f)
      .to(7.0f, -3.0f, 8.5f)
      .face(Direction.NORTH).uvs(14.0f, 0.0f, 16.0f, 2.0f).texture("blade").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.SOUTH).uvs(1.0f, 0.0f, 3.0f, 2.0f).texture("blade").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .end()

      .element() // Blade 2
      .from(9.0f, -7.0f, 7.5f)
      .to(11.0f, -5.0f, 8.5f)
      .face(Direction.NORTH).uvs(14.0f, 2.0f, 16.0f, 4.0f).texture("blade").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.SOUTH).uvs(0.0f, 2.0f, 2.0f, 4.0f).texture("blade").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .end()

      .element() // Blade 3
      .from(5.0f, -9.0f, 7.5f)
      .to(7.0f, -7.0f, 8.5f)
      .face(Direction.NORTH).uvs(14.0f, 4.0f, 16.0f, 6.0f).texture("blade").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.SOUTH).uvs(0.0f, 4.0f, 2.0f, 6.0f).texture("blade").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .end()

      .element() // Blade 4
      .from(9.0f, -11.0f, 7.5f)
      .to(11.0f, -9.0f, 8.5f)
      .face(Direction.NORTH).uvs(14.0f, 6.0f, 16.0f, 8.0f).texture("blade").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.SOUTH).uvs(0.0f, 6.0f, 2.0f, 8.0f).texture("blade").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 2.0f).texture("blade").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 2.0f, 1.0f).texture("blade").end()
      .end();
  }

  public static void woodenAxle(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation side, final ResourceLocation end) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("side", side)
      .texture("end", end)
      .texture("particle", side)

      .element()
      .from(5.0f, 0.0f, 5.0f)
      .to(11.0f, 16.0f, 11.0f)
      .face(Direction.NORTH).texture("side").end()
      .face(Direction.EAST).texture("side").end()
      .face(Direction.SOUTH).texture("side").end()
      .face(Direction.WEST).texture("side").end()
      .face(Direction.UP).texture("end").end()
      .face(Direction.DOWN).texture("end").end()
      .end();
  }

  public static void woodenConveyorBelt(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation particle, final ResourceLocation belt, final ResourceLocation side) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("particle", particle)
      .texture("belt", belt)
      .texture("side", side)

      .element() // west
      .from(3.0f, 0.0f, 0.0f)
      .to(4.0f, 4.0f, 16.0f)
      .face(Direction.NORTH).uvs(12.0f, 12.0f, 13.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 4.0f, 16.0f, 8.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(0.0f, 12.0f, 1.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 4.0f, 16.0f, 8.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 11.0f, 16.0f, 12.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(0.0f, 15.0f, 16.0f, 16.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .end()

      .element() // east
      .from(12.0f, 0.0f, 0.0f)
      .to(13.0f, 4.0f, 16.0f)
      .face(Direction.NORTH).uvs(3.0f, 12.0f, 4.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 16.0f, 4.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(9.0f, 12.0f, 10.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 16.0f, 4.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 15.0f, 16.0f, 16.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.DOWN).uvs(0.0f, 11.0f, 16.0f, 12.0f).texture("side").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .end()

      .element() // north
      .from(4.0f, 0.0f, 0.0f)
      .to(12.0f, 4.0f, 1.0f)
      .face(Direction.NORTH).uvs(4.0f, 12.0f, 12.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 4.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(4.0f, 12.0f, 12.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 4.0f).texture("side").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 8.0f, 1.0f).texture("side").end()
      .face(Direction.DOWN).uvs(8.0f, 0.0f, 16.0f, 1.0f).texture("side").end()
      .end()

      .element() // south
      .from(4.0f, 0.0f, 15.0f)
      .to(12.0f, 4.0f, 16.0f)
      .face(Direction.NORTH).uvs(1.0f, 12.0f, 9.0f, 16.0f).texture("side").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 4.0f).texture("side").end()
      .face(Direction.SOUTH).uvs(1.0f, 12.0f, 9.0f, 16.0f).texture("side").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 4.0f).texture("side").end()
      .face(Direction.UP).uvs(8.0f, 0.0f, 16.0f, 1.0f).texture("side").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 8.0f, 1.0f).texture("side").end()
      .end()

      .element() // belt
      .from(4.1f, 3.0f, 1.1f)
      .to(11.9f, 4.0f, 14.9f)
      .face(Direction.NORTH).uvs(4.0f, 0.0f, 12.0f, 1.0f).texture("belt").end()
      .face(Direction.EAST).uvs(15.0f, 0.0f, 16.0f, 14.0f).texture("belt").rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
      .face(Direction.SOUTH).uvs(4.0f, 15.0f, 12.0f, 16.0f).texture("belt").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 14.0f).texture("belt").rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
      .face(Direction.UP).uvs(4.0f, 1.0f, 12.0f, 15.0f).texture("belt").end()
      .face(Direction.DOWN).uvs(4.0f, 1.0f, 12.0f, 15.0f).texture("belt").rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
      .end()
    ;
  }

  public static void mechanicalGrindstone(final GradientDataGenerator.BlockModels generator, final String id, final ResourceLocation particle, final ResourceLocation surface, final ResourceLocation casing) {
    generator.getBuilder(id)
      .parent(generator.getExistingFile(generator.mcLoc("block/block")))
      .texture("particle", particle)
      .texture("surface", surface)
      .texture("casing", casing)

      .element() // surface 1
      .from(7.0f, 0.5f, 2.0f)
      .to(10.5f, 2.5f, 14.0f)
      .rotation().angle(-22.5f).axis(Direction.Axis.Z).origin(8.0f, 8.0f, 8.0f).end()
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 12.0f, 2.0f).texture("surface").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 12.0f, 2.0f).texture("surface").end()
      .face(Direction.UP).uvs(0.0f, 2.0f, 4.0f, 14.0f).texture("surface").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 4.0f, 12.0f).texture("surface").end()
      .end()

      .element() // surface 2
      .from(5.5f, 0.5f, 2.0f)
      .to(9.0f, 2.5f, 14.0f)
      .rotation().angle(22.5f).axis(Direction.Axis.Z).origin(8.0f, 8.0f, 8.0f).end()
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
      .face(Direction.EAST).uvs(2.0f, 0.0f, 14.0f, 2.0f).texture("surface").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 4.0f, 2.0f).texture("surface").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 12.0f, 2.0f).texture("surface").end()
      .face(Direction.UP).uvs(12.0f, 2.0f, 16.0f, 14.0f).texture("surface").end()
      .face(Direction.DOWN).uvs(0.0f, 0.0f, 4.0f, 12.0f).texture("surface").end()
      .end()

      .element() // north
      .from(5.0f, 0.0f, 0.0f)
      .to(11.0f, 4.0f, 2.0f)
      .face(Direction.NORTH).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("casing").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("casing").end()
      .face(Direction.SOUTH).uvs(5.0f, 13.0f, 11.0f, 16.0f).texture("casing").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("casing").end()
      .face(Direction.UP).uvs(5.0f, 0.0f, 11.0f, 2.0f).texture("casing").end()
      .face(Direction.DOWN).uvs(5.0f, 0.0f, 12.0f, 2.0f).texture("casing").end()
      .end()

      .element() // east
      .from(11.0f, 0.0f, 0.0f)
      .to(14.0f, 4.0f, 16.0f)
      .face(Direction.NORTH).uvs(2.0f, 12.0f, 5.0f, 16.0f).texture("casing").end()
      .face(Direction.EAST).uvs(0.0f, 12.0f, 16.0f, 16.0f).texture("casing").end()
      .face(Direction.SOUTH).uvs(11.0f, 12.0f, 14.0f, 16.0f).texture("casing").end()
      .face(Direction.WEST).uvs(1.0f, 0.0f, 15.0f, 3.0f).texture("casing").end()
      .face(Direction.UP).uvs(11.0f, 0.0f, 14.0f, 16.0f).texture("casing").end()
      .face(Direction.DOWN).uvs(2.0f, 0.0f, 5.0f, 16.0f).texture("casing").end()
      .end()

      .element() // west
      .from(2.0f, 0.0f, 0.0f)
      .to(5.0f, 4.0f, 16.0f)
      .face(Direction.NORTH).uvs(11.0f, 12.0f, 14.0f, 16.0f).texture("casing").end()
      .face(Direction.EAST).uvs(1.0f, 0.0f, 15.0f, 3.0f).texture("casing").end()
      .face(Direction.SOUTH).uvs(2.0f, 12.0f, 5.0f, 16.0f).texture("casing").end()
      .face(Direction.WEST).uvs(0.0f, 12.0f, 16.0f, 16.0f).texture("casing").end()
      .face(Direction.UP).uvs(2.0f, 0.0f, 5.0f, 16.0f).texture("casing").end()
      .face(Direction.DOWN).uvs(11.0f, 0.0f, 14.0f, 16.0f).texture("casing").end()
      .end()

      .element() // south
      .from(5.0f, 0.0f, 14.0f)
      .to(11.0f, 4.0f, 16.0f)
      .face(Direction.NORTH).uvs(5.0f, 13.0f, 11.0f, 16.0f).texture("casing").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("casing").end()
      .face(Direction.SOUTH).uvs(5.0f, 12.0f, 11.0f, 16.0f).texture("casing").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 1.0f, 3.0f).texture("casing").end()
      .face(Direction.UP).uvs(5.0f, 14.0f, 11.0f, 16.0f).texture("casing").end()
      .face(Direction.DOWN).uvs(5.0f, 14.0f, 11.0f, 16.0f).texture("casing").end()
      .end()

      .element() // bottom
      .from(5.0f, 0.0f, 2.0f)
      .to(11.0f, 1.0f, 14.0f)
      .face(Direction.NORTH).uvs(0.0f, 0.0f, 6.0f, 1.0f).texture("casing").end()
      .face(Direction.EAST).uvs(0.0f, 0.0f, 12.0f, 1.0f).texture("casing").end()
      .face(Direction.SOUTH).uvs(0.0f, 0.0f, 6.0f, 1.0f).texture("casing").end()
      .face(Direction.WEST).uvs(0.0f, 0.0f, 12.0f, 1.0f).texture("casing").end()
      .face(Direction.UP).uvs(0.0f, 0.0f, 6.0f, 12.0f).texture("casing").end()
      .face(Direction.DOWN).uvs(5.0f, 2.0f, 11.0f, 14.0f).texture("casing").end()
      .end()
    ;
  }
}
