package master.sudoku.ocr.matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import master.sudoku.ocr.util.MatrixUtil;
import master.sudoku.ocr.util.ThresholdUtil;

class CountArray
{
    public int count1 = 0;
    public int count2 = 0;
    public int count3 = 0;
    public int count4 = 0;
    public int countN = 0;
}

public class MatrixFeature
{
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

    public List<Integer> mPossibleValue = new ArrayList<Integer>();

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="matrix"></param>
    public MatrixFeature(MatrixBase matrix)
    {
        generateFeature(matrix);
        checkMode();
        if (mSegmentX.size() > 0)
            MaxSegmentX = MatrixUtil.getMax(mSegmentX);
        if (mSegmentY.size() > 0)
            MaxSegmentY = MatrixUtil.getMax(mSegmentY);
    }

    public int SegmentModeX;
    public int SegmentModeY;
    public int MaxSegmentX;
    public int MaxSegmentY;

    public List<Integer> getDensityX()
    {
        return new ArrayList<Integer>(mDensityX);
    }

    public List<Integer> getDensityY()
    {
        return new ArrayList<Integer>(mDensityY);
    }

    public List<Integer> getSegmentX()
    {
        return new ArrayList<Integer>(mSegmentX);
    }

    public List<Integer> getSegmentY()
    {
        return new ArrayList<Integer>(mSegmentY);
    }

    public int getDensitySumX()
    {
        return getListSum(mDensityX);
    }

    public int getDensitySumY()
    {
        return getListSum(mDensityY);
    }

    public int getSegmentSumX()
    {
        return getListSum(mSegmentX);
    }

    public int getSegmentSumY()
    {
        return getListSum(mSegmentY);
    }

    private int getListSum(List<Integer> list)
    {
        int sum = 0;
        for (int i = 0; i < list.size(); i++)
        {
            sum += list.get(i);
        }
        return sum;
    }

    public boolean IsBlank()
    {
        return mDensityX.size() == 0 && mDensityY.size() == 0 && mSegmentX.size() == 0 && mSegmentY.size() == 0;
    }

    public int GetResampleDensityX(int x, int dimensionX)
    {
        return resample(mDensityX, x, dimensionX);
    }

    public int GetResampleDensityY(int y, int dimensionY)
    {
        return resample(mDensityY, y, dimensionY);
    }

    public int GetResampleSegmentX(int x, int dimensionX)
    {
        return resample(mSegmentX, x, dimensionX);
    }

    public int GetResampleSegmentY(int y, int dimensionY)
    {
        return resample(mSegmentY, y, dimensionY);
    }

    private int resample(List<Integer> source, int newIndex, int newCount)
    {
        if (source.size() == 0 || newCount == 0) return 0;
        if (newCount == source.size()) return source.get(newIndex);

        int idx = source.size() * newIndex / newCount;
        int cur = source.get(idx);
        int prev = idx > 1 ? source.get(idx - 1) : cur;
        int next = idx < source.size() - 1 ? source.get(idx + 1) : cur;

        return (int)(cur + prev + next)/3;
    }

    /// <summary>
    /// get similarity of two MatrixFeature
    /// </summary>
    /// <param name="another"></param>
    /// <returns>in between 0 and 100</returns>
    public double getSimilarity(MatrixFeature another)
    {
        double result = 100;
        for (int x = 0; x < mDensityX.size(); x++)
        {
            int anotherDensity = another.GetResampleDensityX(x, mDensityX.size());
            double diff = (anotherDensity - mDensityX.get(x)) / (double)mDensityX.get(x);
            result -= diff > 0 ? diff : (-diff);
        }

        for (int y = 0; y < mDensityY.size(); y++)
        {
            int anotherDensity = another.GetResampleDensityY(y, mDensityY.size());
            double diff = (anotherDensity - mDensityY.get(y)) / (double)mDensityY.get(y);
            result -= diff > 0 ? diff : (-diff);
        }


        for (int x = 0; x < mSegmentX.size(); x++)
        {
            int anotherSegment = another.GetResampleSegmentX(x, mSegmentX.size());
            double diff = (anotherSegment - mSegmentX.get(x)) / (double)mSegmentX.get(x);
            result -= diff > 0 ? diff : (-diff);
        }

        for (int y = 0; y < mSegmentY.size(); y++)
        {
            int anotherSegment = another.GetResampleSegmentY(y, mSegmentY.size());
            double diff = (anotherSegment - mSegmentY.get(y)) / (double)mSegmentY.get(y);
            result -= diff > 0 ? diff : (-diff);
        }

        result -= Math.abs(this.SegmentModeX - another.SegmentModeX);
        result -= Math.abs(this.SegmentModeY - another.SegmentModeY);
        result -= Math.abs(this.MaxSegmentX - another.MaxSegmentX);
        result -= Math.abs(this.MaxSegmentY - another.MaxSegmentY);

        return result;
    }

