package CoreEngine;

import RenderEngine.Models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class DataLoader {
    private static final List<Integer> VERTEX_ARRAY_LIST = new ArrayList<>();
    private static final List<Integer> VERTEX_BUFFER_LIST = new ArrayList<>();
    private static final List<Integer> textures = new ArrayList<>();

    public static RawModel loadRawModel(float[] positions, float[] normals, float[] texCoords, int[] indices) {
        int VAO = createVAO();
        VERTEX_ARRAY_LIST.add(VAO);
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 3, normals);
        storeDataInAttributeList(2, 2, texCoords);
        unBindVAO();
        return new RawModel(VAO, indices.length);
    }

    public static int loadTexture(String name) {
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = STBImage.stbi_load( "src/Assets/Textures/"+ name, width, height, channels, 0);

        if(image != null) {
            int format = channels.get(0) == 3 ? GL_RGB : GL_RGBA;
            glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL_UNSIGNED_BYTE, image);
            System.out.print("IMAGE LOADED SUCCESSFULLY: " + name);
        }
        else {
            assert false : "COULDN'T LOAD THIS IMAGE: " + name;
        }

        STBImage.stbi_image_free(image);
        textures.add(id);
        return id;
    }

    public static void bindVAO(int VAO) {
        glBindVertexArray(VAO);
    }

    public static void unBindVAO() {
        glBindVertexArray(0);
    }

    private static void storeDataInAttributeList(int i, int coordsSize, float[] data) {
        int VBO = glGenBuffers();
        VERTEX_BUFFER_LIST.add(VBO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        FloatBuffer VBOBuffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, VBOBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(i);
        glVertexAttribPointer(i, coordsSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private static void bindIndicesBuffer(int[] indices) {
        int EBO = glGenBuffers();
        VERTEX_BUFFER_LIST.add(EBO);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        IntBuffer EBOBuffer = storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, EBOBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private static IntBuffer storeDataInIntBuffer(int[] indices) {
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip(); //Finished writing To this Buffer
        return buffer;
    }

    private static FloatBuffer storeDataInFloatBuffer(float[] positions) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(positions.length);
        buffer.put(positions);
        buffer.flip(); //Finished writing To this Buffer
        return buffer;
    }

    public static void cleanUp() {
        for(int VAO: VERTEX_ARRAY_LIST) {
            glDeleteVertexArrays(VAO);
        }

        for(int VBO: VERTEX_BUFFER_LIST) {
            glDeleteBuffers(VBO);
        }

        for (int texture: textures) {
            glDeleteTextures(texture);
        }
    }

    private static int createVAO() {
        int VAO = glGenVertexArrays();
        bindVAO(VAO);
        return VAO;
    }

}
