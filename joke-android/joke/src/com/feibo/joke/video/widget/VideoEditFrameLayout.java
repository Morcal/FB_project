package com.feibo.joke.video.widget;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.video.widget.FrameTrackView.OnPlayListener;
import com.feibo.joke.video.widget.PasteMarkView.MarkInfo;
import com.feibo.joke.video.widget.PasteMarkView.OnPasteListener;

public class VideoEditFrameLayout extends FrameLayout {
	private static int SUSPEND_BUTTON_SIZE = 55;

	private PlayButton mPlayView;
	private TimeButton mTimeView;
	private DecimalFormat mTimeFormat;

	private int mButtonSize;

	// 内容
	private ImageView mContentView;

	// 贴图板
	private PasteMarkView mPasteView;

	// 轨道
	private FrameTrackView mTrackView;

	// 监听编辑操作
	private OnEditListener mEditListener;

	// 用来读取帧
	private OnFrameAdapter mFrameAdapter;

	// 视频总时长
	private long mVideoDuration;

	// 视频的宽度和高度
	private int mVideoWidth;
	private int mVideoHeight;

	private int mWindowHeight;
	private int mWindowWidth;
	private int mTrackHeight;

	public VideoEditFrameLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public VideoEditFrameLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public VideoEditFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoEditFrameLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
		
		// 按钮的尺寸，占屏幕宽度70/640 = 0.0109375
		mButtonSize = (int) (mScreenWidth * 0.109375);
		mTimeFormat = new DecimalFormat("0.0");

		// 编辑窗口
		mWindowWidth = mScreenWidth;
		mWindowHeight = mWindowWidth;
		
		// 轨道高度，占屏幕高度1/10 = 0.1
		mTrackHeight = (int) (mWindowWidth * 0.1);

		// 视频的默认高度和默认宽度为1:1，宽度为屏幕宽度
		mVideoWidth = mWindowWidth;
		mVideoHeight = mVideoWidth;

		// 视频内容区
		mContentView = new ImageView(context);
		LayoutParams contentParams = new LayoutParams(mVideoWidth, mVideoHeight);
		addViewInLayout(mContentView, 0, contentParams);

		// 贴图区
		mPasteView = new PasteMarkView(context);
		mPasteView.setBackgroundColor(Color.TRANSPARENT);
		mPasteView.setMarkInitSize((int) (mVideoWidth * 0.3f),
				(int) (mVideoWidth * 0.3f));
		mPasteView.setMarkMinSize((int) (mVideoWidth * 0.16),
				(int) (mVideoWidth * 0.16));
		mPasteView.setMarkMaxSize(mVideoWidth, mVideoWidth);
		LayoutParams pasteParams = new LayoutParams(mVideoWidth, mVideoHeight);
		addViewInLayout(mPasteView, 1, pasteParams);

		// 帧轨道区
		mTrackView = new FrameTrackView(context);
		LayoutParams trackParams = new LayoutParams(mWindowWidth, mTrackHeight);
		trackParams.topMargin = mWindowHeight;
		addViewInLayout(mTrackView, 2, trackParams);

		// 播放按钮和时间按钮
		mTimeView = new TimeButton(context);
		LayoutParams timeParams = new LayoutParams(mButtonSize, mButtonSize);
		timeParams.leftMargin = (int) (mButtonSize * 0.4);
		timeParams.topMargin = (int) (mWindowHeight - mButtonSize - mTrackHeight * 0.3);
		addViewInLayout(mTimeView, 3, timeParams);

		mPlayView = new PlayButton(context);
		LayoutParams playParams = new LayoutParams(mButtonSize, mButtonSize);
		playParams.leftMargin = (int) (mButtonSize * 0.4);
		playParams.topMargin = (int) (mWindowHeight - mButtonSize - mButtonSize * 0.3);
		addViewInLayout(mPlayView, 4, playParams);

