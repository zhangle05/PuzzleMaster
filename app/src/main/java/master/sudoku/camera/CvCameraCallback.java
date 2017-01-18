package master.sudoku.camera;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.content.Context;

/**
 * Created by zhangle on 12/01/2017.
 */
public class CvCameraCallback implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final Context mContext;
    private CvCameraFrameCallback mFrameCallback;

    public CvCameraCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat src = inputFrame.rgba();
        try {
            // rotate 90º clockwise
            Core.flip(src.t(), src, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (mFrameCallback != null) {
            mFrameCallback.onFrameCaptured(src);
        }
        return src;
    }

    public void setFrameCallback(CvCameraFrameCallback callback) {
        mFrameCallback = callback;
    }

    public interface CvCameraFrameCallback {
        void onFrameCaptured(Mat mat);
    }
}
