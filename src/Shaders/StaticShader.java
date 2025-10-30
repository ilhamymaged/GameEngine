package Shaders;

import org.joml.Matrix4f;

public class StaticShader extends Shader{

    private int transformationMatrixLoc;
    private int viewMatrixLoc;
    private int projectionMatrixLoc;


    public StaticShader() {
        super("simple");
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLoc = super.getUniformLocation("transformationMatrix");
        viewMatrixLoc = super.getUniformLocation("viewMatrix");
        projectionMatrixLoc = super.getUniformLocation("projectionMatrix");
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