		initEvents();
		showPlayView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(MeasureSpec.makeMeasureSpec(mWindowWidth,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mWindowHeight
				+ mTrackHeight, MeasureSpec.EXACTLY));
	}

	private void initEvents() {
		mPlayView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startPlay();
			}
		});

		mTimeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopPlay();
			}
		});

		/**
		 * 监听轨道的播放和选择器与指针的移动
		 */
		mTrackView.setPlayListener(new OnPlayListener() {

			@Override
			public void onFinish() {
				// 轨道结束播放
				stopPlay();
			}

			@Override
			public void onSelectorStart(float progress, boolean show) {
				MarkInfo markInfo = mPasteView.getSelectedViewInfo();
				if (markInfo != null) {
					// 记录新的起始进度
					markInfo.setProgressStart(progress);
				}

				// 显示可显示的view
				if (show) {
					mPasteView.showMarks(progress);
					mContentView.setImageBitmap(mFrameAdapter
							.getFrame(progress));
				}
			}

			@Override
			public void onSelectorEnd(float progress, boolean show) {
				MarkInfo markInfo = mPasteView.getSelectedViewInfo();
				if (markInfo != null) {
					// 记录新的起始进度
					markInfo.setProgressEnd(progress);
				}

				if (show) {
					mPasteView.showMarks(progress);
					mContentView.setImageBitmap(mFrameAdapter
							.getFrame(progress));
				}
			}

			@Override
			public void onPointer(float progress) {
				mPasteView.showMarks(progress);
				mContentView.setImageBitmap(mFrameAdapter.getFrame(progress));

				// 设置时间按钮的时间显示
				float time = mVideoDuration * progress / 1000;
				mTimeView.setTime(mTimeFormat.format(time));
			}

			@Override
			public void onPointerStart(float progress) {
				mTrackView.hideSelectorView();
				mPasteView.saveSelectedView();

				mPasteView.showMarks(progress);
				mContentView.setImageBitmap(mFrameAdapter.getFrame(progress));

				// 设置时间按钮的时间显示
				float time = mVideoDuration * progress / 1000;
				mTimeView.setTime(mTimeFormat.format(time));
				if (mTimeView.getVisibility() != View.VISIBLE) {
					showTimeView();
				}

				if (mPasteView.getSelectedViewInfo() != null) {
					mEditListener.onSave(mPasteView.getSelectedViewInfo()
							.getMarkView());
				}
			}

			@Override
			public void onPointerEnd(float progress) {
				mPasteView.showMarks(progress);
				mContentView.setImageBitmap(mFrameAdapter.getFrame(progress));

				// 设置时间按钮的时间显示
				float time = mVideoDuration * progress / 1000;
				mTimeView.setTime(mTimeFormat.format(time));
				showPlayView();
			}
		});

	}

	/**
	 * 开始播放
	 */
	private void startPlay() {
		showTimeView();
		// 保存贴图
		mPasteView.saveSelectedView();
		mTrackView.play();
		mEditListener.onPlay();
	}

	/**
	 * 停止播放
	 */
	private void stopPlay() {
		showPlayView();
		mTrackView.stop();
		mEditListener.onStop();
	}

	/**
	 * 显示播放按钮
	 */
	private void showPlayView() {
		mPlayView.setVisibility(View.VISIBLE);
		mTimeView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示时间按钮
	 */
	private void showTimeView() {
		mPlayView.setVisibility(View.INVISIBLE);
		mTimeView.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置视频的宽高比，当视频为导入的视频时调用
	 * 
	 * @param ratio
	 */
	public void setVideoRatio(float ratio) {
		if (ratio == 1) {
			return;
		}

		LayoutParams params;
		if (ratio < 1) {
			// 宽比高短
			mVideoHeight = mWindowHeight;
			mVideoWidth = (int) (mWindowHeight * ratio);
			params = new LayoutParams(mVideoWidth, mVideoHeight);
			params.leftMargin = mWindowWidth / 2 - mVideoWidth / 2;
		} else {
			// 宽比高长
			mVideoWidth = mWindowWidth;
			mVideoHeight = (int) (mVideoWidth * (1 / ratio));
			params = new LayoutParams(mVideoWidth, mVideoHeight);
			params.topMargin = mWindowHeight / 2 - mVideoHeight / 2;
		}

		mContentView.setLayoutParams(params);
		mPasteView.setLayoutParams(params);
		mPasteView.setMarkInitSize((int) (mVideoWidth * 0.3f),
				(int) (mVideoWidth * 0.3f));
		mPasteView.setMarkMinSize((int) (mVideoWidth * 0.16),
				(int) (mVideoWidth * 0.16));

		if (mVideoWidth < mVideoHeight) {
			mPasteView.setMarkMaxSize(mVideoWidth, mVideoWidth);
		} else if (mVideoWidth > mVideoHeight) {
			mPasteView.setMarkMaxSize(mVideoHeight, mVideoHeight);
		}

		requestLayout();
	}

	/**
	 * 开始编辑
	 */
	public void startEditVideo() {
		// 准备编辑视频
		showPlayView();
		// 停止帧
		mTrackView.stop();
		// 显示选择器
		mTrackView.showSelectorView();
		// 内容图像恢复到选择器起始点
		float progress = mTrackView.getSelectorStartProgress();
		mPasteView.showMarks(progress);
		mContentView.setImageBitmap(mFrameAdapter.getFrame(progress));

		requestLayout();
		System.out.println("startEditVideo============:" + progress);
	}

	/**
	 * 取消编辑
	 */
	public void stopEditVideo() {
		// 保存操作
		if (getSelectedMarkView() != null) {
			mPasteView.saveSelectedView();
		}
		// 隐藏选择器
		mTrackView.hideSelectorView();
	}

	/**
	 * 设置视频时长
	 * 
	 * @param videoDuration
	 */
	public void setVideoDuration(long videoDuration) {
		mVideoDuration = videoDuration;
		mTrackView.setVideoDuration(mVideoDuration);
		mContentView.setImageBitmap(mFrameAdapter.getFrame(mTrackView
				.getSelectorStartProgress()));
	}

	public void setEditListener(OnEditListener listener) {
		mEditListener = listener;
		mPasteView.setPasteListener(new OnPasteListener() {

			@Override
			public void onSelect(MarkInfo markInfo) {
				// 如果在播放，停止播放
				if (mTrackView.isPlaying()) {
					mTrackView.stop();
					showPlayView();
				}

				// 贴图被选中，轨道显示选择器，抛出被选中的view
				// 轨道选择器显示该view的进度区间
				mTrackView.setSelectorStartProgress(markInfo.getProgressStart());
				mTrackView.setSelectorEndProgress(markInfo.getProgressEnd());
				mTrackView.showSelectorView();
				mEditListener.onSelect(markInfo.getMarkView());
			}

			@Override
			public void onSave(MarkInfo markInfo) {
				mTrackView.hideSelectorView();
				mEditListener.onSave(markInfo.getMarkView());
			}
		});

	}

	public void setFrameAdapter(OnFrameAdapter adapter) {
		mFrameAdapter = adapter;
		mTrackView.setFrameAdapter(mFrameAdapter);
	}

	/**
	 * 获取被选中的贴图
	 * 
	 * @return
	 */
	public View getSelectedMarkView() {
		MarkInfo info = mPasteView.getSelectedViewInfo();
		if (info == null) {
			return null;
		}
		return info.getMarkView();
	}

	/**
	 * 添加贴图
	 * 
	 * @param view
	 */
	public void addMarkView(MarkView view) {
		if (view == null) {
			return;
		}
		// 加入帧记录，帧的位置为点击的地方
		view.setCenterPoint(new PointF(mVideoWidth / 2, mVideoHeight / 2));
		// 加入帧所占的时间区域
		MarkInfo markInfo = new MarkInfo(view,
				mTrackView.getSelectorStartProgress(),
				mTrackView.getSelectorEndProgress());

		mPasteView.addMarkInfo(markInfo);
	}

	public interface OnEditListener {
		/**
		 * 播放
		 */
		void onPlay();

		/**
		 * 停止播放
		 */
		void onStop();

		/**
		 * 有贴图被选中
		 * 
		 * @param view
		 */
		void onSelect(View view);

		/**
		 * 进行保存
		 * 
		 * @param event
		 * @return
		 */
		void onSave(View view);

	}

	public interface OnFrameAdapter {
		/**
		 * 根据进度百分比读取帧位图
		 * 
		 * @param progress
		 * @return
		 */
		Bitmap getFrame(float progress);
	}

	public class PlayButton extends View {

		public PlayButton(Context context) {
			super(context);
			init(context);
		}

		private void init(Context context) {
			setClickable(true);
			setBackgroundResource(R.drawable.btn_play);
		}
	}

	public class TimeButton extends TextView {

		public TimeButton(Context context) {
			super(context);
			setBackgroundResource(R.drawable.bg_frame_time);
			setTextColor(Color.WHITE);
			int textSize = (int) (20/(float)640 * mWindowWidth);
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			setGravity(Gravity.CENTER);
			setText("0.0S");
		}

		public void setTime(String time) {
			setText(time + "S");
			invalidate();
		}
	}
}
