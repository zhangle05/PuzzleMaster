/**
 * 
 */
package master.sudoku.ocr;

import android.graphics.Bitmap;

import java.util.List;

import master.sudoku.ocr.matrix.ImageMatrix;


/**
 * @author dannyzha
 *
 */
public class Recognizer
{
    private Bitmap mImage;

//    private static final String BASE_FOLDER = FileUtils.getCachePath() + "\\Sudoku\\training\\";
    /// <summary>
    /// key: Integer
    /// value: List of ImageMatrix with the standard images
    /// </summary>
//    private static Hashtable<Integer, List<ImageMatrix>> s_StandardTable;

    /// <summary>
    /// static initializer
    /// </summary>
//    static
//    {
//        buildStandardTable();
//        printStandardTable();
//    }

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="image"></param>
    public Recognizer(Bitmap image)
    {
        this.mImage = image;
    }

    public int determine()
    {
        ImageMatrix target = new ImageMatrix(mImage);
        int result = 0;
        if (target.isBlank())
        {
            return 0;
        }
        int featureValue = target.getFeature().getFeatureValue();
        if (featureValue > 0)
        {
            return featureValue;
        }
//        double maxSimilarity = Double.MIN_VALUE;
//        while(enu.hasMoreElements())
//        {
//            int i = enu.nextElement();
//            List<ImageMatrix> standards = s_StandardTable.get(i);
//            for (int j = 0; j < standards.size(); j++)
//            {
//                //System.Console.WriteLine("Determine " + i + "," + j);
//                double similarity = target.getFeature().getSimilarity(standards.get(j));
//                //System.Console.WriteLine("Similarity is:" + similarity);
//                if (similarity > maxSimilarity)
//                {
//                    maxSimilarity = similarity;
//                    result = i;
//                }
//            }
//        }
//        System.Console.WriteLine("Max similarity is:" + maxSimilarity);
//        System.Console.WriteLine("Result is:" + result);
        return result;
    }

//    private static void buildStandardTable()
//    {
//        s_StandardTable = new Hashtable<Integer, List<ImageMatrix>>();
//        for (int i = 0; i <= 9; i++)
//        {
//            String folder = BASE_FOLDER + i;
//            File dic = new File(folder);
//            File[] files = dic.listFiles();
//            List<ImageMatrix> imgMatrixList = new ArrayList<ImageMatrix>();
//            for (int j = 0; j < files.length; j++)
//            {
//                try
//                {
//                    Bitmap img = BitmapFactory.decodeFile(files[i].getAbsolutePath());
//                    ImageMatrix matrix = new ImageMatrix(img);
//                    imgMatrixList.add(matrix);
//                }
//                catch (Exception ex)
//                {
//                    ex.printStackTrace();
//                }
//            }
//            s_StandardTable.put(i, imgMatrixList);
//        }
//    }

//    private static void printStandardTable()
//    {
//        Enumeration<Integer> enu = s_StandardTable.keys();
//        while (enu.hasMoreElements())
//        {
//            int i = (int)enu.nextElement();
//            List<ImageMatrix> standards = (List<ImageMatrix>)s_StandardTable.get(i);
//            for (int j = 0; j < standards.size(); j++)
//            {
//                MatrixFeature2 feature = standards.get(j).getFeature();
//                System.out.println("Feature of " + i + ":");
//                System.out.print("Density X:");
//                printList(feature.mDensityX);
//                System.out.print("Density Y:");
//                printList(feature.mDensityY);
//                System.out.print("Segment X:");
//                printList(feature.mSegmentX);
//                System.out.println("Segment Y:");
//                printList(feature.mSegmentY);
//                System.out.println("============================");
//            }
//        }
//    }

    private static void printList(List<Integer> list)
    {
        for(int i=0; i<list.size(); i++)
        {
            System.out.print(list.get(i)+" ");
        }
        System.out.println();
    }

}
