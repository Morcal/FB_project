/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.feibo.joke.view.widget.waterpull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;

public abstract class XListViewFooter extends LinearLayout {
    protected final static int STATE_NORMAL = 0;
//  protected final static int STATE_READY = 1;
    protected final static int STATE_START_LOADING = 2;
    protected final static int STATE_LOADING_OVER = 3;
//    protected final static int STATE_GONE = 4;
    protected final static int STATE_OTHER = 5;
    
    private int mState = -1;

	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;
	
	private String loadMoreOverText;
	
    private final int ROTATE_ANIM_DURATION = 500;
    private Animation rotateAnim;

	public XListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
	    if(mState == state) {
	        return;
	    }
        this.setVisibility(View.VISIBLE);
	    mState = state;
		mProgressBar.setVisibility(View.VISIBLE);
//		mHintView.setVisibility(View.INVISIBLE);
		switch (state) {
        case STATE_START_LOADING:
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.startAnimation(rotateAnim);
            mHintView.setVisibility(View.GONE);
            break;
        case STATE_NORMAL:
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.clearAnimation();
            mHintView.setVisibility(View.GONE);
            break;
//        case STATE_GONE: //没有更多加载数据，隐藏footer
//            mProgressBar.clearAnimation();
//            this.setVisibility(View.INVISIBLE);
//            break;
        case STATE_OTHER: //设置其他信息，如错误信息等，
            mProgressBar.clearAnimation();
            mHintView.setVisibility(View.VISIBLE);
            break;
        case STATE_LOADING_OVER:  //没有更多数据，加载完成
            mProgressBar.clearAnimation();
            mProgressBar.setVisibility(View.GONE);
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(StringUtil.isEmpty(loadMoreOverText) ? "加载结束" : loadMoreOverText);
            break;
        }    
		    
	}
	
	public int getState() {
	    return mState;
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return; 
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
//		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * loading status
	 */
	public void loading() {
//		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context) {
		mContext = context;
		int layout = getFooterLayout() == 0 ? R.layout.xlistview_footer : getFooterLayout();
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(layout, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		mContentView = moreView.findViewById(R.id.xlistview_footer_content);
		mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);
		
        rotateAnim = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setInterpolator(new LinearInterpolator());// 设置匀速，无加速度不卡顿
        rotateAnim.setRepeatMode(Animation.RESTART);
	}
	
    public abstract int getFooterLayout(); 
    public void setFooterLoadMoreOverText(String loadMoreOverText) {
        this.loadMoreOverText = loadMoreOverText;
    }
}
