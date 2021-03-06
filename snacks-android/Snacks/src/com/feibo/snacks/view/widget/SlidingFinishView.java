package com.feibo.snacks.view.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Scroller;

import com.feibo.snacks.R;

import java.util.LinkedList;
import java.util.List;

public class SlidingFinishView extends FrameLayout {
    private View mContentView;
    private int mTouchSlop;
    private int downX;
    private int downY;
    private int tempX;
    private Scroller mScroller;
    private int viewWidth;
    private boolean isSilding;
    private boolean isFinish;
    private Activity mActivity;
    private List<ViewGroup> mViewPagers = new LinkedList<ViewGroup>();
    private Drawable mShadowDrawable;

    public SlidingFinishView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingFinishView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        mShadowDrawable = getResources().getDrawable(R.drawable.shadow_left);
    }

    public void attachToActivity(Activity activity) {
        mActivity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.windowBackground });
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    private void setContentView(View decorChild) {
        mContentView = (View) decorChild.getParent();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //处理ViewPager冲突问题
        getAlLViewPager(mViewPagers, this);
        ViewGroup mViewPager = getTouchViewPager(mViewPagers, ev);
        if (mViewPager instanceof  ViewPager) {
            ViewPager viewPager = (ViewPager) mViewPager;
            if(viewPager != null && viewPager.getCurrentItem() != 0){
                return super.onInterceptTouchEvent(ev);
            }
        }
        if (mViewPager instanceof RecyclerView) {
            return super.onInterceptTouchEvent(ev);
        }

        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            downX = tempX = (int) ev.getRawX();
            downY = (int) ev.getRawY();
            break;
        case MotionEvent.ACTION_MOVE:
            int moveX = (int) ev.getRawX();
            // 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
            if (moveX - downX > mTouchSlop
                    && Math.abs((int) ev.getRawY() - downY) < mTouchSlop && (tempX < viewWidth / 2)) {
                return true;
            }
            break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            int moveX = (int) event.getRawX();
            int deltaX = tempX - moveX;
            tempX = moveX;
            if (moveX - downX > mTouchSlop
                    && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                isSilding = true;
            }

            if (moveX - downX >= 0 && isSilding) {
                mContentView.scrollBy(deltaX, 0);
            }
            break;
        case MotionEvent.ACTION_UP:
            isSilding = false;
            if (mContentView.getScrollX() <= -viewWidth / 2) {
                isFinish = true;
                scrollRight();
            } else {
                scrollOrigin();
                isFinish = false;
            }
            break;
        }

        return true;
    }

    private void getAlLViewPager(List<ViewGroup> mViewPagers, ViewGroup parent){
        int childCount = parent.getChildCount();
        for(int i=0; i<childCount; i++){
            View child = parent.getChildAt(i);
            if(child instanceof ViewPager){
                mViewPagers.add((ViewPager)child);
            }else if(child instanceof ListView) {
                ListView listView = (ListView) child;
                if (listView.getChildCount() > 0) {
                    for (int j = 0; j < listView.getChildCount(); j++) {
                        View view = listView.getChildAt(j);
                        if (view instanceof ViewGroup) {
                            ViewGroup group = (ViewGroup) view;
                            getRecycleView(mViewPagers, group);
                        }
                    }
                }
            } else if (child instanceof ViewGroup) {
                getAlLViewPager(mViewPagers, (ViewGroup) child);
            }
        }
    }

    private void getRecycleView(List<ViewGroup> list, ViewGroup viewGroup) {
        if (viewGroup instanceof RecyclerView) {
            list.add(viewGroup);
            return;
        }
        int childCount = viewGroup.getChildCount();
        for(int i=0; i<childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                getRecycleView(list, (ViewGroup) child);
            }
        }
    }

    private ViewGroup getTouchViewPager(List<ViewGroup> mViewPagers, MotionEvent ev){
        if(mViewPagers == null || mViewPagers.size() == 0){
            return null;
        }
        Rect mRect = new Rect();
        for(ViewGroup v : mViewPagers){
            if (v instanceof RecyclerView) {
                v.getGlobalVisibleRect(mRect);
                mRect.left = Math.abs(mRect.left);
                mRect.bottom = Math.abs(mRect.bottom);
                mRect.right = Math.abs(mRect.right);
                mRect.top = Math.abs(mRect.top);
            } else {
                v.getHitRect(mRect);
            }
            if(mRect.contains((int)ev.getX(), (int)ev.getY())){
                return v;
            }
        }
        return null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            viewWidth = this.getWidth();
            getAlLViewPager(mViewPagers, this);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mShadowDrawable != null && mContentView != null) {

            int left = mContentView.getLeft()
                    - mShadowDrawable.getIntrinsicWidth();
            int right = left + mShadowDrawable.getIntrinsicWidth();
            int top = mContentView.getTop();
            int bottom = mContentView.getBottom();

            mShadowDrawable.setBounds(left, top, right, bottom);
            mShadowDrawable.draw(canvas);
        }

    }

    private void scrollRight() {
        final int delta = (viewWidth + mContentView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        mScroller.startScroll(mContentView.getScrollX(), 0, -delta + 1, 0,
                Math.abs(delta));
        postInvalidate();
    }

    private void scrollOrigin() {
        int delta = mContentView.getScrollX();
        mScroller.startScroll(mContentView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (mScroller.computeScrollOffset()) {
            mContentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();

            if (mScroller.isFinished() && isFinish) {
                mActivity.finish();
            }
        }
    }
}
