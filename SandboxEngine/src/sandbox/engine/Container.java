package sandbox.engine;

import sandbox.engine.rendering.Renderer;

public abstract class Container {
    // Runs at the beginning of the program
    public abstract void onStartUp(Manager manager);

    // Runs every frame
    public abstract void onUpdate(Manager manager);
}