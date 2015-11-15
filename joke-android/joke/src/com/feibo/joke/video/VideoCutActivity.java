package com.feibo.joke.video;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import fbcore.log.LogUtil;
import fbcore.utils.Files;

import com.feibo.ffmpeg.FFmpeg;
import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.video.manager.VideoCutManager;
import com.feibo.joke.video.manager.VideoCutManager.CutVideoListener;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.video.widget.VideoCutFrameLayout;
import com.feibo.joke.video.widget.VideoEditFrameLayout.OnFrameAdapter;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;

/**
 * 裁剪视频
 *
 * @author Lidiqing
 *
 */
public class VideoCutActivity extends BaseActivity {
	private static final String TAG = "VideoCutActivity";
	public static final String PARAM_VIDEO_PATH = "video_path";

	private VideoCutFrameLayout videoCutFrameLayout;
	private String mVideoPath;

	private VideoCutManager mVideoCutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_cut);
		initTitle();
		initVideo();

		mVideoCutManager = VideoCutManager.getInstance();
		videoCutFrameLayout = (VideoCutFrameLayout) findViewById(R.id.cut_video);
		videoCutFrameLayout.setFrameAdapter(new OnFrameAdapter() {

			@Override
			public Bitmap getFrame(float progress) {
				// 载入测试数据
				return mVideoCutManager.getFrameBitmap(progress);
			}
		});
		videoCutFrameLayout.setVideoPath(this, mVideoPath);

	}

	@Override
	public void onDataChange(int code) {
		switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
            setChangeTypeAndFinish(code);
            break;
		case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_CANCEL:
			// 取消封面选择，释放资源
			VideoManager.getInstance(VideoCutActivity.this).release();
			break;
		default:
			break;
		}
	}

	private void initTitle() {
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setChangeTypeAndFinish(DataChangeEventCode.CHANGE_TYPE_VIDEO_CUT_CANCEL);
			}
		});

		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cutVideo();
			}
		});
	}

	/**
	 * 初始化视频信息
	 */
	private void initVideo() {
		mVideoPath = getIntent().getExtras().getString(PARAM_VIDEO_PATH);
		if (mVideoPath == null) {
			finish();
		}
	}

	/**
	 * 裁剪视频
	 */
	private void cutVideo() {
	    DialogUtils.showProgressDialog(this, "视频导入中...", false);
		int start = videoCutFrameLayout.getVideoStart();
		int end = videoCutFrameLayout.getVideoEnd();

		LogUtil.i(TAG, "start:" + start + "; end:" + end);

		// 开始裁剪
		mVideoCutManager.cutVideo(start, end, mVideoPath, new CutVideoListener() {
			@Override
			public void success(File file) {
				LogUtil.i(TAG, "success:" + file.getAbsolutePath());

				File outputDir = DirContext.getInstance().getOutputDir();
				File thumbsDir = DirContext.getInstance().getThumbsDir(outputDir);
				FFmpeg.saveFrameImage(file.getAbsolutePath(), thumbsDir.getAbsolutePath(), System.currentTimeMillis() + ".");

				File finalOutput = new File(outputDir, VideoConstants.OUTPUT_VIDEO);
				Files.copy(file, finalOutput);

				CleanService.cleanFile(VideoCutActivity.this, file.getParent());

				final boolean initVideo = VideoManager.getInstance(VideoCutActivity.this).init(thumbsDir, finalOutput);

			    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.hideProgressDialog();
                        if (initVideo) {
                            LaunchUtil.launchSubActivity(VideoCutActivity.this, VideoShareFragment.class, null);
                        } else {
                            ToastUtil.showSimpleToast(VideoCutActivity.this, "裁剪视频失败");
                        }
                    }
                });
			}

			@Override
			public void error() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					    DialogUtils.hideProgressDialog();
                        ToastUtil.showSimpleToast(VideoCutActivity.this, "裁剪视频失败");
					}
				});
			}
		});
	}

}
