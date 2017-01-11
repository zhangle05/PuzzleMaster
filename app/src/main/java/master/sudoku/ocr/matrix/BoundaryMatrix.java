/**
 * 
 */
package master.sudoku.ocr.matrix;

import java.util.ArrayList;
import java.util.List;

import master.sudoku.logs.Logger;
import master.sudoku.ocr.util.ThresholdUtil;

/**
 * @author dannyzha
 *
 */
public class BoundaryMatrix extends MatrixBase {

    private int mLeft;
    private int mRight;
    private int mTop;
    private int mBottom;

    private List<Integer> mBoundaryXList;
    private List<Integer> mBoundaryYList;

    private List<Integer> mBoundaryWidthXList;
    private List<Integer> mBoundaryWidthYList;

    /// <summary>
    /// average interval between boundaries
    /// </summary>
    private int mAveIntervalX = 0;
    private int mAveIntervalY = 0;

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="dimensionX"></param>
    /// <param name="dimensionY"></param>
    public BoundaryMatrix(int dimensionX, int dimensionY)
    {
        super(dimensionX, dimensionY);
        mBoundaryXList = new ArrayList<Integer>();
        mBoundaryYList = new ArrayList<Integer>();
        mBoundaryWidthXList = new ArrayList<Integer>();
        mBoundaryWidthYList = new ArrayList<Integer>();
    }


    public int getLeft() {
        return mLeft;
    }

    public void setLeft(int left) {
        mLeft = left;
    }

    public int getRight() {
        return mRight;
    }

    public void setRight(int right) {
        mRight = right;
    }

    public int getTop() {
        return mTop;
    }

    public void setTop(int top) {
        mTop = top;
    }

    public int getBottom() {
        return mBottom;
    }

    public void setBottom(int bottom) {
        mBottom = bottom;
    }

    /// <summary>
    /// 
    /// </summary>
    public List<Integer> getBoundaryXList()
    {
       return new ArrayList<Integer>(mBoundaryXList);
    }

    public List<Integer> getBoundaryYList()
    {
        return new ArrayList<Integer>(mBoundaryYList);
    }

    public List<Integer> getBoundaryWidthXList()
    {
        return new ArrayList<Integer>(mBoundaryWidthXList);
    }

    public List<Integer> getBoundaryWidthYList()
    {
        return new ArrayList<Integer>(mBoundaryWidthYList);
    }

    /* (non-Javadoc)
     * @see com.skyway.pandora.digitsrecognizer.matrix.MatrixBase#getValue(int, int)
     */
    @Override
    public int getValue(int x, int y)
    {
	    if(x < 0 || x > mDimensionX || y < 0 || y > mDimensionY)
        {
		    return -1;
	    }
        for (int i = 0; i < mBoundaryXList.size(); i++)
        {
            if (mBoundaryXList.get(i) == x)
            {
                return ThresholdUtil.DARK_VALUE;
            }
        }
        for (int i = 0; i < mBoundaryYList.size(); i++)
        {
            if (mBoundaryYList.get(i) == y)
            {
                return ThresholdUtil.DARK_VALUE;
            }
        }
        return ThresholdUtil.SHALLOW_VALUE;
    }
//
//    public void addBoundaryX(int x)
//    {
//        if (mBoundaryXList.size() > 0)
//        {
//            int interval = Math.abs(x - mBoundaryXList.get(mBoundaryXList.size() - 1));
//            if (interval <= mBoundaryWidthXList.get(mBoundaryXList.size() - 1)
//                || interval < mAveIntervalX / 2)
//            {
//                mBoundaryWidthXList.set(mBoundaryXList.size() - 1, interval + 1);
//                return;
//            }
//            mAveIntervalX = ((mAveIntervalX * mBoundaryXList.size()) + interval) / (mBoundaryXList.size() + 1);
//        }
//        mBoundaryXList.add(x);
//        mBoundaryWidthXList.add(1);
//    }
//
//    public void addBoundaryY(int y)
//    {
//        if (mBoundaryYList.size() > 0)
//        {
//            int interval = Math.abs(y - mBoundaryYList.get(mBoundaryYList.size() - 1));
//            if (interval <= mBoundaryWidthYList.get(mBoundaryYList.size() - 1)
//                || interval < mAveIntervalY / 2)
//            {
//                mBoundaryWidthYList.set(mBoundaryYList.size() - 1, interval + 1);
//                return;
//            }
//            mAveIntervalY = ((mAveIntervalY * mBoundaryYList.size()) + interval) / (mBoundaryYList.size() + 1);
//        }
//        mBoundaryYList.add(y);
//        mBoundaryWidthYList.add(1);
//    }

    public void clear()
    {
        mBoundaryXList.clear();
        mBoundaryYList.clear();
    }

    public void generateBoundarys() {
        this.clear();
        Logger.getLogger().debug("left:" + mLeft + ", right:" + mRight + ", top:" + mTop + ", bottom:" + mBottom);
        int mAveIntervalX = (mRight - mLeft) / 9;
        int mAveIntervalY = (mBottom - mTop) / 9;
        Logger.getLogger().debug("interval X:" + mAveIntervalX + ", interval Y:" + mAveIntervalY);
        for (int i = 0; i < 10; i++) {
            int x = mLeft + i * mAveIntervalX;
            int y = mTop + i * mAveIntervalY;
            Logger.getLogger().debug("boundary X" + i + ":" + x + ", boundary Y" + i + ":" + y);
            mBoundaryXList.add(x);
            mBoundaryYList.add(y);
            mBoundaryWidthXList.add(1); // consider boundary line width 1px
            mBoundaryWidthYList.add(1);
        }
    }

    public boolean isValid()
    {
        return mBoundaryXList.size() == 10 && mBoundaryYList.size() == 10;
    }

	@Override
	public void setValue(int x, int y, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSimilar(MatrixBase another) {
		// TODO Auto-generated method stub
		return false;
	}

    public void justifyBoundaries(List<Integer> xBoundaryList, List<Integer> yBoundaryList) {
        if (xBoundaryList.size() == 8) {
            for (int i = 0; i < 8; i++) {
                int idealX = mBoundaryXList.get(i + 1);
                int lineX = xBoundaryList.get(i);
                if (Math.abs(lineX - idealX) <= 5) {
                    mBoundaryXList.set(i + 1, lineX);
                }
            }
        }
        if (yBoundaryList.size() == 8) {
            for (int i = 0; i < 8; i++) {
                int idealY = mBoundaryYList.get(i + 1);
                int lineY = yBoundaryList.get(i);
                if (Math.abs(lineY - idealY) <= 5) {
                    mBoundaryYList.set(i + 1, lineY);
                }
            }
        }
    }
}
