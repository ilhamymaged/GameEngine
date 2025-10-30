package RenderEngine.Models;

import RenderEngine.DisplayManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RawModel {
    int VAO;
    int indicesCount;

    public RawModel(int VAO, int indicesCount) {
        this.VAO = VAO;
        this.indicesCount = indicesCount;
    }

    public int getVAO() {
        return VAO;
    }

    public void setVAO(int VAO) {
        this.VAO = VAO;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void setIndicesCount(int indicesCount) {
        this.indicesCount = indicesCount;
    }

    public static Matrix4f createTransformationMatrix(Vector3f pos , Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(pos);
        matrix.rotate(rotation.x, new Vector3f(1.0f, 0.0f, 0.0f));
        matrix.rotate(rotation.y, new Vector3f(0.0f, 1.0f, 0.0f));
        matrix.rotate(rotation.z, new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createProjectionMatrix(float FOV, float near_plane, float far_plane) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.perspective(FOV, (float)DisplayManager.getWidth() / DisplayManager.getHeight(), near_plane, far_plane);
        return matrix;
    }
}
