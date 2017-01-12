package master.sudoku.fragments;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import master.sudoku.R;
import master.sudoku.camera.CvCameraCallback;
import master.sudoku.ocr.util.MatrixUtil;
import master.sudoku.widgets.ScannerBox;

/**
 * Created by zhangle on 03/01/2017.
 */
public class CapturePuzzleFragment extends Fragment implements CvCameraCallback.CvCameraFrameCallback {

    public static final int REQUEST_CAMERA_OPEN = 1;
    private CameraBridgeViewBase mOpenCvCameraView;
    private ScannerBox mScannerBox;
    private CvCameraCallback mCameraCallback;
    private Callback mCallback;
    private boolean mFrameChecking = false;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getContext()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture_puzzle, container,
                false);
        mOpenCvCameraView = (CameraBridgeViewBase) rootView.findViewById(R.id.cv_camera);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mScannerBox = (ScannerBox) rootView.findViewById(R.id.scanner_box);
        mScannerBox.startAnimation();

        mCameraCallback = new CvCameraCallback(this.getContext());
        mCameraCallback.setFrameCallback(this);
        openCamera();

        mOpenCvCameraView.setCvCameraViewListener(mCameraCallback);

        return rootView;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        try {
            if (!OpenCVLoader.initDebug()) {
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.getContext(), mLoaderCallback);
            } else {
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_OPEN: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void openCamera() {
        if (!checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_OPEN)) {
            return;
        }
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

    @Override
    public void onFrameCaptured(final Mat mat) {
        if (mFrameChecking) {
            return;
        }
        mFrameChecking = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkFrame(mat);
                mFrameChecking = false;
            }
        }).start();

    }

    private void checkFrame(Mat mat) {
        if (mat == null) {
            return;
        }
        if (MatrixUtil.hasBoundary(mat)) {
            mOpenCvCameraView.disableView();
            if (mCallback != null) {
                mCallback.capturePuzzleDone(mat);
            }
        }
    }

    public interface Callback {
        void capturePuzzleDone(Mat mat);
    }
}
