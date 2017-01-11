/**
 * 
 */
//using System.Windows.Controls;
//using System.Windows.Media;
//using System.Windows.Media.Imaging;
package master.sudoku.ocr.matrix;

import master.sudoku.ocr.util.ThresholdUtil;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * @author dannyzha
 *
 */
public class ImageMatrix extends MatrixBase
{
    private Bitmap mImage;
    private MatrixFeature2 mFeature;

    public ImageMatrix(Bitmap image)
    {
    	super(image.getWidth(), image.getHeight());
        mImage = image;

        mFeature = new MatrixFeature2(this);
        //Image img = new Image();
        //img.source = bi;

        //img.Measure(new Size(100, 100));
        //img.Arrange(new Rect(0, 0, 100, 100));

        //ScaleTransform scaleTrans = new ScaleTransform();
        //double scale = (double)500 / (double)Math.Max(bi.PixelHeight, bi.PixelWidth);
        //scaleTrans.CenterX = 0;
        //scaleTrans.CenterY = 0;
        //scaleTrans.ScaleX = scale;
        //scaleTrans.ScaleY = scale;

        //WriteableBitmap writeableBitmap = new WriteableBitmap(500, 500);
        //writeableBitmap.Render(img, scaleTrans);

        //int[] pixelData = writeableBitmap.Pixels;
    }

    public MatrixFeature2 getFeature()
    {
        return mFeature;
    }

    public boolean isBlank()
    {
        return mFeature.isBlank();
    }

    public int getColor(int x, int y)
    {
        if (x < 0 || x > mDimensionX || y < 0 || y > mDimensionY)
        {
            return Color.WHITE;
        }
        return mImage.getPixel(x, y);
    }

    /* (non-Javadoc)
     * @see com.skyway.pandora.digitsrecognizer.matrix.MatrixBase#getValue(int, int)
     */
    @Override
    public int getValue(int x, int y)
    {
        if (x < 0 || x > mDimensionX || y < 0 || y > mDimensionY)
        {
            return -1;
        }
        int c = mImage.getPixel(x, y);
        return ThresholdUtil.GetDarkValue(c);
    }

    /* (non-Javadoc)
     * @see com.skyway.pandora.digitsrecognizer.matrix.MatrixBase#setValue(int, int, int)
     */
    @Override
    public void setValue(int x, int y, int value)
    {
        // intended do nothing
    }

    @Override
    public boolean isSimilar(MatrixBase another)
    {
        return false;
        //if (!(another is ImageMatrix))
        //{
        //    return false;
        //}
        //double similarity = this.mFeature.getSimilarity(((ImageMatrix)another).Feature);
        //System.Console.WriteLine("Similarity is:" + similarity);
        //if (similarity > 90)
        //{
        //    return true;
        //}
        //return false;
    }

    //public double getSimilarity(MatrixBase another)
    //{
    //    if (!(another is ImageMatrix))
    //    {
    //        return 0;
    //    }
    //    return this.mFeature.getSimilarity(((ImageMatrix)another).Feature);
    //}

    //private void buildColorTable()
    //{
    //    for (int x = 0; x < mDimensionX; x++)
    //    {
    //        for (int y = 0; y < mDimensionY; y++)
    //        {
    //            Color c = mImage.GetPixel(x, y);
    //            if (mColorTable.ContainsKey(c))
    //            {
    //                mColorTable[c] = (int)mColorTable[c] + 1;
    //            }
    //            else
    //            {
    //                mColorTable.Add(c, 1);
    //            }
    //        }
    //    }
    //}
}