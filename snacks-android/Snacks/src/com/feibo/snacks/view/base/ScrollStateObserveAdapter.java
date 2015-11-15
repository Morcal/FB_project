package com.feibo.snacks.view.base;

import android.content.Context;

import fbcore.widget.BaseSingleTypeAdapter;

public abstract class ScrollStateObserveAdapter<T> extends BaseSingleTypeAdapter<T>{

    public static final int SCROLLING_STATE = 0;
    public static final int SCROLLED_STATE = 1;

    protected int scrollState = SCROLLED_STATE;

    public ScrollStateObserveAdapter(Context context) {
        super(context);
    }

    public void setScrollState(int scrollState) {
        this.scrollState = scrollState;
    }


}
