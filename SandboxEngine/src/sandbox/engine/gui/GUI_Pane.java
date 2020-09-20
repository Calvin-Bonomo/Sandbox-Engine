package sandbox.engine.gui;

import sandbox.engine.Manager;
import sandbox.engine.rendering.gfx.Sprite;
import sandbox.engine.utility.Input;

import java.util.ArrayList;

public class GUI_Pane {
    private int[] paneDimensions;
    private Sprite sprite;
    private ArrayList<GUI_Element> elements;

    public GUI_Pane(int x, int y, int w, int h, int layer, Sprite sprite) {
        paneDimensions = new int[] {x, y, w, h, layer};
        this.sprite = sprite;
        elements = new ArrayList<>();
    }

    public void updatePane(Manager manager) {
        Input input = manager.getInput();
        int[] mousePos = input.getMousePos();
        boolean mouseDown = input.isMouseDown(1);

        for (GUI_Element element : elements) {
            element.doMouseInteractionCheck(mousePos, mouseDown, paneDimensions);
        }

        for (GUI_Element element : elements) {
            element.update();
        }
    }

    public void addGUI_Element(GUI_Element element) {
        elements.add(element);
    }
    public ArrayList<GUI_Element> getElements() {
            return elements;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int[] getDimensions() {
        return paneDimensions;
    }
}
