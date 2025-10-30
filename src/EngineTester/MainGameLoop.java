package EngineTester;

import CoreEngine.GameEngine;

public class MainGameLoop {

    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.run();
        gameEngine.exit();
    }

}