package com.feibo.joke.video;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.feibo.joke.R;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.video.util.TestData;
import com.feibo.joke.video.widget.MarkBar;
import com.feibo.joke.video.widget.MarkBar.OnMarkBarActionListener;
import com.feibo.joke.video.widget.MarkGridAdapter;
import com.feibo.joke.video.widget.MarkView;
import com.feibo.joke.video.widget.SingleTouchView;
import com.feibo.joke.video.widget.VideoEditFrameLayout;
import com.feibo.joke.video.widget.VideoEditFrameLayout.OnEditListener;
import com.feibo.joke.video.widget.VideoEditFrameLayout.OnFrameAdapter;

public class VideoMarkActivity extends Activity {

	private VideoEditFrameLayout editFrameLayout;

	private MarkBar chartletBar;
	private MarkBar textBar;

	private ViewGroup toolbarGroup;

	private View mappingBtn;
	private View textBtn;
	private View musicBtn;
	
	private VideoManager mVideoManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 读取测试数据
		mVideoManager = VideoManager.getInstance(this);
		String framePath = Environment.getExternalStorageDirectory().toString()
				+ "/test/video_test_img";
		String videoPath = Environment.getExternalStorageDirectory().toString()
				+ "/test/video_test.mp4";
		mVideoManager.init(new File(framePath), new File(videoPath));
		
		setContentView(R.layout.activity_video_mark);
		initTitle();
		initVideoEdit();

		// 工具栏
		toolbarGroup = (ViewGroup) findViewById(R.id.bar_edit);
		initChartletToolbar(toolbarGroup);
		initTextToolbar(toolbarGroup);

		mappingBtn = findViewById(R.id.btn_mapping);
		textBtn = findViewById(R.id.btn_text);
		musicBtn = findViewById(R.id.btn_music);
		// 其他
		initToolbar();
	}

	private void initTitle() {
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(VideoMarkActivity.this, VideoCoverActivity.class));
			}
		});
	}
	
	private void initVideoEdit(){
		editFrameLayout = (VideoEditFrameLayout) findViewById(R.id.edit_video);

		// 用来读取帧的适配器
		editFrameLayout.setFrameAdapter(new OnFrameAdapter() {

			@Override
			public Bitmap getFrame(float progress) {
				return mVideoManager.getFrameBitmap(progress);
			}
		});

		// 监听编辑
		editFrameLayout.setEditListener(new OnEditListener() {

			@Override
			public void onStop() {
				textBar.clearSelectMark();
				chartletBar.clearSelectMark();
			}

			@Override
			public void onSelect(View view) {
				
			}

			@Override
			public void onPlay() {
				textBar.clearSelectMark();
				chartletBar.clearSelectMark();
			}
			
			@Override
			public void onSave(View view) {
				textBar.clearSelectMark();
				chartletBar.clearSelectMark();
			}
		});
		
		editFrameLayout.setVideoDuration(mVideoManager.getVideoDuration());
		editFrameLayout.setVideoRatio(mVideoManager.getVideoRatio());
	}

	private void initToolbar() {
		mappingBtn.setOnClickListener(new OnToolBarClickListener());
		textBtn.setOnClickListener(new OnToolBarClickListener());
		musicBtn.setOnClickListener(new OnToolBarClickListener());
	}

	private void initChartletToolbar(ViewGroup viewGroup) {
		chartletBar = (MarkBar) LayoutInflater.from(this).inflate(
				R.layout.bar_mark, null);
		viewGroup.addView(chartletBar);

		MarkGridAdapter chartletGridAdapter = new MarkGridAdapter(this);
		chartletGridAdapter.setMarks(TestData.chartlets);

		chartletBar.setGridViewAdapter(chartletGridAdapter);
		chartletBar.setVisibility(View.INVISIBLE);
		chartletBar.setOnActionListener(new ViewMarkActionListener());
	}

	private void initTextToolbar(ViewGroup viewGroup) {
		textBar = (MarkBar) LayoutInflater.from(this).inflate(
				R.layout.bar_mark, null);
		viewGroup.addView(textBar);

		int[] texts = new int[21];
		for (int i = 0; i < texts.length; i++) {
			texts[i] = R.drawable.ic_launcher;
		}
		MarkGridAdapter textGridAdapter = new MarkGridAdapter(this);
		textGridAdapter.setMarks(texts);
		textBar.setGridViewAdapter(textGridAdapter);
		textBar.setVisibility(View.INVISIBLE);
		textBar.setOnActionListener(new ViewMarkActionListener());
	}

	public class OnToolBarClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_mapping:
				chartletBar.show();
				break;
			case R.id.btn_text:
				textBar.show();
				break;
			case R.id.btn_music:
				startActivity(new Intent(VideoMarkActivity.this,
						VideoMusicActivity.class));
				break;
			default:
				break;
			}
		}
	}

	public class ViewMarkActionListener implements OnMarkBarActionListener {

		@Override
		public void onShow() {

		}

		@Override
		public void onHide() {

		}

		@Override
		public void onSelect(int resId) {
			editFrameLayout.startEditVideo();
			
			// 如果有选中的贴图，则更换贴图
			SingleTouchView editView = (SingleTouchView) editFrameLayout.getSelectedMarkView();
			if (editView != null) {
				editView.setImageResource(resId);
				return;
			}
			
			// 贴图区没有选中的贴图，直接贴图
			editFrameLayout.addMarkView(new MarkView(VideoMarkActivity.this, resId));
		}
	}
}
