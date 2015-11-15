package com.feibo.joke.video.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.joke.R;

/**
 * 单手对图片进行缩放，旋转，平移操作，详情请查看
 * 
 * @blog http://blog.csdn.net/xiaanming/article/details/42833893
 * 
 * @author xiaanming
 * 
 */
public class SingleTouchView extends View {

	public static final float MAX_SCALE = 10.0f;
	public static final float MIN_SCALE = 0.3f;

	/**
	 * 控制缩放，旋转图标所在四个点得位置
	 */
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;

	/**
	 * 一些默认的常量
	 */
	public static final int DEFAULT_FRAME_PADDING = 8;
	public static final int DEFAULT_FRAME_WIDTH = 2;
	public static final int DEFAULT_FRAME_COLOR = Color.WHITE;
	public static final float DEFAULT_SCALE = 1.0f;
	public static final float DEFAULT_DEGREE = 0;
	public static final int DEFAULT_CONTROL_LOCATION = RIGHT_TOP;
	public static final boolean DEFAULT_EDITABLE = true;

	/**
	 * 状态
	 */
	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_ROTATE_ZOOM = 2;
	public static final int STATUS_CLOSE = 3;

	private Bitmap mBitmap;

	private PointF mCenterPoint;

	/**
	 * View的宽度和高度，随着图片的旋转而变化(不包括控制旋转，缩放图片的宽高)
	 */
	private int mViewWidth, mViewHeight;

	/**
	 * 图片的旋转角度
	 */
	private float mDegree = DEFAULT_DEGREE;

	/**
	 * 图片的缩放比例
	 */
	private float mScale = DEFAULT_SCALE;
	private float mScaleMin = MIN_SCALE;
	private float mScaleMax = MAX_SCALE;

	/**
	 * 用于缩放，旋转，平移的矩阵
	 */
	private Matrix mMatrix = new Matrix();

	/**
	 * 边距，用来控制该View的位置
	 */
	private int mViewPaddingLeft;
	private int mViewPaddingTop;

	/**
	 * 图片四个点坐标
	 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;

	private Point mControlPoint = new Point();
	private Point mClosePoint = new Point();

	private int mLimitLeft;
	private int mLimitRight;
	private int mLimitTop;
	private int mLimitBottom;

	/**
	 * 用于缩放，旋转的图标
	 */
	private Drawable mControlDrawable;
	private Drawable mCloseDrawable;
	private Drawable mCloseNormalDrawable;
	private Drawable mClosePressedDrawable;

	private int mDrawableWidth;
	private int mDrawableHeight;

	private Path mPath = new Path();
	private Paint mPaint;

	private int mStatus = STATUS_INIT;

	private int mFramePadding = DEFAULT_FRAME_PADDING;
	private int mFrameColor = DEFAULT_FRAME_COLOR;
	private int mFrameWidth = DEFAULT_FRAME_WIDTH;

	/**
	 * 是否处于可以缩放，平移，旋转状态
	 */
	private boolean isEditable = DEFAULT_EDITABLE;

	private DisplayMetrics metrics;

	private PointF mPreMovePointF = new PointF();
	private PointF mCurMovePointF = new PointF();

	/**
	 * 图片在旋转时x方向的偏移量
	 */
	private int mOffsetX;

	/**
	 * 图片在旋转时y方向的偏移量
	 */
	private int mOffsetY;

	/**
	 * 控制图标所在的位置（比如左上，右上，左下，右下）
	 */
	private int controlLocation = DEFAULT_CONTROL_LOCATION;

	private Rect mLocationRect;
	
	/**
	 * 真实的可控范围，旋转之前的区域
	 */
	private RectF mRealRectF;
	
	private OnMarkEditListener mOnEditListener;

	public SingleTouchView(Context context) {
		super(context);
		init();
	}

