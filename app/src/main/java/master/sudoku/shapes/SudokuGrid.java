/**
 *
 */
package master.sudoku.shapes;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Vector;

import master.sudoku.model.Index;

/**
 * @author dannyzha
 *
 */
public class SudokuGrid extends NumberGrid {

    /**
     * Constructor
     *
     * @param dimensionX
     * @param dimensionY
     */
    public SudokuGrid(int dimensionX, int dimensionY) {
        super(dimensionX, dimensionY);
        // TODO Auto-generated constructor stub
    }

    protected void initLines(Rect rect) {
        mLineList.removeAllElements();
        int x = mDrawLeft;
        int y = mDrawTop;
        for(int i=0; i<=mDimensionX; i++) {
            // create vertical lines
            Point start = new Point(x, mDrawTop);
            Point end = new Point(x, mDrawBottom);
            ThicknessLine vLine = new ThicknessLine(start, end);

            if(i % 3 == 0) {
                vLine.setThickness(3);
            }
            this.mLineList.addElement(vLine);
            x += mCellWidth;
        }

        for(int j=0; j<=mDimensionY; j++) {
            // create horizontal lines
            Point start = new Point(mDrawLeft, y);
            Point end = new Point(mDrawRight, y);
            ThicknessLine hLine = new ThicknessLine(start, end);

            if(j % 3 == 0) {
                hLine.setThickness(3);
            }
            this.mLineList.addElement(hLine);
            x += mCellWidth;
            y += mCellHeight;
        }
    }

    public void clearErrorFlagForCell(int i, int j) {
        // clear in the row
        for(int k=0; k<mDimensionX; k++) {
            Index idx = new Index(k, j);
            NumberCell cell = mCellTable.get(idx);
            cell.setErrorFlag(false);
        }
        // clear in the column
        for(int k=0; k<mDimensionY; k++) {
            Index idx = new Index(i, k);
            NumberCell cell = mCellTable.get(idx);
            cell.setErrorFlag(false);
        }

        // clear in the square
        Vector<Index> idxList = getSquareIndexList(i, j);
        for(int k=0; k<idxList.size(); k++) {
            Index idx = (Index)idxList.elementAt(k);
            NumberCell cell = mCellTable.get(idx);
            cell.setErrorFlag(false);
        }

        // clear it-self
        Index idx = new Index(i, j);
        NumberCell cell = mCellTable.get(idx);
        cell.setErrorFlag(false);
    }


    /**
     * get the index of the square (3x3) to which the cell[i,j] belongs
     *
     * @param i
     * @param j
     * @return
     */
    private Vector<Index> getSquareIndexList(int i, int j) {
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

}
