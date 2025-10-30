package Shaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

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
