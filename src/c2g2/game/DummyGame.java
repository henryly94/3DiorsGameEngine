package c2g2.game;

import c2g2.engine.GameItem;
import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Window;
import c2g2.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    
    private static final float SCALE_STEP = 0.01f;
    
    private static final float TRANSLATE_STEP = 0.01f;
    
    private static final float ROTATION_STEP = 0.3f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;
    
    private int currentObj;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0f, 1f, 5f);
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        currentObj=0;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        float reflectance = 1f;

        // Different Usage Here
        // Mesh mesh = new Mesh();
//         Mesh mesh = OBJLoader.loadMesh("src/resources/models/bunny.obj", "src/resources/textures/horse.png", false);
        Mesh mesh = OBJLoader.loadMesh("src/resources/models/horse.obj", "src/resources/textures/horse.png", true);//new Mesh();  // comment this line when you enable OBJLoader
        Mesh mesh2 = OBJLoader.loadMesh("src/resources/models/bullet.obj","src/resources/textures/bullet.png", true);
        Material material = new Material(new Vector3f(1f, 1f, 1f), reflectance);
        

        mesh.setMaterial(material);
        GameItem gameItem = new GameItem(mesh);

        // ======================= ======================= ======================= =======================
        // Only for bullet-horse demonstration
        mesh2.setMaterial(material);
        mesh2.scaleMesh(0.001f, 0.001f, 0.001f);
        GameItem gameItem2 = new GameItem(mesh2);
        gameItem2.setPosition(4 * (float)Math.tan(Math.toRadians(30)), 2f, 4f + 1f);
        gameItem2.setRotation(0f, 120f, 0f);
        gameItems = new GameItem[]{gameItem, gameItem2};
        // ======================= ======================= ======================= =======================

        // Uncomment this line when you want to see other models.
        //gameItems = new GameItem[]{gameItem};

        ambientLight = new Vector3f(1f, 1f, 1f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, -1);
        float lightIntensity = 0.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {

        if(window.isKeyPressed(GLFW_KEY_Q)){
            //select current object
            currentObj = currentObj + 1 + gameItems.length;
            currentObj = currentObj % gameItems.length;
        }
        else if(window.isKeyPressed(GLFW_KEY_W)){
            //select current object
            currentObj = currentObj - 1 + gameItems.length;
            currentObj = currentObj % gameItems.length;
        }
        else if(window.isKeyPressed(GLFW_KEY_E)){
            //scale object
            float curr = gameItems[currentObj].getScale();
            gameItems[currentObj].setScale(curr+SCALE_STEP);
        }
        else if(window.isKeyPressed(GLFW_KEY_R)){
            //scale object
            float curr = gameItems[currentObj].getScale();
            gameItems[currentObj].setScale(curr-SCALE_STEP);
        }
        else if(window.isKeyPressed(GLFW_KEY_T)){
            //move object x by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x+TRANSLATE_STEP, curr.y, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_Y)){
            //move object x by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x-TRANSLATE_STEP, curr.y, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_U)){
            //move object y by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y+TRANSLATE_STEP, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_I)){
            //move object y by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y-TRANSLATE_STEP, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_O)){
            //move object z by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y, curr.z+TRANSLATE_STEP);
        }
        else if(window.isKeyPressed(GLFW_KEY_P)){
            //move object z by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y, curr.z-TRANSLATE_STEP);
        }
        else if(window.isKeyPressed(GLFW_KEY_A)){
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x+ROTATION_STEP, curr.y, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_S)){
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x-ROTATION_STEP, curr.y, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_D)){
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y+ROTATION_STEP, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_F)){
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y-ROTATION_STEP, curr.z);
        }
        else if(window.isKeyPressed(GLFW_KEY_G)){
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y, curr.z+ROTATION_STEP);
        }
        else if(window.isKeyPressed(GLFW_KEY_H)){
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y, curr.z-ROTATION_STEP);
        }
        else if(window.isKeyPressed(GLFW_KEY_0)){
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().translateMesh(new Vector3f(0f,0.005f,0.001f));
        }
        else if(window.isKeyPressed(GLFW_KEY_9)){
            //rotation by manipulating mesh
            Vector3f v = new Vector3f(0, 1, 0).normalize();
            gameItems[currentObj].getMesh().rotateMesh(v, 0.3f);
        }
        else if(window.isKeyPressed(GLFW_KEY_8)){
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().scaleMesh(0.99f,0.99f,0.99f);
        }
        else if(window.isKeyPressed(GLFW_KEY_7)){
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().reflectMesh(new Vector3f(1f,0f,0f), new Vector3f(0f, 1f, 0f));
        }
        else if(window.isKeyPressed(GLFW_KEY_1)){
            //get screenshot
            renderer.writePNG(window);
//            gameItems[currentObj].getMesh().setMesh();
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println(rotVec);
            Vector3f curr = gameItems[currentObj].getRotation();
            curr = camera.getRotation();
//            gameItems[currentObj].setRotation(curr.x+ rotVec.x * MOUSE_SENSITIVITY, curr.y+rotVec.y * MOUSE_SENSITIVITY, 0);
            camera.setRotation(curr.x - (float)Math.toRadians(rotVec.x * MOUSE_SENSITIVITY),
                    curr.y - (float)Math.toRadians(rotVec.y * MOUSE_SENSITIVITY),
                    0);
        }

        // ====================== ====================== ====================== ====================== ====================== ======================
        // Only for bullet-horse demonstration, delete when you want to see other model.
        Vector3f curr_pos = gameItems[1].getPosition();
        gameItems[1].setPosition(curr_pos.x - 0.02f * (float)Math.tan(Math.toRadians(30)), curr_pos.y,  curr_pos.z-0.02f);
        // ====================== ====================== ====================== ====================== ====================== ======================

        // Update directional light direction, intensity and color
        lightAngle += 1.1f;
        
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 90) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 -  (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
//        renderer.writePNG(window) ;
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}
