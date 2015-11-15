package com.feibo.joke.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author BigTiger
 *
 * @explain 圆形的ImageView
 *
 * @date 2015年4月7日  上午10:31:39
 */
public class CircleImageView extends ImageView {
    private Paint mPaint;
    private Context mContext;
    private Xfermode mXfermode;
    private Bitmap mMaskBitmap;
    public CircleImageView(Context context) {
        this(context,null);
    }
    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXfermode = new PorterDuffXfermode(Mode.DST_IN);
        //TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.CircleImageView);

        //a.recycle();

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //强制改变View的宽高,使View的宽高保持一致
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(width, width);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        //拿到Drawable
        Drawable drawable = getDrawable();
        if(drawable == null) {
            return;
        }
        //获取drawable的宽和高
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        Bitmap bitmap = null;
        if (drawable != null){
            //创建bitmap
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);
            float scale = 1.0f;
            //创建画布
            Canvas drawCanvas = new Canvas(bitmap);
            //按照bitmap的宽高，以及view的宽高，计算缩放比例；因为设置的src宽高比例可能和imageview的宽高比例不同，这里我们不希望图片失真；
            scale = getWidth() * 1.0F / Math.min(dWidth, dHeight);
            //根据缩放比例，设置bounds，相当于缩放图片了
            drawable.setBounds(0, 0, (int) (scale * dWidth),
                    (int) (scale * dHeight));
            drawable.draw(drawCanvas);
            if (mMaskBitmap == null || mMaskBitmap.isRecycled()){
                mMaskBitmap = getMaskBitmap();
            }
            // Draw Bitmap.
            mPaint.reset();
            mPaint.setFilterBitmap(false);
            mPaint.setXfermode(mXfermode);
            //绘制形状
            drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);
            mPaint.setXfermode(null);
            //将准备好的bitmap绘制出来
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }
    /**
     * 获取ImageView的的形状
     * @return 圆形的Bitmap
     * -
     */
    private Bitmap getMaskBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2,paint);
        return bitmap;
    }
}


