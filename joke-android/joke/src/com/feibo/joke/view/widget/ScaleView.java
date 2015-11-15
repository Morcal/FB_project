package com.feibo.joke.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.feibo.joke.R;

/**
 * 按比例显示缩放图片
 * 
 * @author lidiqing
 * @version 2015-1-5 上午9:09:01
 */
public class ScaleView extends ImageView {

    private int mRatioWidth;
    private int mRatioHeight;

    private int type;

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mRatioWidth = typedArray.getInteger(R.styleable.ScaleView_ratioWidth, 1);
        mRatioHeight = typedArray.getInteger(R.styleable.ScaleView_ratioHeight, 1);
        type = typedArray.getInt(R.styleable.ScaleView_radioType, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (type == 0) {
            float mRatio = mRatioHeight / (float) mRatioWidth;
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = (int) Math.ceil(widthSize * mRatio);
            int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, newHeightSpec);
        } else {
            float mRatio = mRatioWidth / (float) mRatioHeight;
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthSize = (int) Math.ceil(heightSize * mRatio);
            int newWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            super.onMeasure(newWidthSpec, heightMeasureSpec);
        }
    }

}
