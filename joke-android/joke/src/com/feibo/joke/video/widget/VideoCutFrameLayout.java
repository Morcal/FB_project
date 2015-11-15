package com.feibo.joke.video.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import fbcore.log.LogUtil;

import com.feibo.joke.video.widget.FrameTrackView.OnPlayListener;
import com.feibo.joke.video.widget.JokeVideoPlayer.OnVideoListener;
import com.feibo.joke.video.widget.VideoEditFrameLayout.OnFrameAdapter;

public class VideoCutFrameLayout extends FrameLayout {
	private static final String TAG = "VideoCutFrameLayout";

	private static final int VIDEO_MAX_DURATION = 10000;
	private static final int VIDEO_MIN_DURATION = 3000;

	// 内容
	private LinearLayout mContentView;

	// 视频控制
	private JokeVideoPlayer mVideoPlayer;

	// 轨道
	private FrameTrackView mTrackView;

	// 视频的宽度和高度
	private int mVideoWidth;
	private int mVideoHeight;

	// 窗口的宽高度
	private int mWindowHeight;
	private int mWindowWidth;

	// 轨道高度
	private int mTrackHeight;

	// 剪辑起始点
	private float mStartProgress;

	// 剪辑终点
	private float mEndProgress;

	private TextToast mWarnToast;

	public VideoCutFrameLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public VideoCutFrameLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public VideoCutFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoCutFrameLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		int mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		// 编辑窗口
		mWindowWidth = mScreenWidth;
		mWindowHeight = mWindowWidth;

		// 轨道高度，占屏幕高度1/10 = 0.1
		mTrackHeight = (int) (mWindowWidth * 0.1);

		// 视频的默认高度和默认宽度为1:1，宽度为屏幕宽度
		mVideoWidth = mWindowWidth;
		mVideoHeight = mVideoWidth;

		// 视频内容区
		mContentView = new LinearLayout(context);
		LayoutParams contentParams = new LayoutParams(mWindowWidth,
				mWindowHeight);
		addViewInLayout(mContentView, 0, contentParams);

		// 帧轨道区
		mTrackView = new FrameTrackView(context);
		LayoutParams trackParams = new LayoutParams(mWindowWidth, mTrackHeight);
		trackParams.topMargin = mWindowHeight;
		addViewInLayout(mTrackView, 1, trackParams);
		mTrackView.showSelectorView();
		mTrackView.hidePointerView();

		// 提示到最大长度
		mWarnToast = new TextToast(context, "不能再长了",
				(int) (mTrackHeight * 0.7f));
		mWarnToast.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.topMargin = (int) (mWindowHeight
				- mWarnToast.getMeasuredHeight() - mWarnToast
				.getMeasuredHeight() * 0.2);
		addViewInLayout(mWarnToast,2,layoutParams);
		mWarnToast.setVisibility(View.INVISIBLE);
		initEvents();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(MeasureSpec.makeMeasureSpec(mWindowWidth,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mWindowHeight
				+ mTrackHeight, MeasureSpec.EXACTLY));
	}

	private void initEvents() {
		mTrackView.setPlayListener(new OnPlayListener() {

			@Override
			public void onSelectorStart(float progress, boolean show) {
				if (show) {
					mVideoPlayer.seekTo(progress);
				}
				mStartProgress = progress;
				handleWarnShow();
			}

			@Override
			public void onSelectorEnd(float progress, boolean show) {
				if (show) {
					mVideoPlayer.seekTo(progress);
				}
				mEndProgress = progress;
				handleWarnShow();
			}

			@Override
			public void onPointerStart(float progress) {
			}

			@Override
			public void onPointerEnd(float progress) {
			}

			@Override
			public void onPointer(float progress) {
			}

			@Override
			public void onFinish() {
			}
		});
	}

	/**
	 * 显示帧轨道的图片
	 *
	 * @param adapter
	 */
	public void setFrameAdapter(OnFrameAdapter adapter) {
		mTrackView.setFrameAdapter(adapter);
	}

	/**
	 * 设置视频路径
	 *
	 * @param path
	 */
	public void setVideoPath(Context context, String path) {
		mVideoPlayer = new JokeVideoPlayer(context, path,
				new OnVideoListener() {

					@Override
					public void onPrepare(MediaPlayer mediaPlayer) {
						// 设置选择器的最大区域
						int duration = mediaPlayer.getDuration();
						float maxProgress;
						float minProgress;
						if (duration > VIDEO_MIN_DURATION && duration < VIDEO_MAX_DURATION) {
							maxProgress = 1.0f;
							minProgress = VIDEO_MIN_DURATION / (float) duration;

						} else if (duration > VIDEO_MAX_DURATION){
							maxProgress = VIDEO_MAX_DURATION / (float) duration;
							minProgress = VIDEO_MIN_DURATION / (float) duration;

						} else {
						    maxProgress = 1.0f;
						    minProgress = 1.0f;
						}

						mStartProgress = 0;
						mEndProgress = maxProgress;

						mTrackView.setSelectorMinWidth((int)(mWindowWidth * minProgress));
						mTrackView
								.setSelectorMaxWidth((int) (mWindowWidth * maxProgress));

						// 重新设置选择器范围
						mTrackView.resetSelectorArea();
					}
				});

		SurfaceView surfaceView = mVideoPlayer.getSurfaceView();
		mContentView.removeAllViews();
		mContentView.addView(surfaceView);
	}

	/**
	 * 处理提醒对话框的显示
	 */
	private void handleWarnShow(){
		LogUtil.i(TAG, "selectorWidth:"+mTrackView.getSelectorCurrentWidth());
		if(mTrackView.isSelectorOverMaxWidth()){

			mWarnToast.setText("不能再长了");
			int centerPos = mTrackView.getSelectorCenterPos();
			LayoutParams params = (LayoutParams) mWarnToast.getLayoutParams();
			params.leftMargin = centerPos - mWarnToast.getMeasuredWidth()/2;
			mWarnToast.setLayoutParams(params);
			mWarnToast.setVisibility(View.VISIBLE);
		}else if (mTrackView.isSelectorLessMinWidth()){
		    mWarnToast.setText("不能再短了");
		    int centerPos = mTrackView.getSelectorCenterPos();
            LayoutParams params = (LayoutParams) mWarnToast.getLayoutParams();
            params.leftMargin = centerPos - mWarnToast.getMeasuredWidth()/2;
            mWarnToast.setLayoutParams(params);
            mWarnToast.setVisibility(View.VISIBLE);
		} else {
		    mWarnToast.setVisibility(View.INVISIBLE);
		}
	}

	public int getVideoStart() {
		return (int) (mStartProgress * mVideoPlayer.getVideoDuration());
	}

	public int getVideoEnd() {
		return (int) (mEndProgress * mVideoPlayer.getVideoDuration());
	}

	public int getVideoDuration() {
		return mVideoPlayer.getVideoDuration();
	}

	public int getTrackHeight() {
		return mTrackHeight;
	}
}
