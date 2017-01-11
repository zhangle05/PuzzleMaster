/**
 *
 */
package master.sudoku.model;

import java.util.Vector;

import master.sudoku.exception.SolutionException;

/**
 * @author dannyzha
 *
 */
public class Sudoku {
    public static final int SUDOKU_SIZE = 9;

    private static final int BLANK_VALUE = 0;
    // matrix to store the values
    private int[][] mInitMatrix;
    private int[][] mResultMatrix;

    /**
     *
     */
    public Sudoku() {
        mInitMatrix = new int[SUDOKU_SIZE][SUDOKU_SIZE];
        mResultMatrix = new int[SUDOKU_SIZE][SUDOKU_SIZE];
        initMatrix(mInitMatrix);
        initMatrix(mResultMatrix);
    }

    public boolean isBlank(int i, int j) {
        return mResultMatrix[i][j] == BLANK_VALUE;
    }

    public int getBlankSize() {
        int size = 0;
        for(int i=0; i<mResultMatrix.length; i++) {
            int[] row = mResultMatrix[i];
            for(int j=0; j<row.length; j++) {
                if(mResultMatrix[i][j] == BLANK_VALUE) {
                    size ++;
                }
            }
        }
        return size;
    }

    public Sudoku copyResultModel() {
        Sudoku result = new Sudoku();
        for(int i=0; i<SUDOKU_SIZE; i++) {
            for(int j=0; j<SUDOKU_SIZE; j++)
                result.setInitValue(i, j, this.getValue(i, j));
        }
        return result;
    }

    public void copyResultFrom(Sudoku source) throws SolutionException {
        initMatrix(mResultMatrix);
        for(int i=0; i<Sudoku.SUDOKU_SIZE; i++) {
            for(int j=0; j<Sudoku.SUDOKU_SIZE; j++) {
                if(!source.isBlank(i, j)) {
                    this.setResultValue(i, j, source.getValue(i, j));
                }
            }
        }
    }

    public int getValue(int i, int j) {
        return mResultMatrix[i][j];
    }

    public int getInitValue(int i, int j) {
        return mInitMatrix[i][j];
    }

    public void setInitValue(int i, int j, int value) {
        mInitMatrix[i][j] = value;
        mResultMatrix[i][j] = value;
    }

    public void setResultValue(int i, int j, int value) throws SolutionException {
        if(!this.acceptValue(i, j, value)) {
            throw new SolutionException("Internal exception: confict value set to Cell["+i+","+j+"]:" + value);
        }
        mResultMatrix[i][j] = value;
    }

    public void setResultValueWithoutChecking(int i, int j, int value) {
        mResultMatrix[i][j] = value;
    }

    public boolean acceptValue(int i, int j, int value) {
        // check value in the row and column
        for(int k=0; k<Sudoku.SUDOKU_SIZE; k++) {
            if(!this.isBlank(i, k)) {
                if(value == this.getValue(i, k)) {
                    return false;
                }
            }
            if(!this.isBlank(k, j)) {
                if(value == this.getValue(k, j)) {
                    return false;
                }
            }
        }
        // check value in the square
        Vector<Index> idxList = getSquareIndexList(i, j);
        for(int k=0; k<idxList.size(); k++) {
            Index idx = (Index)idxList.elementAt(k);
            if(!this.isBlank(idx.getI(), idx.getJ())) {
                if(value == this.getValue(idx.getI(), idx.getJ())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * get conflict cell indexes for a value in a certain cell
     * @param i: target cell index i
     * @param j: target cell index j
     * @param value: target cell value
     * @return
     */
    public Vector<Index> getConflicts(int i, int j, int value) {
        Vector<Index> result = new Vector<Index>();
        // check value in the row and column
        for(int k=0; k<Sudoku.SUDOKU_SIZE; k++) {
            if(!this.isBlank(i, k)) {
                if(value == this.getValue(i, k)) {
                    Index idx = new Index(i, k);
                    result.addElement(idx);
                }
            }
            if(!this.isBlank(k, j)) {
                if(value == this.getValue(k, j)) {
                    Index idx = new Index(k, j);
                    result.addElement(idx);
                }
            }
        }
        // check value in the square
        Vector<Index> idxList = getSquareIndexList(i, j);
        for(int k=0; k<idxList.size(); k++) {
            Index idx = (Index)idxList.elementAt(k);
            if(!this.isBlank(idx.getI(), idx.getJ())) {
                if(value == this.getValue(idx.getI(), idx.getJ())) {
                    result.addElement(idx);
                }
            }
        }
        return result;
    }

    /**
     * get the index of the square (3x3) to which the cell[i,j] belongs
     *
     * @param i
     * @param j
     * @return
     */
    public Vector<Index> getSquareIndexList(int i, int j) {
        Vector<Index> result = new Vector<Index>();
        int iStart = i / 3 * 3;
        int jStart = j / 3 * 3;
        for(i=iStart; i<iStart+3; i++) {
            for(j=jStart; j<jStart+3; j++) {
                result.addElement(new Index(i, j));
            }
        }
        return result;
    }

    private void initMatrix(int [][] matrix) {
        for(int i=0; i<matrix.length; i++) {
            int[] row = matrix[i];
            for(int j=0; j<row.length; j++) {
                matrix[i][j] = BLANK_VALUE;
            }
        }
    }

}