	private void init() {
		metrics = getContext().getResources().getDisplayMetrics();
		mFramePadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_PADDING, metrics);
		mFrameWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_WIDTH, metrics);

		mFrameColor = DEFAULT_FRAME_COLOR;
		mScale = DEFAULT_SCALE;
		mDegree = DEFAULT_DEGREE;
		controlLocation = DEFAULT_CONTROL_LOCATION;
		isEditable = DEFAULT_EDITABLE;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mFrameColor);
		mPaint.setStrokeWidth(mFrameWidth);
		mPaint.setStyle(Style.STROKE);

		mControlDrawable = getContext().getResources().getDrawable(
				R.drawable.btn_rotation);

		mCloseNormalDrawable = getContext().getResources().getDrawable(
				R.drawable.btn_delete_normal);
		mClosePressedDrawable = getContext().getResources().getDrawable(
				R.drawable.btn_delete_pressed);
		mCloseDrawable = mCloseNormalDrawable;

		mDrawableWidth = mControlDrawable.getIntrinsicWidth();
		mDrawableHeight = mControlDrawable.getIntrinsicHeight();

		mLocationRect = new Rect();

		mRealRectF = new RectF();
		// transformDraw();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 获取SingleTouchView所在父布局的中心点
		ViewGroup mViewGroup = (ViewGroup) getParent();
		int parentWidth = mViewGroup.getWidth();
		int parentHeight = mViewGroup.getHeight();

		if (mCenterPoint == null) {
			mCenterPoint = new PointF();
			mCenterPoint.set(parentWidth / 2, parentHeight / 2);
		}
		// 边界限制
		mLimitLeft = (int) (parentWidth * 0.16);
		mLimitTop = (int) (parentHeight * 0.16);
		mLimitRight = parentWidth - mLimitLeft;
		mLimitBottom = parentHeight - mLimitTop;

		// 计算View的宽度和高度
		int bitmapWidth = (int) (mBitmap.getWidth() * mScale);
		int bitmapHeight = (int) (mBitmap.getHeight() * mScale);

		computeRect(-mFramePadding, -mFramePadding,
				bitmapWidth + mFramePadding, bitmapHeight + mFramePadding,
				mDegree);

		int actualWidth = mViewWidth + mDrawableWidth;
		int actualHeight = mViewHeight + mDrawableHeight;

		int newPaddingLeft = (int) (mCenterPoint.x - actualWidth / 2);
		int newPaddingTop = (int) (mCenterPoint.y - actualHeight / 2);

		// 记录位置坐标
		mLocationRect.set(newPaddingLeft, newPaddingTop, newPaddingLeft
				+ actualWidth, newPaddingTop + actualHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 每次draw之前调整View的位置和大小
		adjustLayout();

		super.onDraw(canvas);

		if (mBitmap == null)
			return;

		canvas.drawBitmap(mBitmap, mMatrix, null);

		// 处于可编辑状态才画边框和控制图标
		if (isEditable) {
			mPath.reset();
			mPath.moveTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);
			mPath.lineTo(mRBPoint.x, mRBPoint.y);
			mPath.lineTo(mLBPoint.x, mLBPoint.y);
			mPath.lineTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);

			canvas.drawPath(mPath, mPaint);
			// mFrameRectF.set(mLTPoint.y, mLTPoint.x, mRBPoint.x, mRBPoint.y);
			// canvas.drawRoundRect(mFrameRectF, 6, 6, mPaint);
			// 画旋转, 缩放图标

			mControlDrawable.setBounds(mControlPoint.x - mDrawableWidth / 2,
					mControlPoint.y - mDrawableHeight / 2, mControlPoint.x
							+ mDrawableWidth / 2, mControlPoint.y
							+ mDrawableHeight / 2);
			mControlDrawable.draw(canvas);

			mCloseDrawable.setBounds(mLTPoint.x - mDrawableWidth / 2,
					mLTPoint.y - mDrawableHeight / 2, mLTPoint.x
							+ mDrawableWidth / 2, mLTPoint.y + mDrawableHeight
							/ 2);
			mCloseDrawable.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			if(!checkInArea(event.getX(), event.getY())){
				return false;
			}
			
			if (!isEditable) {
				isEditable = true;
				if (mOnEditListener != null) {
					mOnEditListener.onEditable(this, true);
				}
				invalidate();
				return true;
			}
			
			mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY()
					+ mViewPaddingTop);

			mStatus = judgeStatus(event.getX(), event.getY());

			break;
		case MotionEvent.ACTION_UP:
			if (mStatus == STATUS_CLOSE) {
				mCurMovePointF.set(event.getX(), event.getY());

				float distanceToClose = distance4PointF(mCurMovePointF,
						new PointF(mClosePoint));

				System.out.println("distance to close:" + distanceToClose);

				if (distanceToClose < Math.min(mDrawableWidth / 2,
						mDrawableHeight / 2)) {
					mCloseDrawable = mClosePressedDrawable;
					handleClose();
					mStatus = STATUS_INIT;
					return true;
				}

				mCloseDrawable = mCloseNormalDrawable;
				invalidate();
			}
			mStatus = STATUS_INIT;
			break;
		case MotionEvent.ACTION_MOVE:
			mCurMovePointF.set(event.getX() + mViewPaddingLeft, event.getY()
					+ mViewPaddingTop);
			if (mStatus == STATUS_ROTATE_ZOOM) {
				float scale = 1f;

				int halfBitmapWidth = mBitmap.getWidth() / 2;
				int halfBitmapHeight = mBitmap.getHeight() / 2;

				// 图片某个点到图片中心的距离
				float bitmapToCenterDistance = FloatMath
						.sqrt(halfBitmapWidth * halfBitmapWidth
								+ halfBitmapHeight * halfBitmapHeight);

				// 移动的点到图片中心的距离
				float moveToCenterDistance = distance4PointF(mCenterPoint,
						mCurMovePointF);

				// 计算缩放比例
				scale = moveToCenterDistance / bitmapToCenterDistance;

				// 缩放比例的界限判断
				if (scale <= mScaleMin) {
					scale = mScaleMin;
				} else if (scale >= mScaleMax) {
					scale = mScaleMax;
				}

				// 角度
				double a = distance4PointF(mCenterPoint, mPreMovePointF);
				double b = distance4PointF(mPreMovePointF, mCurMovePointF);
				double c = distance4PointF(mCenterPoint, mCurMovePointF);

				double cosb = (a * a + c * c - b * b) / (2 * a * c);

				if (cosb >= 1) {
					cosb = 1f;
				}

				double radian = Math.acos(cosb);
				float newDegree = (float) radianToDegree(radian);

				// center -> proMove的向量， 我们使用PointF来实现
				PointF centerToProMove = new PointF(
						(mPreMovePointF.x - mCenterPoint.x),
						(mPreMovePointF.y - mCenterPoint.y));

				// center -> curMove 的向量
				PointF centerToCurMove = new PointF(
						(mCurMovePointF.x - mCenterPoint.x),
						(mCurMovePointF.y - mCenterPoint.y));

				// 向量叉乘结果, 如果结果为负数， 表示为逆时针， 结果为正数表示顺时针
				float result = centerToProMove.x * centerToCurMove.y
						- centerToProMove.y * centerToCurMove.x;

				if (result < 0) {
					newDegree = -newDegree;
				}

				mDegree = mDegree + newDegree;
				mScale = scale;

				transformDraw();
			} else if (mStatus == STATUS_DRAG) {
				// 修改中心点
				mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
				mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;

				adjustLayout();
			} else if (mStatus == STATUS_CLOSE) {
				invalidate();
			}
			mPreMovePointF.set(mCurMovePointF);
			break;
		}
		return true;
	}

	
	private boolean checkInArea(float x1, float y1){
		// 计算出真实的矩阵
		float width = (float) Math.sqrt(Math.pow(mRTPoint.x - mLTPoint.x, 2)+Math.pow(mRTPoint.y - mLTPoint.y, 2)) + mDrawableWidth;
		float height = width;
		
		float k= (float)(Math.toRadians(mDegree)); 
		float x0 = getWidth()/(float)2;
		float y0 = getHeight()/(float)2;
		
		float x2=(float)((x1-x0)*Math.cos(k) +(y1-y0)*Math.sin(k)+x0); 
		float y2=(float)(-(x1-x0)*Math.sin(k) + (y1-y0)*Math.cos(k)+y0);
		
//		System.out.println("degree:"+mDegree);
//		System.out.println("x1,y1:"+x1+","+y1);
//		System.out.println("x0,y0:"+x0+","+y0);
//		System.out.println("x2,y2:"+x2+","+y2);
		
		mRealRectF.set(x0 - width/2, y0 - height/2,x0 + width/2, y0 + height/2);
		if(mRealRectF.contains(x2, y2)){
			return true;
		}
		
		return false;
				
	}
	
	/**
	 * 调整View的大小，位置
	 */
	private void adjustLayout() {

		int actualWidth = mViewWidth + mDrawableWidth;
		int actualHeight = mViewHeight + mDrawableHeight;

		// 边界处理
		if(mLimitLeft!=0 && mLimitRight!=0 && mLimitTop!=0 && mLimitBottom !=0){
			handleLimit();
		}
		
		
		int newPaddingLeft = (int) (mCenterPoint.x - actualWidth / 2);
		int newPaddingTop = (int) (mCenterPoint.y - actualHeight / 2);

		if (mViewPaddingLeft != newPaddingLeft
				|| mViewPaddingTop != newPaddingTop) {

			mViewPaddingLeft = newPaddingLeft;
			mViewPaddingTop = newPaddingTop;

			mLocationRect.left = mViewPaddingLeft;
			mLocationRect.right = mViewPaddingLeft + mViewWidth
					+ mDrawableWidth;
			mLocationRect.top = mViewPaddingTop;
			mLocationRect.bottom = mViewPaddingTop + mViewHeight
					+ mDrawableHeight;

			layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth,
					newPaddingTop + actualHeight);
		}
	}

	/**
	 * 处理贴图边界
	 */
	private void handleLimit() {
		// 计算X坐标最大的值和最小的值
		int maxCoordinateX = getMaxValue(mLTPoint.x, mRTPoint.x, mRBPoint.x,
				mLBPoint.x);
		int minCoordinateX = getMinValue(mLTPoint.x, mRTPoint.x, mRBPoint.x,
				mLBPoint.x);

		// 计算Y坐标最大的值和最小的值
		int maxCoordinateY = getMaxValue(mLTPoint.y, mRTPoint.y, mRBPoint.y,
				mLBPoint.y);
		int minCoordinateY = getMinValue(mLTPoint.y, mRTPoint.y, mRBPoint.y,
				mLBPoint.y);

		// View中心点的坐标
		Point viewCenterPoint = new Point(
				(maxCoordinateX + minCoordinateX) / 2,
				(maxCoordinateY + minCoordinateY) / 2);

		// 计算在父坐标的偏移
		int offsetX = (int) (mCenterPoint.x - viewCenterPoint.x);
		int offsetY = (int) (mCenterPoint.y - viewCenterPoint.y);

		int minX = minCoordinateX + offsetX;
		int maxX = maxCoordinateX + offsetX;
		int minY = minCoordinateY + offsetY;
		int maxY = maxCoordinateY + offsetY;

		// x轴边界处理
		if (minX > mLimitRight) {
			// 超出右边界, 向左移动
			mCenterPoint.x -= (minX - mLimitRight);
		} else if (maxX < mLimitLeft) {
			// 超出左边界， 向右移动
			mCenterPoint.x += (mLimitLeft - maxX);
		}

		// y轴边界处理
		if (minY > mLimitBottom) {
			// 超出下边界， 向上移动
			mCenterPoint.y -= (minY - mLimitBottom);
		} else if (maxY < mLimitTop) {
			// 超出上边界， 向下移动
			mCenterPoint.y += (mLimitTop - maxY);
		}
	}

	public Rect getViewLocation() {
		return mLocationRect;
	}

	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw() {
		int bitmapWidth = (int) (mBitmap.getWidth() * mScale);
		int bitmapHeight = (int) (mBitmap.getHeight() * mScale);
		computeRect(-mFramePadding, -mFramePadding,
				bitmapWidth + mFramePadding, bitmapHeight + mFramePadding,
				mDegree);

		// 设置缩放比例
		mMatrix.setScale(mScale, mScale);
		// 绕着图片中心进行旋转
		mMatrix.postRotate(mDegree % 360, bitmapWidth / 2, bitmapHeight / 2);
		// 设置画该图片的起始点
		mMatrix.postTranslate(mOffsetX + mDrawableWidth / 2, mOffsetY
				+ mDrawableHeight / 2);

		invalidate();
	}

	private void handleClose() {
		mOnEditListener.onClose(this);
	}

	/**
	 * 获取四个点和View的大小
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param degree
	 */
	private void computeRect(int left, int top, int right, int bottom,
			float degree) {
		Point lt = new Point(left, top);
		Point rt = new Point(right, top);
		Point rb = new Point(right, bottom);
		Point lb = new Point(left, bottom);
		Point cp = new Point((left + right) / 2, (top + bottom) / 2);
		mLTPoint = obtainRoationPoint(cp, lt, degree);
		mRTPoint = obtainRoationPoint(cp, rt, degree);
		mRBPoint = obtainRoationPoint(cp, rb, degree);
		mLBPoint = obtainRoationPoint(cp, lb, degree);

		// 计算X坐标最大的值和最小的值
		int maxCoordinateX = getMaxValue(mLTPoint.x, mRTPoint.x, mRBPoint.x,
				mLBPoint.x);
		int minCoordinateX = getMinValue(mLTPoint.x, mRTPoint.x, mRBPoint.x,
				mLBPoint.x);

		mViewWidth = maxCoordinateX - minCoordinateX;

		// 计算Y坐标最大的值和最小的值
		int maxCoordinateY = getMaxValue(mLTPoint.y, mRTPoint.y, mRBPoint.y,
				mLBPoint.y);
		int minCoordinateY = getMinValue(mLTPoint.y, mRTPoint.y, mRBPoint.y,
				mLBPoint.y);

		mViewHeight = maxCoordinateY - minCoordinateY;

		// View中心点的坐标
		Point viewCenterPoint = new Point(
				(maxCoordinateX + minCoordinateX) / 2,
				(maxCoordinateY + minCoordinateY) / 2);

		mOffsetX = mViewWidth / 2 - viewCenterPoint.x;
		mOffsetY = mViewHeight / 2 - viewCenterPoint.y;

		int halfDrawableWidth = mDrawableWidth / 2;
		int halfDrawableHeight = mDrawableHeight / 2;

		// 将Bitmap的四个点的X的坐标移动offsetX + halfDrawableWidth
		mLTPoint.x += (mOffsetX + halfDrawableWidth);
		mRTPoint.x += (mOffsetX + halfDrawableWidth);
		mRBPoint.x += (mOffsetX + halfDrawableWidth);
		mLBPoint.x += (mOffsetX + halfDrawableWidth);

		// 将Bitmap的四个点的Y坐标移动offsetY + halfDrawableHeight
		mLTPoint.y += (mOffsetY + halfDrawableHeight);
		mRTPoint.y += (mOffsetY + halfDrawableHeight);
		mRBPoint.y += (mOffsetY + halfDrawableHeight);
		mLBPoint.y += (mOffsetY + halfDrawableHeight);

		mControlPoint = locationToPoint(controlLocation);
		mClosePoint = locationToPoint(LEFT_TOP);
	}

	/**
	 * 根据位置判断控制图标处于那个点
	 * 
	 * @return
	 */
	private Point locationToPoint(int location) {
		switch (location) {
		case LEFT_TOP:
			return mLTPoint;
		case RIGHT_TOP:
			return mRTPoint;
		case RIGHT_BOTTOM:
			return mRBPoint;
		case LEFT_BOTTOM:
			return mLBPoint;
		}
		return mLTPoint;
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int judgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF controlPointF = new PointF(mControlPoint);
		PointF closePointF = new PointF(mClosePoint);

		// 点击的点到控制旋转，缩放点的距离
		float distanceToControl = distance4PointF(touchPoint, controlPointF);

		// 如果两者之间的距离小于 控制图标的宽度，高度的最小值，则认为点中了控制图标
		if (distanceToControl < Math.min(mDrawableWidth / 2,
				mDrawableHeight / 2)) {
			return STATUS_ROTATE_ZOOM;
		}

		float distanceToClose = distance4PointF(touchPoint, closePointF);

		if (distanceToClose < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			mCloseDrawable = mClosePressedDrawable;
			return STATUS_CLOSE;
		}

		return STATUS_DRAG;
	}

	/**
	 * 两个点之间的距离
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return FloatMath.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 设置旋转图
	 * 
	 * @param bitmap
	 */
	public void setImageBitamp(Bitmap bitmap) {
		this.mBitmap = bitmap;
		transformDraw();
	}

	/**
	 * 设置旋转图
	 * 
	 * @param drawable
	 */
	public void setImageDrawable(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			this.mBitmap = bd.getBitmap();

			transformDraw();
		} else {
			throw new NotSupportedException(
					"SingleTouchView not support this Drawable " + drawable);
		}
	}

	/**
	 * 根据id设置旋转图
	 * 
	 * @param resId
	 */
	public void setImageResource(int resId) {
		Drawable drawable = getContext().getResources().getDrawable(resId);
		setImageDrawable(drawable);
	}

	public float getImageDegree() {
		return mDegree;
	}

	/**
	 * 设置图片旋转角度
	 * 
	 * @param degree
	 */
	public void setImageDegree(float degree) {
		if (this.mDegree != degree) {
			this.mDegree = degree;
			transformDraw();
		}
	}

	public float getImageScale() {
		return mScale;
	}

	/**
	 * 设置图片缩放比例
	 * 
	 * @param scale
	 */
	public void setImageScale(float scale) {
		if (this.mScale != scale) {
			this.mScale = scale;
			transformDraw();
		}
	}

	/**
	 * 获取内容位图
	 * 
	 * @return
	 */
	public Bitmap getBitmap() {
		return mBitmap;
	}

	public Drawable getControlDrawable() {
		return mControlDrawable;
	}

	/**
	 * 设置控制图标
	 * 
	 * @param drawable
	 */
	public void setControlDrawable(Drawable drawable) {
		this.mControlDrawable = drawable;
		mDrawableWidth = drawable.getIntrinsicWidth();
		mDrawableHeight = drawable.getIntrinsicHeight();
		transformDraw();
	}

	public int getFramePadding() {
		return mFramePadding;
	}

	public void setFramePadding(int framePadding) {
		if (this.mFramePadding == framePadding)
			return;
		this.mFramePadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, framePadding, metrics);
		transformDraw();
	}

	public int getFrameColor() {
		return mFrameColor;
	}

	public void setFrameColor(int frameColor) {
		if (this.mFrameColor == frameColor)
			return;
		this.mFrameColor = frameColor;
		mPaint.setColor(frameColor);
		invalidate();
	}

	public int getFrameWidth() {
		return mFrameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		if (this.mFrameWidth == frameWidth)
			return;
		this.mFrameWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, frameWidth, metrics);
		mPaint.setStrokeWidth(frameWidth);
		invalidate();
	}

	/**
	 * 设置控制图标的位置, 设置的值只能选择LEFT_TOP ，RIGHT_TOP， RIGHT_BOTTOM，LEFT_BOTTOM
	 * 
	 * @param controlLocation
	 */
	public void setControlLocation(int location) {
		if (this.controlLocation == location)
			return;
		this.controlLocation = location;
		transformDraw();
	}

	public void setMinImageScale(float imageScale) {
		mScaleMin = imageScale;
	}

	public void setMaxImageScale(float imageScale) {
		mScaleMax = imageScale;
	}

	public int getControlLocation() {
		return controlLocation;
	}

	public PointF getCenterPoint() {
		return mCenterPoint;
	}

	/**
	 * 设置图片中心点位置，相对于父布局而言
	 * 
	 * @param mCenterPoint
	 */
	public void setCenterPoint(PointF mCenterPoint) {
		this.mCenterPoint = mCenterPoint;
		adjustLayout();
	}

	public void initCenterPoint(PointF centerPoint) {
		this.mCenterPoint = centerPoint;
	}

	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * 设置是否处于可缩放，平移，旋转状态
	 * 
	 * @param isEditable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		invalidate();
	}

	public void setOnEditListener(OnMarkEditListener onEditListener) {
		this.mOnEditListener = onEditListener;
	}

	/**
	 * 获取旋转某个角度之后的点
	 * 
	 * @param viewCenter
	 * @param source
	 * @param degree
	 * @return
	 */
	public static Point obtainRoationPoint(Point center, Point source,
			float degree) {
		// 两者之间的距离
		Point disPoint = new Point();
		disPoint.x = source.x - center.x;
		disPoint.y = source.y - center.y;

		// 没旋转之前的弧度
		double originRadian = 0;

		// 没旋转之前的角度
		double originDegree = 0;

		// 旋转之后的角度
		double resultDegree = 0;

		// 旋转之后的弧度
		double resultRadian = 0;

		// 经过旋转之后点的坐标
		Point resultPoint = new Point();

		double distance = Math.sqrt(disPoint.x * disPoint.x + disPoint.y
				* disPoint.y);
		if (disPoint.x == 0 && disPoint.y == 0) {
			return center;
			// 第一象限
		} else if (disPoint.x >= 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.y / distance);

			// 第二象限
		} else if (disPoint.x < 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.x) / distance);
			originRadian = originRadian + Math.PI / 2;

			// 第三象限
		} else if (disPoint.x < 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.y) / distance);
			originRadian = originRadian + Math.PI;
		} else if (disPoint.x >= 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.x / distance);
			originRadian = originRadian + Math.PI * 3 / 2;
		}

		// 弧度换算成角度
		originDegree = radianToDegree(originRadian);
		resultDegree = originDegree + degree;

		// 角度转弧度
		resultRadian = degreeToRadian(resultDegree);

		resultPoint.x = (int) Math.round(distance * Math.cos(resultRadian));
		resultPoint.y = (int) Math.round(distance * Math.sin(resultRadian));
		resultPoint.x += center.x;
		resultPoint.y += center.y;

		return resultPoint;
	}

	/**
	 * 弧度换算成角度
	 * 
	 * @return
	 */
	public static double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}

	/**
	 * 角度换算成弧度
	 * 
	 * @param degree
	 * @return
	 */
	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180;
	}

	/**
	 * 获取变长参数最大的值
	 * 
	 * @param array
	 * @return
	 */
	public static int getMaxValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(list.size() - 1);
	}

	/**
	 * 获取变长参数最大的值
	 * 
	 * @param array
	 * @return
	 */
	public static int getMinValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(0);
	}

	public static interface OnMarkEditListener {
		void onEditable(View view, boolean editable);

		void onClose(View view);
	}

	public static class NotSupportedException extends RuntimeException {
		private static final long serialVersionUID = 1674773263868453754L;

		public NotSupportedException() {
			super();
		}

		public NotSupportedException(String detailMessage) {
			super(detailMessage);
		}

	}

}
