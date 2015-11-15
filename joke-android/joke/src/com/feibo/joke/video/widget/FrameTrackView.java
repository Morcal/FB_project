package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.video.widget.VideoEditFrameLayout.OnFrameAdapter;

/**
 * 帧轨道
 *
 * @author Lidiqing 2015/4/29
 */
public class FrameTrackView extends ViewGroup {

	// 默认视频时长
	private static long DEFAULT_VIDEO_DURATION = 4000;
	// 默认帧步长
	private static long DEFAULT_FRAME_DURATION = 10;

	private static int STATE_REST = 0;
	private static int STATE_DRAG_LEFT = 1;
	private static int STATE_DRAG_RIGHT = 2;
	private static int STATE_DRAG_CENTER = 3;
	private static int STATE_PLAY = 4;
	private static int STATE_POINT = 5;

	public static final float FRAME_PADDING = 0.5f;
	public static final float FRAME_RATIO = 0.8f;
	public static final int FRAME_NUM = 10;
	public static int POINTER_WIDHT = 5;

	private float density;

	private int mFrameNum;
	private int mFramePadding;

	private int mFrameHeight;
	private int mFrameWidth;

	private int mScreenWidth;

	private int mState;

	// 帧选择器相关属性
	private FrameSelectorView mSelectorView;
	private int mSelectorOffset;
	private int mSelectorMinWidth;
	private int mSelectorMaxWidth;
	private int mSelectorXStartOffset;
	private int mSelectorXEndOffset;
	private int mSelectorSlideWidth;

	// 指针和相关属性
	private FramePointer mPointerView;
	private int mPointerWidth;
	private int mPointerClickSize;
	private int mPointerXCenterOffset;
	private float mPointerOffset;
	private float mPointerPerOffset;

	// 帧列表
	private FrameListView mFrameListView;

	// 视频时长
	private long mVideoDuration;

	// 每帧时长
	private long mFrameDuration;

	// 播放长度
	private int mPlayLength;

	private boolean mRefreshing;

	private Handler mMainThreadHandler;

	private OnPlayListener mPlayListener;

	public FrameTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public FrameTrackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FrameTrackView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setBackgroundColor(Color.WHITE);

		density = getResources().getDisplayMetrics().density;
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		mMainThreadHandler = new Handler(Looper.getMainLooper());

		// 帧
		mFrameNum = FRAME_NUM;
		mFramePadding = (int) (FRAME_PADDING * density);
		mFrameWidth = (int) Math.ceil(((mScreenWidth - (mFrameNum - 1)
				* mFramePadding) / (float) mFrameNum));
		mFrameHeight = mFrameWidth;

		mFrameListView = new FrameListView(context);
		mFrameListView.setFrameBlock(mFrameNum, mFramePadding, mFrameWidth,
				mFrameHeight);
		addViewInLayout(mFrameListView, 0, new LayoutParams(mScreenWidth,
				mFrameHeight));

		// 选择器
		mSelectorMinWidth = (int) (mScreenWidth * 0.1 + mFrameHeight * 0.6);
		mSelectorMaxWidth = mScreenWidth;
		mSelectorSlideWidth = (int) (mScreenWidth * 0.25);

		// 初始化选择器位移为一个帧宽度
		mSelectorOffset = mFrameWidth;
		mSelectorView = new FrameSelectorView(context);
		mSelectorView.initSize(mFrameHeight, mSelectorOffset,
				(int) (mScreenWidth * 0.3) + mFrameWidth / 2);

