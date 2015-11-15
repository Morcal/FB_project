package com.feibo.joke.video.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import fbcore.log.LogUtil;

@SuppressWarnings("deprecation")
public class CameraEngine {
    private static final String TAG = CameraEngine.class.getSimpleName();

    private Camera mCamera;
    private Camera.Parameters mParams;
    private int videoFrameRate = 15;

    /**
     * 前置或者后置摄像头.
     */
    private int whichCamera = -1;

    private boolean isPreviewing = false;

    private CamOpenedCallback mCamOpenedCallback;

    public static interface CamOpenedCallback {
        void onCamOpened(int which);

        void onFail();
    }

    public void setCamOpenedCallback(CamOpenedCallback callback) {
        this.mCamOpenedCallback = callback;
    }

    public void openCamAsync() {
        new Thread() {
            @Override
            public void run() {
                stopPreview(true);
                try {
                    if (whichCamera == -1) {
                        try {
                            mCamera = Camera.open();

                        } catch (RuntimeException e) {
                            mCamera = Camera.open(Camera.getNumberOfCameras()-1);
                            whichCamera = Camera.getNumberOfCameras()-1;
                        }
                    } else {
                        mCamera = Camera.open(whichCamera);
                    }
                    mParams = mCamera.getParameters();
                    videoFrameRate = CamParaUtil.getInstance().setFrameRate(mCamera, mParams, videoFrameRate);
                    if (mCamOpenedCallback != null) {
                        mCamOpenedCallback.onCamOpened(whichCamera);
                    }
                } catch (Exception e) {
                    LogUtil.printStackTrace(e);
                    if (mCamOpenedCallback != null) {
                        mCamOpenedCallback.onFail();
                    }
                }
            }
        }.start();
    }

    public void startPreview(SurfaceHolder holder, PreviewCallback previewCallback) {
        if (mCamera == null) {
            return;
        }
        if (isPreviewing) {
            mCamera.stopPreview();
        }
        try {

            CamParaUtil.getInstance().printAll(mParams);

            CamParaUtil.getInstance().printPreviewFpsRange(mParams);
            if (!isFlashModeOff()) {
                updateFlashMode();
            }

            CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            CamParaUtil.getInstance().printSupportPictureSize(mParams);

            mCamera.setPreviewDisplay(holder);

            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(mParams, 640, 480);
            mParams.setPreviewSize(previewSize.width, previewSize.height);
            mParams.setPictureSize(previewSize.width, previewSize.height);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            CamParaUtil.getInstance().updateFocusMode(mParams);

            CamParaUtil.getInstance().printAll(mParams);

            mCamera.setParameters(mParams);
            mCamera.setDisplayOrientation(90);

            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            isPreviewing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Final:PreviewSize--With = " + mParams.getPreviewSize().width
                + "Height = " + mParams.getPreviewSize().height);
        Log.i(TAG, "Final:PictureSize--With = " + mParams.getPictureSize().width
                + "Height = " + mParams.getPictureSize().height);
    }

    public void stopPreview(boolean needRelease) {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            if (needRelease) {
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
    }

    public Size getPropPreviewSize() {
        if (mCamera == null) {
            return null;
        }

        return CamParaUtil.getInstance().getPropPreviewSize(mCamera.getParameters(), 640, 480);
    }

    public void changeCamera(boolean backOrFront) {
        int camera = backOrFront ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;

        if (whichCamera == camera) {
            return;
        }

        whichCamera = camera;

        openCamAsync();
    }

    public boolean isFrontCamera() {
        return whichCamera == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public void updateFlashMode() {
        if (isFrontCamera()) {
            return;
        }
        String flashMode = mParams.getFlashMode();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }

        mCamera.setParameters(mParams);
    }

    public boolean isFlashModeOff() {
        return Camera.Parameters.FLASH_MODE_OFF.equals(mParams.getFlashMode());
    }

    // TODO 拍照
    public void takePicture() {

    }

    public int getCameraFrameRate() {
        return videoFrameRate;
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }

    public static class Params {
        int previewWidth;
        int previewHeight;
    }

    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    private static int getSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
