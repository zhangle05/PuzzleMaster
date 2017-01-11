/**
 *
 */
package master.sudoku.ocr;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import master.sudoku.ocr.matrix.BoundaryMatrix;
import master.sudoku.ocr.matrix.ImageMatrix;
import master.sudoku.ocr.util.MatrixUtil;
import master.sudoku.ocr.util.ThresholdUtil;
import master.sudoku.utils.FileUtils;

/**
 * @author dannyzha
 *
 */
public class ImageCutter
{
    /// <summary>
    /// key: Index
    /// value: Bitmap
    /// </summary>
    private Hashtable<Index, Bitmap> mImageTable;

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="orgImage"></param>
    public ImageCutter(Bitmap orgImage) throws Exception
    {
        int bgColor = binarization(orgImage);
        ThresholdUtil.BG_COLOR = bgColor;
        ImageMatrix imgMatrix = new ImageMatrix(orgImage);

        BoundaryMatrix boundary = MatrixUtil.detectBoundary(imgMatrix);
        if (!boundary.isValid())
        {
            throw new Exception("Invalid image.");
        }
        buildImageTable(orgImage, boundary);
    }

    /// <summary>
    /// Another constructor
    /// </summary>
    /// <param name="orgImage"></param>
    /// <param name="boundary"></param>
    public ImageCutter(Bitmap orgImage, BoundaryMatrix boundary) throws Exception
    {
        if (!boundary.isValid())
        {
            throw new Exception("Invalid image.");
        }

        buildImageTable(orgImage, boundary);
    }

    public Bitmap get(Index index)
    {
        return mImageTable.get(index);
    }

    public Bitmap getImage(int i, int j)
    {
        Enumeration<Index> enu = mImageTable.keys();
        while (enu.hasMoreElements())
        {
            Index idx = enu.nextElement();
            if (idx.Equals(i, j))
            {
                return (Bitmap)mImageTable.get(idx);
            }
        }
        return null;
    }

    /// <summary>
    /// binaryzation an image, global threshold
    /// </summary>
    /// <param name="image"></param>
    /// <returns>the BG color of the image</returns>
    private int binarization(Bitmap image)
    {
        int dimensionX = image.getWidth();
        int dimensionY = image.getHeight();
        int whitePixelCnt = 0, blackPixelCnt = 0;

        for (int x = 0; x < dimensionX; x++)
        {
            for (int y = 0; y < dimensionY; y++)
            {
                int value = ThresholdUtil.GetDarkValue(image.getPixel(x, y));
                if (value > ThresholdUtil.DARK_THRESHOLD)
                {
                    image.setPixel(x, y, Color.argb(255, 255, 255, 255));
                    whitePixelCnt++;
                }
                else
                {
                    image.setPixel(x, y, Color.argb(255, 0, 0, 0));
                    blackPixelCnt++;
                }
            }
        }
        // save the image to see the binarization result
        saveImage(image, "binaryzation.jpg");

        if (whitePixelCnt >= blackPixelCnt)
        {
            return Color.WHITE;
        }
        else
        {
            return Color.BLACK;
        }
    }

    /// <summary>
    /// Adaptive Thresholding Using the Integral Image
    /// http://people.scs.carleton.ca/~roth/iit-publications-iti/docs/gerh-50002.pdf
    /// </summary>
    /// <param name="image"></param>
    /// <returns></returns>
    private int binarization2(Bitmap image)
    {
        int dimensionX = image.getWidth();
        int dimensionY = image.getHeight();
        int whitePixelCnt = 0, blackPixelCnt = 0;

        // create integral table
        int[] integralTable = new int[dimensionX * dimensionY];
        for (int x = 0; x < dimensionX; x++)
        {
            // reset this column sum
            int sum = 0;
            for (int y = 0; y < dimensionY; y++)
            {
                int index = y * dimensionX + x;
                sum += ThresholdUtil.GetDarkValue(image.getPixel(x, y));
                if (x == 0)
                    integralTable[index] = sum;
                else
                    integralTable[index] = integralTable[index - 1] + sum;
            }
        }

        int S = dimensionX >> 3;
        int T = 8;
        int S2 = S / 2;

        for (int x = 0; x < dimensionX; x++)
        {
            for (int y = 0; y < dimensionY; y++)
            {
                int x1=x-S2, x2=x+S2;
                int y1=y-S2, y2=y+S2;
                // check the border
                if (x1 < 0) x1 = 0;
                if (x2 >= dimensionX) x2 = dimensionX-1;
                if (y1 < 0) y1 = 0;
                if (y2 >= dimensionY) y2 = dimensionY-1;

                int count = (x2 - x1) * (y2 - y1);
                int sum = integralTable[y2 * dimensionX + x2] -
                        integralTable[y1 * dimensionX + x2] -
                        integralTable[y2 * dimensionX + x1] +
                        integralTable[y1 * dimensionX + x1];


                int value = ThresholdUtil.GetDarkValue(image.getPixel(x, y));
                if (value * count > sum * (100 - T) / 100)
                {
                    image.setPixel(x, y, Color.WHITE);
                    whitePixelCnt++;
                }
                else
                {
                    image.setPixel(x, y, Color.BLACK);
                    blackPixelCnt++;
                }
            }
        }
        // save the image to see the binarization result
        saveImage(image, "binaryzation.jpg");

        if (whitePixelCnt >= blackPixelCnt)
        {
            return Color.WHITE;
        }
        else
        {
            return Color.BLACK;
        }
    }

