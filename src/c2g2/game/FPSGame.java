package c2g2.game;

import c2g2.engine.*;
import c2g2.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;
import java.util.ArrayList;
import java.util.Date;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.glfw.GLFW.*;

public class FPSGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.06f;

    private static final float STEP = 0.1f;

    private static final float rate = 60.0f;

    private Renderer renderer;
    private FPSCamera camera;
    private DirectionalLight directionalLight;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private ArrayList<FPSGameItem> gameItems;

    private double last_frame_time;

    private double last_left_buttom_time;

    private long last_fire_time;

    private long fire_interval = 60;

    private boolean left_pressed = false;

    private SceneManager sceneManager;

    public FPSGame(){

        renderer = new Renderer();
        camera = new FPSCamera();
        camera.setPosition(0f, 1f, 5f);
        sceneManager = new SceneManager(){
            @Override
            public void init() {
                super.init();
                try {
                    Mesh mesh = OBJLoader.loadMesh("src/resources/models/gun.obj", "src/resources/textures/gun.png", true);
                    Material material = new Material(new Vector3f(1f, 1f, 1f), 1f);
                    mesh.setMaterial(material);

                    Mesh mesh2 = OBJLoader.loadMesh("src/resources/models/horse.obj", "src/resources/textures/horse.png", true);
                    mesh2.setMaterial(material);
                    FPSGameItem test = new FPSGameItem(mesh2, "gun");
                    FPSGameItem horse = new FPSGameItem(mesh2, "horse");
                    test.setPosition(0.25f, 0, -5.5f);
//                    camera.bindItem(test);

                    Mesh mesh3 = OBJLoader.loadMesh("src/resources/models/Skybox.obj", "src/resources/textures/sky.png", true);
                    mesh3.setMaterial(material);
                    FPSGameItem sky = new FPSGameItem(mesh3, "sky");
                    sky.setBg(true);
                    sky.setScale(10f);
                    sky.setPosition(0, 0, 0);

                    Mesh mesh4 = OBJLoader.loadMesh("src/resources/models/ground.obj", "src/resources/textures/grassblock.png", true);
                    mesh4.setMaterial(material);
                    FPSGameItem ground = new FPSGameItem(mesh4, "ground");
                    ground.setBg(true);
                    ground.setScale(1000f);
                    ground.setPosition(-500, 0, -500);
                    gameItems = new ArrayList<FPSGameItem>();
                    gameItems.add(test);
                    gameItems.add(horse);
                    gameItems.add(sky);
                    gameItems.add(ground);
                    setGameItems(gameItems);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        // Set hud
        FrontItem aim = new FrontItem("+", "src/resources/textures/alphabet.png", 16, 16);

        Mesh mesh = OBJLoader.loadMesh("src/resources/models/gun.obj", "src/resources/textures/gun.png", true);
        Material material = new Material(new Vector3f(1f, 1f, 1f), 1f);
        mesh.scaleMesh(0.02f, 0.02f, 0.02f);
        mesh.rotateMesh(new Vector3f(1, 0, 0), (float)Math.toRadians(-15));
        mesh.rotateMesh(new Vector3f(0, 1, 0), (float)Math.toRadians(200));
        mesh.rotateMesh(new Vector3f(0, 0, 1), (float)Math.toRadians(-10));
        mesh.translateMesh(new Vector3f(0.275f, -0.35f, 0.6f));
        mesh.setMaterial(material);
        FrontItem gun = new FrontItem(mesh);

        FrontItem[] frontItems = new FrontItem[]{aim, gun};
        renderer.setHud(frontItems);


        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, -1, -0.3f).normalize();
        Vector3f lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightDirection, lightIntensity);
        ambientLight = new Vector3f(1f, 1f, 1f);

        Vector3f lightPosition = new Vector3f(0, 0, -1);
        pointLight = new PointLight(lightColour, lightPosition, 0.0f);


        sceneManager.init();
        gameItems = sceneManager.getGameItems();

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.move(FPSCamera.DIRECTION.FORWARD, STEP);
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.move(FPSCamera.DIRECTION.LEFT, STEP);

        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.move(FPSCamera.DIRECTION.BACKWARD, STEP);

        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.move(FPSCamera.DIRECTION.RIGHT, STEP);

        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        Vector2f rotVec = mouseInput.getDisplVec();
        long cur_time = System.currentTimeMillis();
        if (rotVec.x != 0 || rotVec.y != 0) {
            camera.rotateTarget(-rotVec.y, -rotVec.x, MOUSE_SENSITIVITY);
        }
        if (!left_pressed && mouseInput.isLeftButtonPressed()){
            last_left_buttom_time = cur_time;
            left_pressed = true;
        } else if(!mouseInput.isLeftButtonPressed()) {
            left_pressed = false;
        }

        if (left_pressed){
            if (cur_time - last_fire_time > fire_interval){
                last_fire_time = cur_time;
                Vector3f rotate = new Vector3f(camera.getRotation());
                Vector3f target = camera.getTarget();
                Vector3f position = new Vector3f(camera.getPosition());
                Matrix4f m = new Matrix4f();
                m.identity();
                m.rotateAffineXYZ(rotate.x, rotate.y, rotate.z);
                m.transformDirection(target);
                sceneManager.getShot(position, target);
                camera.recoil();
            }

        }


        sceneManager.update();
    }

    @Override
    public void render(Window window) {
//        glDrawElements();

        GameItem[] gameItems = new GameItem[this.gameItems.size()];
        for (int i=0; i<this.gameItems.size(); i++){
            gameItems[i] = this.gameItems.get(i);
        }
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
