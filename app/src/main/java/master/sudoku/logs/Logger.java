/**
 *
 */
package master.sudoku.logs;

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
        System.out.println(log);
    }
}
