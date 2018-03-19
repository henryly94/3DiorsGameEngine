package c2g2.game;

import c2g2.engine.*;
import c2g2.engine.graph.*;
import c2g2.engine.IKeyPressCallBack;
import c2g2.engine.sound.SoundBuffer;
import c2g2.engine.sound.SoundSource;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.openal.ALC10.alcOpenDevice;


import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

import static org.lwjgl.glfw.GLFW.*;

public class FPSGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.06f;

    private static final float STEP = 0.1f;

    private static final float rate = 60.0f;

    private long last_fire_time;

    private long last_left_buttom_time;

    private long fire_interval = 60;

    private int fireCount = 0;

    private int curScore = 0;

    private boolean left_pressed = false;

    private Renderer renderer;
    private FPSCamera camera;
    private UserInput userInput;
    private DirectionalLight directionalLight;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private ArrayList<FPSGameItem> gameItems;



    private SceneManager sceneManager;

    private SoundSource gunShot, bgSound;

    private SoundBuffer gunShotBuf, bgSoundBuf;

    private FrontItem score;

    private long soundDevice;

    private long soundContext;

    public FPSGame(){

        renderer = new Renderer();
        camera = new FPSCamera();
        camera.setPosition(0f, 1f, 5f);
        sceneManager = new SceneManager(){
            @Override
            public void init() throws Exception{
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

    private void setHud() throws Exception{
        // Set hud
        FrontItem aim = new FrontItem("+", "src/resources/textures/alphabet.png", 16, 16);

        aim.getMesh().scaleMesh(0.002f, 0.002f, 1);
        aim.getMesh().translateMesh(new Vector3f(-0.038f, -0.06f, 0));
        Mesh mesh = OBJLoader.loadMesh("src/resources/models/gun.obj", "src/resources/textures/gun1.png", true);
        Material material = new Material(new Vector3f(1f, 1f, 1f), 1f);
        mesh.scaleMesh(0.02f, 0.02f, 0.02f);
        mesh.rotateMesh(new Vector3f(1, 0, 0), (float)Math.toRadians(-15));
        mesh.rotateMesh(new Vector3f(0, 1, 0), (float)Math.toRadians(200));
        mesh.rotateMesh(new Vector3f(0, 0, 1), (float)Math.toRadians(-10));
        mesh.translateMesh(new Vector3f(0.275f, -0.35f, 0.6f));
        mesh.setMaterial(material);
        FrontItem gun = new FrontItem(mesh);

        score = new FrontItem(curScore + "", "src/resources/textures/alphabet.png", 16, 16);
        score.getMesh().scaleMesh(0.002f, 0.002f, 1);
        score.getMesh().translateMesh(new Vector3f( 0.6f, 0.45f, 0));
        FrontItem[] frontItems = new FrontItem[]{aim, gun, score};
        renderer.setHud(frontItems);

    }

    private void setSound() throws Exception{

        this.soundDevice = alcOpenDevice((ByteBuffer) null);
        if (soundDevice == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(soundDevice);
        this.soundContext = alcCreateContext(soundDevice, (IntBuffer) null);
        if (soundContext == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(soundContext);
        AL.createCapabilities(deviceCaps);
        gunShotBuf = new SoundBuffer("src/resources/sounds/gunshot.ogg");
        gunShot = new SoundSource(false, false);
        gunShot.setBuffer(gunShotBuf.getBufferId());
        bgSoundBuf = new SoundBuffer("src/resources/sounds/bg1.ogg");
        bgSound = new SoundSource(true, false);
        bgSound.setBuffer(bgSoundBuf.getBufferId());
    }

    @Override
    public void init(Window window, UserInput userInput) throws Exception {
        renderer.init(window);

        setUserInput(userInput);

        setHud();

        setLight();

        setSound();

        bgSound.play();

        sceneManager.init();
        gameItems = sceneManager.getGameItems();

    }


    private void setUserInput(UserInput userInput) {
        this.userInput = userInput;
        //Register Input Here
        userInput.bindKeyCallBack(GLFW_KEY_W,
                ()-> camera.move(FPSCamera.DIRECTION.FORWARD, STEP));

        userInput.bindKeyCallBack(GLFW_KEY_S,
                ()-> camera.move(FPSCamera.DIRECTION.BACKWARD, STEP));

        userInput.bindKeyCallBack(GLFW_KEY_A,
                ()-> camera.move(FPSCamera.DIRECTION.LEFT, STEP));

        userInput.bindKeyCallBack(GLFW_KEY_D,
                ()->camera.move(FPSCamera.DIRECTION.RIGHT, STEP));

        userInput.bindKeyCallBack(GLFW_KEY_M,
                ()-> {
                    if (bgSound.isPlaying()) {
                        bgSound.pause();
                    } else {
                        bgSound.play();
                    }
                });

//        userInput.bindKeyCallBack(GLFW_MOUSE_BUTTON_LEFT,
//                ()->{
//                    gunShot.play();
//                    Vector3f rotate = new Vector3f(camera.getRotation());
//                    Vector3f target = camera.getTarget();
//                    Vector3f position = new Vector3f(camera.getPosition());
//                    Matrix4f m = new Matrix4f();
//                    m.identity();
//                    m.rotateAffineXYZ(rotate.x, rotate.y, rotate.z);
//                    m.transformDirection(target);
//                    sceneManager.getShot(position, target);
//                    fireCount ++;
//                    if (fireCount >= 3) {
//                        camera.recoil();
//                    }
//                }, fire_interval);
    }

    private void setLight() {
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, -1, -0.3f).normalize();
        Vector3f lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightDirection, lightIntensity);
        ambientLight = new Vector3f(1f, 1f, 1f);

        Vector3f lightPosition = new Vector3f(0, 0, -1);
        pointLight = new PointLight(lightColour, lightPosition, 0.0f);

    }

    @Override
    public void input(Window window, UserInput mouseInput) {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.move(FPSCamera.DIRECTION.FORWARD, STEP);
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.move(FPSCamera.DIRECTION.LEFT, STEP);

        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.move(FPSCamera.DIRECTION.BACKWARD, STEP);

        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.move(FPSCamera.DIRECTION.RIGHT, STEP);
        } else if (window.isKeyPressed(GLFW_KEY_M)){
            if (bgSound.isPlaying()){
                bgSound.pause();
            } else {
                bgSound.play();
            }
        }
    }

    @Override
    public void update(float interval) {
        Vector2f rotVec = userInput.getDisplVec();
        long cur_time = System.currentTimeMillis();
        if (rotVec.x != 0 || rotVec.y != 0) {
            camera.rotateTarget(-rotVec.y, -rotVec.x, MOUSE_SENSITIVITY);
        }
        if (!left_pressed && userInput.isLeftButtonPressed()){
            last_left_buttom_time = cur_time;
            left_pressed = true;
        } else if(!userInput.isLeftButtonPressed()) {
            left_pressed = false;
            fireCount = 0;
        }

        if (left_pressed){
            if (cur_time - last_fire_time > fire_interval){
                gunShot.play();
                last_fire_time = cur_time;
                Vector3f rotate = new Vector3f(camera.getRotation());
                Vector3f target = camera.getTarget();
                Vector3f position = new Vector3f(camera.getPosition());
                Matrix4f m = new Matrix4f();
                m.identity();
                m.rotateAffineXYZ(rotate.x, rotate.y, rotate.z);
                m.transformDirection(target);
                sceneManager.getShot(position, target);
                fireCount ++;
                if (fireCount >= 3) {
                    camera.recoil();
                }
            }

        }


        sceneManager.update();
        if (curScore != sceneManager.getScore()) {
            curScore = sceneManager.getScore();
            score.changeText("" + curScore);
            score.getMesh().scaleMesh(0.002f, 0.002f, 1);
            score.getMesh().translateMesh(new Vector3f( 0.6f, 0.45f, 0));
        }
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

        gunShot.cleanup();

        gunShotBuf.cleanup();

        if (soundContext != NULL) {
            alcDestroyContext(soundContext);
        }
        if (soundDevice != NULL) {
            alcCloseDevice(soundDevice);
        }

    }
}
