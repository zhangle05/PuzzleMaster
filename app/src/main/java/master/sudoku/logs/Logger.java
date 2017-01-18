/**
 *
 */
package master.sudoku.logs;

import android.util.Log;

/**
 * @author dannyzha
 *
 */
public final class Logger {

    public static boolean ON = true;

    private static Logger sInstance;

    public static Logger getLogger() {
        if(sInstance == null) {
            sInstance = new Logger();
        }
        return sInstance;
    }

    public void debug(String log) {
        Log.i("LOGGER", log);
    }
}
