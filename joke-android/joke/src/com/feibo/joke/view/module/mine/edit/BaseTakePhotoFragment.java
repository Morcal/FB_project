package com.feibo.joke.view.module.mine.edit;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.User;
import com.feibo.joke.upyun.PicBucket;
import com.feibo.joke.utils.BitmapUtil;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.dialog.PhotoChooseDialog;
import com.feibo.joke.view.dialog.PhotoChooseDialog.DialogItemClickListener;
import com.feibo.joke.view.util.ToastUtil;

import fbcore.cache.image.ImageLoader;
import fbcore.log.LogUtil;
import fbcore.utils.Files;

public abstract class BaseTakePhotoFragment extends BaseTitleFragment {

	private final static int REQUEST_CODE_TAKE_PHOTO = 0x10; // 拍照
	private static final int REQUEST_CODE_PICK_PHOTO = 0x11; // 从相册中选择
	private static final int REQUEST_CODE_CLIP_PHOTO = 0x13;

	protected Uri imageUri;

	private PicBucket bucket;

	protected AlertDialog dialog;
	private String imagePath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case REQUEST_CODE_PICK_PHOTO:
			if (data == null || data.getData() == null) {
				return;
			}

			Intent intent = new Intent(this.getActivity(),
					EditPhotoActivity.class);
			intent.setData(data.getData());
			// intent.putExtra("isFromAddArtActivity", isFromAddArtActivity);
			startActivityForResult(intent, REQUEST_CODE_CLIP_PHOTO);
			break;
		case REQUEST_CODE_TAKE_PHOTO:
			intent = new Intent(this.getActivity(), EditPhotoActivity.class);
			intent.setData(imageUri);
			// intent.putExtra("isFromAddArtActivity", isFromAddArtActivity);
			startActivityForResult(intent, REQUEST_CODE_CLIP_PHOTO);
			break;
		case REQUEST_CODE_CLIP_PHOTO:
			if (data == null || data.getData() == null) {
				return;
			}
			fillImageView(data.getData().toString());
			break;
		}
	}

	protected void fillImageView(final String path) {
		this.imagePath = path;
		try {
			if (Files.isGif(path)) {
				ToastUtil.showSimpleToast("暂不支持此格式");
				return;
			}
		} catch (Exception e) {
		}

		final int degree = BitmapUtil.getPicRotate(path);
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
								if (degree > 0) {
									bm = BitmapUtil.reviewPicRotate(bm, path);
								}
								fillImageView(bm, path);
							}

							@Override
							public void onFail() {
							}
						}, 800, 800,
						ImageLoader.FLAG_NET_EXCEPT);
	}

	/**
	 * 回调选择图库或者相机中的图片的回调
	 * 
	 * @param bitmap
	 */
	public abstract void fillImageView(Bitmap bitmap, String path);

	public void showChoosePhotoDialog() {
		PhotoChooseDialog.show(this.getActivity())
				.setOnDialogItemClickListener(new DialogItemClickListener() {

					@Override
					public void onClick(int type) {
						switch (type) {
						case PhotoChooseDialog.TAKE_PHOTO: {
							takePhoto();
							break;
						}
						case PhotoChooseDialog.PHOTO_ALBUM: {
							loadImage();
							break;
						}
						}
					}
				});
	}

	/**
	 * 拍摄照片
	 */
	private void takePhoto() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			ToastUtil.showSimpleToast("SD 卡不可用!!");
			return;
		}
		DirContext.getInstance().getDir(DirContext.DirEnum.IMAGE);
		File file = new File(DirContext.getInstance().getDir(
				DirContext.DirEnum.PUBLISH), System.currentTimeMillis()
				+ ".jpg");
		imageUri = Uri.fromFile(file);
		// imagePath = imageUri.toString().replace("file://", "");

		// /小米BUG 必须指定拍照存放路径
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, imageUri),
				REQUEST_CODE_TAKE_PHOTO);
	}

	/**
	 * 从图库中选择图片
	 */
	private void loadImage() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
	}

	public void uploadUserInfo(final User user) {
		uploadUserInfo(user, false);
	}

	public boolean hasImagePath() {
		return !StringUtil.isEmpty(imagePath);
	}

	/**
	 * 上传用户信息，包含头像
	 */
	public void uploadUserInfo(final User user, boolean needHeadPhoto) {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(this.getActivity()).create();
			/*
			 * WindowManager.LayoutParams params =
			 * dialog.getWindow().getAttributes(); params.width = 200;
			 * params.gravity = Gravity.CENTER;
			 * dialog.getWindow().setAttributes(params);
			 */
			dialog.setMessage("保存中...");
			dialog.setCancelable(false);
		}
		dialog.show();
		if (hasImagePath()) {
			if (bucket == null) {
				bucket = new PicBucket();
				bucket.setOnUploadListener(new PicBucket.UploadListener() {
					@Override
					public void onSuccess(final String picUrl,
							String localPath, int width, int height) {
						postOnUiHandle(new Runnable() {

							@Override
							public void run() {
								user.avatar = picUrl;
								uploadUser(user);
							}
						});
					}

					@Override
					public void onFail(String picUrl, String localPath) {
						postOnUiHandle(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								ToastUtil.showSimpleToast("上传用户信息失败");
							}
						});
					}
				});
			}
			bucket.uploadSinglePic(imagePath);
		} else {
			if (needHeadPhoto) {
				ToastUtil.showSimpleToast("请添加头像");
			} else {
				uploadUser(user);
			}
		}
	}

	private void uploadUser(User user) {
		LogUtil.d("", "update -> avatar="+user.avatar);
        UserManager.getInstance().modifyUser(user.avatar, user.nickname, 
        		user.detail.gender, user.detail.province, user.detail.city, 
        		user.detail.signature, user.detail.birth, new LoadListener() {
					
					@Override
					public void onSuccess() {
						if(dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}
						ToastUtil.showSimpleToast("修改成功");
						setChangeTypeAndFinish(DataChangeEventCode.CHANGE_TYPE_MODIFY_USER);
					}
					
					@Override
					public void onFail(int code) {
						ToastUtil.showSimpleToast("修改资料失败");
						if(dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});
    }

	public void uploadUserCallback(Response<User> response) {
		if (response.rsCode == ReturnCode.RS_SUCCESS) {
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
		} else {
			ToastUtil.showSimpleToast(this.getActivity().getResources().getString(R.string.not_network));
		}
	}

}
