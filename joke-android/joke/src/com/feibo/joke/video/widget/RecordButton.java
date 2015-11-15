package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.feibo.joke.R;

/**
 * 录制按钮
 * @author Lidiqing
 *
 */
public class RecordButton extends View {

	private static final int STATE_REST = 0;
	private static final int STATE_LIGHT = 1;

	private int mState;

	private Drawable mDrawable;
	private int mRingColor;
	private int mRingStartColor;
	private int mRingEndColor;

	private int mRingWidth;
	private int mPadding;

	private Paint mRingPaint;

	private long mDownTime;
	private boolean mRefreshing;
	private int mRefreshTime;
	private long mTotalLightTime;
	
	private ColorInterpolator mColorInterpolator;
	private OnRecordClickListener mRecordListener;

	public RecordButton(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecordButton(Context context) {
		super(context);
	}

	private void init(Context context) {

		float density = getResources().getDisplayMetrics().density;

		mDrawable = getResources().getDrawable(R.drawable.btn_record_normal);

		mRingStartColor = 0xFF76747C;
		mRingEndColor = Color.WHITE;
		
		mColorInterpolator = new ColorInterpolator(mRingStartColor, mRingEndColor);

		mRingColor = mRingStartColor;
		mRingWidth = (int) (2 * density);
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setStyle(Style.STROKE);
		mRingPaint.setStrokeWidth(mRingWidth);

		mPadding = (int) (5 * density);
		mRefreshTime = 10;
		mRefreshing = true;
		
		mTotalLightTime = 1000;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startRefreshing();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mRefreshing = false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		mDrawable.setBounds(0 + mPadding, 0 + mPadding, width - mPadding,
				height - mPadding);
		mDrawable.draw(canvas);

		mRingPaint.setColor(mRingColor);
		canvas.drawCircle(width / 2, height / 2, width / 2 - mRingWidth / 2,
				mRingPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownTime = System.currentTimeMillis();
			if (mState == STATE_REST) {
				// 开始录制
				mRecordListener.onStart();
				swichState(STATE_LIGHT);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mState == STATE_LIGHT) {
				// 结束录制
				mRingColor = mRingStartColor;
				mRecordListener.onStop();
				swichState(STATE_REST);
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void swichState(int state) {
		mState = state;
	}

	private void startRefreshing() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				long refreshTime = 0;

				while (mRefreshing) {
					postInvalidate();
					try {
						Thread.sleep(mRefreshTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					refreshTime += mRefreshTime;

					// 设置外环颜色
					if (mState == STATE_REST) {
						refreshTime = 0;
						mRingColor = mRingStartColor;
					} else if (mState == STATE_LIGHT) {
						mRingColor = mColorInterpolator.getColor(getInterpolotorProgress(refreshTime));
					}
				}
			}
		}).start();
	}
	
	private float getInterpolotorProgress(long time){
		long curTime = time % (mTotalLightTime+mTotalLightTime);
		
		if(curTime < mTotalLightTime){
			return curTime/(float)mTotalLightTime;
		}else {
			return 1- (curTime-mTotalLightTime)/(float)mTotalLightTime;
		}
	}
	
	public void setRecordClickListener(OnRecordClickListener listener){
		mRecordListener = listener;
	}
	
	public class ColorInterpolator {
		private int mStartColor;
		private int mEndColor;

		private int[] mStartRGB;
		private int[] mEndRGB;

		public ColorInterpolator(int startColor, int endColor) {
			mStartColor = startColor;
			mEndColor = endColor;

			mStartRGB = new int[4];
			mEndRGB = new int[4];

			int colorShift = 0;
			for (int i = 0; i < 4; i++) {
				mStartRGB[i] = (mStartColor >> colorShift) & 0xFF;
				mEndRGB[i] = (mEndColor >> colorShift) & 0xFF;
				colorShift += 8;
			}
		}

		public int getColor(float interpolatorTime) {
			float interpolator = interpolatorTime;

			int[] mInterRGB = new int[4];
			for (int i = 0; i < 4; i++) {
				mInterRGB[i] = (int) (mStartRGB[i] + (mEndRGB[i] - mStartRGB[i])
						* interpolator);
			}
			return Color.argb(mInterRGB[3], mInterRGB[2], mInterRGB[1],
					mInterRGB[0]);
		}
	}

	public interface OnRecordClickListener{
		void onStart();
		void onStop();
	}
	
}
