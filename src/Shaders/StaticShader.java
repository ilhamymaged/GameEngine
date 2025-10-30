package Shaders;

import CoreEngine.Camera;
import RenderEngine.Lighting.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StaticShader extends Shader{

    private int transformationMatrixLoc;
    private int viewMatrixLoc;
    private int projectionMatrixLoc;
    private int lightPosLoc;
    private int lightColorLoc;
    private int cameraPosLoc;


    public StaticShader() {
        super("simple");
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLoc = super.getUniformLocation("transformationMatrix");
        viewMatrixLoc = super.getUniformLocation("viewMatrix");
        projectionMatrixLoc = super.getUniformLocation("projectionMatrix");
        lightPosLoc = super.getUniformLocation("lightPos");
        lightColorLoc = super.getUniformLocation("lightColor");
        cameraPosLoc = super.getUniformLocation("cameraPos");
    }

    public void loadLight(Light light, Camera camera) {
        super.loadVector3f(lightPosLoc, light.getPos());
        super.loadVector3f(lightColorLoc, light.getColor());
        super.loadVector3f(cameraPosLoc, camera.getPosition());
    }

    public void loadTransformationMatrix(Matrix4f transformationMatrix) {
        super.loadMatrix4f(transformationMatrixLoc, transformationMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix4f(projectionMatrixLoc, projectionMatrix);
    }

    public void loadViewMatrix(Matrix4f viewMatrix) {
        super.loadMatrix4f(viewMatrixLoc, viewMatrix);
    }

}
