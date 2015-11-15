package com.feibo.joke.video.widget;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import fbcore.log.LogUtil;

import com.feibo.joke.R;

/**
 * 录制轨道
 *
 * @author Lidiqing
 *
 */
public class RecordTrackView extends View {
    private static final String TAG = RecordTrackView.class.getSimpleName();
	private static int STATE_REST = 0;
	private static int STATE_RECORD = 1;

	private static int RECORD_REFRESH_TIME = 10;
	private static float PROGRESS_MIN = 0.3f;

	private int mScreenWidth;
	private int mTrackHeight;

	// 每段之间的间隔宽度
	private int mIntervalWidth;

	// 光标宽度
	private int mCursorWidth;

	private int mColorInterval;
	private int mColorPart;
	private int mColorPartSelected;
	private int mColorCursor;
	private int mColorCursorOn;
	private int mColorCursorOff;
	private int mColorBackgroud;

	private Rect mPaintRect;

	private Paint mPaint;

	private List<Float> mProgressList;

	// 已经录制完成的视频片段长度总和
	private int mPartTotal;

	// 当前录制的视频时长
	private float mPartRecording;
	// 最后一段是否被选中
	private boolean mPartSelected;

	private int mState;

	/**
	 * 单位(ms)
	 */
	private long mRecordMaxTime;
	private long mRecordRefreshTime;
	private long mRecordTotalRefreshTime;
	private long mRecordStartTime;

	private float mProgressCur;
	private float mProgressMin;

	private boolean mRefreshing;

	private OnRecordTrackListener mRecordTrackListener;
	private Handler mMainThreadHandler;

	public RecordTrackView(Context context) {
		super(context);
//		setBackgroundColor(Color.WHITE);
		// float density = getResources().getDisplayMetrics().density;
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		mTrackHeight = (int) (mScreenWidth * 0.015625);
		mIntervalWidth = (int) (mTrackHeight * 0.15);
		mCursorWidth = (int) (mTrackHeight * 0.5);

		mColorInterval = getResources().getColor(R.color.c5_dark_grey);
		mColorPart = getResources().getColor(R.color.c1_light_orange);
		mColorPartSelected = getResources().getColor(R.color.c3_red_alpha_70);
		mColorCursorOn = getResources().getColor(R.color.c9_white);
		mColorCursorOff = Color.TRANSPARENT;
		mColorCursor = mColorCursorOn;
		mColorBackgroud = getResources().getColor(R.color.c5_dark_grey);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mColorPart);

		mPaintRect = new Rect();

		mPartTotal = 0;
		mPartRecording = 0;
		mPartSelected = false;

		mProgressList = Collections.synchronizedList(new LinkedList<Float>());

		mRecordRefreshTime = RECORD_REFRESH_TIME;
		mRecordMaxTime = 10 * 1000;

		mProgressCur = 0;
		mProgressMin = PROGRESS_MIN;

		mMainThreadHandler = new Handler(Looper.getMainLooper());
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
	protected void onDraw(Canvas canvas) {
	    System.currentTimeMillis();
		int bottom = getHeight();
		int top = bottom - mTrackHeight;

		// 画出轨道背景
		mPaintRect.set(0, top, getWidth(), bottom);
		mPaint.setColor(mColorBackgroud);
		canvas.drawRect(mPaintRect, mPaint);

		// 画出最短视频提示线
		int minLen = (int) (mScreenWidth*PROGRESS_MIN - mCursorWidth/2);
		mPaintRect.set(minLen, top,minLen+mCursorWidth,bottom);
		mPaint.setColor(Color.WHITE);
		canvas.drawRect(mPaintRect, mPaint);


		int offset = 0;
		for (int i = 0; i < mProgressList.size(); i++) {

		    float partProgress = 0f;
		    if (i == 0) {
		        partProgress = mProgressList.get(0);
		    } else {
		        partProgress = mProgressList.get(i) - mProgressList.get(i-1);
		    }

		    int partWidth = (int)(partProgress * mScreenWidth);
		    int mPartShowWidth = partWidth - mIntervalWidth;

		    if (i == mProgressList.size()-1 && mPartSelected) {
		        mPaintRect.set(offset, top, offset+partWidth, bottom);
                mPaint.setColor(mColorPartSelected);
                canvas.drawRect(mPaintRect, mPaint);
		    } else {
		        mPaintRect.set(offset, top, offset + mPartShowWidth, bottom);
                mPaint.setColor(mColorPart);
                canvas.drawRect(mPaintRect, mPaint);
                // 绘制间隔
                mPaintRect.set(offset + mPartShowWidth, top, offset + partWidth, bottom);
                mPaint.setColor(mColorInterval);
                canvas.drawRect(mPaintRect, mPaint);
		    }

		    offset += partWidth;
		}

		// 画出正在录制的片段
		if (mState == STATE_RECORD) {
			mPaintRect.set(offset, top, (int) (offset + mPartRecording), bottom);
			mPaint.setColor(mColorPart);
			canvas.drawRect(mPaintRect, mPaint);
			offset += mPartRecording;
		}

		// 画出光标
		mPaintRect.set(offset, top, offset + mCursorWidth, bottom);
		mPaint.setColor(mState == STATE_RECORD ? mColorCursorOn : mColorCursor);
		canvas.drawRect(mPaintRect, mPaint);
	}

