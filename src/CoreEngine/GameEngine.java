package CoreEngine;

import RenderEngine.DisplayManager;

import java.io.FileNotFoundException;

public class GameEngine {

    public GameEngine() {
        DisplayManager.createDisplay();
    }

    public void run() {
        DisplayManager.updateDisplay();
    }

    public void exit() {
        DisplayManager.closeDisplay();
    }
}
