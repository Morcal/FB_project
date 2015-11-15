package com.feibo.joke.video.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class ThumbnailUtil {
	private static int THUMBNAIL_WIDTH = 100;
	private static int THUMBNAIL_HEIGHT = 100;
	private static ThumbnailUtil util;

	public static ThumbnailUtil getInstance() {
		if (util == null) {
			util = new ThumbnailUtil();
		}
		return util;
	}

	private Map<String, SoftReference<Bitmap>> mThumbnailCacheMap;
	private Map<String, Boolean> mThumbnailSuccess;
	private Executor mExecutor;
	private Handler mMainThreadHandler;

	private ThumbnailUtil() {
		mThumbnailCacheMap = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
		mThumbnailSuccess = new HashMap<String, Boolean>();
		mExecutor = Executors.newFixedThreadPool(10);
		mMainThreadHandler = new Handler(Looper.getMainLooper());
	}

	/**
	 * 设置缩略图
	 * 
	 * @param videoPath
	 * @param imageView
	 * @param defautResourse
	 */
	public void setImage(String videoPath, ImageView imageView,
			int defautResourse) {
		// 从缓存取
		SoftReference<Bitmap> bitmapCache = mThumbnailCacheMap.get(videoPath);
		if (bitmapCache != null && bitmapCache.get() != null) {
			imageView.setImageBitmap(bitmapCache.get());
			return;
		}

		imageView.setImageResource(defautResourse);
		imageView.setTag(videoPath);
		mExecutor.execute(new ThumbnailRunnable(videoPath, imageView,
				new OnLoadThumbnailListener() {
					@Override
					public void onSuccess(String path, ImageView imageView,
							Bitmap bitmap) {
						String videoPath = (String) imageView.getTag();
						if (videoPath.equals(path) && bitmap != null) {
							imageView.setImageBitmap(bitmap);
							AlphaAnimation alphaAnimation = new AlphaAnimation(
									0.0f, 1.0f);
							alphaAnimation.setDuration(1000);
							alphaAnimation
									.setInterpolator(new DecelerateInterpolator());
							imageView.startAnimation(alphaAnimation);
							mThumbnailSuccess.put(videoPath, true);
						} else {
							mThumbnailSuccess.put(videoPath, false);
						}
					}
				}));
	}

	/**
	 * 判断缩略图是否读取成功
	 * 
	 * @param path
	 * @return
	 */
	public boolean isVideoThumbSuccess(String path) {
		Boolean success = mThumbnailSuccess.get(path);
		if (success != null && success.booleanValue() == true) {
			return true;
		} else {
			return false;
		}
	}

	public class ThumbnailRunnable implements Runnable {

		private String mPath;
		private ImageView mImageView;
		private OnLoadThumbnailListener mListener;
		private Bitmap mBitmap;

		private ThumbnailRunnable(String path, ImageView imageView,
				OnLoadThumbnailListener listener) {
			mPath = path;
			mImageView = imageView;
			mListener = listener;
			mBitmap = null;
		}

		@Override
		public void run() {
			// 获取视频的缩略图
			mBitmap = ThumbnailUtils.createVideoThumbnail(mPath,
					MediaStore.Images.Thumbnails.MICRO_KIND);
			if (mBitmap != null) {
				System.out.println("w" + mBitmap.getWidth());
				System.out.println("h" + mBitmap.getHeight());
				mBitmap = ThumbnailUtils.extractThumbnail(mBitmap,
						THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

				mThumbnailCacheMap.put(mPath,
						new SoftReference<Bitmap>(mBitmap));
			}

			mMainThreadHandler.post(new Runnable() {

				@Override
				public void run() {
					mListener.onSuccess(mPath, mImageView, mBitmap);
				}
			});
		}
	}

	public static interface OnLoadThumbnailListener {
		void onSuccess(String path, ImageView imageView, Bitmap bitmap);
	}
}
