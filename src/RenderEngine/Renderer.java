package RenderEngine;

import CoreEngine.Camera;
import CoreEngine.DataLoader;
import RenderEngine.Models.*;
import Shaders.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class Renderer {

    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 100.0f;

    public Renderer(Shader shader) {
        Matrix4f projectionMatrix = RawModel.createProjectionMatrix((float) Math.toRadians(Camera.getFOV()),  NEAR_PLANE, FAR_PLANE);
        shader.use();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void renderEntity(Model.ModelAsset model, Vector3f entityPos, Vector3f entityRotation, Vector3f entityScale, Shader shader) {
        for (Model.MeshAsset mesh : model.meshes()) {
            Entity entity = new Entity(new TexturedModel(mesh.rawModel(), mesh.texture()), entityPos, entityRotation, entityScale);
            render(entity, shader);
        }
    }

    public void render(Entity entity, Shader shader) {
        TexturedModel model = entity.texturedModel();
        DataLoader.bindVAO(model.rawModel().getVAO());

        ModelTexture.activeTextureUnit(GL_TEXTURE0);
        model.modelTexture().bind();

        Matrix4f transformationMatrix = RawModel.createTransformationMatrix(entity.pos(), entity.rot(), entity.scale());
        shader.loadTransformationMatrix(transformationMatrix);

        glDrawElements(GL_TRIANGLES, model.rawModel().getIndicesCount(), GL_UNSIGNED_INT, 0);
        DataLoader.unBindVAO();
    }

    public static void EnableDepthTest(boolean enable) {
        if(enable) glEnable(GL_DEPTH_TEST);
        else glDisable(GL_DEPTH_TEST);
    }

    public void clearScreen(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public void clearBuffer(int buffer) {
        glClear(buffer);
    }
}
