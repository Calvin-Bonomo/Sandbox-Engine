package sandbox.engine.rendering.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PartialSprite {
    int w;
    int h;

    int sectW;
    int ms;

    int[] p;
    int[] pSect;

    long lastTime;
    int cS;
    int maxS;

    // Split up a sprite-sheet (path) into columns of a given value (sectW)
    public PartialSprite(String path, int sectW, int ms) {
        this.sectW = sectW;

        this.ms = ms;

        lastTime = 0;
        cS = 0;

        try {
            BufferedImage img = ImageIO.read(new File(path));

            assert img.getWidth() % sectW == 0;

            w = img.getWidth();
            h = img.getHeight();
            p = img.getRGB(0, 0, w, h, null, 0, w);
            pSect = new int[sectW * h];
        } catch (IOException e) {
            e.printStackTrace();
        }

        maxS = w / sectW;
    }

    // Returns the pixel data for the current sprite-section
    public int[] getPixelData(int chosenFrame) {
        if (ms != 0) {
            long currentTime = System.nanoTime() / 1000000;

            if (lastTime == 0) lastTime = currentTime;

            if (currentTime - lastTime >= ms || lastTime == currentTime) {

                cS++;
                if (cS == maxS) cS = 0;
                lastTime = currentTime;

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < sectW; x++) {
                        pSect[x + y*sectW] = p[sectW*cS + x + y*w];
                    }
                }
            }
        } else {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < sectW; x++) {
                    pSect[x + y*sectW] = p[sectW*chosenFrame + x + y*w];
                }
            }
        }

        return pSect;
    }

    public int getWidth() {
        return sectW;
    }
    public int getHeight() {
        return h;
    }
}
