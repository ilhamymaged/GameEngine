package RenderEngine.Models;

import org.joml.Vector3f;

public class Entity {
    private TexturedModel texturedModel;
    private Vector3f pos;
    private Vector3f rot;
    private Vector3f scale;


    public Entity(TexturedModel texturedModel, Vector3f pos, Vector3f rot, Vector3f scale) {
        this.texturedModel = texturedModel;
        this.pos = pos;
        this.rot = rot;
        this.scale = scale;
    }

    public TexturedModel getTexturedModel() {
        return texturedModel;
    }

    public void setTexturedModel(TexturedModel texturedModel) {
        this.texturedModel = texturedModel;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(Vector3f rot) {
        this.rot = rot;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
