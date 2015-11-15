package com.feibo.joke.video.camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;

import fbcore.log.LogUtil;

@SuppressWarnings("deprecation")
public class CamParaUtil {
	private static final String TAG = "CamParaUtil";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CamParaUtil myCamPara = null;
	private CamParaUtil(){

	}
	public static CamParaUtil getInstance(){
		if(myCamPara == null){
			myCamPara = new CamParaUtil();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}

	public  Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
		Collections.sort(list, sizeComparator);

		int i = 0;
		for(Size s:list){
			if((s.width >= minWidth) && equalRate(s, th)){
				Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if(i == list.size()){
			i = 0;//如果没找到，就选最小的size
		}
		return list.get(i);
	}
	public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
		Collections.sort(list, sizeComparator);

		int i = 0;
		for(Size s:list){
			if((s.width >= minWidth) && equalRate(s, th)){
				Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if(i == list.size()){
			i = 0;//如果没找到，就选最小的size
		}
		return list.get(i);
	}

	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.03)
		{
			return true;
		}
		else{
			return false;
		}
	}

	public class CameraSizeComparator implements Comparator<Camera.Size>{
		@Override
        public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
				return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}

	}

	/**打印支持的previewSizes
	 * @param params
	 */
	public  void printSupportPreviewSize(Camera.Parameters params){
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		for(int i=0; i< previewSizes.size(); i++){
			Size size = previewSizes.get(i);
			Log.i(TAG, "previewSizes:width = "+size.width+" height = "+size.height);
		}

	}

	/**打印支持的pictureSizes
	 * @param params
	 */
	public  void printSupportPictureSize(Camera.Parameters params){
		List<Size> pictureSizes = params.getSupportedPictureSizes();
		for(int i=0; i< pictureSizes.size(); i++){
			Size size = pictureSizes.get(i);
			Log.i(TAG, "pictureSizes:width = "+ size.width
					+" height = " + size.height);
		}
	}
	/**打印支持的聚焦模式
	 * @param params
	 */
	public void printSupportFocusMode(Camera.Parameters params){
		List<String> focusModes = params.getSupportedFocusModes();
		for(String mode : focusModes){
			Log.i(TAG, "focusModes--" + mode);
		}
	}

	public Size getPropPreviewSize(Camera.Parameters parameters, int width, int height) {
        // 获取摄像头的所有支持的分辨率
        List<Camera.Size> resolutionList = parameters.getSupportedPreviewSizes();

        if (resolutionList == null || resolutionList.isEmpty()) {
            return null;
        }

        Collections.sort(resolutionList, new ResolutionComparator());
        Camera.Size previewSize = null;
        boolean hasSize = false;
        // 如果摄像头支持640*480，那么强制设为640*480
        for (int i = 0; i < resolutionList.size(); i++) {
            Size size = resolutionList.get(i);
            if (size != null && size.width == width && size.height == height) {
                previewSize = size;
                hasSize = true;
                break;
            }
        }
        // 如果不支持设为中间的那个
        if (!hasSize) {
            int mediumResolution = resolutionList.size() / 2;
            if (mediumResolution >= resolutionList.size()) {
                mediumResolution = resolutionList.size() - 1;
            }
            previewSize = resolutionList.get(mediumResolution);
        }
        return previewSize;
    }

	public static class ResolutionComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            if(size1.height != size2.height) {
                return size1.height -size2.height;
            } else {
                return size1.width - size2.width;
            }
        }
    }

	// 设置对焦模式
    public void updateFocusMode(Camera.Parameters parameters) {
        // 系统版本8以下不支持该方式对焦
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            return;
        }
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes != null) {
            Log.i("video", Build.MODEL);
            if (((Build.MODEL.startsWith("GT-I950")) || (Build.MODEL.endsWith("SCH-I959")) || (Build.MODEL.endsWith("MEIZU MX3")))
                    && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)){
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
    }

    public void printAll(Camera.Parameters param) {
        LogUtil.d(TAG, "params:" + param.flatten());
    }

    public void printPreviewFpsRange(Camera.Parameters parameters) {
        List<int[]> ranges = parameters.getSupportedPreviewFpsRange();
        if (ranges != null && ranges.size() > 0) {
            for (int[] range : ranges) {
                for (int e : range) {
                    LogUtil.d(TAG, "printPreviewFpsRange, e:" + e);
                }
            }
        }
    }

    public int setFrameRate(Camera camera, Camera.Parameters parameters, int frameRate) {
        Camera.Parameters p = camera.getParameters();
        int supportFrameRate = frameRate;
        try {
            p.setPreviewFpsRange(frameRate * 1000, frameRate * 1000);
            camera.setParameters(p);
            parameters.setPreviewFpsRange(frameRate * 1000, frameRate * 1000);
        } catch (Exception e) {
         //   parameters.setPreviewFrameRate(frameRate);
            supportFrameRate = parameters.getPreviewFrameRate();
        }
        return supportFrameRate;
    }

    public boolean hasPermission(Context context, String permission) {
        context.getPackageManager();
        int p = context.checkCallingOrSelfPermission(permission);
        return PackageManager.PERMISSION_GRANTED == p;
//        return PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, context.getPackageName());
    }
}
