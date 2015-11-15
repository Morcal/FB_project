package com.feibo.joke.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.feibo.joke.R;

/**
 * Created by Lidiqing on 2015/5/10.
 */
public class RatioLayout extends LinearLayout {

    private int mRatioWidth;
    private int mRatioHeight;
    private float mRatio;

    private int ratioType;

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mRatioWidth = t.getInteger(R.styleable.ScaleView_ratioWidth, 1);
        mRatioHeight = t.getInteger(R.styleable.ScaleView_ratioHeight, 1);
        ratioType = t.getInt(R.styleable.ScaleView_radioType, 0);
        t.recycle();
        mRatio = mRatioHeight / (float) mRatioWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratioType != 0) {
            int hSize = MeasureSpec.getSize(heightMeasureSpec);
            int wSize = (int) (hSize * mRatio);
            super.onMeasure(MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.EXACTLY), heightMeasureSpec);
            return;
        }

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        // 根据宽和高宽比来设置高
        if (wMode == MeasureSpec.EXACTLY) {
            int wSize = MeasureSpec.getSize(widthMeasureSpec);
            int hSize = (int) (wSize * mRatio);
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.EXACTLY));
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
