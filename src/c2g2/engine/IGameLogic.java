package c2g2.engine;

public interface IGameLogic {

    void init(Window window, UserInput userInput) throws Exception;
    
    void input(Window window, UserInput mouseInput);

    void update(float interval);
    
    void render(Window window);
    
    void cleanup();
}