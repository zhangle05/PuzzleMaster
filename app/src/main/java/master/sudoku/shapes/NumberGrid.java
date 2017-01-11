/**
 *
 */
package master.sudoku.shapes;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import master.sudoku.logs.Logger;
import master.sudoku.model.Index;

/**
 * @author dannyzha
 *
 */
public class NumberGrid extends ShapeBase {

    protected int mCellWidth;
    protected int mCellHeight;
    protected int mDrawTop;
    protected int mDrawBottom;
    protected int mDrawLeft;
    protected int mDrawRight;

    protected int mDimensionX;
    protected int mDimensionY;

    private int mSelectedI;
    private int mSelectedJ;
    private NumberCell mSelectedCell;

    private boolean mHasError = false;

    protected Hashtable<Index, NumberCell> mCellTable = new Hashtable<Index, NumberCell>();
    protected Vector<ThicknessLine> mLineList = new Vector<ThicknessLine>();

    /**
     * Constructor
     */
    public NumberGrid(int dimensionX, int dimensionY) {
        this.mDimensionX = dimensionX;
        this.mDimensionY = dimensionY;

        for(int i=0; i<mDimensionX; i++) {
            for(int j=0; j<mDimensionY; j++) {
                NumberCell cell = new NumberCell();
                Index idx = new Index(i, j);
                this.mCellTable.put(idx, cell);
            }
        }
    }

    public void setBound(Rect rect) {
        super.setBound(rect);
        mCellWidth = rect.width() / mDimensionX;
        mCellHeight = rect.height() / mDimensionY;

        centralizeGrid(this.mBound);
        initLines(this.mBound);
        initGrid();
    }

    /* (non-Javadoc)
     * @see com.skyway.pandora.sudoku.shapes.ShapeBase#paint(javax.microedition.lcdui.Graphics)
     */
    public void paint(Canvas canvas) {
//		if(Logger.ON) {
//			Logger.getLogger().debug("paiting NumberGrid");
//		}
        for(Enumeration<Index> e = mCellTable.keys(); e.hasMoreElements(); ) {
            ShapeBase shape = (ShapeBase)mCellTable.get(e.nextElement());
            shape.paint(canvas);
        }
        for(int i=0; i<mLineList.size(); i++) {
            ShapeBase shape = (ShapeBase)mLineList.elementAt(i);
            shape.paint(canvas);
        }
    }

    public void setNumber(int i, int j, int number) {
        Index idx = new Index(i, j);
        NumberCell cell = (NumberCell)mCellTable.get(idx);
        cell.setNumber(number);
    }

    public void setText(int i, int j, String text) {
        Index idx = new Index(i, j);
        NumberCell cell = (NumberCell)mCellTable.get(idx);
        cell.setText(text);
    }

    public void setReadOnly(int i, int j, boolean readOnly) {
        Index idx = new Index(i, j);
        NumberCell cell = mCellTable.get(idx);
        cell.setReadonly(readOnly);
    }

    public void setErrorFlag(int i, int j, boolean errorFlag) {
        Index idx = new Index(i, j);
        NumberCell cell = mCellTable.get(idx);
        cell.setErrorFlag(errorFlag);
        this.mHasError = true;
    }

    public void clearErrorFlag() {
        for(Enumeration<Index> e = mCellTable.keys(); e.hasMoreElements(); ) {
            NumberCell cell = mCellTable.get(e.nextElement());
            cell.setErrorFlag(false);
        }
    }

    public boolean hasError() {
        return mHasError;
    }

    public void selectByPixel(int pixelX, int pixelY) {
        for(Enumeration<Index> e = mCellTable.keys(); e.hasMoreElements(); ) {
            Index idx = (Index)e.nextElement();
            NumberCell cell = mCellTable.get(idx);
            if(cell.getBound().contains(pixelX, pixelY)) {
                if(mSelectedCell != null) {
                    mSelectedCell.setHightlighted(false);
                }
                if(Logger.ON) {
                    Logger.getLogger().debug("ready to set selected cell");
                }
                cell.setHightlighted(true);
                mSelectedCell = cell;
                mSelectedI = idx.getI();
                mSelectedJ = idx.getJ();
                break;
            }
        }
    }

    public void selectByIndex(int i, int j) {
        if(mSelectedCell != null) {
            mSelectedCell.setHightlighted(false);
        }
        Index idx = new Index(i, j);
        NumberCell cell = mCellTable.get(idx);
        cell.setHightlighted(true);
        mSelectedCell = cell;
        mSelectedI = i;
        mSelectedJ = j;
    }

    public void clearSelection() {
        if(mSelectedCell != null) {
            mSelectedCell.setHightlighted(false);
        }
        mSelectedCell = null;
        mSelectedI = 0;
        mSelectedJ = 0;
    }

    public int getSelectedI() {
        return mSelectedI;
    }

    public int getSelectedJ() {
        return mSelectedJ;
    }

    public void setSelectedCellNumber(int number) {
        if(mSelectedCell != null) {
            mSelectedCell.setNumber(number);
        }
    }

    public int getSelectedCellNumber() {
        if(mSelectedCell != null) {
            return mSelectedCell.getNumber();
        }
        return 0;
    }

    private void centralizeGrid(Rect rect) {
        // centralize the grid
        int totalWidth = mCellWidth * mDimensionX;
        int totalHeight = mCellHeight * mDimensionY;

        mDrawTop = rect.top;
        mDrawLeft = rect.left;
        if(rect.width() > totalWidth) {
            mDrawTop = mDrawTop + (rect.height() - totalHeight)/2;
        }
        if(rect.height() > totalHeight) {
            mDrawLeft = mDrawLeft + (rect.width() - totalWidth)/2;
        }
        mDrawRight = mDrawLeft + totalWidth;
        mDrawBottom = mDrawTop + totalHeight;
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

            if(i % mDimensionX == 0) {
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

            if(j % mDimensionY == 0) {
                hLine.setThickness(3);
            }
            this.mLineList.addElement(hLine);
            x += mCellWidth;
            y += mCellHeight;
        }
    }

    protected void initGrid() {
        for(int i=0; i<mDimensionX; i++) {
            int x = mDrawLeft + i * mCellWidth;
            for(int j=0; j<mDimensionY; j++) {
                int y = mDrawTop + j * mCellHeight;
                Index idx = new Index(i, j);
                NumberCell cell = mCellTable.get(idx);
                cell.setBound(new Rect(x, y, x + mCellWidth, y + mCellHeight));
            }
        }
    }

}
