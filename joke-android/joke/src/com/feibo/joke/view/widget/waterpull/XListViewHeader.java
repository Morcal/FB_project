/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.feibo.joke.view.widget.waterpull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.feibo.joke.R;

public abstract class XListViewHeader extends LinearLayout {
    private LinearLayout mContainer;
    private ImageView downView;
    private ImageView loadingView;
    private ImageView noNetView;

    private int mState = STATE_NORMAL;

    private Animation rotateAnim;
    private Animation showAnim;
    private Animation hideAnim;

    private final int ROTATE_ANIM_DURATION = 500;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_NO_NET = 3;

    private boolean isCustom = false;
    private int orilgHeight;
    private int mDrawableMaxHeight = -1;
    private double mZoomRatio = 1;
    private ImageView mImageView;

    public XListViewHeader(Context context, float mZoomRatio) {
        super(context);
        this.mZoomRatio = mZoomRatio;
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public View getCustomView() {
        return mContainer;
    }

    private void initView(Context context) {
        LinearLayout.LayoutParams lp;
        int layout = getHeaderView();
        if (layout != R.layout.xlistview_header) {
            isCustom = true;
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mContainer = (LinearLayout) LayoutInflater.from(context).inflate(layout, null);
            mImageView = (ImageView) mContainer.findViewById(R.id.user_avatar_max);
            addView(mContainer, lp);
            setGravity(Gravity.BOTTOM);
            post(new Runnable() {
                @Override
                public void run() {
                    setViewsBounds(mZoomRatio);
                }
            });
        } else {
            layout = R.layout.xlistview_header;
            // 初始情况，设置下拉刷新view高度为0
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);

            mContainer = (LinearLayout) LayoutInflater.from(context).inflate(layout, null);
            addView(mContainer, lp);
            // setGravity(Gravity.BOTTOM);

            downView = (ImageView) findViewById(R.id.xlistview_header_down);
            loadingView = (ImageView) findViewById(R.id.xlistview_header_loading);
            noNetView = (ImageView) findViewById(R.id.xlistview_header_no_net);

            rotateAnim = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(ROTATE_ANIM_DURATION);
            rotateAnim.setRepeatCount(-1);
            rotateAnim.setInterpolator(new LinearInterpolator());// 设置匀速，无加速度不卡顿
            rotateAnim.setRepeatMode(Animation.RESTART);
            // rotateAnim.setFillAfter(true);

            showAnim = new AlphaAnimation(0, 1.0f);
            showAnim.setDuration(ROTATE_ANIM_DURATION);
            showAnim.setFillAfter(true);

            hideAnim = new AlphaAnimation(1.0f, 0);
            hideAnim.setDuration(ROTATE_ANIM_DURATION);
            hideAnim.setFillAfter(true);

        }
    }
    
    public int getState() {
        return mState;
    }
    
    public void setState(int state, final INoNetListener listener) {
        if (isCustom) {
            return;
        }
        if (state == mState) {
            return;
        }
        switch (state) {

        case STATE_NORMAL:
        case STATE_READY:
            mClearAnimation();
            downView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
            noNetView.setVisibility(View.INVISIBLE);
            break;
        case STATE_REFRESHING:
            showRefreshUI();
            break;
        case STATE_NO_NET:
            mClearAnimation();
            
            downView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
            noNetView.setVisibility(View.VISIBLE);
//            noNetView.startAnimation(showAnim);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(listener != null) {
                        listener.showNoNetAnimationFinish();
                    }
                }
            }, 800);
            
            break;
        default:
            break;
        }
        mState = state;
    }

    protected void showRefreshUI() {
        mClearAnimation();
        downView.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        noNetView.setVisibility(View.INVISIBLE);
        loadingView.startAnimation(rotateAnim);
    }

    public void setState(int state) {
        setState(state, null);
    }
    
    public interface INoNetListener{
        public void showNoNetAnimationFinish();
    }

    private void mClearAnimation() {
        noNetView.clearAnimation();
        loadingView.clearAnimation();
        downView.clearAnimation();
    }

    /**
     * @param negative 是否隐藏
     * @param height
     */
    public void setVisiableHeight(boolean negative, int height) {
        if (orilgHeight == 0) {
            orilgHeight = getVisiableHeight();
        }
        if (height < 0) {
            height = 0;
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public void setImageViewHeight(int deltaY) {
        if (orilgHeight == 0) {
            orilgHeight = getVisiableHeight();
        }
        if (mImageView.getHeight() <= mDrawableMaxHeight) {
            if (deltaY < 0) {
                if (mImageView.getHeight() - deltaY / 2 >= orilgHeight) {
                    mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY / 2 < mDrawableMaxHeight ? mImageView
                            .getHeight() - deltaY / 2
                            : mDrawableMaxHeight;
                    mImageView.requestLayout();
                }
            } else {
                if (mImageView.getHeight() > orilgHeight) {
                    mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY > orilgHeight ? mImageView
                            .getHeight() - deltaY : orilgHeight;
                    mImageView.requestLayout();
                }
            }
        }
    }

    public void setonTouchUp() {
        if (orilgHeight - 1 < mImageView.getHeight()) {
            BackAnimimation animation = new BackAnimimation(mImageView, orilgHeight, false);
            animation.setDuration(300);
            mImageView.startAnimation(animation);
        }
    }

    public void setTopMargin(int margin) {
        if (orilgHeight == 0) {
            orilgHeight = getVisiableHeight();
        }
        RelativeLayout.LayoutParams imgLp = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        imgLp.topMargin = margin;
        mImageView.setLayoutParams(imgLp);
    }

    public void resetVisibleHeight() {
        setVisiableHeight(true, orilgHeight);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

    public abstract int getHeaderView();

    /**
     * Set the bounds of the views and set the zoom of the view.
     * <p>
     * Necessary to get the size of the Views.
     * <p>
     * Have to put in the {@link #onWindowFocusChanged(boolean)} of the
     * activity.
     * 
     * @param zoomRatio Double - How many times is the max zoom of the image,
     *            minimum 1.
     */
    public void setViewsBounds(double zoomRatio) {
        if (orilgHeight == 0 && mImageView != null) {
            orilgHeight = mImageView.getHeight();
            double imageRatio = ((double) mImageView.getDrawable().getIntrinsicWidth())
                    / ((double) mImageView.getWidth());
            mDrawableMaxHeight = (int) ((mImageView.getDrawable().getIntrinsicHeight() / imageRatio) * (zoomRatio > 1 ? zoomRatio
                    : 1));
        }
    }

}
