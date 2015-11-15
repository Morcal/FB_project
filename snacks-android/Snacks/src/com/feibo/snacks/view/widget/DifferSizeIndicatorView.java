package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.feibo.snacks.R;

public class DifferSizeIndicatorView extends View{

    private static final  String TAG = DifferSizeIndicatorView.class.getSimpleName();
    private int count = 2;
    private int currentPos = 0;
    private Drawable drawable;
    private int space;
    private Rect bounds;

    private int normalWidth = 0;
    private int normalHeight = 0;
    private int selectHeight = 0;
    private int selectWidth = 0;

    private static final int[] FOCUSED_STATE = new int[]{android.R.attr.state_focused};
    private static final int[] NORMAL_STATE = new int[]{};

    public DifferSizeIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public DifferSizeIndicatorView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(attrs, 0);
    }

    public DifferSizeIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.IndicatorView, defStyleAttr, 0);
        drawable = a.getDrawable(R.styleable.IndicatorView_drawable);
        space = a.getDimensionPixelOffset(R.styleable.IndicatorView_space, 0);
        bounds = new Rect();
        if(drawable == null) {
            return;
        }
        drawable.setState(FOCUSED_STATE);
        selectHeight = drawable.getIntrinsicHeight();
        selectWidth = drawable.getIntrinsicWidth();
        drawable.setState(NORMAL_STATE);
        normalHeight = drawable.getIntrinsicHeight();
        normalWidth = drawable.getIntrinsicWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = count == 0 ? 0 : normalWidth * (count - 1) + selectWidth + space * (count) + getPaddingLeft() + getPaddingRight();
        int height = count == 0 ? 0 : selectHeight + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            drawable.setState(NORMAL_STATE);
            if (i == currentPos) {
                drawable.setState(FOCUSED_STATE);
            }
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bounds.set(0, 0, width, height);
            int x = 0;
            int y = 0;
            if(i == currentPos) {
                x = i == 0 ? 0 : (normalWidth + space) * i + getPaddingLeft();
            } else if(i < currentPos) {
                x = (normalWidth + space) * i + getPaddingLeft();
            } else {
                x = (normalWidth + space)*  (i - 1) + (selectWidth + space)  + getPaddingLeft();
            }
            if (i != currentPos) {
                y =  getPaddingTop() + (selectHeight - normalHeight) / 2;
            } else {
                y =  getPaddingTop();
            }
            bounds.offsetTo(x, y);
            drawable.setBounds(bounds);
            drawable.draw(canvas);
        }
    }

    public void setCurrentPosition(int position) {
        this.currentPos = position;
        invalidate();
    }

    public void setCount(int count) {
        this.count = count;
        requestLayout();
    }
}
