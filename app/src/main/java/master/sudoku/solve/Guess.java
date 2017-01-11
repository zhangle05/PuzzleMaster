/**
 *
 */
package master.sudoku.solve;

import java.util.Vector;

import master.sudoku.exception.SolutionException;
import master.sudoku.model.Index;
import master.sudoku.model.Sudoku;

/**
 * @author dannyzha
 *
 */
class Guess {
    private Sudoku mBakModel;
    private Sudoku mGuessModel;
    private CellValue mCellValue;
    private Vector<Integer> mGuessHistory;
    private int mCurrentGuess;

    /**
     * Constructor
     */
    public Guess(CellValue cellValue, Sudoku bakModel) {
        this.mCellValue = cellValue;
        this.mBakModel = bakModel.copyResultModel();
        this.mGuessHistory = new Vector<Integer>();
    }

    public boolean canGuess() {
        return mGuessHistory.size() < mCellValue.getValueCount();
    }

    /**
     * if able to guess, guess one value and fill in the guess model, otherwise do nothing
     * @return true if able to guess, false if guess number is used out.
     */
    public boolean guessOne() throws SolutionException {
        if(mGuessHistory.size() >= mCellValue.getValueCount()) {
            return false;
        }
        mGuessModel = mBakModel.copyResultModel();
        Vector<Integer> valueList = mCellValue.getValueList();
        for(int m=0; m<valueList.size(); m++) {
            Integer value = (Integer)valueList.elementAt(m);
            if(!mGuessHistory.contains(value)) {
                mGuessHistory.addElement(value);
                mGuessModel.setResultValue(mCellValue.getIndex().getI(), mCellValue.getIndex().getJ(), value.intValue());
                mCurrentGuess = value.intValue();
                return true;
            }
        }
        return false;
    }

    public int getCurrentGuess() {
        return mCurrentGuess;
    }

    public Sudoku getGuessModel() {
        return mGuessModel.copyResultModel();
    }

    public Index getIndex() {
        return mCellValue.getIndex();
    }
}
