package RenderEngine;

import CoreEngine.Camera;
import CoreEngine.DataLoader;
import RenderEngine.Models.*;
import Shaders.StaticShader;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DisplayManager {

    private static long window;
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 900;
    private static final String TITLE = "3D GAME ENGINE";

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static void createDisplay() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode imode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert imode != null;
            glfwSetWindowPos(
                    window,
                    (imode.width() - pWidth.get(0)) / 2,
                    (imode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

    }

    public static void updateDisplay() throws FileNotFoundException {
        GL.createCapabilities();
        glViewport(0, 0, WIDTH, HEIGHT);
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> glViewport(0, 0, width, height));
        Renderer.EnableDepthTest(true);

        StaticShader staticShader = new StaticShader();
        RawModel rawModel = ObjModel.loadObjModel("backpack");
        ModelTexture texture = new ModelTexture(DataLoader.loadTexture("highqualitybrick.jpg"));
        TexturedModel model = new TexturedModel(rawModel, texture);
        Renderer renderer = new Renderer(staticShader);

        Vector3f entityPos = new Vector3f(0.0f, 0.0f, -5.0f);
        Vector3f entityRotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f entityScale = new Vector3f(1.0f, 1.0f, 1.0f);
        Entity entity = new Entity(model, entityPos, entityRotation, entityScale);

        Vector3f cameraPos = new Vector3f(0.0f);
        float pitch = -90.0f;
        float yaw = 0.0f;
        Camera camera = new Camera(cameraPos, pitch, yaw);

        float lastFrame = 0.0f;
        while ( !glfwWindowShouldClose(window) ) {
            renderer.clearScreen(0.0f, 0.0f, 0.0f, 0.0f);
            renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float currentFrame = (float) glfwGetTime();
            float deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            camera.move(window, deltaTime);

            staticShader.use();
            staticShader.loadViewMatrix(camera.createViewMatrix());
            renderer.render(entity, staticShader);
            staticShader.stop();

            swapBuffers(window);
            pullEvents();
        }

        staticShader.cleanUp();
    }

    public static void closeDisplay() {
        DataLoader.cleanUp();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private static void swapBuffers(long window) {
        glfwSwapBuffers(window);
    }

    private static void pullEvents() {
        glfwPollEvents();
    }
}
