package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.feibo.joke.R;

/**
 * 轨道帧选择器
 * @author Lidiqing 2015/4/29
 *
 */
public class FrameSelectorView extends View {

	private int mViewHeight;
	private int mViewWidth;
	private int mBtnWidth;

	private float density;

	private Drawable mLeftDrawable;
	private Drawable mRightDrawable;

	private int mStartPosition;
	private int mEndPosition;

	private Paint mPaint;

	// 左边按钮可点击区域
	private RectF mLeftRectF;

	// 右边按钮可点击区域
	private RectF mRightRectF;
	
	// 中心可拖动区域
	private RectF mCenterRectF;
	
	// 按钮点击区域
	private int mClickWidth;

	public FrameSelectorView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setBackgroundColor(Color.TRANSPARENT);
		density = context.getResources().getDisplayMetrics().density;

		mLeftDrawable = getResources().getDrawable(R.drawable.btn_arrow_left);
		mRightDrawable = getResources().getDrawable(R.drawable.btn_arrow_right);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(0x7F, 0xFC, 0xD6, 0x52);
//		mPaint.setColor(Color.BLUE);
		
		mLeftRectF = new RectF();
		mRightRectF = new RectF();
		mCenterRectF = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int rightStart = mEndPosition - mStartPosition - mBtnWidth;

		mLeftDrawable.setBounds(0, 0, mBtnWidth, mViewHeight);
		mRightDrawable.setBounds(rightStart, 0, rightStart + mBtnWidth,
				mViewHeight);

//		mLeftDrawable.setBounds((int)mLeftRectF.left, (int)mLeftRectF.top, (int)mLeftRectF.right, (int)mLeftRectF.bottom);
//		mRightDrawable.setBounds((int)mRightRectF.left, (int)mRightRectF.top, (int)mRightRectF.right, (int)mRightRectF.bottom);
		
		mLeftDrawable.draw(canvas);
		canvas.drawRect(mBtnWidth, 0, rightStart, mViewHeight, mPaint);
		mRightDrawable.draw(canvas);
	}

	/**
	 * 
	 * @param height
	 * @param centerWidth
	 */
	public void initSize(int height, int start, int end) {
		mViewHeight = height;
		mBtnWidth = (int) (mViewHeight * 0.3);

		mStartPosition = start;
		mEndPosition = end;
		mViewWidth = mEndPosition - mStartPosition;

		mClickWidth = mBtnWidth*2;
		
		mLeftRectF.set(0, 0, mClickWidth, mViewHeight);
		mRightRectF.set(mViewWidth - mClickWidth, 0, mViewWidth, mViewHeight);
		mCenterRectF.set(mClickWidth, 0, mViewWidth-mClickWidth, mViewHeight);
		requestLayout();
	}

	public boolean leftBtnContains(float x, float y) {
		System.out.println("left x:"+x);
		if(this.getVisibility() != View.VISIBLE){
			System.out.println("false");
			return false;
		}
		return mLeftRectF.contains(x, y);
	}

	public boolean rightBtnContains(float x, float y) {
		System.out.println("right x:"+x);
		if(this.getVisibility() != View.VISIBLE){
			return false;
		}
		return mRightRectF.contains(x, y);
	}
	
	public boolean centerContains(float x, float y){
		System.out.println("center x:"+x);
		if(this.getVisibility() != View.VISIBLE){
			return false;
		}
		return mCenterRectF.contains(x, y);
	}

	public int getStartPosition() {
		return mStartPosition;
	}

	public int getEndPosition() {
		return mEndPosition;
	}

	public int getViewWidth(){
		return mEndPosition - mStartPosition;
	}
	
	/**
	 * 设置选择器起始点
	 * @param start
	 */
	public void setStartPosition(int start) {
		mStartPosition = start;
		mViewWidth = mEndPosition - mStartPosition;
		// 重设左右按钮的区域
		mLeftRectF.set(0, 0, mClickWidth, mViewHeight);
		mRightRectF.set(mViewWidth - mClickWidth, 0, mViewWidth, mViewHeight);
		mCenterRectF.set(mClickWidth, 0, mViewWidth-mClickWidth, mViewHeight);
		requestLayout();
	}

	/**
	 * 设置选择器终止点
	 * @param end
	 */
	public void setEndPosition(int end) {
		mEndPosition = end;
		mViewWidth = mEndPosition - mStartPosition;
		// 重设左右按钮的区域
		mLeftRectF.set(0, 0, mClickWidth, mViewHeight);
		mRightRectF.set(mViewWidth - mClickWidth, 0, mViewWidth, mViewHeight);
		mCenterRectF.set(mClickWidth, 0, mViewWidth-mClickWidth, mViewHeight);
		requestLayout();
	}
	
	/**
	 * 同时设置起始点和终止点
	 * @param start
	 * @param end
	 */
	public void setStartAndEndPosition(int start, int end){
		mStartPosition = start;
		mEndPosition = end;
		mViewWidth = mEndPosition - mStartPosition;
		// 重设左右按钮的区域
		mLeftRectF.set(0, 0, mClickWidth, mViewHeight);
		mRightRectF.set(mViewWidth - mClickWidth, 0, mViewWidth, mViewHeight);
		mCenterRectF.set(mClickWidth, 0, mViewWidth-mClickWidth, mViewHeight);
		requestLayout();
	}
}
