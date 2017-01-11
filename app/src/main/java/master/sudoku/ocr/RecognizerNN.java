package master.sudoku.ocr;

import android.graphics.Bitmap;

import java.io.InputStream;

import master.sudoku.ocr.util.NeuralNetwork;
import master.sudoku.ocr.util.ThresholdUtil;

/**
 * Created by zhangle on 10/01/2017.
 */
public class RecognizerNN {
    private static final int TARGET_IMG_HEIGHT = 30;
    private static final int TARGET_IMG_WIDTH = 20;
    private static final double LEARNING_RATE = .2;
    private static final int HIDDEN_LAYERS = 50;

    private NeuralNetwork nn;

    public RecognizerNN(InputStream weightIs) {
        nn = new NeuralNetwork(LEARNING_RATE, TARGET_IMG_HEIGHT
                * TARGET_IMG_WIDTH, HIDDEN_LAYERS, 10);
        try {
            nn.loadWeights(weightIs);
        } catch (Exception e) {
            nn = null;
            e.printStackTrace();
        }
    }

    public int determine(Bitmap image) {
        if (image.getHeight() < 2 || image.getWidth() < 2) {
            return 0;
        }
        if (nn == null) {
            return 0;
        }
        Bitmap img = Bitmap.createScaledBitmap(image, TARGET_IMG_WIDTH, TARGET_IMG_HEIGHT, false);
        int[][] data = getImageData(img);
        int[] intBits = new int[TARGET_IMG_HEIGHT * TARGET_IMG_WIDTH];
        for (int j = 0; j < TARGET_IMG_HEIGHT; j++) {
            for (int i = 0; i < TARGET_IMG_WIDTH; i++) {
                intBits[TARGET_IMG_WIDTH * j + i] = data[i][j];
            }
        }
        return nn.eval(intBits);
    }

    private int[][] getImageData(Bitmap image) {
        int[][] result = new int[TARGET_IMG_WIDTH][TARGET_IMG_HEIGHT];
        for (int j = 0; j < TARGET_IMG_HEIGHT; j++) {
            for (int i = 0; i < TARGET_IMG_WIDTH; i++) {
                int color = image.getPixel(i, j);
                int gray = ThresholdUtil.GetDarkValue(color);
                if (gray > ThresholdUtil.DARK_THRESHOLD) {
                    result[i][j] = 1;
                } else {
                    result[i][j] = 0;
                }
            }
        }
        return result;
    }
}
