package sandbox.engine.utility;

public class RGBConverter {
    // Convert an RGB value to a hexadecimal integer
    public static int RGBToHex(int a, int r, int g, int b) {
        if ((r > 255 || r < 0) || (g > 255 || g < 0) || (b > 255 || b < 0) || (a > 255 || a < 0)) return 0;
        return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }
}
