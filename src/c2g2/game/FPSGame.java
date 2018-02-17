package c2g2.game;

import c2g2.engine.*;
import c2g2.engine.graph.*;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class FPSGame implements IGameLogic {

    private Renderer renderer;
    private Camera camera;
    private DirectionalLight directionalLight;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private GameItem[] gameItems;

    private final float STEP = 0.1f;

    private SceneManager sceneManager;

    public FPSGame(){

        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0f, 1f, 5f);
        sceneManager = new SceneManager();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, -1, -0.3f).normalize();
        Vector3f lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightDirection, lightIntensity);
        ambientLight = new Vector3f(0.6f, 0.6f, 0.6f);

        Vector3f lightPosition = new Vector3f(0, 0, -1);
        pointLight = new PointLight(lightColour, lightPosition, 0.0f);


        Mesh mesh = OBJLoader.loadMesh("../model/bullet.obj", "../model/bullet.png", true);
        Material material = new Material(new Vector3f(1f, 1f, 1f), 1f);
        mesh.setMaterial(material);

        Mesh mesh2 = OBJLoader.loadMesh("../model/horse.obj", "../model/horse_tex.png", true);
        mesh2.setMaterial(material);


        GameItem test = new GameItem(mesh);
        GameItem horse = new GameItem(mesh2);
        test.setScale(0.005f);
        test.setRotation(0f, 105f, 25f);
        test.setPosition(0.2f, 0.8f, 5f);
        gameItems = new GameItem[]{test, horse};
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            Vector3f cameraPos = camera.getPosition();
            camera.setPosition(cameraPos.x, cameraPos.y, cameraPos.z - STEP);
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            Vector3f cameraPos = camera.getPosition();
            camera.setPosition(cameraPos.x - STEP, cameraPos.y, cameraPos.z);

        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            Vector3f cameraPos = camera.getPosition();
            camera.setPosition(cameraPos.x, cameraPos.y, cameraPos.z + STEP);

        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            Vector3f cameraPos = camera.getPosition();
            camera.setPosition(cameraPos.x + STEP, cameraPos.y, cameraPos.z );

        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        sceneManager.update();
        gameItems = sceneManager.getGameItems();
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }

    }
}
