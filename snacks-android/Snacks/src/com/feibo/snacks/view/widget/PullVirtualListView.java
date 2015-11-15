package com.feibo.snacks.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

public class PullVirtualListView extends RelativeLayout {
    public static final int MODE_PULL_NORMAL = 0;
    public static final int MODE_PULL_REFRESH = 1;
    private static final float PULL_RESISTANCE = 2;
    private static final int BOUNCE_ANIMATION_DURATION = 500;
    private static final int BOUNCE_ANIMATION_DELAY = 100;
    private static final float BOUNCE_OVERSHOOT_TENSION = 2f;
    private boolean isOutOfList = false;
    private float ox, oy;
    private float om;
    private int scrollY = 0;
    private int scrollD = 0;
    private Scroller mScroller;
    private VirtualListView mListView;

    public PullVirtualListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PullVirtualListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullVirtualListView(Context context) {
        super(context);
        init();
    }

    public void setView(View view) {
        mListView.setView(view);
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        mScroller = new Scroller(getContext(), new OvershootInterpolator(
                BOUNCE_OVERSHOOT_TENSION));

        View child = getChildCount() > 0 ? getChildAt(0) : null;
        removeAllViews();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mListView = new VirtualListView(getContext());
        mListView.setLayoutParams(params);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setVerticalFadingEdgeEnabled(false);
        mListView.setHorizontalFadingEdgeEnabled(false);
        addView(mListView);
        if (child != null) {
            mListView.addView(child);
        }
    }

    @Override
    public void computeScroll() {
        if (isInEditMode()) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            int y = mScroller.getCurrY();
            scrollY = y;
            scrollTo(0, y);
            postInvalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            ox = event.getX();
            oy = event.getY();
            om = 0;
            isOutOfList = false;
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (isOutOfList) {
                isOutOfList = false;
                if (scrollY != 0) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScroller.startScroll(0, scrollY, 0, -scrollY,
                                    BOUNCE_ANIMATION_DURATION);
                            postInvalidate();
                        }
                    }, BOUNCE_ANIMATION_DELAY);
                }
                return true;
            }
            om = 0;
            break;
        case MotionEvent.ACTION_MOVE:
            if (om == 1) {
                break;
            }
            float mx = event.getX();
            float my = event.getY();
            float dis = oy - my;
            float diffY = Math.abs(dis);
            float diffX = Math.abs(ox - mx);
            if (om == 0 && (diffY > 5 || diffX > 1)) {
                if (diffY < diffX) {
                    om = 1;
                    break;
                } else {
                    om = 2;
                }
            }
            oy = event.getY();
            if (isToOut(dis)) {
                scrollY += dis / PULL_RESISTANCE;
                if (!isOutOfList) {
                    isOutOfList = true;
                }
                scrollTo(0, scrollY);
                postInvalidate();
                return true;
            }
            if (isOutOfList) {
                return true;
            }
            break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isToOut(float dis) {
        if (mListView.getChildCount() == 0) {
            return true;
        }
        if (mListView.computeVerticalScrollOffset() == 0) {
            if (dis < 0 || scrollY < 0) {
                scrollD = -1;
                return true;
            }
        }
        if (mListView.computeVerticalScrollOffset() + mListView.computeVerticalScrollExtent() >=
                mListView.computeVerticalScrollRange()) {
            if (dis > 0 || scrollY > 0) {
                scrollD = 1;
                return true;
            }
        }
        scrollD = 0;
        scrollY = 0;
        return false;
    }

    public VirtualListView lv() {
        return mListView;
    }
}

class VirtualListView extends ScrollView {
    public VirtualListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VirtualListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VirtualListView(Context context) {
        super(context);
    }

    public void setView(View view) {
        addView(view);
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }
}