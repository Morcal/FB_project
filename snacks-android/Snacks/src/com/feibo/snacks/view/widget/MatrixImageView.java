package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.feibo.snacks.R;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * -----------------------------------------------------------
 * 版 权 ： BigTiger 版权所有 (c) 2015
 * 作 者 : BigTiger
 * 版 本 ： 1.0
 * 创建日期 ：2015/7/10 18:06
 * 描 述 ：
 * <p>
 * -------------------------------------------------------------
 */
public class MatrixImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final int MODE_NONE = 0x00123;// 默认的触摸模式
    private static final int MODE_DRAG = 0x00321;// 拖拽模式
    private static final int MODE_ZOOM = 0x00132;// 缩放or旋转模式
    private static final float MAX_SCALE = 5.0f;
    private boolean isScaling = false; //是否在自动缩放中

    private int mode;// 当前的触摸模式

    private float preMove = 1F;// 上一次手指移动的距离
    private float saveRotate = 0F;// 保存了的角度值
    private float[] preEventCoor;// 上一次各触摸点的坐标集合

    private PointF start, mid;// 起点、中点对象
    private Matrix currentMatrix, savedMatrix;// 当前和保存了的Matrix对象
    private Context mContext;// Fuck……

    private int viewW,viewH;

    private float mRadius;    //待选矩形半边长
    private PointF mCenter;   //待选矩形中心

    private boolean canRotate = false; //是否可以旋转的标记
    private boolean isNeedTrans = true; //当偏离中心太远的话是否需要重新移回到View中心
    /**
     * drawable 的初始化的边框
     */
    private RectF initRect = new RectF();

    /** 图片的拉伸方式-----沿着View的宽高拉伸,填充整个View **/
    public final static int FIT_VIEW = 0;
    /** 图片的拉伸方式-----沿着裁剪区域拉伸,填充整个将要裁剪的区域 **/
    public final static int FIT_CROP = 1;
    private int mFitType = FIT_CROP;
    /** 限制缩放平移区域 **/
    private RectF restrictRect = new RectF();
    /**
     * 旋转角度
     */
    private float rotate = 0F;
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private boolean isInitSize = false;
    private Paint mPaint;

    private boolean once = true;

    public MatrixImageView(Context context) {
        this(context, null);
    }

    public MatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MatrixImageView);
        mRadius = a.getDimension(R.styleable.MatrixImageView_radius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        a.recycle();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 实例化对象
        currentMatrix = new Matrix();
        savedMatrix = new Matrix();
        start = new PointF();
        mid = new PointF();
        mode = MODE_NONE;

        setScaleType(ScaleType.MATRIX);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.GREEN);


    }
    public void setFitType(int fitType){
        if (fitType != FIT_CROP && fitType != FIT_VIEW){
            mFitType = FIT_VIEW;
        }else {
            mFitType = fitType;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onGlobalLayout();
        super.onDraw(canvas);

    }

    /**
     * 设置是否需要放大(当图片大小小于限定区域时是否需要放大还原到限定区域)
     * @param canRotate
     */
    public void rotateable(boolean canRotate){
        this.canRotate = canRotate;
    }

    /**
     * 设置是否需要平移(当图片移出限定区域时是否需要移回到限定区域)
     * @param isNeedTrans
     */
    public void transable(boolean isNeedTrans){
        this.isNeedTrans = isNeedTrans;
    }
    private long downTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isScaling = false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {// 单点接触屏幕时
                savedMatrix.set(currentMatrix);
                start.set(event.getX(), event.getY());
                mode = MODE_DRAG;
                preEventCoor = null;
                downTime = System.currentTimeMillis();
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {// 第二个点接触屏幕时
                preMove = calSpacing(event);
                if (preMove > 10F) {
                    savedMatrix.set(currentMatrix);
                    calMidPoint(mid, event);
                    mode = MODE_ZOOM;
                }
                preEventCoor = new float[4];
                preEventCoor[0] = event.getX(0);
                preEventCoor[1] = event.getX(1);
                preEventCoor[2] = event.getY(0);
                preEventCoor[3] = event.getY(1);
                saveRotate = calRotation(event);
                break;
            }
            case MotionEvent.ACTION_UP:{// 单点离开屏幕时
                if(mode == MODE_DRAG && isNeedTrans){
                    RectF rect = getMatrixRectF();
                    PointF pointF = computeTrans(rect);
                    if (pointF != null) {
                        startTrans(new PointF(rect.centerX(), rect.centerY()), pointF);
                    }

                }
                preEventCoor = null;
                mode = MODE_NONE;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{// 第二个点离开屏幕时
                RectF rect = getMatrixRectF();
                float limitScale = computeScale(rect);
                if (limitScale < 1.0f){
                    startScale(1.0f, 1.0f / limitScale);
                    startTrans(new PointF(rect.centerX(), rect.centerY()), mCenter);
                }else if (limitScale > MAX_SCALE){
                    startScale(1.0f, MAX_SCALE/limitScale);
                    startTrans(new PointF(rect.centerX(), rect.centerY()), mCenter);
                }else {
                    PointF pointF = computeTrans(rect);
                    if (pointF != null) {
                        startTrans(new PointF(rect.centerX(), rect.centerY()), pointF);
                    }
                }

                mode = MODE_NONE;
                preEventCoor = null;
                break;
            }
            case MotionEvent.ACTION_MOVE: {// 触摸点移动时
                if (mode == MODE_DRAG) {//单点触控拖拽平移
                    currentMatrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    currentMatrix.postTranslate(dx, dy);
                } else if (mode == MODE_ZOOM && event.getPointerCount() == 2) {//两点触控拖放旋转
                    float currentMove = calSpacing(event);
                    currentMatrix.set(savedMatrix);
                    if (currentMove > 10F) {//指尖移动距离大于10F缩放
                        float scale = currentMove / preMove;
                        currentMatrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (canRotate && preEventCoor != null) {//保持两点时旋转
                        rotate = calRotation(event);
                        float r = rotate - saveRotate;
                        currentMatrix.postRotate(r, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                    }
                }
                setImageMatrix(currentMatrix);
                break;
            }
        }
        invalidate();
        return true;
    }

    /**
     * 计算偏移裁剪区域的范围
     * @param curr
     * @return
     */
    private PointF computeTrans(RectF curr){
        float restrictLen = restrictRect.width();

        float transX = 0f;
        float transY = 0f;

        float height = curr.height() + 0.01f;
        float width = curr.width() + 0.01f;

        if (height < restrictLen && width <restrictLen){

            return null;
        }

        if (height < restrictLen){
            if (curr.left > restrictRect.left){
                transX = restrictRect.left - curr.left;
                return new PointF(curr.centerX() + transX, mCenter.y);
            }
            if (curr.right < restrictRect.right){
                transX = restrictRect.right - curr.right;
                return new PointF(curr.centerX() + transX, mCenter.y);
            }
            if (curr.bottom <= restrictRect.top  || curr.top >= restrictRect.bottom){
                return new PointF(mCenter.x, mCenter.y);
            }

        }

        if ( width < restrictLen){
            if (curr.top > restrictRect.top){
                transY = restrictRect.top - curr.top;
                return new PointF(mCenter.x, curr.centerY() + transY);
            }
            if (curr.bottom < restrictRect.bottom){
                transY = restrictRect.bottom - curr.bottom;
                return new PointF(mCenter.x, curr.centerY() + transY);
            }
            if (curr.right <= restrictRect.left  || curr.left >= restrictRect.right){
                return new PointF(mCenter.x, mCenter.y);
            }
        }

        if (height >= restrictLen && width >= restrictLen){
            float l = restrictRect.left -curr.left  ;//l<0
            float t = restrictRect.top - curr.top; //t<0
            float r =  restrictRect.right - curr.right  ; //r>0
            float b =  restrictRect.bottom - curr.bottom ; //b>0

            if(l<0){
                transX = l;
            }
            if(r>0){
                transX = r;
            }

            if(t<0){
                transY = t;
            }
            if( b>0){
                transY = b;
            }
            return new PointF(curr.centerX() + transX, curr.centerY() + transY);
        }
        return null;
    }

    /**
     * 计算缩放
     * @param curr
     * @return
     */
    private float computeScale(RectF curr){
        return Math.max(curr.width() / restrictRect.width(), curr.height() / restrictRect.height());
    }

    /**
     * 计算两个触摸点间的距离
     */
    private float calSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两个触摸点的中点坐标
     */
    private void calMidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 计算旋转角度
     *
     * @param
     * @return 角度值
     */
    private float calRotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        double radius = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radius);
    }

    /**
     * 设置图片裁剪圆的中心
     * @param center
     */
    public void setCenter(PointF center){
        mCenter = center;
    }
    /**
     * 设置图片裁剪圆的半径
     * @param radius
     */
    public void setRadius(float radius){
        mRadius = radius;
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix tempMatrix = currentMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            tempMatrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象
     *
     * @return
     */
    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        int horizontalPadding = (int) ((getWidth() - 2* mRadius)/2 + 0.5f);
        int verticalPadding = (int) ((getHeight() - 2* mRadius)/2 + 0.5f);
        return Bitmap.createBitmap(bitmap, horizontalPadding, verticalPadding, getWidth() - 2 * horizontalPadding,
                getWidth() - 2 * horizontalPadding);
    }

    public final float getScale() {
        currentMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }
    @Override
    public void onGlobalLayout() {
        if (once) {
            initSize();
            once = false;
        }
    }

    public void initSize(){
        mCenter = new PointF();
        viewW = getWidth();
        viewH = getHeight();
        mCenter.x = viewW/2;
        mCenter.y = viewH/2;

        Drawable d = getDrawable();
        if (d == null) {
            return;
        }
        // 拿到图片的宽和高
        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();

        float scale = 1.0f;
        if(dw < viewW && dh < viewH){ //计算缩放比
            scale = Math.min(viewW * 1.0f / dw, viewH * 1.0f / dh);
        }
        //初始化裁剪区域 -- 这里只是简单的将View的中心作为裁剪区域的中心,没有进一步扩展
        restrictRect.left = mCenter.x - mRadius;
        restrictRect.top = mCenter.y - mRadius;
        restrictRect.right = mCenter.x + mRadius;
        restrictRect.bottom = mCenter.y +mRadius;

        currentMatrix.reset();
        currentMatrix.setTranslate((viewW - dw)*1.0f / 2, (viewH - dh)*1.0f / 2);
        currentMatrix.postScale(scale, scale, viewW / 2, viewH / 2);
        setImageMatrix(currentMatrix);

        initRect = getMatrixRectF();
        isInitSize = true;
    }
    public boolean isInitSize(){
        return isInitSize;
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 强
     */
    public void rotateImage(float degree){
        RectF matrixRectF = getMatrixRectF();
        //currentMatrix.postRotate(degree, matrixRectF.centerX(), matrixRectF.centerY());
        //setImageMatrix(currentMatrix);
        startRotate(0, degree, new PointF(matrixRectF.centerX(), matrixRectF.centerY()));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    private float preScale ; //记录缩放前的缩放比
    private PointF preTrans; //记录平以前的中心点
    private float preRotateDegree; // 记录旋转前的角度

    /**
     * 开始缩放动画
     * @param fromScale
     * @param toScale
     */
    private void startScale(float fromScale, float toScale){
        isScaling = true;
        ValueAnimator animator = ValueAnimator.ofFloat(fromScale, toScale);
        animator.setInterpolator(new OvershootInterpolator());
        preScale = fromScale;
        animator.addUpdateListener((ValueAnimator animation) -> {
            Float s = (Float) animation.getAnimatedValue();
            RectF rect = getMatrixRectF();
            currentMatrix.postScale(s/preScale, s/preScale, rect.centerX(), rect.centerY());
            preScale = s;
            setImageMatrix(currentMatrix);
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * 开始平移动画
     * @param pointStart
     * @param pointEnd
     */
    private void startTrans(PointF pointStart, PointF pointEnd){
        preTrans = pointStart;
        ValueAnimator animator = ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                PointF s = (PointF) startValue;
                PointF e = (PointF) endValue;
                return new PointF(s.x + (e.x - s.x) * fraction, s.y + (e.y - s.y) * fraction);
            }
        }, pointStart, pointEnd);

        animator.addUpdateListener((ValueAnimator animation) ->{
            PointF point = (PointF) animation.getAnimatedValue();
            currentMatrix.postTranslate(point.x - preTrans.x, point.y - preTrans.y);
            setImageMatrix(currentMatrix);
            preTrans = point;
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * 旋转图片
     * @param fromDegree 开始旋转的教徒
     * @param toDegree
     * @param rotateCenter 旋转中心
     */
    private void startRotate(float fromDegree, float toDegree, PointF rotateCenter){
        preRotateDegree =  fromDegree;
        ValueAnimator animator = ValueAnimator.ofFloat(fromDegree, toDegree);
        animator.addUpdateListener((ValueAnimator animation) -> {
            Float degree = (Float) animation.getAnimatedValue();
            currentMatrix.postRotate(degree - preRotateDegree, rotateCenter.x, rotateCenter.y);
            setImageMatrix(currentMatrix);
            preRotateDegree = degree;
        });
        animator.setDuration(300);
        animator.start();
    }

}