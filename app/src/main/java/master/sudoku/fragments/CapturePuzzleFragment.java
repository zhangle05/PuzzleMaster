package master.sudoku.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import master.sudoku.R;
import master.sudoku.camera.CameraCallback;
import master.sudoku.widgets.ScannerBox;

/**
 * Created by zhangle on 03/01/2017.
 */
public class CapturePuzzleFragment extends Fragment {

    public static final int REQUEST_CAMERA_OPEN = 1;
    private SurfaceView mSurfaceView;
    private ScannerBox mScannerBox;
    private SurfaceHolder mSurfaceHolder;
    private CameraCallback mCameraCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture_puzzle, container,
                false);
        mSurfaceView = (SurfaceView) rootView.findViewById(R.id.sv_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);

        mScannerBox = (ScannerBox) rootView.findViewById(R.id.scanner_box);
        mScannerBox.startAnimation();

        mCameraCallback = new CameraCallback(this.getContext());
        openCamera();

        return rootView;
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

    private void openCamera() {
        if (!checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_OPEN)) {
            return;
        }
        CameraManager mgr = (CameraManager) this.getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] idList = mgr.getCameraIdList();
            mgr.openCamera(idList[0], mCameraCallback, null);
            mSurfaceHolder.addCallback(mCameraCallback);
        } catch (Exception ex) {
            ex.printStackTrace();
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


}
