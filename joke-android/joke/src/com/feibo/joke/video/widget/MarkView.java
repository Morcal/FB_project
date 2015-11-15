package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Color;

import com.feibo.joke.R;

/**
 * 贴图
 * @author Lidiqing 2014/4/29
 *
 */
public class MarkView extends SingleTouchView {

	private static final float CHARTLET_IMAGE_SCALE = 1.2f;

	public MarkView(Context context, int imageRes) {
		super(context);
		init(context, imageRes);
	}

	private void init(Context context, int imageRes) {
		setImageResource(imageRes);
		setBackgroundColor(Color.TRANSPARENT);
		setFrameColor(getResources().getColor(R.color.c9_white_alpha_70));
		setControlLocation(SingleTouchView.RIGHT_BOTTOM);
		setImageScale(CHARTLET_IMAGE_SCALE);
	}
	
	
}
