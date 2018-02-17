package c2g2.game;

import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Window;
import c2g2.engine.graph.Camera;

public class FPSGame implements IGameLogic {

    private Renderer renderer;
    private Camera camera;

    public FPSGame(){

        renderer = new Renderer();
        camera = new Camera();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

    }

    @Override
    public void render(Window window) {

    }

    @Override
    public void cleanup() {

    }
}
