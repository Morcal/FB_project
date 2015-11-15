package com.feibo.joke.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.feibo.joke.R;

public class FallbackButton extends View implements OnClickListener {
	private static final int STATE_REST = 0;
	private static final int STATE_SELECTED = 1;
	private int mState;

	private OnFallBackListener mBackListener;
	private int mNormalResource;
	private int mSelecteResource;
	
	public FallbackButton(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public FallbackButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public FallbackButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FallbackButton(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		
		mNormalResource = R.drawable.btn_fall_back_normal;
		mSelecteResource = R.drawable.btn_fall_back_selected;
		setBackgroundResource(mNormalResource);
		
		mState = STATE_REST;
		setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(mState == STATE_REST){
			setBackgroundResource(mSelecteResource);
			mState = STATE_SELECTED;
			mBackListener.onSelected();
		}else if (mState == STATE_SELECTED) {
			setBackgroundResource(mNormalResource);
			mState = STATE_REST;
			mBackListener.onFallback();
		}
	}
	
	public void cancelSeleted(){
		setBackgroundResource(mNormalResource);
		mState = STATE_REST;
	}
	
	public void setFallBackListener(OnFallBackListener listener){
		mBackListener = listener;
	}
	
	public interface OnFallBackListener{
		void onSelected();
		void onFallback();
	}

}
