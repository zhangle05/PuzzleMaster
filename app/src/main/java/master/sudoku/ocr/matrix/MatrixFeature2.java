package master.sudoku.ocr.matrix;

import java.util.ArrayList;
import java.util.List;

import master.sudoku.ocr.util.MatrixUtil;
import master.sudoku.ocr.util.ThresholdUtil;


public class MatrixFeature2
{
    private int mAxisX = 0;
    private int mHoleCnt = 0;
    private int mLeftPitCnt = 0;
    private int mRightPitCnt = 0;
    private int mFirstPitPos = 0; // 1 for left, 2 for right, to distinguish '2' and '5'
    private boolean mHasAxisX = false;

    /// <summary>
    /// intersections on the x-axis
    /// </summary>
    private List<Integer> mIntersections = new ArrayList<Integer>();

    /// <summary>
    /// density of dark pixels, in persentage
    /// </summary>
    public List<Integer> mDensityX = new ArrayList<Integer>();   // indexed by X
    public List<Integer> mDensityY = new ArrayList<Integer>();   // indexed by Y

    /// <summary>
    /// the number of segments with dark pixels
    /// </summary>
    public List<Integer> mSegmentX = new ArrayList<Integer>();   // indexed by X
    public List<Integer> mSegmentY = new ArrayList<Integer>();   // indexed by Y

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="matrix"></param>
    public MatrixFeature2(MatrixBase matrix)
    {
        if (matrix.getDimensionX() <= 3 || matrix.getDimensionY() <= 3)
            return;
        generateFeature(matrix);
    }

    public boolean isBlank()
    {
        return mSegmentX.size() == 0 || mSegmentY.size() == 0
                || mDensityX.size() == 0 || mDensityY.size() == 0
                || mIntersections.size() == 0;
    }

    public int getFeatureValue()
    {
        int maxSegmentX = MatrixUtil.getMax(mSegmentX);
        int maxSegmentY = MatrixUtil.getMax(mSegmentY);
        int maxDensityX = MatrixUtil.getMax(mDensityX);

        if (mHasAxisX)
        {
            if (maxSegmentY == 1)
                return 1;
            else
                return 4;
        }
        switch (mHoleCnt)
        {
            case 0:
                if (mLeftPitCnt == 0)
                {
                    if (mRightPitCnt == 1)
                    {
                        return 7;
                    }
                    if (mRightPitCnt == 2)
                        return 3;
                }
                if (mLeftPitCnt == 1)
                {
                    if (mRightPitCnt == 1)
                    {
                        if (mFirstPitPos == 1)
                            return 5;
                        else
                            return 2;
                    }
                }
                break;
            case 1:
                if (mLeftPitCnt == 1)
                {
                    if (mRightPitCnt == 2)
                    {
                        if (maxDensityX >= 99)
                            return 4;
                        else
                            return 9;
                    }
                    else
                        return 4;
                }
                else if (mLeftPitCnt == 2)
                    return 6;
                break;
            case 2:
                return 8;

        }
        return 0;
    }

    private void generateFeature(MatrixBase matrix)
    {
        mAxisX = matrix.getDimensionX() / 2;
        generateIntersections(matrix);
        generateDensityAndSegment(matrix);
        checkPitAndHole(matrix);
    }

