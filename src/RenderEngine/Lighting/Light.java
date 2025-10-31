package RenderEngine.Lighting;

import org.joml.Vector3f;

public class Light {
    private final Vector3f pos;
    private final Vector3f color;

    public Light(Vector3f pos, Vector3f color) {
        this.pos = pos;
        this.color = color;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getColor() {
        return color;
    }

}
