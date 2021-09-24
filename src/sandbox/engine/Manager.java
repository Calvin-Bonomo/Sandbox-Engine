package sandbox.engine;

import sandbox.engine.rendering.Display;
import sandbox.engine.rendering.Renderer;
import sandbox.engine.utility.Input;

public class Manager implements Runnable{
    private String title;
    private int width;
    private int height;
    private float scale;

    private Display display;
    private Renderer renderer;
    private Input input;
    private Container project;

    public Manager(Container project, String title, int width, int height, float scale) {
        this.project = project;
        this.title = title;
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    public void start() {
        display = new Display(this, title);
        renderer = new Renderer(this);
        input = new Input(this);

        Thread t = new Thread(this);
        t.run();
    }

    public void run() {
        boolean running = true;
        project.onStartUp(this);

        input.updateKeys();

        while (running) {
            input.updateKeys();
            renderer.clear();
            // Maybe find a way to do double threading if the program calls for it
            project.onUpdate(this);
            display.update();
            // TODO: Maybe find a better solution or lock FPS?
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Display getDisplay() {
        return display;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Input getInput() {
        return input;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getScale() {
        return scale;
    }
}
