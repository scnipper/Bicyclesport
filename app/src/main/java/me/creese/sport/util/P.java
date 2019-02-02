package me.creese.sport.util;

public class P {
    public static int WIDTH = 0;
    public static int HEIGHT = 0;
    public static float DENSITY = 0;

    public static int getPixelFromDP(int dp) {
        return (int) (dp * DENSITY);
    }
}
