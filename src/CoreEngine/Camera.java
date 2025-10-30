package CoreEngine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final float SPEED = 2.5f;
    private static final Vector3f FRONT_DIRECTION = new Vector3f(0.0f, 0.0f, -1.0f);
    private static final Vector3f UP_DIRECTION = new Vector3f(0.0f, 1.0f, 0.0f);

    private Vector3f position;
    private float pitch;
    private float yaw;

    public void move(long window, float deltaTime) {
        float velocity = SPEED * deltaTime;
        Vector3f front = new Vector3f(FRONT_DIRECTION);
        Vector3f right = new Vector3f(FRONT_DIRECTION).cross(UP_DIRECTION);

        if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            position.add(new Vector3f(front).mul(velocity));
        if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            position.sub(new Vector3f(front).mul(velocity));

        if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            position.add(new Vector3f(right).mul(velocity));

        if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            position.sub(new Vector3f(right).mul(velocity));
    }

    public Matrix4f createViewMatrix() {
        Vector3f center = new Vector3f(position).add(FRONT_DIRECTION);
        return new Matrix4f().lookAt(position, center, UP_DIRECTION);
    }

    public Camera(Vector3f position, float pitch, float yaw) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
