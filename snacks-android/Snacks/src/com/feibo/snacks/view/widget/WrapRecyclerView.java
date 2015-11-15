package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.feibo.snacks.R;

/**
 * Created by Jayden on 2015/7/13.
 */
public class WrapRecyclerView extends RecyclerView {

    private final static int DEFAULT_ROW_HEIGHT = 55;
    public boolean hasScrollBar = true;
    private int rowHeight;
    private int row = 0;

    /**
     * Constructor
     *
     * @param context the context
     */
    public WrapRecyclerView(Context context) {
        this(context,null);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs the attribute set
     */
    public WrapRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WrapRecyclerView, defStyle, 0);

        int mRowWidth = a.getInteger(R.styleable.WrapRecyclerView_row_height, DEFAULT_ROW_HEIGHT);
        a.recycle();

        float scale = context.getResources().getDisplayMetrics().density;
        rowHeight = (int) (mRowWidth * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = heightMeasureSpec;
//        if (hasScrollBar) {
//            expandSpec = MeasureSpec.makeMeasureSpec(row * rowHeight,
//                    MeasureSpec.EXACTLY);
//            super.onMeasure(widthMeasureSpec, expandSpec);// 注意这里,这里的意思是直接测量出GridView的高度
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 0.52);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRow(int row) {
        this.row = row;
        requestLayout();
    }
}
