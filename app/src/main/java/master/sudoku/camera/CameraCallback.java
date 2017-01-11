package master.sudoku.camera;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangle on 11/01/2017.
 */
public class CameraCallback extends CameraDevice.StateCallback implements SurfaceHolder.Callback {
    public static final String TAG = "CameraCallback";
    private final Context mContext;

    private android.hardware.camera2.CameraDevice mCamera;
    private CaptureRequest.Builder mCptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;

    private SurfaceHolder mHolder;

    private CameraCaptureSession.StateCallback mSessionCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            mCameraCaptureSession = session;
            updatePreview();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            mCameraCaptureSession = null;
        }
    };

    public CameraCallback(Context context) {
        mContext = context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera == null) {
                mHolder = holder;
                return;
            }
            List<Surface> surfaceList = new ArrayList<Surface>();
            surfaceList.add(holder.getSurface());
            mCptureRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCptureRequestBuilder.addTarget(holder.getSurface());
            mCamera.createCaptureSession(surfaceList, mSessionCallback, null);
        } catch (Exception ex) {
            if (mCamera != null) {
                mCamera = null;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        if (mCamera == null) {
            return;
        }
//        mParameters = mCamera.getParameters();
//        mParameters.setPictureFormat(ImageFormat.JPEG);
//        setPictureSize(mParameters);
//        Point previewSizePt;
//        if (mFoundCommonSize) {
//            previewSizePt = new Point(mParameters.getPictureSize().width,
//                    mParameters.getPictureSize().height);
//        } else {
//            previewSizePt = getPreviewSize(mParameters,
//                    mParameters.getPictureSize());
//        }
//
//        if (previewSizePt.x * previewSizePt.y < SharedConstants.MIN_PICTURE_SIZE) {
//            Camera.Size size = getBestFitSize(mParameters.getSupportedPreviewSizes());
//            mParameters.setPreviewSize(size.width, size.height);
//        } else {
//            mParameters.setPreviewSize(previewSizePt.x, previewSizePt.y);
//        }
//        if (mContext.getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
//            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        }
//        mCamera.setParameters(mParameters);
//        mCamera.startPreview();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera = null;
        }
    }

    public void setFlashLight(boolean isOn) {
        if (mCamera == null) {
            return;
        }
    }

    public void updatePreview() {
        if(null == mCamera || null == mCameraCaptureSession) {
            return;
        }
        mCptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mCameraCaptureSession.setRepeatingRequest(mCptureRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onOpened(CameraDevice camera) {
        this.mCamera = camera;
        if (mHolder != null) {
            surfaceCreated(mHolder);
        }
    }

    @Override
    public void onDisconnected(CameraDevice camera) {
        this.mCamera = null;
    }

    @Override
    public void onError(CameraDevice camera, int error) {
        this.mCamera = null;
    }
}
