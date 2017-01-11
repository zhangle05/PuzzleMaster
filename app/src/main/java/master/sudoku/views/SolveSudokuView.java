/**
 *
 */
package master.sudoku.views;

import android.content.Context;
import android.util.AttributeSet;

import master.sudoku.event.EventArgs;
import master.sudoku.exception.SolutionException;
import master.sudoku.model.Sudoku;
import master.sudoku.solve.Solution;

/**
 * @author dannyzha
 *
 */
public class SolveSudokuView extends MainGameView {

    /**
     * Need this constructor to fix the "Error inflating class" error
     * @param context
     * @param attrs
     */
    public SolveSudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInputPanel.addEventListener(this);
        mGrid.setIsEditing(true);
    }
    public SolveSudokuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mInputPanel.addEventListener(this);
        mGrid.setIsEditing(true);
    }

    public boolean handleEvent(EventArgs args) {
        switch(args.getEventType()) {
            case EventArgs.INPUT_PANEL_SELECT:
                Integer value = (Integer)args.getEventData();
                try {
                    if(value != null && value.intValue() == 0) {
                        Solution s = new Solution(mModel);
                        printModel(mModel, System.out);
                        s.solve();
                        printModel(mModel, System.out);
                        mGrid.setIsEditing(false);
                    }

                } catch (SolutionException e) {
                    e.printStackTrace();
                }
                this.invalidate();
                return true;
        }
        return false;
    }

    private static void printModel(Sudoku model, java.io.PrintStream ps) {
        for(int i=0; i<9; i++) {
            if(i % 3 == 0) {
                for(int j=0; j<9; j++) {
                    if(j % 3 == 0) {
                        ps.print('-');
                    }
                    ps.print("---");
                }
                ps.println();
            }
            for(int j=0; j<9; j++) {
                if(j % 3 == 0) {
                    ps.print('|');
                }
                ps.print(" " + model.getValue(i, j) + " ");
            }
            ps.println('|');
        }
    }
}
