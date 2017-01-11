/**
 *
 */
package master.sudoku.solve;

import java.util.Stack;
import java.util.Vector;

import master.sudoku.exception.SolutionException;
import master.sudoku.model.Sudoku;

/**
 * @author dannyzha
 *
 */
public class Solution {
    private Sudoku mModel;
    private Vector<CellValue> mCellValueList;
    private SolutionStatus mStatus;
    private boolean mDebug = true;

    private Stack<Guess> mGuessStack;
    private Vector<Sudoku> mGuessHistory;

    // numbers for statistics
    private int mCalculateRound = 0;
    private int mRollbackRound = 0;
    private int mGuessRound = 0;

    /**
     * Constructor
     * @param model
     */
    public Solution(Sudoku model) {
        this.mModel = model;
        this.mCellValueList = new Vector<CellValue>();
        this.mStatus = SolutionStatus.Solving;
        this.mGuessStack = new Stack<Guess>();
        this.mGuessHistory = new Vector<Sudoku>();
    }

    public void solve() throws SolutionException {
        while(mModel.getBlankSize() > 0) {
            mCalculateRound ++;
            int oldBlankSize = mModel.getBlankSize();
            try {
                this.buildCellValue();
                this.checkSingleValue();
            } catch(SolutionException ex) {
                if(mDebug) {
                    System.err.println(ex.getMessage());
                    System.err.println("ready to roll-back");
                }
                rollBackAndGuessOtherValue();
                continue;
            }

            if(oldBlankSize == mModel.getBlankSize()) {
                addNewGuess();
            }
        }
    }

    public void buildCellValue() throws SolutionException {
        mCellValueList.removeAllElements();
        for(int i=0; i<Sudoku.SUDOKU_SIZE; i++) {
            for(int j=0; j<Sudoku.SUDOKU_SIZE; j++) {
                if(mModel.isBlank(i, j)) {
                    mCellValueList.addElement(getCellValue(i, j, mModel));
                }
            }
        }
    }

    public boolean isSolved() {
        return this.mStatus == SolutionStatus.Solved;
    }

    public void checkSingleValue() throws SolutionException {
        for(int m=0; m<mCellValueList.size(); m++) {
            CellValue cv = (CellValue)mCellValueList.elementAt(m);
            if(cv.getValueCount() == 1) {
                mModel.setResultValue(cv.getIndex().getI(), cv.getIndex().getJ(), cv.getSingleValue());
            }
        }
    }

    public int getmCalculateRound() {
        return mCalculateRound;
    }

    public int getmRollbackRound() {
        return mRollbackRound;
    }

    public int getmGuessRound() {
        return mGuessRound;
    }

    /**
     * get possible cell values for position (i, j)
     * @param i
     * @param j
     * @param model
     * @return
     * @throws SolutionException
     */
    private CellValue getCellValue(int i, int j, Sudoku model) throws SolutionException {
        Vector<Integer> valueList = new Vector<Integer>();

        for(int value=1; value<=9; value++) {
            if(mModel.acceptValue(i, j, value)) {
                valueList.addElement(Integer.valueOf(value));
            } else {
                if(mDebug) {
                    System.out.println("for Cell["+i+","+j+"]:" + value + " not valid.");
                }
            }
        }

        if(valueList.size() == 0) {
            this.mStatus = SolutionStatus.Failed;
            throw new SolutionException("Impossible to fill value in Cell["+i+","+j+"]");
        }

        if(mDebug) {
            System.out.println("getting value size for Cell["+i+","+j+"]:" + valueList.size());
        }
        CellValue result = new CellValue(i, j);
        for(int k=0; k<valueList.size(); k++) {
            if(mDebug) {
                System.out.println("getting value for Cell["+i+","+j+"]:" + valueList.elementAt(k));
            }
            result.addValue(((Integer)valueList.elementAt(k)).intValue());
        }
        return result;
    }

    private void addNewGuess() throws SolutionException {
        mGuessRound ++;
        CellValue guessCell = null;
        for(int m=0; m<mCellValueList.size(); m++) {
            CellValue tmp = (CellValue)mCellValueList.elementAt(m);
            if(guessCell == null || tmp.getValueCount() < guessCell.getValueCount()) {
                guessCell = tmp;
            }
        }
        if(mDebug) {
            System.out.println("guessing Cell["+guessCell.getIndex().getI()+","+guessCell.getIndex().getJ()+"]");
        }
        Guess guess = new Guess(guessCell, this.mModel);
        while(guess.guessOne()) {
            if(mDebug) {
                System.out.println("guessing Cell["+guessCell.getIndex().getI()+","+guessCell.getIndex().getJ()+"]:" + guess.getCurrentGuess());
            }
            Sudoku guessModel = guess.getGuessModel();
            boolean guessed = false;
            for(int m=0; m<mGuessHistory.size(); m++) {
                if(guessModel.equals(mGuessHistory.elementAt(m))) {
                    // the same guess has been made before
                    guessed = true;
                    break;
                }
            }
            if(!guessed) {
                mGuessHistory.addElement(guessModel);
                mModel.copyResultFrom(guessModel);
                mGuessStack.push(guess);
                return;
            }
        }
        // not possible to make new guesses, roll back
        rollBackAndGuessOtherValue();
    }

    private void rollBackAndGuessOtherValue() throws SolutionException {
        mRollbackRound ++;
        while(!mGuessStack.isEmpty()) {
            Guess g = (Guess)mGuessStack.peek();
            while(g.guessOne()) {
                if(mDebug) {
                    System.out.println("guessing another for Cell["+g.getIndex().getI()+","+g.getIndex().getJ()+"]:" + g.getCurrentGuess());
                }
                Sudoku guessModel = g.getGuessModel();
                boolean guessed = false;
                for(int m=0; m<mGuessHistory.size(); m++) {
                    if(guessModel.equals(mGuessHistory.elementAt(m))) {
                        // the same guess has been made before
                        guessed = true;
                        break;
                    }
                }
                if(!guessed) {
                    mGuessHistory.addElement(guessModel);
                    mModel.copyResultFrom(guessModel);
                    return;
                }
            }
            mGuessStack.pop();
        }

        throw new SolutionException("Not able to guess more values.");
    }
}

class SolutionStatus {
    int mSolveStatus = 0;

    private SolutionStatus(int status) {
        this.mSolveStatus = status;
    }

    public static SolutionStatus Solving = new SolutionStatus(0);
    public static SolutionStatus Solved = new SolutionStatus(1);
    public static SolutionStatus Failed = new SolutionStatus(2);
}
