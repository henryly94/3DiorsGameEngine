package c2g2.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class UserInput {
    /*
     * Original MouseInput
     * Modified to completely control the input
     * Use Callback function to interact with model.
     */

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;

    private boolean inWindow = false;

    private static final long defaultInterval = 20L;

    private boolean leftButtonPressed = false;

    private boolean rightButtonPressed = false;

    private static final float MOUSE_MIN_MOVE = 4;

    private IKeyPressCallBack mouseLeftPress;

    private IKeyPressCallBack mouseRightPress;

    private IKeyPressCallBack mouseLeftRelease;

    private IKeyPressCallBack mouseRightRelease;

    private Map<Integer, IKeyPressCallBack> keyPressCallBackMap;

    private Map<Integer, Long> keyPressLastTimeMap;

    private Map<Integer, Long> keyPressIntervalMap;

    private ArrayList<Integer> keyIds;

    public UserInput() {
        keyPressCallBackMap = new HashMap<>();
        keyPressLastTimeMap = new HashMap<>();
        keyPressIntervalMap = new HashMap<>();

        mouseLeftPress = null;
        mouseLeftRelease = null;
        mouseRightPress =  null;
        mouseRightRelease = null;

        keyIds = new ArrayList<>();
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                currentPos.x = xpos;
                currentPos.y = ypos;
            }
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                inWindow = entered;
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            }
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
                rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            }
        });


    }
    public void bindKeyCallBack(int key, IKeyPressCallBack callBack){
        bindKeyCallBack(key, callBack, defaultInterval);
    }

    public void bindKeyCallBack(int key, IKeyPressCallBack callBack, long interval){
        if (!keyPressCallBackMap.containsKey(key)){
            keyIds.add(key);
            keyPressLastTimeMap.put(key, 0L);
        }
        keyPressCallBackMap.put(key, callBack);
        keyPressIntervalMap.put(key, interval);
    }

    public void bindMousePressCallBack(IKeyPressCallBack callBack, boolean left){
        if (left) {
            this.mouseLeftPress = callBack;
        } else {
            this.mouseRightPress = callBack;
        }

    }

    public void bindMouseReleaseCallBack(IKeyPressCallBack callBack, boolean left) {
        if (left) {
            this.mouseLeftRelease = callBack;
        } else {
            this.mouseRightRelease = callBack;
        }
    }



    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input(Window window) {
        long curTime = System.currentTimeMillis();
        for (int keyId : keyIds){
            if (window.isKeyPressed(keyId)){
                if (curTime > keyPressLastTimeMap.get(keyId) + keyPressIntervalMap.get(keyId)) {
                    keyPressCallBackMap.get(keyId).CallBack();
                    keyPressLastTimeMap.put(keyId, curTime);
                }
            }
        }


        // Cursor behavior
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;

            if (deltax * deltax + deltay * deltay > MOUSE_MIN_MOVE){
                if (rotateX) {
                    displVec.y = (float) deltax;
                }
                if (rotateY) {
                    displVec.x = (float) deltay;
                }
            }
            previousPos.x = window.getWidth() / 2;
            previousPos.y = window.getHeight() / 2;
            glfwSetCursorPos(window.getWindowHandle(), previousPos.x, previousPos.y);
        } else if (inWindow) {
            previousPos.x = window.getWidth() / 2;
            previousPos.y = window.getHeight() / 2;
            glfwSetCursorPos(window.getWindowHandle(), previousPos.x, previousPos.y);
        }




    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
