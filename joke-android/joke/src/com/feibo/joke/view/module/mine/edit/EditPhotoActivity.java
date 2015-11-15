package com.feibo.joke.view.module.mine.edit;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.Constant;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.utils.BitmapUtil;
import com.feibo.joke.utils.FunctionUtils;
import com.feibo.joke.utils.ScreenShot;
import com.feibo.joke.utils.ThreadManager;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.MatrixImageView;

import fbcore.cache.image.ImageLoader;
import fbcore.utils.Files;

public class EditPhotoActivity extends BaseActivity implements
		View.OnClickListener {
	private MatrixImageView mivCropPhoto;
	private TextView tvRotate;

	boolean isFromAddArtActivity;
	
	private long lastClickTime;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(containChildView());
	}
	
	public View containChildView() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.fragment_crop_photo, null);
		initWidget(view);
		initContentView();
		initListener();
		setTitlebar(view);
		return view;
	}

	public void initWidget(View view) {
		mivCropPhoto = (MatrixImageView) view.findViewById(R.id.miv_crop_photo);
		tvRotate = (TextView) view.findViewById(R.id.tv_rotate_left);
	}

	public void initContentView() {
		Intent intent = this.getIntent();
		isFromAddArtActivity = intent.getBooleanExtra("isFromAddArtActivity",
				false);
		Uri data = intent.getData();
		if (data == null) {
			return;
		}
		String s = data.toString();
		String path = "";
		if (!s.startsWith("file://")) {// content://
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(data, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			path = cursor.getString(columnIndex);
			cursor.close();
		} else { // file://
			path = data.toString().replace("file://", "");
		}
		fillImageView(path);
	}

	public void initListener() {
		tvRotate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		long millis = System.currentTimeMillis();
		if (millis - lastClickTime < Constant.CLICK_INTERVAL)
			return;
		lastClickTime = millis;
		switch (v.getId()) {
		case R.id.tv_rotate_left:
			mivCropPhoto.rotateImage(90);
			break;
		case R.id.head_right:
			crop();
			break;
		}
	}

	private void fillImageView(final String path) {
		try {
			if (Files.isGif(path)) {
				ToastUtil.showSimpleToast("暂不支持此格式");
				return;
			}
		} catch (Exception e) {
		}
		DataProvider
				.getInstance()
				.getImageLoader()
				.loadImage(
						Uri.fromFile(new File(path)),
						new ImageLoader.OnLoadListener() {
							@Override
							public void onSuccess(Drawable drawable,
									boolean immediate) {
								if (drawable == null
										|| !(drawable instanceof BitmapDrawable)) {
									return;
								}
								Bitmap bm = ((BitmapDrawable) drawable)
										.getBitmap();
								bm = BitmapUtil.reviewPicRotate(bm, path);

								mivCropPhoto.setImageBitmap(bm);
								if (!mivCropPhoto.isInitSize()) {
									mivCropPhoto.initSize();
								}
							}

							@Override
							public void onFail() {

							}
						}, 480, 800,
						ImageLoader.FLAG_NET_EXCEPT);
	}

	private Bitmap bitmap;

	/**
	 * 裁剪图片
	 */
	private void crop() {
		mivCropPhoto.setDrawingCacheEnabled(true);
		bitmap = mivCropPhoto.clip();
		saveToFile(bitmap);
	}

	/**  保存截图 */
	public void saveToFile(final Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		ThreadManager.getShortPool().execute(new Runnable() {

			@Override
			public void run() {
				DirContext.DirEnum dirEnum = DirContext.DirEnum.PUBLISH;
				final String filePath = DirContext.getInstance().getDir(dirEnum)
						.getAbsolutePath()
						+ File.separator + new Date().getTime() + ".png";
				// appDir.getAbsolutePath() + File.separator + new Date().getTime() + ".png";
				ScreenShot.saveScreenshot(bitmap, filePath);
				
				EditPhotoActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						FunctionUtils.notifyMediaScanner(
								EditPhotoActivity.this, filePath);
						Log.i("path", filePath);
						dialog.dismiss();
						cropComplete(filePath);
					}
				});
			}
		});
	}
	
	/** 裁剪完成 */
	private void cropComplete(String path) {
		Intent intent = new Intent();
		if (path != null) {
			Uri uri = Uri.parse(path);
			intent.setData(uri);
			setResult(Activity.RESULT_OK, intent);
		} else {
			setResult(Activity.RESULT_CANCELED, intent);
		}
		finish();
	}

	public void setTitlebar(View view) {
		((TextView) view.findViewById(R.id.tv_head_right)).setText("确认");
		view.findViewById(R.id.head_right).setOnClickListener(this);
		view.findViewById(R.id.head_left).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditPhotoActivity.this.finish();
			}
		});
	}
}
