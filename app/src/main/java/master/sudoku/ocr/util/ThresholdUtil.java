package master.sudoku.ocr.util;

import android.graphics.Color;

public class ThresholdUtil
{
    public static int DARK_VALUE = 1;
    public static int SHALLOW_VALUE = 255;

    public static int DARK_THRESHOLD = 128;

    public static int BG_COLOR = Color.WHITE;

    public static boolean almostSameColor(int c1, int c2)
    {
        int tolerance = 10;
        return (Math.abs(Color.alpha(c1) - Color.alpha(c2)) < tolerance
                && Math.abs(Color.red(c1) - Color.red(c2)) < tolerance
                && Math.abs(Color.green(c1) - Color.green(c2)) < tolerance
                && Math.abs(Color.blue(c1) - Color.blue(c2)) < tolerance);
    }

    public static int GetDarkValue(int color)
    {
        //return (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
        return (int)(Color.red(color) * 0.299 + Color.green((color)) * 0.587 + Color.blue(color) * 0.114);
    }

    public static boolean IsDark(int color)
    {
        return !almostSameColor(color, BG_COLOR);
    }
}
