package com.feibo.snacks.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FixFrameLayout extends FrameLayout {

    public FixFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FixFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixFrameLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mesureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mesureHeight == 0) {
            mesureHeight = 1800;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mesureHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