    private void generateFeature(MatrixBase matrix)
    {
        // generate the features on X-direction
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

        // generate the features on Y-direction
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

    /// <summary>
    /// directly get value based on the feature
    /// </summary>
    /// <returns></returns>
    public int getFeatureValue()
    {
        //if (mDensityX.size() == 0 || mDensityY.size() == 0)
        //{
        //    return 0;
        //}
        //for (int i = 1; i <= 9; i++)
        //{
        //    mPossibleValue.Add(i);
        //}
        //IEnumerable<int> modePossibleList = checkMode();
        //IEnumerable<int> maxPossibleList = checkMax();

        //IEnumerable<int> result = modePossibleList.Intersect(maxPossibleList);

        //mPossibleValue.Clear();
        //IEnumerator<int> enu = result.GetEnumerator();
        //while (enu.MoveNext())
        //{
        //    mPossibleValue.Add(enu.Current);
        //}
        //if (mPossibleValue.size() == 1)
        //{
        //    return mPossibleValue[0];
        //}
        return -1;
    }

    private void checkMode()
    {
        int count1 = 0, count2 = 0, count3 = 0, count4 = 0, countN = 0;
        // check mode on X direction
        CountArray countArr = new CountArray();
        getCount(mSegmentX, countArr);
        int max = getMax(countArr);
        HashSet<Integer> possibleValueX = new HashSet<Integer>();
        if (max == count3)
        {
            this.SegmentModeX = 3;
            //possibleValueX.Add(2);
            //possibleValueX.Add(5);
            //possibleValueX.Add(6);
            //possibleValueX.Add(8);
            //possibleValueX.Add(9);
        }
        else if (max == count2)
        {
            this.SegmentModeX = 2;
            //possibleValueX.Add(3);
            //possibleValueX.Add(4);
            //possibleValueX.Add(7);
        }
        else if (max == count1)
        {
            this.SegmentModeX = 1;
            //possibleValueX.Add(1);
        }

        // check mode on Y direction
        getCount(mSegmentY, countArr);
        max = getMax(countArr);
        HashSet<Integer> possibleValueY = new HashSet<Integer>();
        if (max == count2)
        {
            this.SegmentModeY = 2;
            //possibleValueY.Add(2);
            //possibleValueY.Add(3);
            //possibleValueY.Add(4);
            //possibleValueY.Add(6);
            //possibleValueY.Add(8);
            //possibleValueY.Add(9);
        }
        else if (max == count1)
        {
            this.SegmentModeY = 1;
            //possibleValueY.Add(1);
            //possibleValueY.Add(2);
            //possibleValueY.Add(3);
            //possibleValueY.Add(4);
            //possibleValueY.Add(5);
            //possibleValueY.Add(7);
        }

        //return possibleValueX.Intersect(possibleValueY);
    }

    private Iterable<Integer> checkMax()
    {
        int xMax = MatrixUtil.getMax(mSegmentX);
        HashSet<Integer> possibleValueX = new HashSet<Integer>();
        if (xMax == 1)
        {
            possibleValueX.add(1);
        }
        else if (xMax == 2)
        {
            possibleValueX.add(1);
            possibleValueX.add(7);
        }
        else if (xMax == 3)
        {
            possibleValueX.add(2);
            possibleValueX.add(3);
            possibleValueX.add(5);
            possibleValueX.add(6);
            possibleValueX.add(9);
        }
        else if (xMax == 4)
        {
            possibleValueX.add(2);
            possibleValueX.add(3);
            possibleValueX.add(5);
            possibleValueX.add(6);
            possibleValueX.add(8);
        }
        int yMax = MatrixUtil.getMax(mSegmentY);
        HashSet<Integer> possibleValueY = new HashSet<Integer>();
        if (yMax == 1)
        {
            possibleValueY.add(1);
            possibleValueY.add(7);
        }
        else if (yMax == 2)
        {
            possibleValueY.add(2);
            possibleValueY.add(3);
            possibleValueY.add(4);
            possibleValueY.add(5);
            possibleValueY.add(6);
            possibleValueY.add(7);
            possibleValueY.add(8);
            possibleValueY.add(9);
        }
        else if (yMax == 3)
        {
            possibleValueY.add(6);
            possibleValueY.add(9);
        }
        return MatrixUtil.intersect(possibleValueX, possibleValueY);
    }

    private void getCount(List<Integer> input, CountArray countArr)
    {
        for (int i = 0; i < input.size(); i++)
        {
            switch (input.get(i))
            {
                case 1:
                    countArr.count1 ++;
                    break;
                case 2:
                    countArr.count2++;
                    break;
                case 3:
                    countArr.count3++;
                    break;
                case 4:
                    countArr.count4++;
                    break;
                default:
                    countArr.countN++;
                    break;
            }
        }
    }

    private int getMax(CountArray countArr)
    {
        return Math.max(
                Math.max(
                        Math.max(
                                Math.max(countArr.count1, countArr.count2),
                                countArr.count3),
                        countArr.count4),
                countArr.countN);
    }
}
