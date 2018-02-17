package c2g2.engine;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private Map<String, GameItem> gameItemMap;


    public SceneManager(){
        gameItemMap = new HashMap<>();

    }


    public void update() {

    }

    public GameItem[] getGameItems(){
        GameItem[] gameItems = new GameItem[]{};
        return gameItems;
    }
}
