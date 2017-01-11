package master.sudoku.config;

public class DeviceConfig {
    public static int mHeight;
    public static int mWidth;
    public static float mFontSize;
    /**
     * 0: no error hint
     * 1: hint once
     * 2: hint kept all through the game
     * 3: block game if error found
     */
    public static int mErrorHintLevel = 1;
}
