/**
 *
 */
package master.sudoku.exception;

/**
 * @author dannyzha
 *
 */
public class SolutionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param errMsg
     */
    public SolutionException(String errMsg) {
        super(errMsg);
    }
}
