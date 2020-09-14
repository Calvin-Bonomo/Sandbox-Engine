package sandbox.engine.utility;

import sandbox.engine.Manager;

import java.awt.*;
import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private float s;

    private boolean[] key = new boolean[256];
    private boolean[] key1 = new boolean[256];

    private int[] mousePos = new int[2];

    private boolean[] mouse = new boolean[3];
    private boolean[] mouse1 = new boolean[3];


    public Input(Manager manager) {
        s = manager.getScale();

        Canvas c = manager.getDisplay().getCanvas();
        c.addKeyListener(this);
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
        c.addMouseWheelListener(this);
    }

    public void updateKeys() {
        key1 = key;
    }

    public boolean isKeyDown(int keyCode) {
        if (keyCode < 0 || keyCode >= key.length) return false;
        return key1[keyCode];
    }

    public int[] getMousePos() {
        return mousePos;
    }

    public boolean isMouseDown(int mouseButton) {
        if (mouseButton < 0 || mouseButton > mouse.length) return false;
        return mouse[mouseButton];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        key[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        key[e.getKeyCode()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() <= mouse.length) mouse[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() <= mouse.length) mouse[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePos[0] = (int)(e.getX() / s);
        mousePos[1] = (int)(e.getY() / s);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos[0] = (int)(e.getX() / s);
        mousePos[1] = (int)(e.getY() / s);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
