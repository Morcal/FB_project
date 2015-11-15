package com.feibo.joke.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.feibo.joke.R;



/**
 * 
 * @author Diqing Li
 */
public class LToggleButton extends View implements OnTouchListener {
    public static final int STATE_LEFT = 1;
    public static final int STATE_RIGHT = 2;
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_DOWN = 1;
    private static final int TOUCH_STATE_UP = 2;
    private static final int SCROLL_DURATION = 700;

    private int mState;
    private int mTouchState;
    private float mCircleSize;
    private Paint mCirclePaint;
    private Paint mBgPaint;
    private CircleItem mCircleItem;

    private int mLeftColor;
    private int mRightColor;
    private int mRollColor;

    private float mLastMotionX;
    private RectF mBgRectF;

    private long mDownTime;

    private Scroller mScroller;
    private ColorInterpolator mColorInterpolator;

    private OnStateChangeListener mStateChangeListener;

    public LToggleButton(Context context) {
        super(context);
        init(context, null);
    }

    public LToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public LToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.LToggleButton);
        mLeftColor = typedArray.getColor(R.styleable.LToggleButton_leftBackground, 0xffd9d9d9);
        mRightColor = typedArray.getColor(R.styleable.LToggleButton_rightBackground, 0xffeec900);
        typedArray.recycle();

        setClickable(true);
        setOnTouchListener(this);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.WHITE);
        mCircleItem = new CircleItem(0, 0);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mLeftColor);
        mBgRectF = new RectF();

        mRollColor = mLeftColor;
        mColorInterpolator = new ColorInterpolator(mLeftColor, mRightColor);
        mTouchState = TOUCH_STATE_NONE;

        mScroller = new Scroller(context, new Interpolator() {

            @Override
            public float getInterpolation(float t) {
                t -= 1;
                return t * t * t * t * t + 1;
            }
        });

        switchState(STATE_LEFT);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mCircleSize = heightSize - getPaddingTop() - getPaddingBottom();
        mCircleItem.rollStartX = 0;
        mCircleItem.rollEndX = widthSize - heightSize;
        mCircleItem.centerRollX = (widthSize - heightSize) / 2;
        mBgRectF.set(0, 0, widthSize, heightSize);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        mCircleItem.posX = height / 2.0f;
        mCircleItem.posY = height / 2.0f;
        if (mState == STATE_RIGHT) {
            rollTo(-mCircleItem.rollEndX, 0);
        } else {
            rollTo(0, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBgPaint.setColor(mRollColor);
        canvas.drawRoundRect(mBgRectF, getHeight() / 2.0f, getHeight() / 2.0f, mBgPaint);
        canvas.drawCircle(mCircleItem.posX, mCircleItem.posY, mCircleSize / 2, mCirclePaint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mTouchState = TOUCH_STATE_DOWN;
            mLastMotionX = event.getX();
            mDownTime = System.currentTimeMillis();
            return true;
        case MotionEvent.ACTION_MOVE:
            if (checkToRoll(event)) {
                int xDiff = (int) (event.getX() - mLastMotionX);
                mLastMotionX = event.getX();
                rollBy(xDiff);
            }
            break;
        case MotionEvent.ACTION_UP:
            mTouchState = TOUCH_STATE_UP;
//            if (mScroller.computeScrollOffset()) {
//                break;
//            }
            rollEnd();
            break;
        default:
            break;
        }
        return true;
    }

    private boolean checkToRoll(MotionEvent event) {
        if (event.getY() < 0 || event.getY() > getHeight()) {
            rollEnd();
            return false;
        }
        return true;
    }

    private void rollBy(int diff) {
        int rollX = -getScrollX();
        int offset = diff;
        if (rollX + diff < mCircleItem.rollStartX) {
            offset = (int) (mCircleItem.rollStartX - rollX);
        }
        if (rollX + diff > mCircleItem.rollEndX) {
            offset = (int) (mCircleItem.rollEndX - rollX);
        }
        mBgRectF.offset(-offset, 0);
        scrollBy(-offset, 0);
        mRollColor = mColorInterpolator.getColor(Math.abs(getScrollX())
                / (mCircleItem.rollEndX - mCircleItem.rollStartX));
    }

    private void rollTo(float newLeft, float newTop) {
        mBgRectF.offsetTo(newLeft, newTop);
        scrollTo((int) newLeft, (int) newTop);
        mRollColor = mColorInterpolator.getColor(Math.abs(getScrollX())
                / (mCircleItem.rollEndX - mCircleItem.rollStartX));
    }

    private void rollEnd() {
        // 处理点击事件
        if (System.currentTimeMillis() - mDownTime < ViewConfiguration.getTapTimeout()) {
            toggle();
            return;
        }

        // 滑动结束
        int rollX = -getScrollX();
        boolean bSlideLeft = rollX < mCircleItem.centerRollX ? true : false;
        if (bSlideLeft) {
            rollTo(0, 0);
        } else {
            rollTo(-mCircleItem.rollEndX, 0);
        }
        invalidate();
    }

    private void smoothRollTo(float newLeft, float newTop) {
        int dx = (int) (newLeft - getScrollX());
        int dy = (int) (newTop - getScrollY());
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, SCROLL_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            rollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            return;
        }

        if (Math.abs(getScrollX()) == mCircleItem.rollStartX && mTouchState == TOUCH_STATE_UP) {
            switchState(STATE_LEFT);
            mTouchState = TOUCH_STATE_NONE;

            if (mStateChangeListener != null) {
                mStateChangeListener.onStateChange(mState == STATE_RIGHT);
            }
            return;
        }

        if (Math.abs(getScrollX()) == mCircleItem.rollEndX && mTouchState == TOUCH_STATE_UP) {
            switchState(STATE_RIGHT);
            mTouchState = TOUCH_STATE_NONE;
            if (mStateChangeListener != null) {
                mStateChangeListener.onStateChange(mState == STATE_RIGHT);
            }
            return;
        }
    }

    private void switchState(int state) {
        mState = state;
    }

    public void setToggleState(int state) {
        switch (state) {
        case STATE_LEFT:
            // rollTo(0, 0);
            mTouchState = TOUCH_STATE_UP;
            smoothRollTo(0, 0);
            switchState(state);
            break;
        case STATE_RIGHT:
            // rollTo(-mCircleItem.rollEndX, 0);
            mTouchState = TOUCH_STATE_UP;
            smoothRollTo(-mCircleItem.rollEndX, 0);
            switchState(state);
            break;
        default:
            throw new IllegalArgumentException("ToggleButton setToggleState 参数state不符合要求");
        }
    }

    public void setToggleState(Boolean open) {
        setToggleState(open ? STATE_RIGHT : STATE_LEFT);
    }

    public boolean getToggleState() {
        return mState == STATE_RIGHT;
    }

    public void initState(boolean open) {
        switchState(open ? STATE_RIGHT : STATE_LEFT);
    }
    
    public boolean isOpen(){
        return mState == STATE_RIGHT;
    }
    
    public void toggle(){
        setToggleState(! (mState == STATE_RIGHT));
    }
    
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        mStateChangeListener = listener;
    }

    public class CircleItem {
        public float posX;
        public float posY;
        public float rollStartX;
        public float rollEndX;
        public float centerRollX;

        public CircleItem(float pX, float pY) {
            posX = pX;
            posY = pY;
        }
    }

    public static interface OnStateChangeListener {
        void onStateChange(boolean open);
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
                mInterRGB[i] = (int) (mStartRGB[i] + (mEndRGB[i] - mStartRGB[i]) * interpolator);
            }
            return Color.argb(mInterRGB[3], mInterRGB[2], mInterRGB[1], mInterRGB[0]);
        }
    }

}
