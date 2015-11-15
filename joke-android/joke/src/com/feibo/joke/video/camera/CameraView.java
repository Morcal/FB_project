package com.feibo.joke.video.camera;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
@SuppressWarnings("deprecation")
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = CameraView.class.getSimpleName();
    private SurfaceHolder mHolder;
    private CameraEngine mCameraEngine;

    private int frameCount;
    private long lastTime = 0;

    private OnPreviewCallback mOnPreviewCallback;

    public static interface OnPreviewCallback {
        void onPreviewFrame(byte[] data);
    }

    public void setOnPreviewCallback(OnPreviewCallback callback) {
        this.mOnPreviewCallback = callback;
    }

    public CameraView(Context context, CameraEngine camera) {
        super(context);
        mCameraEngine = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
//        mHolder.setFormat(PixelFormat.TRANSPARENT);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        /*try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }*/
//        startPreview();
        mCameraEngine.openCamAsync();
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        Log.d(TAG, "surfaceDestroyed");
        mCameraEngine.stopPreview(true);
    }

    public void startPreview() {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        mCameraEngine.startPreview(mHolder, new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (mOnPreviewCallback != null) {
                    mOnPreviewCallback.onPreviewFrame(data);
                }
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.d(TAG, "surfaceChanged, w:" + w + ", h:" + h);
        mCameraEngine.stopPreview(false);
        startPreview();
        // stop preview before making changes
        /*try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewCallback(new PreviewCallback() {

                @Override
                public void onPreviewFrame(byte[] arg0, Camera arg1) {
//                    Log.d(TAG, "onPreviewFrame, size:" + arg0.length);

                    if (arg0 == null || arg0.length == 0) {
                        return;
                    }
                    long now = System.currentTimeMillis();
                    if (now - lastTime < 40) {
                        return;
                    }
                    lastTime = now;


                    Camera.Parameters params = mCamera.getParameters();
                    if (params.getPreviewFormat() == ImageFormat.NV21) {
                        new ImageTask(params.getPreviewSize().width, params.getPreviewSize().height, frameCount++).execute(arg0);
                    } else {
                        Log.v(TAG,"NOT THE RIGHT FORMAT");
                    }
                }
            });

            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }*/
    }
    public static class ImageTask extends AsyncTask<byte[], Void, Void> {
        private final String TAG = ImageTask.class.getSimpleName();

        private int width;
        private int height;
        private int count;

        public ImageTask(int width, int height, int count) {
            this.width = width;
            this.height = height;
            this.count = count;
        }
        @Override
        protected Void doInBackground(byte[]... params) {
            Log.v(TAG, "Started Writing Frame");
            long now = System.currentTimeMillis();
            byte[] arg0 = params[0];

            int w = width;
            int h = height;

            String path = Environment.getExternalStorageDirectory().getPath() + "/123/frame_" + count + ".jpg";
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(path);

                byte[] temp = rotateYUV420Degree90(arg0, w, h);
                final YuvImage image = new YuvImage(temp, ImageFormat.NV21, h, w, null);
                image.compressToJpeg(new Rect(0, 0, h, h), 100, fos);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d(TAG, "elapse:" + (System.currentTimeMillis() - now));
            Log.v(TAG,"Finished Writing Frame");
            return null;

            /*String path = Environment.getExternalStorageDirectory().getPath() + "/123/frame_" + count + ".jpg";
            final YuvImage image = new YuvImage(arg0, ImageFormat.NV21, w, h, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(arg0.length);
            if(!image.compressToJpeg(new Rect(0, 0, h, h), 100, os)){
                return null;
            }
            byte[] tmp = os.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
            Bitmap bmp1 = rotateBitmapByDegree(bmp, 90);


            saveScreenshot(bmp1, path);
            Log.d(TAG, "elapse:" + (System.currentTimeMillis() - now));
            Log.v(TAG,"Finished Writing Frame");
            return null;*/
        }

        public byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {

            byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
            // Rotate the Y luma
            int i = 0;
            for (int x = 0; x < imageWidth; x++) {
                for (int y = imageHeight - 1; y >= 0; y--) {
                    yuv[i] = data[y * imageWidth + x];
                    i++;
                }

            }
            // Rotate the U and V color components
            i = imageWidth * imageHeight * 3 / 2 - 1;
            for (int x = imageWidth - 1; x > 0; x = x - 2) {
                for (int y = 0; y < imageHeight / 2; y++) {
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                    i--;
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                    i--;
                }
            }
            return yuv;
        }

    }
}

