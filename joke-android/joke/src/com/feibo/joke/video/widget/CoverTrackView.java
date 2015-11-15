package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class CoverTrackView extends FrameLayout {

	public final static int COVER_NUM = 8;
	public final static int COVER_SPACING = 6;
	public final static float COVER_SELETED_RATIO = 1.3f;

	private final static int STATE_REST = 0;
	private final static int STATE_DRAG = 1;

	private float mCoverWidth;
	private float mCoverHeight;
	private float mCoverSpacing;
	private int mCoverNum;

	private float mCoverSelectedWidth;
	private float mCoverSelectedHeight;
	private PointF mCoverSelectedPointF;

	private CoverView mCoverSelectedView;
	private CoverListView mCoverListView;
	private CoverAdapter mCoverAdapter;

	private int mState;
	private float mCoverSelectedMinCenterX;
	private float mCoverSelectedMaxCenterX;
	
	private OnCoverChangeListener mChangeListener;

	public CoverTrackView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public CoverTrackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CoverTrackView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		float screenWidth = getResources().getDisplayMetrics().widthPixels;

		// 帧列
		mCoverNum = COVER_NUM;
		mCoverSpacing = COVER_SPACING * getResources().getDisplayMetrics().density;
		mCoverWidth = (screenWidth - mCoverSpacing * (mCoverNum - 1))
				/ mCoverNum;
		mCoverHeight = mCoverWidth;
		mCoverListView = new CoverListView(context);
		mCoverListView.setCoverSize(mCoverNum, mCoverSpacing, mCoverWidth,
				mCoverHeight);

		LayoutParams layoutParams = new LayoutParams((int) screenWidth,
				(int) mCoverHeight);
		addViewInLayout(mCoverListView, 0, layoutParams);

		// 选择的图片
		mCoverSelectedView = new CoverView(context);
		mCoverSelectedWidth = mCoverWidth * COVER_SELETED_RATIO;
		mCoverSelectedHeight = mCoverSelectedWidth;
		mCoverSelectedPointF = new PointF(mCoverSelectedWidth / 2,
				mCoverSelectedHeight / 2);
		mCoverSelectedMinCenterX = mCoverSelectedWidth / 2;
		mCoverSelectedMaxCenterX = screenWidth - mCoverSelectedMinCenterX;

		LayoutParams coverParams = new LayoutParams((int) mCoverSelectedWidth,
				(int) mCoverSelectedHeight);
		addViewInLayout(mCoverSelectedView, 1, coverParams);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int viewHeight = (int) mCoverSelectedHeight;
		super.onMeasure(widthMeasureSpec,
				MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		int listOffY = (int) ((mCoverSelectedHeight - mCoverHeight) / 2);
		mCoverListView.layout(0, listOffY, getWidth(),
				(int) (mCoverHeight + listOffY));

		float coverSelectedLeft = mCoverSelectedPointF.x - mCoverSelectedWidth
				/ 2;
		float coverSelectedTop = mCoverSelectedPointF.y - mCoverSelectedHeight
				/ 2;
		mCoverSelectedView.layout((int) coverSelectedLeft,
				(int) coverSelectedTop,
				(int) (coverSelectedLeft + mCoverSelectedWidth),
				(int) (coverSelectedTop + mCoverSelectedHeight));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mState == STATE_REST) {
				dragSelectedView(event.getX());
				switchState(STATE_DRAG);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mState == STATE_DRAG) {
				dragSelectedView(event.getX());
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mState == STATE_DRAG) {
				dragSelectedView(event.getX());
				switchState(STATE_REST);
			}
			break;

		default:
			break;
		}
		return true;
	}

	private void dragSelectedView(float x) {
		if (x <= mCoverSelectedMinCenterX) {
			x = mCoverSelectedMinCenterX;
		}

		if (x >= mCoverSelectedMaxCenterX) {
			x = mCoverSelectedMaxCenterX;
		}

		mCoverSelectedPointF.x = x;
		
		float progress = (x- mCoverSelectedMinCenterX)/(mCoverSelectedMaxCenterX - mCoverSelectedMinCenterX);
		mCoverSelectedView.setCover(mCoverAdapter.getCover(progress));
		mCoverSelectedView.invalidate();
		mChangeListener.onChange(progress);
		System.out.println("progress=============:"+progress);
		requestLayout();
	}

	private void switchState(int state) {
		mState = state;
	}

	public void setCoverAdapter(CoverAdapter adapter) {
		mCoverAdapter = adapter;
		mCoverListView.setAdapter(mCoverAdapter);
		mCoverSelectedView.setCover(adapter.getCover(0));
	}

	public void setCoverChangerListener(OnCoverChangeListener listener){
		mChangeListener = listener;
	}
	
	public static interface CoverAdapter {
		Bitmap getCover(float progress);
	}

	public static interface OnCoverChangeListener {
		void onChange(float progress);
	}

	private static class CoverView extends View {

		public static final int RECT_STROKE_WIDTH = 4;

		private Bitmap mCoverBitmap;
		private Rect mRectDst;
		private Rect mRectSrc;
		private Paint mPaint;
		private int mRectStrokeWidth;

		public CoverView(Context context) {
			super(context);
			mRectDst = new Rect();
			mRectSrc = new Rect();
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(0xFFFFFFFF);
			mRectStrokeWidth = RECT_STROKE_WIDTH;
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(mRectStrokeWidth
					* getResources().getDisplayMetrics().density);
		}

		public void setCover(Bitmap bitmap) {
			mCoverBitmap = bitmap;
		}

		@Override
		protected void onDraw(Canvas canvas) {
	
			int bWidth = mCoverBitmap.getWidth();
			int bHeight = mCoverBitmap.getHeight();
			
			if(bWidth > bHeight){
				mRectSrc.set(bWidth/2 - bHeight/2, 0, bWidth/2+bHeight/2, bHeight);
			}else if (bWidth == bHeight) {
				mRectSrc.set(0, 0, bWidth, bHeight);
			}else {
				mRectSrc.set(0, bHeight/2-bWidth/2, bWidth, bHeight/2+bWidth/2);
			}
			
			mRectDst.set(0, 0, getWidth(), getHeight());
			
			canvas.drawBitmap(mCoverBitmap, mRectSrc, mRectDst, null);
			canvas.drawRect(mRectDst, mPaint);
		}
	}

	private static class CoverListView extends View {

		private CoverAdapter mCoverAdapter;
		private int mCoverNum;
		private float mCoverWidth;
		private float mCoverHeight;
		private float mCoverSpacing;
		private Rect mCoverRectDst;
		private Rect mCoverRectSrc;
		private Paint mCoverPaint;

		public CoverListView(Context context) {
			super(context);
			mCoverPaint = new Paint();
			mCoverPaint.setAntiAlias(true);
			mCoverPaint.setColor(Color.argb(0x50, 0, 0, 0));
		}

		public void setCoverSize(int coverNum, float spacing, float coverWidth,
				float coverheight) {
			mCoverNum = coverNum;
			mCoverSpacing = spacing;
			mCoverWidth = coverWidth;
			mCoverHeight = coverheight;
			mCoverRectDst = new Rect();
			mCoverRectSrc = new Rect();
		}

		public void setAdapter(CoverAdapter adapter) {
			mCoverAdapter = adapter;
		}

		@Override
		protected void onDraw(Canvas canvas) {

			float offset = 0;
			for (int i = 0; i < mCoverNum; i++) {
				
				Bitmap bitmap = mCoverAdapter.getCover(i
						/ (float) mCoverNum);
				
				
				mCoverRectDst.set((int) offset, 0, (int) (offset + mCoverWidth),
						(int) mCoverHeight);
				
				int bWidth = bitmap.getWidth();
				int bHeight = bitmap.getHeight();
				
				if(bWidth > bHeight){
					mCoverRectSrc.set(bWidth/2 - bHeight/2, 0, bWidth/2+bHeight/2, bHeight);
				}else if (bWidth == bHeight) {
					mCoverRectSrc.set(0, 0, bWidth, bHeight);
				}else {
					mCoverRectSrc.set(0, bHeight/2-bWidth/2, bWidth, bHeight/2+bWidth/2);
				}
				
				canvas.drawBitmap(bitmap, mCoverRectSrc, mCoverRectDst, null);
				canvas.drawRect(mCoverRectDst, mCoverPaint);
				offset += mCoverWidth + mCoverSpacing;
			}
		}
	}
}