package com.feibo.joke.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TouchedLinearLayout extends LinearLayout {

    public TouchedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TouchedLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);
        return false;
    }
}
