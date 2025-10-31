package CoreEngine;

import RenderEngine.DisplayManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static float FOV;
    private static final float SPEED = 2.5f;

    private static Vector3f position;
    private static Vector3f front;
    private static Vector3f up;
    private static float pitch;
    private static float yaw;
    private static boolean firstMouse;
    private static float lastX;
    private static float lastY;

    public Camera(Vector3f position, Vector3f front, Vector3f up, float pitch, float yaw, float FOV) {
        Camera.position = position;
        Camera.front = front;
        Camera.up = up;
        Camera.pitch = pitch;
        Camera.yaw = yaw;
        firstMouse = true;
        Camera.FOV = FOV;
        lastX = (float) DisplayManager.getWidth() / 2;
        lastY = (float) DisplayManager.getHeight() / 2;
    }


    public static void setFront(Vector3f front) {
        Camera.front = front;
    }

    public static float getFOV() {
        return FOV;
    }

    public void setUp(Vector3f up) {
        Camera.up = up;
    }


    public static void mouse_scrollBack(long window, double xoffset, double yoffset) {
        FOV = FOV - (float)yoffset * 5.0f;
        if (FOV < 1.0f)
            FOV = 1.0f;
        if (FOV > 45.0f)
            FOV = 45.0f;
    }

    public static void mouse_callback(long window, double xpos, double ypos) {
        if (firstMouse)
        {
            lastX = (float) xpos;
            lastY = (float) ypos;
            firstMouse = false;
            return;
        }

        float xoffset = (float) (xpos - lastX);
        float yoffset = (float) (lastY - ypos);
        lastX = (float) xpos;
        lastY = (float) ypos;

        final float sensitivity = 0.1f;
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        setYaw(yaw);
        setPitch(pitch);

        Vector3f direction = new Vector3f(
                (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))),
                (float) Math.sin(Math.toRadians(pitch)),
                (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)))
        ).normalize();

        setFront(direction);
    }

    public void move(long window, float deltaTime) {
        float velocity = SPEED * deltaTime;
        Vector3f f = new Vector3f(front);
        Vector3f r = new Vector3f(front).cross(up);

        if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            position.add(new Vector3f(f).mul(velocity));
        if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            position.sub(new Vector3f(f).mul(velocity));

        if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            position.add(new Vector3f(r).mul(velocity));

        if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            position.sub(new Vector3f(r).mul(velocity));

        if(glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
            position.add(new Vector3f(up).mul(velocity));
    }

    public Matrix4f createViewMatrix() {
        Vector3f center = new Vector3f(position).add(front);
        return new Matrix4f().lookAt(position, center, up);
    }

    public Vector3f getPosition() {
        return position;
    }



    public static void setPitch(float pitch) {
        Camera.pitch = pitch;
    }


    public static void setYaw(float yaw) {
        Camera.yaw = yaw;
    }
}
