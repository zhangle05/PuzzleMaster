/**
 *
 */
package master.sudoku.ocr.util;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import master.sudoku.ocr.matrix.BoundaryMatrix;
import master.sudoku.ocr.matrix.ImageMatrix;

/**
 * @author dannyzha
 *
 */
public class MatrixUtil
{
    public static boolean hasBoundary(ImageMatrix imgMatrix) {
        int left = findBoundary(imgMatrix, true, true);
        if (left == 0) {
            return false;
        }
        int top = findBoundary(imgMatrix, false, true);
        if (top == 0) {
            return false;
        }
        int right = findBoundary(imgMatrix, true, false);
        if (right == 0) {
            return false;
        }
        int bottom = findBoundary(imgMatrix, false, false);
        if (bottom == 0) {
            return false;
        }
        return true;
    }


    public static boolean hasBoundary(Mat cvMat) {
        int left = findBoundary(cvMat, true, true);
        if (left == 0) {
            return false;
        }
        int top = findBoundary(cvMat, false, true);
        if (top == 0) {
            return false;
        }
        int right = findBoundary(cvMat, true, false);
        if (right == 0) {
            return false;
        }
        int bottom = findBoundary(cvMat, false, false);
        if (bottom == 0) {
            return false;
        }
        return true;
    }

    public static BoundaryMatrix detectBoundary(ImageMatrix imgMatrix)
    {
        BoundaryMatrix result = new BoundaryMatrix(imgMatrix.getDimensionX(), imgMatrix.getDimensionY());
        int left = findBoundary(imgMatrix, true, true);
        int top = findBoundary(imgMatrix, false, true);
        int right = findBoundary(imgMatrix, true, false);
        int bottom = findBoundary(imgMatrix, false, false);

        result.setLeft(left);
        result.setRight(right);
        result.setTop(top);
        result.setBottom(bottom);

        result.generateBoundarys();

        List<Integer> xList = getInnerBoundarys(imgMatrix, left, right, top, bottom, true);
        List<Integer> yList = getInnerBoundarys(imgMatrix, left, right, top, bottom, false);
        result.justifyBoundaries(xList, yList);
        return result;
    }

    private static int findBoundary(ImageMatrix imgMatrix, boolean isHorizontal, boolean isAscend) {
        int limit = isHorizontal ? imgMatrix.getDimensionX() : imgMatrix.getDimensionY();
        int sampleLimit = isHorizontal ? imgMatrix.getDimensionY() : imgMatrix.getDimensionX();
        int sampleSize = (int)(sampleLimit * 0.6);
        for (int i = 0; i < limit; i++)
        {
            int foreColorCount = 0;
            int idx = isAscend ? i : (limit - i -1);
            for (int sampleIdx = 0; sampleIdx < sampleLimit; sampleIdx++)
            {
                int c = isHorizontal ? imgMatrix.getColor(idx, sampleIdx) : imgMatrix.getColor(sampleIdx, idx);
                if (!ThresholdUtil.almostSameColor(ThresholdUtil.BG_COLOR, c))
                {
                    foreColorCount++;
                }
                if (foreColorCount > sampleSize) {
                    break;
                }
            }
            if (foreColorCount > sampleSize) {
                return idx;
            }
        }
        return 0;
    }

    private static int findBoundary(Mat mat, boolean isHorizontal, boolean isAscend) {
        int limit = isHorizontal ? mat.cols() : mat.rows();
        int sampleLimit = isHorizontal ? mat.rows() : mat.cols();
        int sampleSize = (int)(sampleLimit * 0.6);
        for (int i = 1; i < limit; i++)
        {
            int foreColorCount = 0;
            int idx = isAscend ? i : (limit - i -1);
            for (int sampleIdx = 1; sampleIdx < sampleLimit; sampleIdx++)
            {
                double[] tmp = isHorizontal ? mat.get(sampleIdx, idx) : mat.get(idx, sampleIdx);
                int c = (int)tmp[0];
                if (!ThresholdUtil.almostSameColor(ThresholdUtil.BG_COLOR, c))
                {
                    foreColorCount++;
                }
                if (foreColorCount > sampleSize) {
                    break;
                }
            }
            if (foreColorCount > sampleSize) {
                return idx;
            }
        }
        return 0;
    }