		addViewInLayout(mSelectorView, 1, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// 指针
		mPointerOffset = 0;
		mPointerWidth = (int) (POINTER_WIDHT * density);

		mPointerClickSize = (int) (POINTER_WIDHT * density * 2);
		mPointerView = new FramePointer(context);
		mPointerView.setPointerWidth(mPointerWidth);

		addViewInLayout(mPointerView, 2, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mFrameDuration = DEFAULT_FRAME_DURATION;
		mVideoDuration = DEFAULT_VIDEO_DURATION;

		mPlayLength = mScreenWidth;

		hideSelectorView();
		switchState(STATE_REST);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startRefresh();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mRefreshing = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec,
				MeasureSpec.makeMeasureSpec(mFrameHeight, MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		LogUtil.i("VideoFrameTrackView", "onLayout");
		// 帧列表
		mFrameListView.layout(0, 0, mScreenWidth, mFrameHeight);

		// 帧选择器
		int selectorStart = mSelectorView.getStartPosition();
		int selectorEnd = mSelectorView.getEndPosition();
		mSelectorView.layout(selectorStart, 0, selectorEnd, mFrameHeight);

		// 帧指针
		mPointerView.layout(0, 0, getWidth(), getHeight());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mState == STATE_PLAY) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mState == STATE_REST) {
				// 选择器可响应
				if (mSelectorView.getVisibility() == View.VISIBLE) {
					if (mSelectorView.leftBtnContains(event.getX()
							- mSelectorOffset, event.getY())) {
						mSelectorXStartOffset = (int) (event.getX() - mSelectorView
								.getStartPosition());
						switchState(STATE_DRAG_LEFT);
						break;
					}

					if (mSelectorView.rightBtnContains(event.getX()
							- mSelectorOffset, event.getY())) {
						mSelectorXEndOffset = (int) (mSelectorView
								.getEndPosition() - event.getX());
						switchState(STATE_DRAG_RIGHT);
						break;
					}

					if (mSelectorView.centerContains(event.getX()
							- mSelectorOffset, event.getY())) {

						mSelectorXStartOffset = (int) (event.getX() - mSelectorView
								.getStartPosition());
						mSelectorXEndOffset = (int) (mSelectorView
								.getEndPosition() - event.getX());
						switchState(STATE_DRAG_CENTER);
						break;
					}
				}

				// 指针可响应
				if (containsPointer(event.getX(), event.getY())) {
					hideSelectorView();
					mPointerXCenterOffset = (int) (event.getX() - (mPointerWidth / 2 + mPointerOffset));
					mPlayListener.onPointerStart(mPointerOffset
							/ mPlayLength);
					switchState(STATE_POINT);
					break;
				}

			}
			break;
		case MotionEvent.ACTION_MOVE:

			// 滑动指针
			if (mState == STATE_POINT) {
				mPointerOffset = event.getX() - mPointerXCenterOffset;

				// 边界限制
				if (mPointerOffset < 0) {
					mPointerOffset = 0;
				}

				if (mPointerOffset >= mPlayLength) {
					mPointerOffset = mPlayLength;
				}

				mPointerView.setPointerOffsetInvalidate(mPointerOffset);
				mPlayListener.onPointer(mPointerOffset / mPlayLength);
				break;
			}

			// 移动选择器左边
			if (mState == STATE_DRAG_LEFT) {
				int newStart = (int) (event.getX() - mSelectorXStartOffset);

				// 边界限制：不能超过左边界，选择器范围不能小于最小范围，不能大于最大范围
				if (mSelectorView.getEndPosition() - newStart < mSelectorMinWidth) {
					newStart = mSelectorView.getEndPosition()
							- mSelectorMinWidth;
				}

				if (mSelectorView.getEndPosition() - newStart > mSelectorMaxWidth) {
					newStart = mSelectorView.getEndPosition()
							- mSelectorMaxWidth;
				}

				if (newStart < 0) {
					newStart = 0;
				}

				mSelectorView.setStartPosition(newStart);
				mPlayListener.onSelectorStart(newStart / (float) mPlayLength,
						true);
				break;
			}

			// 移动选择器右边
			if (mState == STATE_DRAG_RIGHT) {
				// 边界限制：不能超过右边界，选择器范围不能小于最小范围，不能大于最大范围
				int newEnd = (int) (event.getX() + mSelectorXEndOffset);
				if (newEnd - mSelectorView.getStartPosition() < mSelectorMinWidth) {
					newEnd = mSelectorView.getStartPosition()
							+ mSelectorMinWidth;
				}

				if(newEnd - mSelectorView.getStartPosition() > mSelectorMaxWidth) {
					newEnd = mSelectorView.getStartPosition()
							+ mSelectorMaxWidth;
				}

				if (newEnd > mPlayLength) {
					newEnd = mPlayLength;
				}

				mSelectorView.setEndPosition(newEnd);
				mPlayListener.onSelectorEnd(newEnd / (float) mPlayLength, true);
				break;
			}

			// 滑动整个选择器
			if (mState == STATE_DRAG_CENTER) {
				int newStart = (int) (event.getX() - mSelectorXStartOffset);
				int newEnd = (int) (event.getX() + mSelectorXEndOffset);

				if (newStart < 0) {
					newStart = 0;
					newEnd = newStart + mSelectorView.getViewWidth();
				}
				if (newEnd > mPlayLength) {
					newEnd = mPlayLength;
					newStart = newEnd - mSelectorView.getViewWidth();
				}

				mSelectorView.setStartAndEndPosition(newStart, newEnd);

				mPlayListener.onSelectorStart(newStart / (float) mPlayLength,
						true);
				mPlayListener
						.onSelectorEnd(newEnd / (float) mPlayLength, false);
			}

			break;

		case MotionEvent.ACTION_UP:
			if (mState == STATE_DRAG_LEFT || mState == STATE_DRAG_RIGHT
					|| mState == STATE_DRAG_CENTER) {
				mSelectorOffset = mSelectorView.getStartPosition();
				mPlayListener.onSelectorStart(mSelectorOffset
						/ (float) mPlayLength, true);
			} else if (mState == STATE_POINT) {

				// 滑动选择器到指针位置
				float progress = mPointerOffset / mPlayLength;
				flipSelector(progress);
				mPlayListener.onPointerEnd(progress);
			}
			switchState(STATE_REST);
			break;
		default:
			break;
		}
		return true;
	}

	private void switchState(int state) {
		mState = state;
	}

	private boolean containsPointer(float x, float y) {
		if (mPointerView.getVisibility() != View.VISIBLE) {
			return false;
		}

		return x > mPointerOffset - mPointerClickSize
				&& x < mPointerWidth + mPointerClickSize + mPointerOffset;
	}

	private void startRefresh() {
		mRefreshing = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (mRefreshing) {
					if (mState == STATE_PLAY) {
						// 刷新界面
						mMainThreadHandler.post(new Runnable() {
							@Override
							public void run() {
								requestLayout();
							}
						});

						// 到达视频结尾，从头开始
						if (mPointerOffset >= mPlayLength) {
							mPointerOffset = 0;
						}

						// 指针按步长偏移
						mPointerOffset += mPointerPerOffset;
						mMainThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								mPointerView
										.setPointerOffsetInvalidate(mPointerOffset);
								mPlayListener.onPointer(mPointerOffset
										/ mPlayLength);
							}
						});

						try {
							Thread.sleep(mFrameDuration);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();
	}

	/**
	 * 滑动指针
	 *
	 * @param progress
	 */
	private void flipSelector(float progress) {
		int start = (int) (mPlayLength * progress);
		int end = start + mSelectorSlideWidth;

		if (end > mPlayLength) {
			end = mPlayLength;
			start = end - mSelectorSlideWidth;
		}
		mSelectorView.setStartAndEndPosition(start, end);
		mSelectorOffset = mSelectorView.getStartPosition();
	}

	/**
	 * 播放视频，启动指针
	 */
	public void play() {
		if (mState == STATE_PLAY) {
			return;
		}
		// 隐藏选择器
		mSelectorView.setVisibility(View.INVISIBLE);
		// 指针每次的步长
		mPointerPerOffset = mPlayLength
				/ (float) (mVideoDuration / mFrameDuration);
		switchState(STATE_PLAY);
	}

	/**
	 * 停止视频，暂停指针
	 */
	public void stop() {
		if (mState == STATE_PLAY) {
			mMainThreadHandler.removeCallbacksAndMessages(null);
			flipSelector(mPointerOffset / mPlayLength);
			switchState(STATE_REST);
		}
	}

	/**
	 * 是否正在播放
	 *
	 * @return
	 */
	public boolean isPlaying() {
		return mState == STATE_PLAY;
	}

	/**
	 * 开始编辑
	 */
	public void startEdit() {
		stop();
		mSelectorView.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化选择器的选择范围
	 */
	public void resetSelectorArea(){
		mSelectorOffset = 0;
	    mSelectorView.initSize(mFrameHeight, mSelectorOffset, mSelectorMinWidth);
	    requestLayout();
	}

	/**
	 * 停止编辑
	 */
	public void stopEdit() {
		mSelectorView.setVisibility(View.INVISIBLE);
	}

	public void hideSelectorView() {
		mSelectorView.setVisibility(View.INVISIBLE);
	}

	public void showSelectorView() {
		mSelectorView.setVisibility(View.VISIBLE);
	}

	public void hidePointerView() {
		mPointerView.setVisibility(View.INVISIBLE);
	}

	public void showPointerView() {
		mPointerView.setVisibility(View.VISIBLE);
	}

	public void setFrameAdapter(OnFrameAdapter frameAdapter) {
		mFrameListView.setAdapter(frameAdapter);
	}

	public void setPlayListener(OnPlayListener listener) {
		mPlayListener = listener;
	}

	public void setVideoDuration(long videoDuration) {
		mVideoDuration = videoDuration;
	}

	public void setSelectorMinWidth(int width) {
		mSelectorMinWidth = width;
	}

	public void setSelectorMaxWidth(int width) {
		mSelectorMaxWidth = width;
	}

	public int getSelectorMaxWidth(){
		return mSelectorMaxWidth;
	}

	public int getSelectorCurrentWidth(){
		return mSelectorView.getEndPosition() - mSelectorView.getStartPosition();
	}

	public int getSelectorCenterPos(){
		return (mSelectorView.getEndPosition() + mSelectorView.getStartPosition())/2;
	}

	public boolean isSelectorOverMaxWidth(){
		return getTrackViewLength()>=(mSelectorMaxWidth-5);
	}

    private int getTrackViewLength() {
        return mSelectorView.getEndPosition() - mSelectorView.getStartPosition();
    }

	public boolean isSelectorLessMinWidth() {
	    LogUtil.i("Frame", "len : %d" + getTrackViewLength() + " minWidth : " + mSelectorMinWidth);
	    return getTrackViewLength() <= (mSelectorMinWidth + 5);
	}

	/**
	 * 获取选择器起始点所占的进度
	 *
	 * @return
	 */
	public float getSelectorStartProgress() {
		return mSelectorView.getStartPosition() / (float) mPlayLength;
	}

	/**
	 * 获取选择器终点所占的进度
	 *
	 * @return
	 */
	public float getSelectorEndProgress() {
		return mSelectorView.getEndPosition() / (float) mPlayLength;
	}

	/**
	 * 设置选择器起始点
	 *
	 * @param progress
	 */
	public void setSelectorStartProgress(float progress) {
		mSelectorView.setStartPosition((int) (mPlayLength * progress));
	}

	/**
	 * 设置选择器终点
	 *
	 * @param progress
	 */
	public void setSelectorEndProgress(float progress) {
		mSelectorView.setEndPosition((int) (mPlayLength * progress));
	}

	public static interface OnPlayListener {
		/**
		 * 选择器的起始点变化
		 *
		 * @param progress
		 */
		void onSelectorStart(float progress, boolean show);

		/**
		 * 选择器的终点变化
		 *
		 * @param progress
		 */
		void onSelectorEnd(float progress, boolean show);

		/**
		 * 启动指针
		 *
		 * @param progress
		 */
		void onPointerStart(float progress);

		/**
		 * 指针的位置变化
		 *
		 * @param progress
		 */
		void onPointer(float progress);

		/**
		 * 停止指针
		 *
		 * @param progress
		 */
		void onPointerEnd(float progress);

		/**
		 * 到达终点
		 */
		void onFinish();
	}

	/**
	 * 帧指针
	 *
	 * @author Lidiqing
	 *
	 */
	public static class FramePointer extends View {

		private Paint mPaint;
		private RectF mRectF;

		private float mPointerOffset;
		private int mPointerWidth;

		public FramePointer(Context context) {
			super(context);
			init(context);
		}

		private void init(Context context) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(context.getResources().getColor(R.color.c2_orange));
			mRectF = new RectF();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			mRectF.set(0 + mPointerOffset, 0, mPointerOffset + mPointerWidth,
					getHeight());
			canvas.drawRoundRect(mRectF, 3, 3, mPaint);
		}

		public void setPointerWidth(int width) {
			mPointerWidth = width;
		}

		public void setPointerOffsetInvalidate(float offset) {
			mPointerOffset = offset;
			invalidate();
		}
	}

	/**
	 * 帧列表
	 *
	 * @author Lidiqing
	 *
	 */
	private static class FrameListView extends View {

		private OnFrameAdapter mFrameAdapter;
		private int mBlockNum;
		private float mBlockWidth;
		private float mBlockHeight;
		private float mBlockSpacing;
		private Rect mBlockRectDst;
		private Rect mBlockRectSrc;
		private Paint mBlockPaint;

		public FrameListView(Context context) {
			super(context);
			mBlockPaint = new Paint();
			mBlockPaint.setAntiAlias(true);
			mBlockPaint.setColor(Color.argb(0x50, 0, 0, 0));
		}

		public void setFrameBlock(int coverNum, float spacing,
				float coverWidth, float coverheight) {
			mBlockNum = coverNum;
			mBlockSpacing = spacing;
			mBlockWidth = coverWidth;
			mBlockHeight = coverheight;
			mBlockRectDst = new Rect();
			mBlockRectSrc = new Rect();
		}

		public void setAdapter(OnFrameAdapter adapter) {
			mFrameAdapter = adapter;

		}

		@Override
		protected void onDraw(Canvas canvas) {
			float offset = 0;
			for (int i = 0; i < mBlockNum; i++) {

				Bitmap bitmap = mFrameAdapter.getFrame(i / (float) mBlockNum);
				if (bitmap == null) {
				    return;
				}

				mBlockRectDst.set((int) offset, 0,
						(int) (offset + mBlockWidth), (int) mBlockHeight);

				int bWidth = bitmap.getWidth();
				int bHeight = bitmap.getHeight();

				if (bWidth > bHeight) {
					mBlockRectSrc.set(bWidth / 2 - bHeight / 2, 0, bWidth / 2
							+ bHeight / 2, bHeight);
				} else if (bWidth == bHeight) {
					mBlockRectSrc.set(0, 0, bWidth, bHeight);
				} else {
					mBlockRectSrc.set(0, bHeight / 2 - bWidth / 2, bWidth,
							bHeight / 2 + bWidth / 2);
				}

				canvas.drawBitmap(bitmap, mBlockRectSrc, mBlockRectDst, null);
				canvas.drawRect(mBlockRectDst, mBlockPaint);
				offset += mBlockWidth + mBlockSpacing;
			}
		}
	}
}
