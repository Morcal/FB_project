package com.feibo.snacks.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

@SuppressWarnings("unused")
public class CurtainView extends RelativeLayout{
	private Context mContext;
	private Scroller mScroller;
	private int curtainHeigh = 0;
	private boolean isOpen = true;
    private boolean isMove = false;
	private int upDuration = 120;
	private int downDuration = 120;
	private ICurTainShowListener listener;

	public CurtainView(Context context) {
		this(context,null);
	}

	public CurtainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CurtainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		Interpolator interpolator = new BounceInterpolator();
		mScroller = new Scroller(context, interpolator);
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
	}

	private void startMoveAnim(int startY, int dy, int duration) {
		isMove = true;
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
		listener.isShow(isOpen);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			isMove = true;
		} else {
			isMove = false;
		}
		super.computeScroll();
	}

	public void onRopeClick(){
		if(isOpen){
			CurtainView.this.startMoveAnim(0, getHeight(), upDuration);
		}else{
			CurtainView.this.startMoveAnim(getHeight(), -getHeight(), downDuration);
		}
        this.isOpen = !isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

	public void setListener(ICurTainShowListener listener) {
		this.listener = listener;
	}

	public interface ICurTainShowListener {
		void isShow(boolean isShow);
	}
}
