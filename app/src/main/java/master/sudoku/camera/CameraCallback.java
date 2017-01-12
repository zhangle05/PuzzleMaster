package master.sudoku.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangle on 11/01/2017.
 */
public class CameraCallback extends CameraDevice.StateCallback implements SurfaceHolder.Callback {

    public static final String TAG = "CameraCallback";

    private static final int sWidth = 640;
    private static final int sHeight = 480;

    private final Context mContext;

    private android.hardware.camera2.CameraDevice mCamera;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;

    private SurfaceHolder mHolder;
    private ImageReader mReader;

    private CameraFrameCallback mFrameCallback;

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

    private ImageReader.OnImageAvailableListener mReaderListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                if (mFrameCallback != null) {
                    mFrameCallback.onFrameCaptured(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (image != null) {
                    image.close();
                }
            }
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
            mReader = ImageReader.newInstance(sWidth, sHeight, ImageFormat.JPEG, 1);
            mReader.setOnImageAvailableListener(mReaderListener, null);
            List<Surface> surfaceList = new ArrayList<Surface>();
            surfaceList.add(holder.getSurface());
            surfaceList.add(mReader.getSurface());
            mCaptureRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(holder.getSurface());
            mCaptureRequestBuilder.addTarget(mReader.getSurface());
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
        if (!holder.getSurface().isValid()) {
            mCamera.close();
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }
    }

    public void setFlashLight(boolean isOn) {
        if (mCamera == null) {
            return;
        }
    }

    public void stopCapture() {
        if(mCamera != null) {
            mCamera.close();
        }
    }

    public void updatePreview() {
        if(null == mCamera || null == mCameraCaptureSession) {
            return;
        }
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);
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

    public interface CameraFrameCallback {
        void onFrameCaptured(Image image);
    }
}
