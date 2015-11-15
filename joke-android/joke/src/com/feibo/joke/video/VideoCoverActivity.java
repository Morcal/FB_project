package com.feibo.joke.video;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.video.widget.CoverTrackView;
import com.feibo.joke.video.widget.CoverTrackView.CoverAdapter;
import com.feibo.joke.video.widget.CoverTrackView.OnCoverChangeListener;
import com.feibo.joke.view.BaseActivity;

public class VideoCoverActivity extends BaseActivity {
	private static final String TAG = "VideoCoverActivity";
	private VideoManager mVideoManager;

	private CoverTrackView mCoverTrackView;
	private ViewGroup mContentContainer;
	private ImageView mContentView;

	private float mCoverProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_cover);
		initTitle();
		mVideoManager = VideoManager.getInstance(this);

		mCoverProgress = 0.0f;

		mContentView = new ImageView(this);
		mContentView.setBackgroundColor(getResources().getColor(R.color.c5_dark_grey));
		mContentContainer = (ViewGroup) findViewById(R.id.container_content);
		mContentContainer.addView(mContentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mCoverTrackView = (CoverTrackView) findViewById(R.id.track_cover);
		mCoverTrackView.setCoverChangerListener(new OnCoverChangeListener() {
			@Override
			public void onChange(float progress) {
			    LogUtil.i(TAG, "getCover:"+progress);
				mContentView.setImageBitmap(mVideoManager.getFrameBitmap(progress));
				mCoverProgress = progress;
			}
		});
		mCoverTrackView.setCoverAdapter(new CoverAdapter() {
			@Override
			public Bitmap getCover(float progress) {
				LogUtil.i(TAG, "getCover:"+progress);
				return mVideoManager.getFrameBitmap(progress);
			}
		});

		mContentView.setImageBitmap(mVideoManager.getFrameBitmap(0));
	}

	private void initTitle() {
		((TextView) findViewById(R.id.text_title)).setText("选取封面");
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mVideoManager.setCover(mCoverProgress);
				setChangeTypeAndFinish(DataChangeEventCode.CHANGE_TYPE_VIDEO_COVER_SUCCESS);
			}
		});
	}
}
