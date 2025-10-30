package RenderEngine;

import CoreEngine.DataLoader;
import RenderEngine.Models.Entity;
import RenderEngine.Models.ModelTexture;
import RenderEngine.Models.RawModel;
import RenderEngine.Models.TexturedModel;
import Shaders.StaticShader;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class Renderer {

    private static final float FOV = 45.0f;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 100.0f;

    public Renderer(StaticShader shader) {
        Matrix4f projectionMatrix = RawModel.createProjectionMatrix(FOV, NEAR_PLANE, FAR_PLANE);
        shader.use();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Entity entity, StaticShader staticShader) {
        TexturedModel model = entity.getTexturedModel();
        DataLoader.bindVAO(model.getRawModel().getVAO());

        ModelTexture.activeTextureUnit(GL_TEXTURE0);
        model.getModelTexture().bind();

        Matrix4f transformationMatrix = RawModel.createTransformationMatrix(entity.getPos(), entity.getRot(), entity.getScale());
        staticShader.loadTransformationMatrix(transformationMatrix);

        glDrawElements(GL_TRIANGLES, model.getRawModel().getIndicesCount(), GL_UNSIGNED_INT, 0);
        DataLoader.unBindVAO();
    }

    public void clearScreen(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public void clearBuffer(int buffer) {
        glClear(buffer);
    }
}
