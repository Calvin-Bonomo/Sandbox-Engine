package sandbox.engine;

import sandbox.engine.rendering.Renderer;

public interface Container {
    // Runs at the beginning of the program
    void onStartUp(Manager manager);
    // Runs every frame
    void onUpdate(Manager manager);
}