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

    private Mesh enemy;

    private int score;

    public SceneManager(){
        gameItemMap = new HashMap<>();
    }

    public void init() throws Exception{

        enemy = OBJLoader.loadMesh("src/resources/models/horse.obj", "src/resources/textures/horse.png", true);
        Material material = new Material(new Vector3f(1f, 1f, 1f), 1f);
        enemy.setMaterial(material);
        score = 0;

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
                        score += 1;
                        gameItems.remove(item);
                        i--;
                    }
                }

            }
        }

    }

    public int getScore() {
        return score;
    }

    public void update(){
        if(Math.random() < 0.01){
            FPSGameItem newItem = new FPSGameItem(enemy, "Enemy");
            newItem.setPosition(((float)Math.random() - 0.5f) * 20, 0,  ((float)Math.random() - 0.5f) * 20);
            gameItems.add(newItem);
        }
        for (FPSGameItem item : gameItems) {
            if (!item.isBg()) {
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
