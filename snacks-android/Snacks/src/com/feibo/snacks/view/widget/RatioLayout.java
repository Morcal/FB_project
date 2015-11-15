package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.feibo.snacks.R;

/**
 * 按比例显示缩放图片
 * 
 * @author lidiqing
 * @version 2015-1-5 上午9:09:01
 */
public class RatioLayout extends FrameLayout {

    private int mRatioWidth;
    private int mRatioHeight;

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mRatioWidth = typedArray.getInteger(R.styleable.RatioLayout_ratioWidth, 1);
        mRatioHeight = typedArray.getInteger(R.styleable.RatioLayout_ratioHeight, 1);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float mRatio = mRatioHeight / (float) mRatioWidth;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) Math.ceil(widthSize * mRatio);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightSpec);
    }

}
