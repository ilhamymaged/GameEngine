package RenderEngine;

import RenderEngine.Models.ModelTexture;
import RenderEngine.Models.RawModel;
import RenderEngine.Models.TexturedModel;
import Shaders.Shader;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
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
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final String TITLE = "3D GAME ENGINE";

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

    public static void updateDisplay() {
        GL.createCapabilities();
        glViewport(0, 0, WIDTH, HEIGHT);
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> glViewport(0, 0, width, height));

        float[] vertices = {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f,0.0f,
             0.5f, -0.5f,0.0f,
             0.5f, 0.5f, 0.0f,
        };

        float[] texCoords = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
        };

        int[] indices = {
                0,1,3,
                3,1,2
        };

        Shader staticShader = new Shader("simple");
        RawModel rawModel = DataLoader.loadRawModel(vertices, texCoords, indices);
        ModelTexture texture = new ModelTexture(DataLoader.loadTexture("highqualitybrick.jpg"));
        TexturedModel model = new TexturedModel(rawModel, texture);

        while ( !glfwWindowShouldClose(window) ) {
            Renderer.clearScreen(0.0f, 0.0f, 0.0f, 0.0f);
            Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            staticShader.use();
            Renderer.render(model);
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