    private static List<Integer> getInnerBoundarys(ImageMatrix imgMatrix, int left, int right, int top, int bottom, boolean isHorizontal) {
        int boundaryTolerance = 5;
        int idx1Lower = (isHorizontal ? left : top) + boundaryTolerance;
        int idx1Upper = (isHorizontal ? right : bottom) - boundaryTolerance;
        int idx2Lower = isHorizontal ? top : left;
        int idx2Upper = isHorizontal ? bottom : right;
        int foreCountLimit = (int)((idx2Upper - idx2Lower) * 0.8);
        List<Integer> result = new ArrayList<Integer>();
        int lastIdx = idx1Lower;
        for (int idx1 = idx1Lower; idx1 < idx1Upper; idx1++) {
            int foreColorCount = 0;
            for (int idx2 = idx2Lower; idx2 < idx2Upper; idx2++) {
                int c = isHorizontal ? imgMatrix.getColor(idx1, idx2) : imgMatrix.getColor(idx2, idx1);
                if (!ThresholdUtil.almostSameColor(ThresholdUtil.BG_COLOR, c))
                {
                    foreColorCount++;
                }
            }
            if (foreColorCount > foreCountLimit && (idx1 - lastIdx) > boundaryTolerance) {
                result.add(idx1);
                lastIdx = idx1;
            }
        }
        return result;
    }

//    public static float calculateVariance(List<Color> samples)
//    {
//        float sumH = 0, sumS = 0, sumV = 0;
//        float aveH = 0, aveS = 0, aveV = 0;
//        for (int i = 0; i < samples.size(); i++)
//        {
//            sumH += samples.get(i)
//            sumS += samples[i].GetSaturation();
//            sumV += samples[i].GetBrightness();
//        }
//        aveH = sumH / samples.Count;
//        aveS = sumS / samples.Count;
//        aveV = sumV / samples.Count;
//        sumH = sumS = sumV = 0;
//        for (int i = 0; i < samples.Count; i++)
//        {
//            sumH += (samples[i].GetHue() - aveH) * (samples[i].GetHue() - aveH);
//            sumS += (samples[i].GetSaturation() - aveS) * (samples[i].GetSaturation() - aveS);
//            sumV += (samples[i].GetBrightness() - aveV) * (samples[i].GetBrightness() - aveV);
//        }
//        float resultH = sumH / samples.Count;
//        float resultS = sumS / samples.Count;
//        float resultV = sumV / samples.Count;
//
//        return resultH + resultS + resultV;
//    }

    public static int calculateVariance(List<Integer> samples)
    {
        int sum = 0;
        int ave = 0;
        for (int i = 0; i < samples.size(); i++)
        {
            sum += samples.get(i);
        }
        ave = sum / samples.size();
        sum = 0;
        for (int i = 0; i < samples.size(); i++)
        {
            sum += (samples.get(i) - ave) * (samples.get(i) - ave);
        }
        int result = sum / samples.size();
        return result / ave;
    }

    public static int getMax(List<Integer> values) {
        int result = Integer.MIN_VALUE;
        for(int i=0; i<values.size(); i++) {
            if(result < values.get(i)) {
                result = values.get(i);
            }
        }
        return result;
    }

    public static Iterable<Integer> intersect(HashSet<Integer> values1, HashSet<Integer> values2) {
        List<Integer> result = new LinkedList<Integer>();
        for(Iterator<Integer> it = values1.iterator(); it.hasNext();) {
            Integer value = it.next();
            if(values2.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }
}