package EngineTester;

import CoreEngine.GameEngine;

import java.io.FileNotFoundException;

public class MainGameLoop {

    public static void main(String[] args) throws FileNotFoundException {
        GameEngine gameEngine = new GameEngine();
        gameEngine.run();
        gameEngine.exit();
    }

}