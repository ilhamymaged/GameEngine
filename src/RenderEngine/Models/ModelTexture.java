package RenderEngine.Models;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

public record ModelTexture(int id) {

    public static void activeTextureUnit(int textureUnit) {
        glActiveTexture(textureUnit);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