    private Bitmap CropImage(Bitmap image, Rect area)
    {
        Bitmap cropped = Bitmap.createBitmap(image, area.left, area.top, area.width(), area.height());
        return cropped;
    }

    private void buildImageTable(Bitmap orgImage, BoundaryMatrix boundary)
    {
        List<Integer> xBoundary = boundary.getBoundaryXList();
        List<Integer> yBoundary = boundary.getBoundaryYList();
        List<Integer> xBoundaryWidth = boundary.getBoundaryWidthXList();
        List<Integer> yBoundaryWidth = boundary.getBoundaryWidthYList();
        mImageTable = new Hashtable<Index, Bitmap>();
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                int left = xBoundary.get(i)+xBoundaryWidth.get(i);
                int top = yBoundary.get(j)+yBoundaryWidth.get(j);
                int right = xBoundary.get(i + 1) - xBoundaryWidth.get(i + 1);
                int bottom = yBoundary.get(j + 1) - yBoundaryWidth.get(j + 1);
                // shrink the rect area
                int xMargin = (int)((right - left) * 0.1);
                int yMargin = (int)((bottom - top) * 0.1);
                left += xMargin;
                right -= xMargin;
                top += yMargin;
                bottom -= yMargin;
                // remove margins
                while (getForePixelSum(orgImage, left, top, bottom, true) == (bottom - top) && (left < right - 1)) {
                    left ++;
                }
                while (getForePixelSum(orgImage, right, top, bottom, true) == (bottom - top) && (right > left + 1)) {
                    right --;
                }
                while (getForePixelSum(orgImage, top, left, right, false) == (right - left) && (top < bottom - 1)) {
                    top ++;
                }
                while (getForePixelSum(orgImage, bottom, left, right, false) == (right - left) && (bottom > top + 1)) {
                    bottom --;
                }

                while (getForePixelSum(orgImage, left, top, bottom, true) <= 1 && (left < right - 1)) {
                    left ++;
                }
                while (getForePixelSum(orgImage, right, top, bottom, true) <= 1 && (right > left + 1)) {
                    right --;
                }
                while (getForePixelSum(orgImage, top, left, right, false) <= 1 && (top < bottom - 1)) {
                    top ++;
                }
                while (getForePixelSum(orgImage, bottom, left, right, false) <= 1 && (bottom > top + 1)) {
                    bottom --;
                }
                Rect area = new Rect(left, top, right, bottom);
                Bitmap crop = CropImage(orgImage, area);
                //crop = removeMargin(crop);
                mImageTable.put(new Index(i, j), crop);
            }
        }
    }

    private int getForePixelSum(Bitmap image, int index, int lower, int upper, boolean isVertical) {
        int sum = 0;
        for (int i = lower; i < upper; i++) {
            int color = isVertical ? image.getPixel(index, i) : image.getPixel(i, index);
            if (color != ThresholdUtil.BG_COLOR) {
                sum ++;
            }
        }
        return sum;
    }

