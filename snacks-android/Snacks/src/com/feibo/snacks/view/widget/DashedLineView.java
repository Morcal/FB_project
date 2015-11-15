package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.feibo.snacks.R;
import com.feibo.snacks.view.util.UIUtil;

public class DashedLineView extends View {

    private static final int HORIZONTAL = 0;

    private Paint paint;
    private Path path;
    private DashPathEffect dashPathEffect;
    private int orientation;

    public DashedLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public DashedLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashedLineView(Context context) {
        this(context,null);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.dashedline, defStyleAttr, 0);
        int lineColor = a.getColor(R.styleable.dashedline_lineColor, 0XFF000000);
        int width = a.getDimensionPixelOffset(R.styleable.dashedline_dashedWidth, 1);
        int distance = a.getDimensionPixelOffset(R.styleable.dashedline_dashedDistance, 1);
        orientation = a.getInt(R.styleable.dashedline_orientation, 0);
        a.recycle();
        paint = new Paint();
        path = new Path();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(UIUtil.dp2Px(getContext(), width));
        float[] arrayOfFloat = new float[4];
        arrayOfFloat[0] = UIUtil.dp2Px(getContext(), distance);
        arrayOfFloat[1] = UIUtil.dp2Px(getContext(), distance);
        dashPathEffect = new DashPathEffect(arrayOfFloat, 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(0.0F, 0.0F);
        if (orientation == HORIZONTAL) {
            path.lineTo(getMeasuredWidth(), 0.0f);
        } else {
            path.lineTo(0.0f, getMeasuredHeight());
        }
        paint.setPathEffect(dashPathEffect);
        canvas.drawPath(path, paint);
    }

}