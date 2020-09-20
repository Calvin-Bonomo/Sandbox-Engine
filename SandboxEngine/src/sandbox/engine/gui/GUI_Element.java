package sandbox.engine.gui;

import sandbox.engine.rendering.gfx.Sprite;

public abstract class GUI_Element {
    private String id;
    private int x, y, w, h;
    private Sprite sprite;
    private GUI_EventListener listener;
    private boolean hovering, startInteract, interacting, endInteract;
    private boolean active = true;

    public GUI_Element(String id, int x, int y, Sprite sprite, GUI_EventListener listener) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.listener = listener;
        w = sprite.getWidth();
        h = sprite.getHeight();
    }

    public void doMouseInteractionCheck(int[] mousePos, boolean mouseDown, int[] paneDimensions) {
        if (listener != null && active) {
            /*
             * 0 == hovering check
             * 1 == start interact check
             * 2 == interacting check
             * 3 == end interact check
             * */
            boolean[] mouseChecks = new boolean[4];
            if ((mousePos[0] >= x + paneDimensions[0] && mousePos[0] <= (x + w) + (paneDimensions[0])) &&
                    (mousePos[1] >= y + paneDimensions[1] && mousePos[1] <= (y + h) + (paneDimensions[1]))) {
                if (mouseDown) {
                    // Player was previously interacting with the element
                    if (interacting || startInteract) {
                        mouseChecks[2] = true;
                    } else { // Player just started interacting with the element
                        mouseChecks[1] = true;
                    }
                } else { // Player is hovering over the element
                    mouseChecks[0] = true;
                }
            } else {
                // Check if the user was interacting with the element one frame before
                if (interacting || startInteract) {
                    mouseChecks[3] = true;
                }
            }

            hovering = mouseChecks[0];
            startInteract = mouseChecks[1];
            interacting = mouseChecks[2];
            endInteract = mouseChecks[3];
        }
    }

    public Sprite getSprite() {
        return sprite;
    }
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public String getId() {
        return id;
    }

    public boolean isHovering() {
        return hovering;
    }
    public boolean isStartInteract() {
        return startInteract;
    }
    public boolean isInteracting() {
        return interacting;
    }
    public boolean isEndInteract() {
        return endInteract;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public GUI_EventListener getListener() {
        return listener;
    }

    public abstract void update();
}
