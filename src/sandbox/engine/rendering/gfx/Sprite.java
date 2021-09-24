package sandbox.engine.rendering.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Sprite {
    private int[] p;
    private int w;
    private int h;

    // TODO: Refine image loading
    public Sprite(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));

            w = img.getWidth();
            h = img.getHeight();
            p = img.getRGB(0, 0, w, h, null, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getPixelData() {
        return p;
    }

    public int getWidth() {
        return w;
    }
    public int getHeight() {
        return h;
    }
}
