package master.sudoku.fragments;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;

import master.sudoku.R;
import master.sudoku.model.Sudoku;
import master.sudoku.ocr.ImageCutter;
import master.sudoku.ocr.RecognizerNN;
import master.sudoku.utils.FileUtils;

/**
 * Created by zhangle on 03/01/2017.
 */
public class LoadPuzzleFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_READ = 2;
    public static final int REQUEST_EXTERNAL_STORAGE_WRITE = 3;
    private ImageView mImgView = null;
    private Button mLoadBtn = null;
    private Bitmap mImageBitmap = null;
    private Callback mCallback = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_load_puzzle, container,
                false);
        mImgView = (ImageView)rootView.findViewById(R.id.load_puzzle_image);
        if (mImageBitmap != null) {
            mImgView.setImageBitmap(mImageBitmap);
        }
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        mLoadBtn = (Button)rootView.findViewById(R.id.load_puzzle_button);
        mLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPuzzle();
            }
        });
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_EXTERNAL_STORAGE_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPuzzle();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
//            InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...

            Uri uri = data.getData();
            if (uri != null) {
                final String path = FileUtils.getFilePathFromUri(uri, this.getActivity());
                final File file = new File(path);
                if (!file.exists()) {
                    return;
                }
                mImageBitmap = decodeBitmapFromFile(path);
                mImgView.setImageBitmap(mImageBitmap);
            } else {
//                mImageBitmap = decodeBitmapFromByte(data);
            }
        }
    }
//
//    @Override
//    public void onResume() {
//        if (mImageBitmap != null) {
//            mImgView.setImageBitmap(mImageBitmap);
//        }
//    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageBitmap = bitmap;
        mImgView.setImageBitmap(mImageBitmap);
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this.getActivity(), permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(permission)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    private void selectImage() {
        if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE_READ)) {
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void loadPuzzle() {
        Bitmap result = this.binarization(mImageBitmap);
        mImgView.setImageBitmap(result);
//
//        try {
//            if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE_WRITE)) {
//                return;
//            }
//            ImageCutter ic = new ImageCutter(mImageBitmap);
//            ic.saveImages();
//            Sudoku model = parseModel(ic);
//            if (mCallback != null) {
//                mCallback.loadPuzzleDone(model);
//            }
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }
    }

    private Sudoku parseModel(ImageCutter ic) {
        Sudoku model = new Sudoku();
        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.nn_weights_printed);
        RecognizerNN rec = new RecognizerNN(is);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Bitmap img = ic.getImage(i, j);
//                Recognizer rec = new Recognizer(img);
                int num = rec.determine(img);
                model.setInitValue(i, j, num);
            }
        }
        return model;
    }

    Bitmap decodeBitmapFromFile(String filename) {
        final int maxWidth = getResources().getDisplayMetrics().widthPixels;
        final int maxHeight = getResources().getDisplayMetrics().heightPixels;

        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, decodeOptions);
        final int actualWidth = decodeOptions.outWidth;
        final int actualHeight = decodeOptions.outHeight;

        // Then compute the dimensions we would ideally like to decode to.
        final int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
        final int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

        Bitmap bitmap;
        final Bitmap tempBitmap = BitmapFactory.decodeFile(filename, decodeOptions);
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle();

        } else {
            bitmap = tempBitmap;
        }

//        mSampleSize = decodeOptions.inSampleSize;
        if (!bitmap.isMutable()) {
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            bitmap = bitmap.copy(config , true);
        }
        return bitmap;
    }

    Bitmap decodeBitmapFromByte(byte[] data) {
        final int maxWidth = getResources().getDisplayMetrics().widthPixels;
        final int maxHeight = getResources().getDisplayMetrics().heightPixels;

        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        final int actualWidth = decodeOptions.outWidth;
        final int actualHeight = decodeOptions.outHeight;

        // Then compute the dimensions we would ideally like to decode to.
        final int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
        final int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

        final Bitmap bitmap;
        final Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle();

        } else {
            bitmap = tempBitmap;
        }

//        mSampleSize = decodeOptions.inSampleSize;
        return bitmap;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. mWidth for
     *                        max mWidth), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to
     *                        maintain aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
            int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual mWidth of the bitmap
     * @param actualHeight  Actual mHeight of the bitmap
     * @param desiredWidth  Desired mWidth of the bitmap
     * @param desiredHeight Desired mHeight of the bitmap
     */
    private static int findBestSampleSize(int actualWidth, int actualHeight,
            int desiredWidth, int desiredHeight) {
        final double wr = (double) actualWidth / desiredWidth;
        final double hr = (double) actualHeight / desiredHeight;
        final double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    public interface Callback {
        void loadPuzzleDone(Sudoku model);
    }

    private Bitmap detectEdges(Bitmap bitmap) {
        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        Mat gray = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY, 4);
        Mat edges = new Mat(gray.height(), gray.width(), CvType.CV_8UC4);
        Imgproc.Canny(gray, edges, 80, 100);
        //Imgproc.cvtColor(edges, rgba, Imgproc.COLOR_GRAY2RGBA, 4);

        try {
            // Don't do that at home or work it's for visualization purpose.
            Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(edges, resultBitmap);
            return resultBitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap binarization(Bitmap bitmap) {
        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        Mat gray = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.threshold(gray, gray, 100, 255, Imgproc.THRESH_OTSU);
        Bitmap resultBitmap = Bitmap.createBitmap(gray.cols(), gray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray, resultBitmap);
        return resultBitmap;
    }
}