	private void startRefresh() {
		mRecordTotalRefreshTime = 0;
		mRefreshing = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (mRefreshing) {
					postInvalidate();
                    try {
                        Thread.sleep(mRecordRefreshTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 更换光标颜色
                    mRecordTotalRefreshTime += mRecordRefreshTime;
                    if (mRecordTotalRefreshTime % 300 == 0) {
                        if (mColorCursor == mColorCursorOn) {
                            mColorCursor = mColorCursorOff;
                        } else {
                            mColorCursor = mColorCursorOn;
                        }
                    }
					// 如果处于拍摄，添加新进度
					if (mState == STATE_RECORD) {
					    // 根据耗时来计算需要绘制的长度
					    mPartRecording = 1.0f * (System.currentTimeMillis() - mRecordStartTime) / mRecordMaxTime * mScreenWidth;
						if(mPartRecording + mPartTotal > mScreenWidth){
							mPartRecording = mScreenWidth - mPartTotal - 3;
							mProgressCur = 1.0f;
						}else {
							mProgressCur = (mPartRecording + mPartTotal)/mScreenWidth;
						}

						mMainThreadHandler.post(new Runnable() {
							@Override
							public void run() {
								mRecordTrackListener.onRecord(mProgressCur);
							}
						});
					}
				}
			}
		}).start();
	}

	public void startRecord() {
		mState = STATE_RECORD;
		mPartSelected = false;
		mRecordStartTime = System.currentTimeMillis();
	}

	public void stopRecord() {
		mState = STATE_REST;
		LogUtil.d(TAG, "stopRecord");
		mProgressList.add(mProgressCur);
		mPartTotal = (int)(mProgressCur * mScreenWidth);
		mPartRecording = 0;
	}

	public void setRecordTrackListener(OnRecordTrackListener listener){
		mRecordTrackListener = listener;
	}

	public void setMaxRecordTime(long time) {
		mRecordMaxTime = time;
	}

	public long getMaxRecordTime() {
	    return mRecordMaxTime;
	}

	// 选中当前最后一段
	public void selected() {
		mPartSelected = true;
	}

	public synchronized void delectedPart() {
		mPartSelected = false;

		if (mProgressList.size() > 0) {
		    mProgressList.remove(mProgressList.size()-1);
		    if (mProgressList.isEmpty()) {
		        mProgressCur = 0f;
		    } else {
		        mProgressCur = mProgressList.get(mProgressList.size()-1);
		    }
		} else {
		    mProgressCur = 0f;
		}
		mPartTotal = (int)(mProgressCur * mScreenWidth);
	}

	public int getVideoPartsNum(){
	    return mProgressList.size();
	}

	public int getTrackHeight(){
		return mTrackHeight;
	}

	public float getCurProgress(){
		return mProgressCur;
	}

	public boolean isOverMinProgress(){
		return mProgressCur >= mProgressMin;
	}

	/**
	 * 获取最小的拍摄进度
	 * @return
	 */
	public float getMinProgress(){
		return mProgressMin;
	}

	public interface OnRecordTrackListener{
		void onRecord(float progress);
	}
}
