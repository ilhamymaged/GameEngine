package Shaders;

import RenderEngine.Lighting.Light;
import org.joml.Matrix4f;

public class LightShader extends Shader{

    private int transformationMatrixLoc;
    private int viewMatrixLoc;
    private int projectionMatrixLoc;
    private int lightColorLoc;


    public LightShader() {
        super("light");
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLoc = super.getUniformLocation("transformationMatrix");
        viewMatrixLoc = super.getUniformLocation("viewMatrix");
        projectionMatrixLoc = super.getUniformLocation("projectionMatrix");
        lightColorLoc = super.getUniformLocation("lightColor");
    }

    public void loadLight(Light light) {
        super.loadVector3f(lightColorLoc, light.getColor());
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
