package c2g2.game;

import c2g2.engine.GameEngine;
import c2g2.engine.IGameLogic;

import java.util.ArrayList;

public class Main {
 
    public static void main(String[] args) {
        try {
            boolean vSync = true;
//            IGameLogic gameLogic = new DummyGame();
            IGameLogic gameLogic = new FPSGame();
            GameEngine gameEng = new GameEngine("COMSW4160-PA1", 600, 480, vSync, gameLogic);
            gameEng.start();

        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}