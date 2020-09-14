package sandbox.engine.rendering;

import sandbox.engine.utility.RGBConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font {
    private Map<Integer, int[]> pixelDataLookup;
    private int h;

    public Font(String path) {
        pixelDataLookup = new HashMap<>();

        int w;
        int[] p;
        try {
            BufferedImage img = ImageIO.read(new File(path));

            w = img.getWidth();
            h = img.getHeight() - 1;

            p = img.getRGB(0, 0, w, img.getHeight(), null, 0, w);

            // Get the spacing of each character in the font image
            int[] spacingLookup = new int[128];
            int charIndex = 0;

            boolean charStarted = false;

            int startChar = RGBConverter.RGBToHex(255, 255, 255, 0);
            int endChar   = RGBConverter.RGBToHex(255, 0, 0, 255);

            for (int x = 0; x < w; x ++) {
                if (charStarted) {
                    if (p[x] == endChar) {
                        charStarted = false;
                    } else {
                        spacingLookup[charIndex] ++;
                    }
                } else {
                    if (p[x] == startChar) {
                        charStarted = true;
                        charIndex ++;
                    }
                }
            }

            assert charIndex == 127;

            int xOffset = 1;

            // Add pixel data to the lookup table
            for (int i = 0; i < 128; i ++) {
                int[] charPixelData;

                charPixelData = img.getRGB(xOffset, 1, spacingLookup[i], h, null, 0, spacingLookup[i]);

                pixelDataLookup.put(i, charPixelData);
                xOffset += 2 + spacingLookup[i];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getCharData(int fontChar) {
        return pixelDataLookup.get(fontChar);
    }

    public int getFontHeight() {
        return h;
    }
}
