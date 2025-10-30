package Shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {

    private final int programId;
    private final int vertexShaderId;
    private final int fragmentShaderId;

    public Shader(String name) {
        vertexShaderId = loadShader("src/Shaders/" + name + ".v" , GL_VERTEX_SHADER);
        fragmentShaderId = loadShader("src/Shaders/" + name + ".f", GL_FRAGMENT_SHADER);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        glValidateProgram(programId);

        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String locName) {
        int loc = glGetUniformLocation(programId, locName);
        if(loc == -1)  {
            System.out.println("DIDN'T FIND: " + locName);
            //System.exit(-1);
        }
        return loc;
    }

    protected void loadVector3f(int loc, Vector3f vec) {
        glUniform3f(loc, vec.x, vec.y, vec.z);
    }

    protected void loadMatrix4f(int loc, Matrix4f matrix) {
        glUniformMatrix4fv(loc, false, matrix.get(new float[16]));
    }

    protected void locVec3(int loc, Vector3f vec) {
        glUniform3f(loc, vec.x, vec.y, vec.z);
    }

    protected void loadBoolean(int loc, boolean value) {
        if(value)
            glUniform1i(loc, 1);
        else
            glUniform1f(loc, 0);
    }


    protected void loadInt(int loc, int value) {
        glUniform1i(loc, value);
    }

    protected void loadFloat(int loc, float value) {
        glUniform1f(loc, value);
    }


    public void use() {
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
        glDeleteProgram(programId);
    }

    private static int loadShader(String file, int type) {
        try {
            String shaderSource = Files.readString(Paths.get(file));

            int shaderId = glCreateShader(type);
            glShaderSource(shaderId, shaderSource);
            glCompileShader(shaderId);
            if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
                System.out.println(glGetShaderInfoLog(shaderId, 500));
                System.out.println("FAILED TO COMPILE THIS SHADER: " + file);
                System.exit(-1);
            }
            System.out.println("SHADER LOADED SUCCESSFULLY: " + file);
            return shaderId;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return 0;
        }
    }
}
