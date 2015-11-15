package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.feibo.snacks.R;

public class HWRadioImageView extends ImageView {

    private float radio;

    public HWRadioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public HWRadioImageView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public HWRadioImageView(Context context) {
        this(context,null);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HWRadioImageView);
        radio = a.getFloat(R.styleable.HWRadioImageView_hwRadio, 1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * radio);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRadio(float radio) {
        this.radio = radio;
        requestLayout();
    }

}
