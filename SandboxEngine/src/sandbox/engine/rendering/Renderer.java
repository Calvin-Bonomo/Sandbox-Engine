package sandbox.engine.rendering;

import sandbox.engine.Manager;
import sandbox.engine.gui.GUI_Element;
import sandbox.engine.gui.GUI_Pane;
import sandbox.engine.rendering.gfx.Font;
import sandbox.engine.rendering.gfx.PartialSprite;
import sandbox.engine.rendering.gfx.Sprite;
import sandbox.engine.utility.RGBConverter;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Renderer {
    private int pW;
    private int pH;
    private int[] p;
    private int[] zBuff;

    public Renderer(Manager manager) {
        pW = manager.getWidth();
        pH = manager.getHeight();
        p = ((DataBufferInt)manager.getDisplay().getImage().getRaster().getDataBuffer()).getData();
        zBuff = new int[p.length];
        Arrays.fill(zBuff, Integer.MAX_VALUE);
    }

    public void clear() {
        Arrays.fill(p, RGBConverter.RGBToHex(255, 0, 0, 0));
        Arrays.fill(zBuff, Integer.MAX_VALUE);
    }

    public void drawPixel(int x, int y, int z, int c) {
        if ((x < 0 || x >= pW) || (y < 0 || y >= pH)) return;
        int index = x + y*pW;

        // Separated alpha channels for use in alpha blending and visibility checks
        float savedColorAlpha = ((p[index] >> 24) & 0xff)/255f;
        float newColorAlpha   = ((c >> 24) & 0xff)/255f;
        if ((z > zBuff[index] && savedColorAlpha == 1f) || newColorAlpha == 0f) return;

        // Alpha blending algorithm
        if ((z <= zBuff[index] && newColorAlpha != 1f) || (z > zBuff[index] && savedColorAlpha != 1f)) {
            float newA = newColorAlpha + savedColorAlpha*(1f - newColorAlpha);

            float newR = ((((c >> 16) & 0xff) / 255f)*newColorAlpha + ((((p[index] >> 16) & 0xff) / 255f)*savedColorAlpha)*(1f - newColorAlpha))/newA;
            float newG = ((((c >> 8) & 0xff) / 255f)*newColorAlpha  + ((((p[index] >> 8) & 0xff) / 255f)*savedColorAlpha)*(1f - newColorAlpha))/newA;
            float newB = (((c & 0xff) / 255f)*newColorAlpha         + (((p[index] & 0xff) / 255f)*savedColorAlpha)*(1f - newColorAlpha))/newA;

            p[index] = RGBConverter.RGBToHex((int)(newA * 255f), (int)(newR * 255f), (int)(newG * 255f), (int)(newB * 255f));
            zBuff[index] = Math.min(z, zBuff[index]);
        } else {
            p[index] = c;
            zBuff[index] = z;
        }
    }

    // Draw a line using DDA algorithm
    public void drawLine(int x1, int y1, int x2, int y2, int z, int c) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        float steps = Math.max(Math.abs(dx), Math.abs(dy));

        float x = x1;
        float y = y1;

        drawPixel(x1, y1, z, c);

        for (int i = 0; i < steps; i ++) {
            x += (float)dx / steps;
            y += (float)dy / steps;

            drawPixel((int)x, (int)y, z, c);
        }
    }

    // Draw an empty rectangle
    public void drawRect(int x, int y, int width, int height, int z, int c) {
        drawLine(x, y, x + width - 1, y, z, c);
        drawLine(x + width, y, x + width, y + height - 1, z, c);
        drawLine(x + width, y + height, x + 1, y + height, z, c);
        drawLine(x, y + height, x, y  + 1, z, c);
    }
    // Draw a filled rectangle
    public void fillRect(int x, int y, int width, int height, int z, int c) {
        for (int dy = 0; dy < height; dy ++) {
            drawLine(x, y + dy, x + width, y + dy, z, c);
        }
    }

    // Draw an empty triangle
    public void drawTri2D(int x1, int y1, int x2, int y2, int x3, int y3, int z, int c) {
        drawLine(x1, y1, x2, y2, z, c);
        drawLine(x2, y2, x3, y3, z, c);
        drawLine(x3, y3, x1, y1, z, c);
    }
    // Draw a filled triangle
    public void fillTri2D(int x1, int y1, int x2, int y2, int x3, int y3, int z, int c) {
        // Sort the points by their y-value
        // A = lowest y-value
        // C = highest y-value
        float Ax, Ay, Bx, By, Cx, Cy;

        Cy = Math.max(y1, Math.max(y2, y3));
        Ay = Math.min(y1, Math.min(y2, y3));

        if      (Cy == y1) { Cx = x1; }
        else if (Cy == y2) { Cx = x2; }
        else               { Cx = x3; }

        if      (Ay == y1) { Ax = x1; }
        else if (Ay == y2) { Ax = x2; }
        else               { Ax = x3; }

        Bx = (x1 + x2 + x3) - (Ax + Cx);
        By = (y1 + y2 + y3) - (Cy + Ay);

        // Check for the triangle type and rasterize the triangle
        // Do flat-top triangle rasterizing algorithm
        if (Ay == By && Cy > Ay) {
            rasterizeFlatTopTriangle(Ax, Ay, Bx, By, Cx, Cy, z, c);
            return;
        }
        // Do flat-bottom triangle rasterizing algorithm
        else if (Cy == By && Ay < Cy) {
            rasterizeFlatBottomTriangle(Ax, Ay, Bx, By, Cx, Cy, z, c);
            return;
        }
        // Do general case triangle rasterizing algorithm
        float Dx = Cx + ((By - Cy) / (Ay - Cy)) * (Ax - Cx);
        float Dy = By;

        rasterizeFlatBottomTriangle(Ax, Ay, Bx, By, Dx, Dy, z, c);
        rasterizeFlatTopTriangle   (Dx, Dy, Bx, By, Cx, Cy, z, c);
    }

    private void rasterizeFlatBottomTriangle(float Ax, float Ay, float Bx, float By, float Cx, float Cy, int z, int c) {
        float inverseCA = (Cx - Ax) / (Cy - Ay);
        float inverseBA = (Bx - Ax) / (By - Ay);

        float xStart = Ax;
        float xEnd   = Ax;

        for (float scanLine = Ay; scanLine <= Cy; scanLine ++) {
            drawLine((int)xStart, (int)scanLine, (int)xEnd, (int)scanLine, z, c);
            xStart += inverseCA;
            xEnd   += inverseBA;
        }
    }
    private void rasterizeFlatTopTriangle(float Ax, float Ay, float Bx, float By, float Cx, float Cy, int z, int c) {
        float inverseAC = (Cx - Ax) / (Cy - Ay);
        float inverseBC = (Cx - Bx) / (Cy - By);

        float xStart = Cx;
        float xEnd   = Cx;

        for (float scanLine = Cy; scanLine >= Ay; scanLine --) {
            drawLine((int)xStart, (int)scanLine, (int)xEnd, (int)scanLine, z, c);
            xStart -= inverseAC;
            xEnd   -= inverseBC;
        }
    }

    // Draw a sprite
    public void drawSprite(Sprite sprite, int xpos, int ypos, int z) {
        int[] spriteP   = sprite.getPixelData();
        int spriteWidth = sprite.getWidth();

        for (int y = 0; y < sprite.getHeight(); y ++) {
            for (int x = 0; x < spriteWidth; x ++) {
                drawPixel(xpos + x, ypos + y, z, spriteP[x + y * spriteWidth]);
            }
        }
    }

    // Draw an animated sprite
    public void drawSprite(PartialSprite sprite, int xpos, int ypos, int z) {
        int[] spriteP   = sprite.getPixelData(0);
        int spriteWidth = sprite.getWidth();

        for (int y = 0; y < sprite.getHeight(); y ++) {
            for (int x = 0; x < spriteWidth; x ++) {
                drawPixel(xpos + x, ypos + y, z, spriteP[x + y * spriteWidth]);
            }
        }
    }

    // Draw a sprite with a chosen frame
    public void drawSprite(PartialSprite sprite, int frame, int xpos, int ypos, int z) {
        int[] spriteP   = sprite.getPixelData(frame);
        int spriteWidth = sprite.getWidth();

        for (int y = 0; y < sprite.getHeight(); y ++) {
            for (int x = 0; x < spriteWidth; x ++) {
                drawPixel(xpos + x, ypos + y, z, spriteP[x + y * spriteWidth]);
            }
        }
    }

    // Draw text
    public void drawText(String text, Font font, int xpos, int ypos, int z) {
        char[] textArray = text.toCharArray();
        List<int[]> pixelData = new ArrayList<>();
        for (int i = 0; i < textArray.length; i ++) {
            pixelData.add(font.getCharData(textArray[i]));
        }
        int h = font.getFontHeight();
        int currentOff = 0;
        for (int j = 0; j < pixelData.size(); j ++) {
            int[] p = pixelData.get(j);
            int w = p.length / h;
            for (int y = 0; y < h; y ++) {
                for (int x = 0; x < w; x ++) {
                    drawPixel(xpos + currentOff + x, ypos + y, z, p[x + y*w]);
                }
            }
            currentOff += w;
        }
    }

    public void drawGUI(GUI_Pane pane) {
        int[] paneDimensions = pane.getDimensions();
        Sprite paneSprite = pane.getSprite();
        ArrayList<GUI_Element> paneElements = pane.getElements();
        if (paneSprite != null) {
            drawSprite(paneSprite, paneDimensions[0], paneDimensions[1], paneDimensions[4]);
        }
        for (GUI_Element element : paneElements) {
            if (element.isActive()) {
                drawSprite(element.getSprite(), paneDimensions[0] + element.getX(), paneDimensions[1] + element.getY(), paneDimensions[2]);
            }
        }
    }
}