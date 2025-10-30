package RenderEngine;

import RenderEngine.Models.ModelTexture;
import RenderEngine.Models.TexturedModel;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class Renderer {

    public static void render(TexturedModel model) {
        DataLoader.bindVAO(model.getRawModel().getVAO());
        ModelTexture.activeTextureUnit(GL_TEXTURE0);
        model.getModelTexture().bind();
        glDrawElements(GL_TRIANGLES, model.getRawModel().getIndicesCount(), GL_UNSIGNED_INT, 0);
        DataLoader.unBindVAO();
    }

    public static void clearScreen(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public static void clearBuffer(int buffer) {
        glClear(buffer);
    }
}
