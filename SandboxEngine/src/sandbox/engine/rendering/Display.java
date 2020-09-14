package sandbox.engine.rendering;

import sandbox.engine.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Display {
    private JFrame f;
    private Canvas c;
    private Graphics g;
    private BufferedImage bI;
    private BufferStrategy bS;

    public Display(Manager manager, String title) {
        int width = manager.getWidth();
        int height = manager.getHeight();
        float scale = manager.getScale();

        bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        c = new Canvas();
        Dimension d = new Dimension((int)(width  * scale),
                                    (int)(height * scale));
        c.setMaximumSize(d);
        c.setPreferredSize(d);
        c.setMinimumSize(d);

        f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.add(c, BorderLayout.CENTER);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);

        c.createBufferStrategy(2);
        bS = c.getBufferStrategy();
        g = bS.getDrawGraphics();
    }

    public void update() {
        g.drawImage(bI, 0, 0, c.getWidth(), c.getHeight(), null);
        bS.show();
    }

    public BufferedImage getImage() {
        return bI;
    }

    public JFrame getFrame() {
        return f;
    }

    public Canvas getCanvas() {
        return c;
    }
}
