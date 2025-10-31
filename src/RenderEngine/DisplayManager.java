package RenderEngine;

import CoreEngine.Camera;
import CoreEngine.DataLoader;
import RenderEngine.Lighting.Light;
import RenderEngine.Models.*;
import Shaders.LightShader;
import Shaders.StaticShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

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
        glfwSwapInterval(0);
        glfwShowWindow(window);

    }
    public static void updateDisplay() {
        GL.createCapabilities();
        glViewport(0, 0, WIDTH, HEIGHT);
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> glViewport(0, 0, width, height));
        Renderer.EnableDepthTest(true);
        STBImage.stbi_set_flip_vertically_on_load(true);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPos(window, WIDTH / 2.0, HEIGHT / 2.0);
        glfwSetCursorPosCallback(window, Camera::mouse_callback);
        glfwSetScrollCallback(window, Camera::mouse_scrollBack);

        StaticShader staticShader = new StaticShader();
        Model.ModelAsset cubeTerrain = Model.loadModel("cubeTerrainModel/cubeTerrainModel.obj");
        Model.ModelAsset cube = Model.loadModel("backPack/backpack.obj");
        Vector3f cameraPos = new Vector3f(0.0f);
        float pitch = 0.0f;
        float yaw = -90.0f;
        Camera camera = new Camera(cameraPos, new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), pitch, yaw, 45.0f);
        Renderer renderer = new Renderer(staticShader);

        LightShader lightShader = new LightShader();
        Model.ModelAsset lightModel = Model.loadModel("cube/cube.obj");
        Renderer lightRenderer = new Renderer(lightShader);

        Vector3f lightRotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f lightScale = new Vector3f(0.2f, 0.2f, 0.2f);

        Vector3f cubeTerrainPos = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f cubeTerrainRotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f cubeTerrainScale = new Vector3f(1.0f, 1.0f, 1.0f);

        Vector3f cubePos = new Vector3f(1.0f, 0.0f, -3.0f);
        Vector3f cubeRotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f cubeScale = new Vector3f(1.0f, 1.0f, 1.0f);

        Vector3f lightPos = new Vector3f(1.0f, 0.0f, -5.0f);
        Vector3f lightColor = new Vector3f(1.0f);
        Light light = new Light(lightPos, lightColor);

        float lastFrame = 0.0f;
        while ( !glfwWindowShouldClose(window) ) {
            renderer.clearScreen(0.0f, 0.0f, 0.0f, 0.0f);
            renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float currentFrame = (float) glfwGetTime();
            float deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            cubeRotation.add(new Vector3f((float) Math.sin((float) glfwGetTime())).mul(deltaTime));
            camera.move(window, deltaTime);

            Matrix4f viewMatrix = camera.createViewMatrix();

            staticShader.use();
            staticShader.loadViewMatrix(viewMatrix);
            staticShader.loadLight(light, camera);

            renderer.renderEntity(cubeTerrain, cubeTerrainPos, cubeTerrainRotation, cubeTerrainScale, staticShader);
            renderer.renderEntity(cube, cubePos, cubeRotation, cubeScale, staticShader);

            staticShader.stop();

            lightShader.use();
            lightShader.loadViewMatrix(viewMatrix);
            lightShader.loadLight(light);

            lightRenderer.renderEntity(lightModel, lightPos, lightRotation, lightScale, lightShader);

            lightShader.stop();

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
