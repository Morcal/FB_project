package com.feibo.joke.video.widget;

import java.util.LinkedList;

import android.R.integer;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.joke.video.widget.SingleTouchView.OnMarkEditListener;

/**
 * 贴图板
 * 
 * @author Lidiqing
 * 
 */
public class PasteMarkView extends ViewGroup {

	private OnPasteListener mPasteListener;
	private MarkInfo mSelectedMarkInfo;
	private OnMarkEditListener mEditListener;
	private LinkedList<MarkInfo> mMarkInfos;

	// 贴图初始宽高
	private int mMarkInitWidth;
	private int mMarkInitHeight;
	
	// 贴图最大宽高
	private int mMarkMinWidth;
	private int mMarkMinHeight;
	
	// 贴图最小宽高
	private int mMarkMaxWidth;
	private int mMarkMaxHeight;
	
	public PasteMarkView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public PasteMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public PasteMarkView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PasteMarkView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mMarkInfos = new LinkedList<MarkInfo>();
		
		mEditListener = new OnMarkEditListener() {
			
			@Override
			public void onEditable(View view, boolean editable) {
				if (editable) {

					// 置顶
					removeView(view);
					addView(view);
					
					// 改变样式，可编辑贴图显然编辑框，其他去除编辑框
					for (int i = 0; i < getChildCount(); i++) {
						View childView = getChildAt(i);
						if (childView != view) {
							((SingleTouchView) childView).setEditable(false);
						}
					}

					// 查到该view对应的viewinfo
					for(MarkInfo info: mMarkInfos){
						if(info.getMarkView() == view){
							mSelectedMarkInfo = info;
							mPasteListener.onSelect(info);
							break;
						}
					}
					
				}
			}
			
			@Override
			public void onClose(View view) {
				for(MarkInfo info: mMarkInfos){
					if(info.getMarkView() == view){
						mMarkInfos.remove(info);
						break;
					}
				}
				removeView(view);
				clearEditableView();
			}
		};
	}

	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		for (int i = 0; i < getChildCount(); i++) {
			SingleTouchView child = (SingleTouchView) getChildAt(i);
			Rect rect = child.getViewLocation();
			child.layout(rect.left, rect.top, rect.right, rect.bottom);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			
			// 如果可编辑view存在，则去除编辑状态
			for (int i = 0; i < getChildCount(); i++) {
				((SingleTouchView) getChildAt(i)).setEditable(false);
			}
			if (mSelectedMarkInfo != null) {
				// 回调保存操作
				mPasteListener.onSave(mSelectedMarkInfo);
				mSelectedMarkInfo = null;
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	public void setMarkMinSize(int minWidth, int minHeight){
		mMarkMinWidth = minWidth;
		mMarkMinHeight = minHeight;
	}
	
	public void setMarkMaxSize(int maxWidth, int maxHeight){
		mMarkMaxWidth = maxWidth;
		mMarkMaxHeight = maxHeight;
	}
	
	public void setMarkInitSize(int initWidth, int initHeight){
		mMarkInitWidth = initWidth;
		mMarkInitHeight = initHeight;
	}
	
	
	/**
	 * 贴图
	 * @param info
	 */
	public void addMarkInfo(MarkInfo info){
//		float density = getResources().getDisplayMetrics().density;
		MarkView view = info.getMarkView();
		addView(view);
		// 初始大小
		float mBitmapWidth = view.getBitmap().getWidth();
		float scale = mMarkInitWidth / mBitmapWidth;
		view.setImageScale(scale);
		// 最小
		float minScale = mMarkMinWidth / mBitmapWidth;
		view.setMinImageScale(minScale);
		// 最大
		float maxScale = mMarkMaxWidth / mBitmapWidth;
		view.setMaxImageScale(maxScale);
		
		mMarkInfos.add(info);
		mSelectedMarkInfo = info;
		view.setOnEditListener(mEditListener);
	}

	/**
	 * 根据百分比显示贴图
	 * @param progress
	 */
	public void showMarks(float progress){
		for(MarkInfo info: mMarkInfos){
			if(progress >= info.getProgressStart() && progress <= info.getProgressEnd()){
				info.getMarkView().setVisibility(View.VISIBLE);
			}else{
				info.getMarkView().setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void clearEditableView() {
		for (int i = 0; i < getChildCount(); i++) {
			((SingleTouchView) getChildAt(i)).setEditable(false);
		}

		if (mSelectedMarkInfo != null) {
			mSelectedMarkInfo = null;
		}
		invalidate();
	}
	
	/**
	 * 保存选中的贴图
	 */
	public void saveSelectedView(){
		for (int i = 0; i < getChildCount(); i++) {
			((SingleTouchView) getChildAt(i)).setEditable(false);
		}
		if (mSelectedMarkInfo != null) {
			mPasteListener.onSave(mSelectedMarkInfo);
			mSelectedMarkInfo = null;
		}
		invalidate();
	}

	public void setPasteListener(OnPasteListener listener){
		mPasteListener = listener;
	}
	
	public MarkInfo getSelectedViewInfo() {
		return mSelectedMarkInfo;
	}

	
	public static interface OnPasteListener {
		void onSave(MarkInfo markInfo);
		void onSelect(MarkInfo markInfo);
	}
	
	/**
	 * 贴图信息
	 * @author Lidiqing
	 *
	 */
	public static class MarkInfo{
		private MarkView markView;
		private float progressStart;
		private float progressEnd;
		
		public MarkInfo(MarkView view, float start, float end){
			markView = view;
			progressStart = start;
			progressEnd = end;
		}

		public MarkView getMarkView() {
			return markView;
		}

		public void setMarkView(MarkView markView) {
			this.markView = markView;
		}

		public float getProgressStart() {
			return progressStart;
		}

		public void setProgressStart(float progressStart) {
			this.progressStart = progressStart;
		}

		public float getProgressEnd() {
			return progressEnd;
		}

		public void setProgressEnd(float progressEnd) {
			this.progressEnd = progressEnd;
		}
	}
	
}
