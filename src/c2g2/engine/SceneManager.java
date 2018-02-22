package c2g2.engine;

import c2g2.engine.graph.Material;
import c2g2.engine.graph.Mesh;
import c2g2.engine.graph.OBJLoader;
import c2g2.game.FPSGame;
import org.joml.Vector3f;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private Map<String, FPSGameItem> gameItemMap;

    private ArrayList<FPSGameItem> gameItems;

    public SceneManager(){
        gameItemMap = new HashMap<>();

    }

    public void init(){
    }

    public void getShot(Vector3f rayPos, Vector3f rayDir){
        for (int i=0; i<gameItems.size(); i++){
            FPSGameItem item = gameItems.get(i);
            if (!item.isBg()) {
                if (item.rayIntersect(rayPos, rayDir)){
                    item.changeHp(-10);
                    item.rayBack(rayDir, 0.5f);
                    System.out.println(item.getName() + item.getHp());
                    if (item.getHp() <= 0){
                        gameItems.remove(item);
                        i--;
                    }
                }

            }
        }

    }


    public void update(){
        for (FPSGameItem item : gameItems){
            if (!item.isBg()){
                item.update();
            }
        }

    }

    public void setGameItems(ArrayList<FPSGameItem> gameItems){
        this.gameItems = gameItems;
    }

    public ArrayList<FPSGameItem> getGameItems(){
        return gameItems;
    }
}
