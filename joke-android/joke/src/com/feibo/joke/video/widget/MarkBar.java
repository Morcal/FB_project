package com.feibo.joke.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.feibo.joke.R;

/**
 * 图标工具栏
 * 
 * @author Lidiqing 2015/4/29
 * 
 */
public class MarkBar extends LinearLayout {

	private static int DURATION_ANIM = 500;

	private MarkGridAdapter mGridAdapter;
	private MarkGridView mGridView;

	private View mBackBtn;
	private boolean mSelectable;

	private TranslateAnimation mShowAnimation;
	private TranslateAnimation mHideAnimation;

	private int mAnimDuration;

	private OnMarkBarActionListener mActionListener;

	public MarkBar(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public MarkBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MarkBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mAnimDuration = DURATION_ANIM;
		mSelectable = true;
	}

	@Override
	protected void onFinishInflate() {
		mBackBtn = findViewById(R.id.btn_back);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hide();
			}
		});

		mGridView = (MarkGridView) findViewById(R.id.grid);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mSelectable) {
					mGridAdapter.setPressedPosition(position);
					mGridAdapter.notifyDataSetChanged();
					mActionListener.onSelect(mGridAdapter.getSelectMark());
				}
			}
		});
	}

	public void show() {
		mSelectable = true;
		mShowAnimation = new TranslateAnimation(getWidth(), 0, 0, 0);
		mShowAnimation.setDuration(mAnimDuration);
		mShowAnimation.setInterpolator(new MarkInterpolator());
		mShowAnimation.setAnimationListener(new AnimationOpenListener(this));
		startAnimation(mShowAnimation);
		mActionListener.onShow();
	}

	public void hide() {
		mSelectable = false;
		mHideAnimation = new TranslateAnimation(0, getWidth(), 0, 0);
		mHideAnimation.setDuration(mAnimDuration);
		mHideAnimation.setInterpolator(new MarkInterpolator());
		mHideAnimation.setAnimationListener(new AnimationCloseListener(this));
		startAnimation(mHideAnimation);
		mActionListener.onHide();
	}

	public void setGridViewAdapter(MarkGridAdapter adapter) {
		mGridAdapter = adapter;
		mGridView.setAdapter(mGridAdapter);
	}

	public void setOnActionListener(OnMarkBarActionListener listener) {
		this.mActionListener = listener;
	}

	public int getSelectMark() {
		return mGridAdapter.getSelectMark();
	}

	public void clearSelectMark() {
		mGridAdapter.clearPressedPosition();
		mGridAdapter.notifyDataSetChanged();
	}

	public static interface OnMarkBarActionListener {
		void onShow();

		void onHide();

		void onSelect(int resId);
	}

	public static class AnimationCloseListener implements AnimationListener {
		private View view;

		public AnimationCloseListener(View view) {
			this.view = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			view.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}

	public static class AnimationOpenListener implements AnimationListener {
		private View view;

		public AnimationOpenListener(View view) {
			this.view = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			view.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}

	public class MarkInterpolator implements Interpolator {

		@Override
		public float getInterpolation(float t) {
			t -= 1;
			return t * t * t * t * t + 1;
		}

	}

}