    private void generateDensityAndSegment(MatrixBase matrix)
    {
        // generate density and segment-count on X-direction
        for (int x = 0; x < matrix.getDimensionX(); x++)
        {
            int preValue = ThresholdUtil.SHALLOW_VALUE;
            int darkCount = 0;
            int segmentCount = 0;
            for (int y = 0; y < matrix.getDimensionY(); y++)
            {
                int value = matrix.getValue(x, y);
                if (value <= ThresholdUtil.DARK_THRESHOLD)
                {
                    darkCount++;
                    if (preValue == ThresholdUtil.SHALLOW_VALUE)
                    {
                        segmentCount++;
                    }
                    preValue = ThresholdUtil.DARK_VALUE;
                }
                else
                {
                    preValue = ThresholdUtil.SHALLOW_VALUE;
                }
            }
            int density = darkCount * 100 / matrix.getDimensionY();
            if (density > 0)
            {
                mDensityX.add(density);
            }
            if (segmentCount > 0)
            {
                mSegmentX.add(segmentCount);
            }
        }

        // generate density and segment-count on Y-direction
        for (int y = 0; y < matrix.getDimensionY(); y++)
        {
            int preValue = ThresholdUtil.SHALLOW_VALUE;
            int darkCount = 0;
            int segmentCount = 0;
            for (int x = 0; x < matrix.getDimensionX(); x++)
            {
                int value = matrix.getValue(x, y);
                if (value <= ThresholdUtil.DARK_THRESHOLD)
                {
                    darkCount++;
                    if (preValue == ThresholdUtil.SHALLOW_VALUE)
                    {
                        segmentCount++;
                    }
                    preValue = ThresholdUtil.DARK_VALUE;
                }
                else
                {
                    preValue = ThresholdUtil.SHALLOW_VALUE;
                }
            }
            int density = darkCount * 100 / matrix.getDimensionX();
            if (density > 0)
            {
                mDensityY.add(density);
            }
            if (segmentCount > 0)
            {
                mSegmentY.add(segmentCount);
            }
        }
    }

    private void generateIntersections(MatrixBase matrix)
    {
        // get intersections on x-axis
        int preValue = ThresholdUtil.SHALLOW_VALUE;
        for (int y = 0; y < matrix.getDimensionY(); y++)
        {
            int value = matrix.getValue(mAxisX, y);
            if (value <= ThresholdUtil.DARK_THRESHOLD && preValue == ThresholdUtil.SHALLOW_VALUE)
            {
                mIntersections.add(y);
                preValue = ThresholdUtil.DARK_VALUE;
            }
            else if (value > ThresholdUtil.DARK_THRESHOLD && preValue == ThresholdUtil.DARK_VALUE)
            {
                mIntersections.add(y);
                preValue = ThresholdUtil.SHALLOW_VALUE;
            }
        }
        if (preValue == ThresholdUtil.DARK_VALUE && mIntersections.size() <= 2)
        {
            mHasAxisX = true;
        }
    }

    private void checkPitAndHole(MatrixBase matrix)
    {
        for (int i = 1; i + 1 < mIntersections.size(); i += 2)
        {
            boolean leftPit = false, rightPit = false;
            if (hasLeftPit(mIntersections.get(i), mIntersections.get(i+1), matrix))
            {
                leftPit = true;
                mLeftPitCnt++;
            }
            if (hasRightPit(mIntersections.get(i), mIntersections.get(i+1), matrix))
            {
                rightPit = true;
                mRightPitCnt++;
            }
            if (leftPit && rightPit)
            {
                mHoleCnt++;
            }
            if (mFirstPitPos==0)
            {
                if (leftPit)
                    mFirstPitPos = 1;
                else if (rightPit)
                    mFirstPitPos = 2;
            }
        }
    }

    private boolean hasLeftPit(int y1, int y2, MatrixBase matrix)
    {
        for (int y = y1; y < y2; y++)
        {
            int x = mAxisX;
            for (; x >= 0; x--)
            {
                int value = matrix.getValue(x, y);
                if (value <= ThresholdUtil.DARK_THRESHOLD)
                {
                    break;
                }
            }
            if (x <= 0)
            {
                return false;
            }
        }
        return true;
    }

    private boolean hasRightPit(int y1, int y2, MatrixBase matrix)
    {
        for (int y = y1; y < y2; y++)
        {
            int x = mAxisX;
            for (; x < matrix.getDimensionX(); x++)
            {
                int value = matrix.getValue(x, y);
                if (value <= ThresholdUtil.DARK_THRESHOLD)
                {
                    break;
                }
            }
            if (x >= matrix.getDimensionX())
            {
                return false;
            }
        }
        return true;
    }

}