//    private Bitmap removeMargin(Bitmap image)
//    {
//        image = denoise(image);
//        int left = 0, right = image.getWidth(), top = 0, bottom = image.getHeight();
//        boolean changed = false;
//        for (int x = 0; x < image.getWidth(); x++)
//        {
//            for (int y = 0; y < image.getHeight(); y++)
//            {
//                int value = ThresholdUtil.GetDarkValue(image.getPixel(x, y));
//                if (value <= ThresholdUtil.DARK_THRESHOLD)
//                {
//                    left = x;
//                    break;
//                }
//            }
//            if (left > 0)
//            {
//                changed = true;
//                break;
//            }
//        }
//        for (int x = image.getWidth() - 1; x >= 0; x--)
//        {
//            for (int y = 0; y < image.getHeight(); y++)
//            {
//                int value = ThresholdUtil.GetDarkValue(image.getPixel(x, y));
//                if (value <= ThresholdUtil.DARK_THRESHOLD)
//                {
//                    right = x;
//                    break;
//                }
//            }
//            if (right < image.getWidth())
//            {
//                changed = true;
//                break;
//            }
//        }
//        for (int y = 0; y < image.getHeight(); y++)
//        {
//            for (int x = 0; x < image.getWidth(); x++)
//            {
//                int value = ThresholdUtil.GetDarkValue(image.getPixel(x, y));
//                if (value <= ThresholdUtil.DARK_THRESHOLD)
//                {
//                    top = y;
//                    break;
//                }
//            }
//            if (top > 0)
//            {
//                changed = true;
//                break;
//            }
//        }
//        for (int y = image.getHeight() - 1; y >= 0; y--)
//        {
//            for (int x = 0; x < image.getWidth(); x++)
//            {
//                int value = ThresholdUtil.GetDarkValue(image.getPixel(x, y));
//                if (value <= ThresholdUtil.DARK_THRESHOLD)
//                {
//                    bottom = y;
//                    break;
//                }
//            }
//            if (bottom < image.getHeight())
//            {
//                changed = true;
//                break;
//            }
//        }
//        if(changed)
//        {
//            Logger.getLogger().debug("changed: " + "left:" + left + ", right:" + right + ", top:" + top + ", bottom:" + bottom);
//            return CropImage(image, new Rect(left, top, right, bottom));
//        }
//        return image;
//    }

    private Bitmap denoise(Bitmap image)
    {
        Bitmap result = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Config.ALPHA_8);
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                if (ThresholdUtil.IsDark(image.getPixel(x, y)))
                {
                    int neighborPixel = 0;
                    int x1 = x - 1, x2 = x + 1;
                    int y1 = y - 1, y2 = y + 1;
                    if (x1 < 0) x1 = 0;
                    if (y1 < 0) y1 = 0;
                    if (x2 > image.getWidth() - 1) x2 = image.getWidth() - 1;
                    if (y2 > image.getHeight() - 1) y2 = image.getHeight() - 1;

                    if (ThresholdUtil.IsDark(image.getPixel(x, y1)))
                    {
                        neighborPixel++;
                    }
                    if (ThresholdUtil.IsDark(image.getPixel(x, y2)))
                    {
                        neighborPixel++;
                    }
                    if (ThresholdUtil.IsDark(image.getPixel(x1, y)))
                    {
                        neighborPixel++;
                    }
                    if (ThresholdUtil.IsDark(image.getPixel(x2, y)))
                    {
                        neighborPixel++;
                    }


                    //if (ThresholdUtil.IsDark(image.GetPixel(x1, y1)))
                    //{
                    //    neighborPixel++;
                    //}

                    //if (ThresholdUtil.IsDark(image.GetPixel(x2, y1)))
                    //{
                    //    neighborPixel++;
                    //}

                    //if (ThresholdUtil.IsDark(image.GetPixel(x2, y2)))
                    //{
                    //    neighborPixel++;
                    //}

                    //if (ThresholdUtil.IsDark(image.GetPixel(x1, y2)))
                    //{
                    //    neighborPixel++;
                    //}


                    if (neighborPixel > 2)
                    {
                        result.setPixel(x, y, Color.BLACK);
                        continue;
                    }
                }
                result.setPixel(x, y, Color.WHITE);
            }
        }
        return result;
    }

    public void saveImages()
    {
        Enumeration<Index> indexEnum = mImageTable.keys();
        while (indexEnum.hasMoreElements())
        {
            Index idx = indexEnum.nextElement();
            Bitmap image = mImageTable.get(idx);
            saveImage(image, idx.getI() + "_" + idx.getJ() + ".jpg");
        }
    }

    public void saveImage(Bitmap img, String fileName) {
        String path = FileUtils.getCachePath();
        OutputStream fOut = null;
        File file = new File(path, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fOut = new FileOutputStream(file);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            img.compress(Bitmap.CompressFormat.JPEG, 85, bytes);
            bytes.flush();
            fOut.write(bytes.toByteArray());
            bytes.close();
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}